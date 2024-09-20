package com.tolgahan.chat_app.controller.interfaces;

import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.response.AccountResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public interface IAccountController {
    @GetMapping
    AccountResponse getUser();
}
