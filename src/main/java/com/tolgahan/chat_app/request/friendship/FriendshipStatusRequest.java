package com.tolgahan.chat_app.request.friendship;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.UUID;
@Data
public class FriendshipStatusRequest {
    @NotEmpty(message = "User userId is required")
    UUID userId;
}
