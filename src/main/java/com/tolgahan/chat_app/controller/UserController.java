package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.controller.interfaces.IUserController;
import com.tolgahan.chat_app.exceptions.TokenIsNotValidException;
import com.tolgahan.chat_app.exceptions.UserNotFoundException;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.response.FriendResponse;
import com.tolgahan.chat_app.response.UserResponse;
import com.tolgahan.chat_app.service.interfaces.IEmailService;
import com.tolgahan.chat_app.service.interfaces.IFriendshipService;
import com.tolgahan.chat_app.service.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserController implements IUserController {
    private final IUserService userService;
    private final IEmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final IFriendshipService friendshipService;


    public UserController(IUserService userService, IEmailService emailService, IFriendshipService friendshipService) {
        this.userService = userService;
        this.emailService = emailService;
        this.friendshipService = friendshipService;
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null; // Kullanıcı doğrulanmamışsa
    }


    @Override
    public UserResponse getUserByUsername(String username) {
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

    @Override
    public UserResponse getLocalUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {

            return new UserResponse(userService.getUserByUsername(userDetails.getUsername()));
        }
        return null; // Kullanıcı doğrulanmamışsa
    }

    @Override
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

    @Override
    public List<FriendResponse> searchUserByUsername(String username) {
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
}
