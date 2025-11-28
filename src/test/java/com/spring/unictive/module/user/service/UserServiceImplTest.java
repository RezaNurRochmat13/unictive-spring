package com.spring.unictive.module.user.service;

import com.spring.unictive.module.hobby.entity.Hobby;
import com.spring.unictive.module.user.entity.Role;
import com.spring.unictive.module.user.entity.User;
import com.spring.unictive.module.user.repository.UserRepository;
import com.spring.unictive.utils.exception.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .build();
    }

    @Test
    void findAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.findAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findUserById_WithValidId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findUserById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class, () -> userService.findUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void createUser_ShouldEncryptPasswordAndSaveUserWithHobbies() {
        // Arrange
        Hobby testHobby1 = new Hobby();
        testHobby1.setId(1L);
        testHobby1.setName("test hobby 1");

        List<Hobby> hobbies = new ArrayList<>();
        hobbies.add(testHobby1);
        
        User newUser = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("plainpassword")
                .role(Role.USER)
                .hobbies(hobbies)
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertNotEquals("plainpassword", result.getPassword()); // Password should be hashed
        assertTrue(passwordEncoder.matches("plainpassword", result.getPassword()));
        assertEquals(1, result.getHobbies().size());
        assertTrue(result.getHobbies().contains(testHobby1));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateAndReturnUserWithHobbies() {
        // Arrange
        Hobby testHobby1 = new Hobby();
        testHobby1.setId(2L);
        testHobby1.setName("test hobby 2");

        Hobby testHobby2 = new Hobby();
        testHobby2.setId(3L);
        testHobby2.setName("test hobby 3");

        List<Hobby> existingHobbies = new ArrayList<>();
        existingHobbies.add(testHobby1);
        
        User existingUser = User.builder()
                .id(1L)
                .username("oldusername")
                .email("old@example.com")
                .password("oldpassword")
                .role(Role.USER)
                .hobbies(existingHobbies)
                .build();

        List<Hobby> updatedHobbies = new ArrayList<>();
        updatedHobbies.add(testHobby2);
        
        User updateData = User.builder()
                .username("newusername")
                .email("new@example.com")
                .password("newpassword")
                .role(Role.ADMIN)
                .hobbies(updatedHobbies)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setHobbies(updatedHobbies);
            return savedUser;
        });

        // Act
        User result = userService.updateUser(1L, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("newusername", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());
        assertNotEquals("newpassword", result.getPassword()); // Password should be hashed
        assertTrue(passwordEncoder.matches("newpassword", result.getPassword()));
        assertEquals(1, result.getHobbies().size());
        assertTrue(result.getHobbies().contains(testHobby2));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        User updateData = User.builder()
                .username("user")
                .email("user@example.com")
                .password("password")
                .role(Role.USER)
                .hobbies(new ArrayList<>())
                .build();

        // Act & Assert
        assertThrows(ResourceNotFound.class, () -> userService.updateUser(999L, updateData));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void deleteUser_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class, () -> userService.deleteUser(999L));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }
}
