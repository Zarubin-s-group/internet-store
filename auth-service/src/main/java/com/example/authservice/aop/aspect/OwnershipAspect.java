package com.example.authservice.aop.aspect;

import com.example.authservice.aop.annotation.CheckOwnership;
import com.example.authservice.exception.AccessDeniedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OwnershipAspect {

    @Before("@annotation(checkOwnership)")
    public void verifyOwnership(JoinPoint joinPoint, CheckOwnership checkOwnership) {
        String name = (String) joinPoint.getArgs()[0];
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!name.equals(currentUsername) && !isAdmin) {
            throw new AccessDeniedException("You are not the owner of this account");
        }
    }
}
