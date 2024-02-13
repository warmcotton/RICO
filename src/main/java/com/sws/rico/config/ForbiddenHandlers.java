package com.sws.rico.config;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class ForbiddenHandlers extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음");
    }
    
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> expireJwt() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인 정보 만료");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> authException(AuthenticationException exception) {
        log.info(exception.getMessage(),exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
    }
}