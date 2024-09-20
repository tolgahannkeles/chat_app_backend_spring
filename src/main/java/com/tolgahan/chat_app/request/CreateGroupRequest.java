package com.tolgahan.chat_app.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
public class CreateGroupRequest {
    @NotEmpty(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;
    @NotEmpty(message = "Participants is required")
    private List<UUID> participants;
}
