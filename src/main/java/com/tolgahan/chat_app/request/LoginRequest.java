package com.tolgahan.chat_app.request;

import lombok.Data;

@Data
public class LoginRequest {
    String username;
    String email;
    String password;
}
