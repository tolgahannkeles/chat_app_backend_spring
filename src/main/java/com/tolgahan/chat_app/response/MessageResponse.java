package com.tolgahan.chat_app.response;

import com.tolgahan.chat_app.enums.DeletionType;
import com.tolgahan.chat_app.model.Message;
import com.tolgahan.chat_app.repository.MessageRepository;
import lombok.Data;

import java.util.Date;

@Data
public class MessageResponse {
    private Long id;
    private String message;
    private String sender;
    private Boolean isDeleted;
    private DeletionType deletionType;
    private Date date;

    public MessageResponse(Message message){
        this.id = message.getId();
        this.message = message.getMessage();
        this.sender = message.getSender().getUsername();
        this.date = message.getSentAt();
        this.isDeleted = message.getIsDeleted();
        this.deletionType = message.getDeletionType();
    }
}
