package com.tolgahan.chat_app.controller.interfaces;

import com.tolgahan.chat_app.response.FriendResponse;
import com.tolgahan.chat_app.response.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public interface IUserController {
    @GetMapping("/{username}")
    UserResponse getUserByUsername(@PathVariable String username);

    @GetMapping
    UserResponse getLocalUser();

    @GetMapping("/all")
    List<UserResponse> all();

    @GetMapping("/search/{username}")
    List<FriendResponse> searchUserByUsername(@PathVariable String username);
}
