package com.tolgahan.chat_app.request;

import lombok.Data;

import java.util.Date;
@Data
public class MessageRequest {
    private String message;
    private Date date;
}
