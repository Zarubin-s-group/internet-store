package com.example.authservice.controller;

import com.example.authservice.dto.ErrorResponse;
import com.example.authservice.exception.AccessDeniedException;
import com.example.authservice.exception.RefreshTokenException;
import com.example.authservice.exception.UserNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private final String UNIQUE_VIOLATION = "23505";

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> sqlExceptionHandler(SQLException ex) {
        if (ex.getSQLState().equals(UNIQUE_VIOLATION)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("User with this name already exists", LocalDateTime.now()));
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal service error", LocalDateTime.now()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notValidExceptionHandler(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> messages = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        ErrorResponse errorDto = new ErrorResponse(String.join("; ", messages), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(UserNotFoundException ex) {
        ErrorResponse errorDto = new ErrorResponse(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> accessDeniedExceptionHandler(AccessDeniedException ex) {
        ErrorResponse errorDto = new ErrorResponse(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> refreshTokenExceptionHandler(RefreshTokenException ex) {
        ErrorResponse errorDto = new ErrorResponse(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }
}
