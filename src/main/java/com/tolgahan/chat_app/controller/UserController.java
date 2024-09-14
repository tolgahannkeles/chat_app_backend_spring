package com.tolgahan.chat_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

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


    @GetMapping("/search/{username}")
    public ResponseEntity<String> searchUserByUsername(@PathVariable String username) {
        try {
            logger.info("Getting users starting with the username: {}", username);
            User currentUser = getCurrentUser();
            if(currentUser == null) {
                logger.error("Your session has expired.");
                return ResponseCreator.unauthorized("Your session has expired.");
            }
            List<User> user = userService.findUserStartingWith(username);
            if (user == null) {
                logger.error("User not found with username: {}", username);
                return ResponseCreator.notFound();
            }



            List<FriendResponse> responses= new ArrayList<>();
            user.forEach(user1 -> {
                FriendResponse response = new FriendResponse();
                response.setId(user1.getId());
                response.setUsername(user1.getUsername());
                response.setEmail(emailService.getEmailsByUser(user1));
                response.setFriendshipStatus(friendshipService.getFriendshipStatus(currentUser.getId(), user1.getId()));
                responses.add(response);
            });

            return ResponseCreator.ok(responses);
        } catch (Exception e) {
            logger.error("Error getting user: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error getting user: " + e.getMessage());
        }
    }



    @GetMapping("/all")
    public ResponseEntity<String> all() {
        try {
            logger.info("Getting all users");
            List<UserResponse> response= userService.getAllUsers().stream().map(user -> {
                UserResponse userResponse = new UserResponse();
                userResponse.setId(user.getId());
                userResponse.setUsername(user.getUsername());
                userResponse.setEmail(emailService.getEmailsByUser(user));
                return userResponse;

            }).toList();
            return ResponseCreator.ok(response);
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error: " + e.getMessage());
        }

    }

    @GetMapping
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null; // Kullanıcı doğrulanmamışsa
    }


}
