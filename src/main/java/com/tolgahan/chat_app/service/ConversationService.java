package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.enums.ConversationType;
import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.ConversationUser;
import com.tolgahan.chat_app.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public Conversation createGroupConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public Conversation getConversationById(UUID id) {
        return conversationRepository.findById(id).orElse(null);
    }
}
