package com.tolgahan.chat_app.request;

import com.tolgahan.chat_app.enums.DeletionType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MessageDeleteRequest {
    @NotEmpty(message = "Message userId is required")
    private Long messageId;
    @NotEmpty(message = "Deletion type is required")
    private DeletionType deletionType;
}
