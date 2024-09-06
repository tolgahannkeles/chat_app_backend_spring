package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.Message;
import com.tolgahan.chat_app.model.User;
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
            Conversation conversation = conversationService.getConversationById(conversationId);
            User user = getCurrentUser();

            if (conversation == null || user == null) {
                return ResponseEntity.badRequest().body("Conversation or user not found");
            }

            Message message = new Message();
            message.setMessage(messageRequest.getMessage());
            message.setSentAt(messageRequest.getDate());
            message.setConversation(conversation);
            message.setSender(user);

            messageService.sendMessage(message);
            
            return ResponseCreator.ok("Message sent successfully");

        } catch (Exception e) {
            logger.error("An error occurred while sending message", e);
            return ResponseCreator.badRequest("An error occurred while sending message");
        }
    }

    @GetMapping("{conversationId}")
    public ResponseEntity<String> getMessages(@PathVariable UUID conversationId) {
        if (conversationId == null) {
            return ResponseEntity.badRequest().body("Conversation id can not be null");
        }

        try {
            Conversation conversation = conversationService.getConversationById(conversationId);

            if (conversation == null) {
                return ResponseEntity.badRequest().body("Conversation not found");
            }
            List<MessageResponse> response = new ArrayList<>();
            conversation.getMessages().forEach(message -> {
                MessageResponse messageResponse = new MessageResponse();
                messageResponse.setMessage(message.getMessage());
                messageResponse.setSender(message.getSender().getUsername());
                messageResponse.setDate(message.getSentAt());
                response.add(messageResponse);
            });


            return ResponseCreator.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred while getting messages");
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
