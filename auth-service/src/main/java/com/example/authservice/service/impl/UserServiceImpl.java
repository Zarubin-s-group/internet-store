package com.example.authservice.service.impl;

import com.example.authservice.domain.User;
import com.example.authservice.dto.SignUpRequest;
import com.example.authservice.exception.UserNotFoundException;
import com.example.authservice.repository.RoleRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUser(String name) {
        return userRepository.findByName(name).orElseThrow(() ->
                new UserNotFoundException(MessageFormat.format("User with name {0} not found", name)));
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User createUser(SignUpRequest signUpRequest) {
        return userRepository.save(new User(
                signUpRequest.getName(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                Collections.singletonList(roleRepository.findByName("ROLE_USER"))));
    }

    @Transactional
    @Override
    public void deleteUser(String name) {
        userRepository.deleteByName(name);
    }
}
