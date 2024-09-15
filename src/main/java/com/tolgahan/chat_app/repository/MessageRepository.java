package com.tolgahan.chat_app.repository;

import com.tolgahan.chat_app.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> getMessagesByConversationId(UUID conversationId);
    List<Message> getMessagesByConversationIdOrderBySentAt(UUID conversationId);
    Optional<Message> getMessageByConversationIdAndId(UUID conversationId, Long id);
}
