package com.example.authservice.controller;

import com.example.authservice.config.SecurityConfig;
import com.example.authservice.domain.Role;
import com.example.authservice.domain.User;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.AppAuthenticationEntryPoint;
import com.example.authservice.service.TokenService;
import com.example.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
public class UserControllerTest {

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

    private User user;

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
        users = Collections.singletonList(user);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUser() throws Exception {
        when(userService.getUser(user.getName())).thenReturn(user);
        mvc.perform(get("/users/Petrov"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(user.getName())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllUsers() throws Exception {
        when(userService.getAllUsers(PageRequest.of(0, 1))).thenReturn(new PageImpl<>(users));
        mvc.perform(get("/users/")
                        .param("page", "0")
                        .param("size", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(user.getName())));
    }

    @Test
    @WithMockUser(username = "Petrov")
    public void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser("Petrov");
        mvc.perform(delete("/users/delete/Petrov"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
