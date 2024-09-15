package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.exceptions.BadRequestException;
import com.tolgahan.chat_app.exceptions.TokenIsNotValidException;
import com.tolgahan.chat_app.model.Email;
import com.tolgahan.chat_app.model.Role;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.friendship.FriendshipStatusRequest;
import com.tolgahan.chat_app.response.AccountResponse;
import com.tolgahan.chat_app.response.FriendResponse;
import com.tolgahan.chat_app.service.UserService;
import com.tolgahan.chat_app.utils.ResponseCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public AccountResponse getUser() {
        try {
            logger.info("Getting account info");
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }
            AccountResponse response = new AccountResponse();
            response.setId(user.getId().toString());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmails().stream().map(Email::getEmail).toList());
            response.setRoles(user.getRoles().stream().map(Role::getName).toList());
            return response;

        } catch (Exception e) {
            logger.error("Error getting user: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null; // Kullanıcı doğrulanmamışsa
    }
}
