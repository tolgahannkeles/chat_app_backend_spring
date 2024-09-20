package com.tolgahan.chat_app.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {
    @NotEmpty(message = "Username is required")
    String username;
    @Email(message = "Email is not valid")
    @NotEmpty(message = "Email is required")
    String email;
    @NotEmpty(message = "Password is required")
    String password;
}
