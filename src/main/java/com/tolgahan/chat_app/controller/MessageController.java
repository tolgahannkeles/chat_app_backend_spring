package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.enums.DeletionType;
import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.Message;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.MessageDeleteRequest;
import com.tolgahan.chat_app.request.MessageRequest;
import com.tolgahan.chat_app.response.MessageResponse;
import com.tolgahan.chat_app.service.ConversationService;
import com.tolgahan.chat_app.service.MessageService;
import com.tolgahan.chat_app.service.UserService;
import com.tolgahan.chat_app.utils.ResponseCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final UserService userService;
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

    public MessageController(UserService userService, ConversationService conversationService, MessageService messageService) {
        this.userService = userService;
        this.conversationService = conversationService;
        this.messageService = messageService;
    }

    @PostMapping("{conversationId}")
    public ResponseEntity<String> sendMessage(@PathVariable UUID conversationId, @RequestBody MessageRequest messageRequest) {
        if (conversationId == null || messageRequest == null) {
            return ResponseEntity.badRequest().body("Conversation id or message request can not be null");
        }

        try {
            messageService.sendMessage(getCurrentUser(), conversationId, messageRequest);
            return ResponseCreator.ok("Message sent successfully");

        } catch (Exception e) {
            logger.error("An error occurred while sending message", e);
            return ResponseCreator.badRequest("An error occurred while sending message");
        }
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<String> getMessages(@PathVariable UUID conversationId) {
        try {
            List<MessageResponse> response = new ArrayList<>();
            User user = getCurrentUser();
            if (user == null) {
                return ResponseCreator.badRequest("User not found");
            }
            messageService.getMessages(getCurrentUser(), conversationId).forEach(message -> {
                if (message.getSender().getId().equals(user.getId()) && message.getIsDeleted() && message.getDeletionType().equals(DeletionType.ME)) {
                    message.setMessage("This message was deleted");
                }else if (message.getIsDeleted() && message.getDeletionType().equals(DeletionType.EVERYONE)){
                    message.setMessage("This message was deleted");
                }
                response.add(new MessageResponse(message));
            });

            return ResponseCreator.ok(response);

        } catch (Exception e) {
            return ResponseCreator.badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<String> deleteMessage(@PathVariable UUID conversationId, @RequestBody MessageDeleteRequest request) {
        try {
            Message message = messageService.deleteMessage(getCurrentUser(), conversationId, request);

            return ResponseCreator.ok("Message deleted successfully");

        } catch (Exception e) {
            return ResponseCreator.badRequest(e.getMessage());
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
