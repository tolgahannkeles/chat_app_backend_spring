package com.tolgahan.chat_app.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PrivateConversationRequest {
    @NotEmpty(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
}
