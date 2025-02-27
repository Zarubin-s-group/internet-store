package com.example.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.access-token-secret}")
    private String jwtAccessSecret;

    public Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtAccessSecret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Could not get all claims from passed token");
            claims = null;
        }
        return claims;
    }

    private boolean isTokenExpired(String tokenString) {
        String jwtToken = tokenString.substring(7);;
        boolean result = true;
        try {
            result = getAllClaimsFromToken(jwtToken)
                    .getExpiration()
                    .before(new Date());
        } catch (Exception ex) {
            log.info("An exception was thrown during JWT verification: {}", ex.getMessage());
        }
        return result;
    }

    public boolean isInvalid(String token) {
        return isTokenExpired(token);
    }
}
