package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.enums.ConversationType;
import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.ConversationUser;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.CreateGroupRequest;
import com.tolgahan.chat_app.response.ConversationResponse;
import com.tolgahan.chat_app.service.ConversationService;
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
    public ResponseEntity<String> getAllConversations(@RequestParam(name = "type", required = false) ConversationType type) {
        User user = getCurrentUser();
        if (user == null) {
            logger.error("User not found.");
            return ResponseCreator.notFound(); // Assuming this returns a 404 ResponseEntity
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

        return ResponseCreator.ok(responses);
    }


    @PostMapping("/groups")
    public ResponseEntity<String> createGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        try {
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

            createGroupRequest.getParticipants().forEach(id -> {
                        try {
                            User participant = userService.getUserById(id);
                            if (participant != null) {
                                conversation.getConversationUsers().add(new ConversationUser(participant, conversation));
                            }
                        } catch (Exception e) {
                            logger.error("Error -> " + e.getMessage());
                        }

                    }
            );


            Conversation saved = conversationService.createGroupConversation(conversation);

            return ResponseCreator.ok(new ConversationResponse(saved));
        } catch (Exception e) {
            logger.error("Error -> " + e.getMessage());
            return ResponseCreator.badRequest("Error -> " + e.getMessage());
        }
    }

    @GetMapping("/groups/{conversationId}")
    public ResponseEntity<String> getGroupById(@PathVariable(name = "conversationId") UUID conversationId) {
        User user = getCurrentUser();
        if (user == null) {
            logger.error("User not found.");
            return ResponseCreator.notFound();
        }

        Conversation conversation = conversationService.getConversationById(user, conversationId);
        if (conversation == null) {
            logger.error("Conversation not found.");
            return ResponseCreator.notFound();
        }

        return ResponseCreator.ok(new ConversationResponse(conversation));

    }

    @DeleteMapping("/groups/{conversationId}")
    public ResponseEntity<String> leaveGroup(@PathVariable(name = "conversationId") UUID conversationId) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                return ResponseCreator.notFound();
            }
            conversationService.leaveGroup(user, conversationId);
            return ResponseCreator.ok("Left group successfully");
        }catch (Exception e){
            logger.error("Error -> " + e.getMessage());
            return ResponseCreator.badRequest("Error -> " + e.getMessage());
        }

    }

    @PatchMapping("/groups/{conversationId}")
    public ResponseEntity<String> addParticipant(@PathVariable(name = "conversationId") UUID conversationId, @RequestBody UUID participantId) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                return ResponseCreator.notFound();
            }
            User participant = userService.getUserById(participantId);
            if (participant == null) {
                logger.error("Participant not found.");
                return ResponseCreator.notFound();
            }

            conversationService.addParticipant(user, conversationId, participant);
            return ResponseCreator.ok("Participant added successfully");
        } catch (Exception e) {
            logger.error("Error -> " + e.getMessage());
            return ResponseCreator.badRequest("Error -> " + e.getMessage());
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
