package com.tolgahan.chat_app.service.interfaces;

import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface IConversationService {
    Conversation createGroupConversation(User user, String title, List<UUID> participants);

    void addParticipant(User user, UUID conversationId, User participant);

    void leaveGroup(User user, UUID conversationId);

    Conversation getConversationById(User user, UUID conversationId);

    List<Conversation> getAllChats(User user);

    List<Conversation> getAllGroups(User user);

    List<Conversation> getAllConversations(User user);

    Conversation getConversationById(UUID id);
}
