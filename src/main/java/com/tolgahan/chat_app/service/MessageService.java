package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.controller.FriendshipController;
import com.tolgahan.chat_app.enums.DeletionType;
import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.Message;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.ConversationRepository;
import com.tolgahan.chat_app.repository.MessageRepository;
import com.tolgahan.chat_app.request.MessageDeleteRequest;
import com.tolgahan.chat_app.request.MessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public MessageService(MessageRepository messageRepository, ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
    }

    public void sendMessage(User user, UUID conversationId, MessageRequest request) {
        if (user == null || conversationId == null || request == null) {
            logger.error("User, conversation id or message request can not be null");
            throw new RuntimeException("User, conversation id or message request can not be null");
        }
        if (request.getMessage() == null || request.getDate() == null) {
            logger.error("Message or date can not be null");
            throw new RuntimeException("Message or date can not be null");
        }
        if (request.getMessage().isEmpty()) {
            logger.error("Message can not be empty");
            throw new RuntimeException("Message can not be empty");
        }
        if (request.getMessage().length() > 1000) {
            logger.error("Message can not be longer than 1000 characters");
            throw new RuntimeException("Message can not be longer than 1000 characters");
        }
        if (request.getDate().after(new java.util.Date())) {
            logger.error("Date can not be in the future");
            throw new RuntimeException("Date can not be in the future");
        }
        Conversation conversation = conversationRepository.getConversationsById(conversationId).orElseThrow(() -> {
            logger.error("Conversation not found");
            return new RuntimeException("Conversation not found");
        });
        if (!conversation.getUsers().contains(user)) {
            logger.error("User is not in the conversation");
            throw new RuntimeException("User is not in the conversation");
        }
        Message message = new Message();

        message.setMessage(request.getMessage());
        message.setSentAt(request.getDate());
        message.setSender(user);
        message.setConversation(conversation);

        messageRepository.save(message);
    }

    public List<Message> getMessages(User user, UUID conversationId) {
        if (conversationId == null) {
            logger.error("Conversation id can not be null");
            throw new RuntimeException("Conversation id can not be null");
        }
        Conversation conversation = conversationRepository.getConversationsById(conversationId).orElseThrow(() -> {
            logger.error("Conversation not found");
            return new RuntimeException("Conversation not found");
        });
        if (!conversation.getUsers().contains(user)) {
            logger.error("User is not in the conversation");
            throw new RuntimeException("User is not in the conversation");
        }

        return messageRepository.getMessagesByConversationIdOrderBySentAt(conversationId);
    }

    public Message deleteMessage(User user, UUID conversationId, MessageDeleteRequest request) {
        if (conversationId == null || request == null) {
            logger.error("Conversation id or request body can not be null");
            throw new RuntimeException("Conversation id or message id can not be null");
        }
        Conversation conversation = conversationRepository.getConversationsById(conversationId).orElseThrow(() -> {
            logger.error("Conversation not found");
            return new RuntimeException("Conversation not found");
        });
        if (!conversation.getUsers().contains(user)) {
            logger.error("User is not in the conversation");
            throw new RuntimeException("User is not in the conversation");
        }
        Message message = messageRepository.getMessageByConversationIdAndId(conversationId, request.getMessageId()).orElseThrow(() -> {
            logger.error("Message not found");
            return new RuntimeException("Message not found");
        });
        if(!message.getSender().getId().equals(user.getId())){
            logger.error("User is not the sender of the message");
            throw new RuntimeException("User is not the sender of the message");
        }
        message.setIsDeleted(true);
        message.setDeletionType(request.getDeletionType());
        messageRepository.save(message);
        return message;
    }
}
