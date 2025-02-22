package com.example.authservice.service;

import com.example.authservice.domain.User;
import com.example.authservice.dto.SignUpRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User getUser(String name);

    Page<User> getAllUsers(Pageable pageable);

    User createUser(SignUpRequest signUpRequest);

    void deleteUser(String name);
}
