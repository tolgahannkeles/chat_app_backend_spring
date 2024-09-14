package com.tolgahan.chat_app.response;

import lombok.Data;

import java.util.List;

@Data
public class AccountResponse {
    private String id;
    private String username;
    private List<String> email;
    private List<String> roles;
}
