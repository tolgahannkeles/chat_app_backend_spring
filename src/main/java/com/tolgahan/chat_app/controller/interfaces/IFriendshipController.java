package com.tolgahan.chat_app.controller.interfaces;

import com.tolgahan.chat_app.request.friendship.FriendshipCreateRequest;
import com.tolgahan.chat_app.request.friendship.FriendshipUpdateRequest;
import com.tolgahan.chat_app.response.FriendResponse;
import com.tolgahan.chat_app.response.FriendshipResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/account/friends")
public interface IFriendshipController {
    @GetMapping
    List<FriendResponse> getAllFriends();

    @GetMapping("/{friendId}")
    FriendshipResponse getStatusWithFriend(@PathVariable("friendId") UUID friendId);

    @GetMapping("/requests")
    List<FriendshipResponse> getAllFriendRequests();

    @PostMapping("/requests")
    Boolean sendFriendRequest(@RequestBody @Valid FriendshipCreateRequest request);

    @PatchMapping("/requests/{friendId}")
    FriendshipResponse updateFriendshipRequestStatus(@PathVariable("friendId") UUID friendId, @RequestBody @Valid FriendshipUpdateRequest request);
}
