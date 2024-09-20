package com.tolgahan.chat_app.request.friendship;

import com.tolgahan.chat_app.enums.FriendshipStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FriendshipUpdateRequest {
    @NotEmpty(message = "Friendship status is required")
    FriendshipStatus status;
}
