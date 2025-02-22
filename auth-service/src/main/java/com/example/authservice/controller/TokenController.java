package com.example.authservice.controller;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LogoutResponse;
import com.example.authservice.dto.UpdateTokensResponse;
import com.example.authservice.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class TokenController {

    private final TokenService tokenService;

    @Operation(summary = "Login")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(tokenService.login(request));
    }

    @Operation(summary = "Get new pair of tokens", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/token/update")
    public ResponseEntity<UpdateTokensResponse> updateTokens(@RequestParam String refreshToken) {
        return ResponseEntity.ok(tokenService.updateTokens(refreshToken));
    }

    @Operation(summary = "Logout", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@AuthenticationPrincipal UserDetails userDetails) {
        tokenService.logout();
        return ResponseEntity.ok(new LogoutResponse(MessageFormat.format("User {0} logged out.",
                userDetails.getUsername())));
    }
}
