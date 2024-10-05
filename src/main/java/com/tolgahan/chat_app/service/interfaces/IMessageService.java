package com.tolgahan.chat_app.service.interfaces;

import com.tolgahan.chat_app.model.Message;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.MessageDeleteRequest;
import com.tolgahan.chat_app.request.MessageRequest;

import java.util.List;
import java.util.UUID;

public interface IMessageService {
    Message sendMessage(User user, UUID conversationId, MessageRequest request);

    List<Message> getMessages(User user, UUID conversationId);

    Message deleteMessage(User user, UUID conversationId, MessageDeleteRequest request);
}
