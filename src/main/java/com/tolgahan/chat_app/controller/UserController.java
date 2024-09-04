package com.tolgahan.chat_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tolgahan.chat_app.model.Email;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.EmailRepository;
import com.tolgahan.chat_app.repository.UserRepository;
import com.tolgahan.chat_app.response.UserResponse;
import com.tolgahan.chat_app.security.JwtTokenProvider;
import com.tolgahan.chat_app.service.EmailService;
import com.tolgahan.chat_app.service.UserService;
import com.tolgahan.chat_app.utils.ResponseCreator;
import com.tolgahan.chat_app.validation.RegistrationValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, EmailService emailService, ObjectMapper objectMapper, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.emailService = emailService;
        this.objectMapper = objectMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @GetMapping
    public ResponseEntity<String> getUser(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            logger.error("Invalid or missing Authorization header");
            return ResponseCreator.unauthorized("Invalid or missing Authorization header");
        }

        try {
            String token = bearerToken.substring(7);

            UUID userId = jwtTokenProvider.getUserIdFromToken(token);
            logger.info("Getting user with id: {}", userId);

            User user = userService.getUserById(userId);
            if (user == null) {
                logger.error("User not found with id: {}", userId);
                return ResponseCreator.notFound();
            }
            return ResponseCreator.ok(user);

        } catch (Exception e) {
            logger.error("Error getting user: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error getting user: " + e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<String> getUserByUsername(@PathVariable String username) {
        try {
            logger.info("Getting user with username: {}", username);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                logger.error("User not found with username: {}", username);
                return ResponseCreator.notFound();
            }
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(emailService.getEmailsByUser(user));
            return ResponseCreator.ok(response);
        } catch (Exception e) {
            logger.error("Error getting user: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error getting user: " + e.getMessage());
        }
    }


    @GetMapping("/all")
    public ResponseEntity<String> all() {
        try {
            logger.info("Getting all users");
            return ResponseCreator.ok(userService.getAllUsers().stream().map(user -> {
                UserResponse userResponse = new UserResponse();
                userResponse.setId(user.getId());
                userResponse.setUsername(user.getUsername());
                userResponse.setEmail(emailService.getEmailsByUser(user));
                return userResponse;

            }));
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error: " + e.getMessage());
        }

    }


}
