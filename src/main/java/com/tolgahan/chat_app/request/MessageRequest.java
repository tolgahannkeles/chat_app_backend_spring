package com.tolgahan.chat_app.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
@Data
public class MessageRequest {
    @NotEmpty(message = "Message cannot be empty")
    @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
    private String message;
    @NotEmpty(message = "Date cannot be empty")
    @Past(message = "Date must be in the past")
    private Date date;
}
