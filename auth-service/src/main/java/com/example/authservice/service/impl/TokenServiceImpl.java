package com.example.authservice.service.impl;

import com.example.authservice.domain.RefreshToken;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.UpdateTokensResponse;
import com.example.authservice.exception.RefreshTokenException;
import com.example.authservice.exception.UserNotFoundException;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.AppUserDetails;
import com.example.authservice.security.JwtUtils;
import com.example.authservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getName(),
                request.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return AuthResponse.builder()
                .username(principal.getUsername())
                .roles(roles)
                .accessToken(jwtUtils.generateToken(principal))
                .refreshToken(createRefreshToken(principal.getId()).getToken())
                .build();
    }

    @Override
    public void logout() {
        AppUserDetails principal = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        refreshTokenRepository.deleteByUserId(principal.getId());
    }

    @Override
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .build();
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public UpdateTokensResponse updateTokens(String refreshToken) {
        RefreshToken currentRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));
        Long userId =currentRefreshToken.getUserId();
        String newAccessToken = jwtUtils.generateTokenByUsername(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MessageFormat.format("User with id {0} not found", userId)))
                .getName()
        );
        String newRefreshToken = createRefreshToken(userId).getToken();
        refreshTokenRepository.delete(currentRefreshToken);

        return new UpdateTokensResponse(newAccessToken, newRefreshToken);
    }
}
