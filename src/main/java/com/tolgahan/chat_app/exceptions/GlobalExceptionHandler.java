package com.tolgahan.chat_app.exceptions;

import com.tolgahan.chat_app.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<Map<String, List<String>>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            if (errors.containsKey(fieldName)) {
                errors.get(fieldName).add(errorMessage);
            } else {
                List<String> errorMessages = new ArrayList<>();
                errorMessages.add(errorMessage);
                errors.put(fieldName, errorMessages);
            }
        }
        return ResponseEntity.badRequest().body(new ErrorResponse<>(errors));
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse<String>> handleBadRequestException(BadRequestException ex) {
        ErrorResponse<String> error = new ErrorResponse<>(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<String>> handleGenericException(Exception ex) {
        ErrorResponse<String> error = new ErrorResponse<>("An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(AlreadyDefinedException.class)
    public ResponseEntity<ErrorResponse<String>> handleAlreadyDefinedException(AlreadyDefinedException ex) {
        ErrorResponse<String> error = new ErrorResponse<>("An error occurred: " + ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse<String> error = new ErrorResponse<>("Invalid request: " + ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ErrorResponse<String>> handleInvalidArgumentException(InvalidArgumentException ex) {
        ErrorResponse<String> error = new ErrorResponse<>("Invalid argument: " + ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorResponse<String>> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        ErrorResponse<String> error = new ErrorResponse<>("Authentication failed: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ConversationNotFoundException.class)
    public ResponseEntity<ErrorResponse<String>> handleConversationNotFoundException(ConversationNotFoundException ex) {
        ErrorResponse<String> error = new ErrorResponse<>("Conversation not found: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientArgumentException.class)
    public ResponseEntity<ErrorResponse<String>> handleInsufficientArgumentException(InsufficientArgumentException ex) {
        ErrorResponse<String> error = new ErrorResponse<>("Insufficient arguments: " + ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(TokenIsNotValidException.class)
    public ResponseEntity<ErrorResponse<String>> handleTokenIsNotValidException(TokenIsNotValidException ex) {
        ErrorResponse<String> error = new ErrorResponse<>("Token is not valid: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse<String>> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse<String> error = new ErrorResponse<>("User not found: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
