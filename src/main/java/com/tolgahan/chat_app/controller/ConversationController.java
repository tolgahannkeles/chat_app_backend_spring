package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.controller.interfaces.IConversationController;
import com.tolgahan.chat_app.enums.ConversationType;
import com.tolgahan.chat_app.exceptions.BadRequestException;
import com.tolgahan.chat_app.exceptions.ConversationNotFoundException;
import com.tolgahan.chat_app.exceptions.TokenIsNotValidException;
import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.CreateGroupRequest;
import com.tolgahan.chat_app.response.ConversationResponse;
import com.tolgahan.chat_app.service.interfaces.IConversationService;
import com.tolgahan.chat_app.service.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ConversationController implements IConversationController {
    private final Logger logger = LoggerFactory.getLogger(ConversationController.class);
    private final IUserService userService;
    private final IConversationService conversationService;

    public ConversationController(IUserService userService, IConversationService conversationService) {
        this.userService = userService;
        this.conversationService = conversationService;
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null;
    }


    @Override
    public List<ConversationResponse> getAllConversationsWithParams(ConversationType type) {
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

    @Override
    public ConversationResponse createGroup(CreateGroupRequest createGroupRequest) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }
            Conversation saved = conversationService.createGroupConversation(user, createGroupRequest.getTitle(), createGroupRequest.getParticipants());

            return new ConversationResponse(saved);
        } catch (Exception e) {
            logger.error("Error creating group -> " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public ConversationResponse getGroupById(UUID conversationId) {
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

    @Override
    public String leaveGroup(UUID conversationId) {
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

    @Override
    public String addParticipant(UUID conversationId, UUID participantId) {
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
}
