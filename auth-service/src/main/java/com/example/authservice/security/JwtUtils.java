package com.example.authservice.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtils {

    @Value("${jwt.access-token-secret}")
    private String jwtAccessSecret;

    @Value("${jwt.access-token-expiration-time}")
    private Duration accessTokenExpiration;

    private final UserDetailsService userDetailsService;

    public String generateTokenByUsername(String username) {
        var userDetails = userDetailsService.loadUserByUsername(username);
        return generateToken((AppUserDetails) userDetails);

    }

    public String generateToken(AppUserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("id", userDetails.getId())
                .claim("roles", String.join(", ", userDetails.getRoleList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + accessTokenExpiration.toMillis()))
                .signWith(SignatureAlgorithm.HS512, jwtAccessSecret)
                .compact();
    }

    public String getSubject(String accessToken) {
        return Jwts.parser()
                .setSigningKey(jwtAccessSecret)
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtAccessSecret)
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("JWT token is expired: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("JWT token is unsupported: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claim string is empty: {}", ex.getMessage());
        }
        return false;
    }
}
