package com.example.authservice.controller;

import com.example.authservice.config.SecurityConfig;
import com.example.authservice.domain.Role;
import com.example.authservice.domain.User;
import com.example.authservice.dto.SignUpRequest;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.AppAuthenticationEntryPoint;
import com.example.authservice.service.TokenService;
import com.example.authservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
public class UserControllerNotAuthenticatedTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Configuration
    @ComponentScan(basePackageClasses = {UserController.class, TokenService.class, SecurityConfig.class,
            AppAuthenticationEntryPoint.class})
    public static class TestConf {
    }

    @Test
    public void getUser() throws Exception {
        mvc.perform(get("/users/Petrov"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllUsers() throws Exception {
        mvc.perform(get("/users/")
                        .param("page", "0")
                        .param("size", "1")
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createUser() throws Exception {
        when(passwordEncoder.encode(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0) + "_some_fake_encoding");
        User newUser = new User(
                "Ivanov",
                passwordEncoder.encode("password"),
                Collections.singletonList(new Role("ROLE_USER"))
        );
        when(userService.createUser(ArgumentMatchers.any(SignUpRequest.class))).thenReturn(newUser);
        mvc.perform(
                        post("/users/signup")
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"Ivanov\",\"password\":\"password\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(newUser.getName())));
    }

    @Test
    public void deleteUser() throws Exception {
        mvc.perform(delete("/user/delete/Petrov"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
