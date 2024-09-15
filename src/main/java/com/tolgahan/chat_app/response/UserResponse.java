package com.tolgahan.chat_app.response;

import com.tolgahan.chat_app.model.Email;
import com.tolgahan.chat_app.model.User;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private List<String> email;

    public UserResponse(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmails().stream().map(Email::getEmail).toList();
    }
}
