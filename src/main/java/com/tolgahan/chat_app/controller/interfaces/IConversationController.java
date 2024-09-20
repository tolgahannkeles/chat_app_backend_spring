package com.tolgahan.chat_app.controller.interfaces;

import com.tolgahan.chat_app.enums.ConversationType;
import com.tolgahan.chat_app.request.CreateGroupRequest;
import com.tolgahan.chat_app.response.ConversationResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversation")
public interface IConversationController {
    @GetMapping
    List<ConversationResponse> getAllConversationsWithParams(@RequestParam(name = "type", required = false) ConversationType type);

    @PostMapping("/groups")
    ConversationResponse createGroup(@RequestBody @Valid CreateGroupRequest createGroupRequest);

    @GetMapping("/groups/{conversationId}")
    ConversationResponse getGroupById(@PathVariable(name = "conversationId") UUID conversationId);

    @DeleteMapping("/groups/{conversationId}")
    String leaveGroup(@PathVariable(name = "conversationId") UUID conversationId);

    @PatchMapping("/groups/{conversationId}")
    String addParticipant(@PathVariable(name = "conversationId") UUID conversationId, @RequestBody UUID participantId);
}
