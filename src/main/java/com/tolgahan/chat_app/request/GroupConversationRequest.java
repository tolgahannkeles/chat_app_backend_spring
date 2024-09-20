package com.tolgahan.chat_app.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class GroupConversationRequest {
    @NotEmpty(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;
    @NotEmpty(message = "Participants are required")
    private List<String> participants;
}
