package com.tolgahan.chat_app.controller.interfaces;

import com.tolgahan.chat_app.request.MessageDeleteRequest;
import com.tolgahan.chat_app.request.MessageRequest;
import com.tolgahan.chat_app.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public interface IMessageController {
    @PostMapping("{conversationId}")
    String sendMessage(@PathVariable UUID conversationId, @RequestBody @Valid MessageRequest messageRequest);

    @DeleteMapping("/{conversationId}")
    String deleteMessage(@PathVariable UUID conversationId, @RequestBody @Valid MessageDeleteRequest request);

    @GetMapping("/{conversationId}")
    List<MessageResponse> getMessages(@PathVariable UUID conversationId);
}
