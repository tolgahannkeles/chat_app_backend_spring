package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.enums.ConversationType;
import com.tolgahan.chat_app.exceptions.BadRequestException;
import com.tolgahan.chat_app.exceptions.ConversationNotFoundException;
import com.tolgahan.chat_app.exceptions.TokenIsNotValidException;
import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.ConversationUser;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.CreateGroupRequest;
import com.tolgahan.chat_app.response.ConversationResponse;
import com.tolgahan.chat_app.service.ConversationService;
import com.tolgahan.chat_app.service.UserService;
import com.tolgahan.chat_app.validation.ConversationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversation")
public class ConversationController {
    private final Logger logger = LoggerFactory.getLogger(ConversationController.class);
    private final UserService userService;
    private final ConversationService conversationService;

    public ConversationController(UserService userService, ConversationService conversationService) {
        this.userService = userService;
        this.conversationService = conversationService;
    }

    @GetMapping
    public List<ConversationResponse> getAllConversationsWithParams(@RequestParam(name = "type", required = false) ConversationType type) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }

            List<ConversationResponse> responses;

            if (type == null) {
                responses = conversationService.getAllConversations(user).stream()
                        .map(ConversationResponse::new)
                        .toList();
            } else {
                responses = switch (type) {
                    case CHAT -> conversationService.getAllChats(user).stream()
                            .map(ConversationResponse::new)
                            .toList();
                    case GROUP -> conversationService.getAllGroups(user).stream()
                            .map(ConversationResponse::new)
                            .toList();
                };
            }

            return responses;
        } catch (Exception e) {
            logger.error("Error -> " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

    }


    @PostMapping("/groups")
    public ConversationResponse createGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }
            ConversationValidator.validateTitle(createGroupRequest.getTitle());
            Conversation saved = conversationService.createGroupConversation(user, createGroupRequest.getTitle(), createGroupRequest.getParticipants());

            return new ConversationResponse(saved);
        } catch (Exception e) {
            logger.error("Error creating group -> " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/groups/{conversationId}")
    public ConversationResponse getGroupById(@PathVariable(name = "conversationId") UUID conversationId) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }

            Conversation conversation = conversationService.getConversationById(user, conversationId);
            if (conversation == null) {
                logger.error("Conversation not found.");
                throw new ConversationNotFoundException("Conversation not found.");
            }

            return new ConversationResponse(conversation);
        } catch (Exception e) {
            logger.error("Error -> " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }


    }

    @DeleteMapping("/groups/{conversationId}")
    public String leaveGroup(@PathVariable(name = "conversationId") UUID conversationId) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }
            conversationService.leaveGroup(user, conversationId);
            return "Left group successfully";
        } catch (Exception e) {
            logger.error("Error -> " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

    }

    @PatchMapping("/groups/{conversationId}")
    public String addParticipant(@PathVariable(name = "conversationId") UUID conversationId, @RequestBody UUID participantId) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }
            User participant = userService.getUserById(participantId);
            if (participant == null) {
                logger.error("Participant not found.");

            }

            conversationService.addParticipant(user, conversationId, participant);
            return "Participant added successfully";
        } catch (Exception e) {
            logger.error("Error -> " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null;
    }


}
