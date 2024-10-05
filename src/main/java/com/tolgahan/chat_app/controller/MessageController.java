package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.controller.interfaces.IMessageController;
import com.tolgahan.chat_app.enums.DeletionType;
import com.tolgahan.chat_app.exceptions.BadRequestException;
import com.tolgahan.chat_app.exceptions.InvalidArgumentException;
import com.tolgahan.chat_app.exceptions.TokenIsNotValidException;
import com.tolgahan.chat_app.model.Message;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.MessageDeleteRequest;
import com.tolgahan.chat_app.request.MessageRequest;
import com.tolgahan.chat_app.response.MessageResponse;
import com.tolgahan.chat_app.service.interfaces.IMessageService;
import com.tolgahan.chat_app.service.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class MessageController implements IMessageController {

    private final IUserService userService;
    private final IMessageService messageService;
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

    public MessageController(IUserService userService, IMessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null; // Kullanıcı doğrulanmamışsa
    }


    @Override
    public String sendMessage(UUID conversationId, MessageRequest messageRequest, Principal principal) {
        if (conversationId == null || messageRequest == null) {
            logger.error("Conversation userId or message request can not be null");
            throw new InvalidArgumentException("Conversation userId or message request can not be null");
        }

        try {
            messageService.sendMessage(userService.getUserByUsername(principal.getName()), conversationId, messageRequest);
            return "Message sent successfully";

        } catch (Exception e) {
            logger.error("An error occurred while sending message", e);
            throw new BadRequestException("An error occurred while sending message -> " + e.getMessage());
        }
    }







    @Override
    public String deleteMessage(UUID conversationId, MessageDeleteRequest request) {
        try {
            Message message = messageService.deleteMessage(getCurrentUser(), conversationId, request);

            return "Message deleted successfully";

        } catch (Exception e) {
            logger.error("An error occurred while deleting message", e);
            throw new BadRequestException("An error occurred while deleting message -> " + e.getMessage());
        }
    }

    @Override
    public List<MessageResponse> getMessages(UUID conversationId) {
        try {
            List<MessageResponse> response = new ArrayList<>();
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }
            messageService.getMessages(getCurrentUser(), conversationId).forEach(message -> {
                if (message.getSender().getId().equals(user.getId()) && message.getIsDeleted() && message.getDeletionType().equals(DeletionType.ME)) {
                    message.setMessage("This message was deleted");
                } else if (message.getIsDeleted() && message.getDeletionType().equals(DeletionType.EVERYONE)) {
                    message.setMessage("This message was deleted");
                }
                response.add(new MessageResponse(message));
            });

            return response;

        } catch (Exception e) {
            logger.error("An error occurred while getting messages", e);
            throw new BadRequestException("An error occurred while getting messages -> " + e.getMessage());
        }
    }
}
