package com.tolgahan.chat_app.request;

import com.tolgahan.chat_app.enums.DeletionType;
import lombok.Data;

@Data
public class MessageDeleteRequest {
    private Long messageId;
    private DeletionType deletionType;
}
