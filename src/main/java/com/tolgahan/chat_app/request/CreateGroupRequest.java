package com.tolgahan.chat_app.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
public class CreateGroupRequest {
    private String title;
    private List<String> usernames;
}
