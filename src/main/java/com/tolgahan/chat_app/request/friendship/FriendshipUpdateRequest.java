package com.tolgahan.chat_app.request.friendship;

import com.tolgahan.chat_app.enums.FriendshipStatus;
import lombok.Data;

@Data
public class FriendshipUpdateRequest {
    FriendshipStatus status;
}
