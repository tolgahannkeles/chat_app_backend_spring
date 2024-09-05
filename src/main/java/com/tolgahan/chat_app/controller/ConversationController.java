package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.PrivateConversationRequest;
import com.tolgahan.chat_app.security.JwtTokenProvider;
import com.tolgahan.chat_app.service.UserService;
import com.tolgahan.chat_app.utils.ResponseCreator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversation")
public class ConversationController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public ConversationController(JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<String> getAllConversations(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        if(user == null) {
            logger.error("User not found.");
            return ResponseCreator.notFound();
        }
        return ResponseCreator.ok(user.getConversations());
    }

    @PostMapping("/create")
    public ResponseEntity<String> createConversation(HttpServletRequest request, @RequestBody PrivateConversationRequest conversationRequest) {
        User user = getUserFromRequest(request);
        if(user == null) {
            logger.error("User not found.");
            return ResponseCreator.notFound();
        }
        Conversation conversation = new Conversation();


        return ResponseCreator.ok("sa");
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            logger.error("Invalid or missing Authorization header");
            throw new RuntimeException("Invalid or missing Authorization header");
        }
        String token = bearerToken.substring(7);
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);
        logger.info("Getting user with id: {}", userId);
        return userService.getUserById(userId);
    }
}
