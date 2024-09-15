package com.tolgahan.chat_app.repository;

import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> getConversationsById(UUID id);
}
