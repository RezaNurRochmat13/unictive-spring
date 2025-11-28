package com.spring.unictive.module.user.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.unictive.UnictiveApplication;
import com.spring.unictive.module.auth.dto.request.AuthenticationRequest;
import com.spring.unictive.module.auth.dto.response.AuthenticationResponse;
import com.spring.unictive.module.hobby.entity.Hobby;
import com.spring.unictive.module.user.entity.Role;
import com.spring.unictive.module.user.entity.User;
import com.spring.unictive.module.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = UnictiveApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UsersPresenterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Hobby testHobby;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        userRepository.deleteAll();

        // Create test user with a hobby
        testHobby = new Hobby();
        testHobby.setName("Reading");
        testHobby.setDescription("Reading books");

        List<Hobby> hobbies = new ArrayList<>();
        hobbies.add(testHobby);

        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .hobbies(hobbies)
                .build();

        // Save the test user
        testUser = userRepository.save(testUser);
        
        // Set the user for the hobby (bidirectional relationship)
        testHobby.setUser(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        String token = getAuthToken();
        
        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].username", is(testUser.getUsername())))
                .andExpect(jsonPath("$[0].hobbies", hasSize(1)))
                .andExpect(jsonPath("$[0].hobbies[0].name", is(testHobby.getName())));
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String getAuthToken() throws Exception {
        String testEmail = "test_" + System.currentTimeMillis() + "@example.com";
        String username = "testuser_" + System.currentTimeMillis();
        
        // Create and save a test user with all required fields
        User authUser = new User();
        authUser.setUsername(username);
        authUser.setEmail(testEmail);
        authUser.setPassword(passwordEncoder.encode("password123"));
        authUser.setRole(Role.USER);
        authUser = userRepository.save(authUser);

        // Create authentication request
        AuthenticationRequest authRequest = new AuthenticationRequest(testEmail, "password123");
        
        // Authenticate and get token
        ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)));

        // Parse and return the token
        String response = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, AuthenticationResponse.class).getToken();
    }

    @Test
    void deleteUser_WithValidId_ShouldReturnNoContent() throws Exception {
        String token = getAuthToken();
        
        User userToDelete = new User();
        userToDelete.setUsername("todelete");
        userToDelete.setEmail("delete@example.com");
        userToDelete.setPassword("password123");
        userToDelete.setRole(Role.USER);
        userRepository.save(userToDelete);

        mockMvc.perform(delete("/users/" + userToDelete.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Verify the user is deleted
        mockMvc.perform(get("/users/" + userToDelete.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        String token = getAuthToken();
        
        testUser.setUsername("updateduser");

        mockMvc.perform(put("/users/" + testUser.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(testUser.getUsername())));
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        String token = getAuthToken();
        
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password123");
        newUser.setRole(Role.USER);

        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        String token = getAuthToken();
        
        User invalidUser = new User();
        invalidUser.setUsername("");
        invalidUser.setEmail("invalid-email");
        invalidUser.setPassword("");

        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }
}
