package com.sws.rico.config;

import com.sws.rico.controller.CartController;
import com.sws.rico.controller.ItemController;
import com.sws.rico.controller.OrderController;
import com.sws.rico.controller.UserController;
import com.sws.rico.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice(assignableTypes = {CartController.class, OrderController.class, ItemController.class, UserController.class})
public class ExceptionHandlers extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> cartException(CustomException exception) {
        log.info(exception.getMessage(),exception);
        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler(UnexpectedRollbackException.class)
    public ResponseEntity<Object> cartException(TransactionException exception) {
        log.info(exception.getMessage(),exception);
        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> argumentException(IllegalArgumentException exception) {
        log.info(exception.getMessage(),exception);
        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> elementException(NoSuchElementException exception) {
        log.info(exception.getMessage(), exception);
        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.info(ex.getMessage(),ex);
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(400).body("Invalid Arguments");
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(400).body("Invalid Arguments");
    }
}
