package com.example.authservice.controller;

import com.example.authservice.aop.annotation.CheckOwnership;
import com.example.authservice.dto.SignUpRequest;
import com.example.authservice.dto.UserListResponse;
import com.example.authservice.dto.UserResponse;
import com.example.authservice.mapper.UserMapper;
import com.example.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user by name", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
        return ResponseEntity.ok(UserMapper.INSTANCE.userToResponse(userService.getUser(username)));
    }

    @Operation(summary = "Get all users", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/")
    public ResponseEntity<UserListResponse> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(UserMapper.INSTANCE.userListToUserListResponse(userService.getAllUsers(pageable)));
    }

    @Operation(summary = "Create new user")
    @PostMapping(value = "/signup")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserMapper.INSTANCE.userToResponse(userService.createUser(signUpRequest)));
    }

    @Operation(summary = "Delete user by name", security = @SecurityRequirement(name = "bearerAuth"))
    @CheckOwnership(entityType = "user")
    @DeleteMapping(value = "/delete/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
