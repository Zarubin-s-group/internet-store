package com.example.authservice.service;

import com.example.authservice.domain.Role;
import com.example.authservice.domain.User;
import com.example.authservice.dto.SignUpRequest;
import com.example.authservice.repository.RoleRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    private User newUser;

    private List<User> users;

    @BeforeEach
    public void setUp() {
        when(passwordEncoder.encode(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0) + "_some_fake_encoding");
        user = new User(
                "Petrov",
                passwordEncoder.encode("password"),
                Collections.singletonList(new Role("ROLE_USER"))
        );
        newUser = new User(
                "Ivanov",
                passwordEncoder.encode("password"),
                Collections.singletonList(new Role("ROLE_USER"))
        );
        users = Collections.singletonList(user);
    }

    @Test
    void whenUserExists_thenReturnUser() {
        when(userRepository.findByName("Petrov")).thenReturn(Optional.ofNullable(user));
        assertDoesNotThrow(() -> userService.getUser("Petrov"));
    }

    @Test
    void whenUserNotFound_thenException() {
        when(userRepository.findByName("Sidorov")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUser("Sidorov"));
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(users);
        assertDoesNotThrow(() -> userService.getAllUsers(PageRequest.of(0, 1)));
    }

    @Test
    void createUser() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setName("Ivanov");
        signUpRequest.setPassword("password");

        when(userRepository.save(any(User.class))).thenReturn(newUser);
        assertDoesNotThrow(() -> userService.createUser(signUpRequest));
    }
}
