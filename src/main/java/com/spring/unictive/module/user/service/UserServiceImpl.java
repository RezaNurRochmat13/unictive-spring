package com.spring.unictive.module.user.service;

import com.spring.unictive.module.user.entity.User;
import com.spring.unictive.module.user.repository.UserRepository;
import com.spring.unictive.utils.exception.ResourceNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found with id " + id));
    }

    @Override
    public User createUser(User user) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User payload) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found with id " + id));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        user.setEmail(payload.getEmail());
        user.setUsername(payload.getUsername());
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found with id " + id));
        userRepository.delete(user);
    }
}
