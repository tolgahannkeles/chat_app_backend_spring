package com.tolgahan.chat_app.response;

import lombok.Data;

import java.util.Date;

@Data
public class MessageResponse {
    private String message;
    private String sender;
    private Date date;
}
