package com.tolgahan.chat_app.repository;

import com.tolgahan.chat_app.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
