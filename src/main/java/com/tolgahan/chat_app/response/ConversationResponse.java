package com.tolgahan.chat_app.response;

import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.User;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
public class ConversationResponse {
    private UUID id;
    private String title;
    private List<UUID> participants;

    public ConversationResponse(Conversation conversation) {
        this.id = conversation.getId();
        this.title = conversation.getTitle();
        this.participants = conversation.getUsers().stream().map(User::getId).toList();
    }
}
