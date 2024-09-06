package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.model.Message;
import com.tolgahan.chat_app.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void sendMessage(Message message){
        messageRepository.save(message);
    }
}
