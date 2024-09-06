package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.enums.ConversationType;
import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.ConversationUser;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.CreateGroupRequest;
import com.tolgahan.chat_app.request.PrivateConversationRequest;
import com.tolgahan.chat_app.response.ConversationResponse;
import com.tolgahan.chat_app.security.JwtTokenProvider;
import com.tolgahan.chat_app.service.ConversationService;
import com.tolgahan.chat_app.service.UserService;
import com.tolgahan.chat_app.utils.ResponseCreator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversation")
public class ConversationController {
    private final Logger logger = LoggerFactory.getLogger(ConversationController.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ConversationService conversationService;

    public ConversationController(JwtTokenProvider jwtTokenProvider, UserService userService, ConversationService conversationService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.conversationService = conversationService;
    }

    @GetMapping("/all")
    public ResponseEntity<String> getAllConversations() {
        User user = getCurrentUser();
        if (user == null) {
            logger.error("User not found.");
            return ResponseCreator.notFound();
        }
        return ResponseCreator.ok(user.getConversations());
    }

    @GetMapping("/chats")
    public ResponseEntity<String> getAllChats() {
        User user = getCurrentUser();
        if (user == null) {
            logger.error("User not found.");
            return ResponseCreator.notFound();
        }
        List<ConversationResponse> response=new ArrayList<>();
        user.getConversations().forEach(conversation -> {
            if (conversation.getConversationType().equals(ConversationType.CHAT)) {
                ConversationResponse conversationResponse = new ConversationResponse();
                conversationResponse.setId(conversation.getId());
                conversationResponse.setTitle(conversation.getTitle());
                response.add(conversationResponse);
            }
        });
        return ResponseCreator.ok(response);
    }


    @GetMapping("/groups")
    public ResponseEntity<String> getAllGroups() {
        User user = getCurrentUser();
        if (user == null) {
            logger.error("User not found.");
            return ResponseCreator.notFound();
        }
        List<ConversationResponse> response=new ArrayList<>();
        user.getConversations().forEach(conversation -> {
            if (conversation.getConversationType().equals(ConversationType.GROUP)) {
                ConversationResponse conversationResponse = new ConversationResponse();
                conversationResponse.setId(conversation.getId());
                conversationResponse.setTitle(conversation.getTitle());
                response.add(conversationResponse);
            }
        });
        return ResponseCreator.ok(response);
    }

    @PostMapping("/groups")
    public ResponseEntity<String> createChat(HttpServletRequest request, @RequestBody CreateGroupRequest createGroupRequest) {
        User user = getCurrentUser();
        if (user == null) {
            logger.error("User not found.");
            return ResponseCreator.notFound();
        }
        Conversation conversation = new Conversation();
        conversation.setConversationType(ConversationType.GROUP);
        conversation.setTitle(createGroupRequest.getTitle());
        conversation.setCreator(user);
        conversation.getConversationUsers().add(new ConversationUser(user, conversation));

        createGroupRequest.getParticipants().forEach(uuid -> {
                    User participant = userService.getUserById(uuid);
                    if (participant != null) {
                        conversation.getConversationUsers().add(new ConversationUser(participant, conversation));
                    }
                }
        );


        Conversation saved = conversationService.createGroupConversation(conversation);
        ConversationResponse response = new ConversationResponse();
        response.setId(saved.getId());
        response.setTitle(saved.getTitle());
        return ResponseCreator.ok(response);
    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null; // Kullanıcı doğrulanmamışsa
    }



}
