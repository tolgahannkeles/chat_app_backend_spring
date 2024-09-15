package com.tolgahan.chat_app.response;

import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.MessageRequest;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ConversationResponse {
    private UUID id;
    private String title;
    private List<UUID> participants;
    private MessageResponse lastMessage;

    public ConversationResponse(Conversation conversation) {
        this.id = conversation.getId();
        this.title = conversation.getTitle();
        this.participants = conversation.getUsers().stream().map(User::getId).toList();
        if (conversation.getLastMessage() != null) {
            this.lastMessage = new MessageResponse(conversation.getLastMessage());
        } else {
            this.lastMessage = null;
        }
    }
}
