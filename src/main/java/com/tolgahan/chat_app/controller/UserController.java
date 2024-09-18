package com.tolgahan.chat_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tolgahan.chat_app.exceptions.TokenIsNotValidException;
import com.tolgahan.chat_app.exceptions.UserNotFoundException;
import com.tolgahan.chat_app.model.Email;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.EmailRepository;
import com.tolgahan.chat_app.repository.UserRepository;
import com.tolgahan.chat_app.response.FriendResponse;
import com.tolgahan.chat_app.response.UserResponse;
import com.tolgahan.chat_app.security.JwtTokenProvider;
import com.tolgahan.chat_app.service.EmailService;
import com.tolgahan.chat_app.service.FriendshipService;
import com.tolgahan.chat_app.service.UserService;
import com.tolgahan.chat_app.utils.ResponseCreator;
import com.tolgahan.chat_app.validation.RegistrationValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
    private final FriendshipService friendshipService;

    @Autowired
    public UserController(UserService userService, EmailService emailService, ObjectMapper objectMapper, JwtTokenProvider jwtTokenProvider, FriendshipService friendshipService) {
        this.userService = userService;
        this.emailService = emailService;
        this.objectMapper = objectMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.friendshipService = friendshipService;
    }

    @GetMapping("/{username}")
    public UserResponse getUserByUsername(@PathVariable String username) {
        try {
            logger.info("Getting user with username: {}", username);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                logger.error("User not found with username: {}", username);
                throw new UserNotFoundException("User not found with username: " + username);
            }
            return new UserResponse(user);
        } catch (Exception e) {
            logger.error("Error getting user: {}", e.getMessage());
            throw new UserNotFoundException("Error getting user: " + e.getMessage());
        }
    }


    @GetMapping("/search/{username}")
    public List<FriendResponse> searchUserByUsername(@PathVariable String username) {
        try {
            logger.info("Getting users starting with the username: {}", username);
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                logger.error("Your session has expired.");
                throw new TokenIsNotValidException();
            }
            List<User> users = userService.findUserStartingWith(username);
            if (users == null) {
                logger.error("User not found with username: {}", username);
                throw new UserNotFoundException("User not found with username: " + username);
            }


            List<FriendResponse> responses = new ArrayList<>();
            users.forEach(user1 -> {
                if (user1.getId().equals(currentUser.getId())) return;
                FriendResponse response = new FriendResponse();
                response.setId(user1.getId());
                response.setUsername(user1.getUsername());
                response.setEmail(emailService.getEmailsByUser(user1));
                response.setFriendshipStatus(friendshipService.getFriendshipStatus(currentUser.getId(), user1.getId()));
                responses.add(response);
            });

            return responses;
        } catch (Exception e) {
            logger.error("Error getting user: {}", e.getMessage());
            throw new UserNotFoundException("Error getting user: " + e.getMessage());
        }
    }


    @GetMapping("/all")
    public List<UserResponse> all() {
        try {
            logger.info("Getting all users");
            List<UserResponse> response = userService.getAllUsers().stream().map(UserResponse::new).toList();
            return response;
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage());
            throw new UserNotFoundException("Error getting all users: " + e.getMessage());
        }

    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null; // Kullanıcı doğrulanmamışsa
    }

    @GetMapping
    public UserResponse getLocalUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {

            return new UserResponse(userService.getUserByUsername(userDetails.getUsername()));
        }
        return null; // Kullanıcı doğrulanmamışsa
    }


}
