package com.example.authservice.service;

import com.example.authservice.domain.RefreshToken;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.UpdateTokensResponse;

public interface TokenService {

    AuthResponse login(LoginRequest request);

    void logout();

    RefreshToken createRefreshToken(Long userId);

    UpdateTokensResponse updateTokens(String refreshToken);
}
