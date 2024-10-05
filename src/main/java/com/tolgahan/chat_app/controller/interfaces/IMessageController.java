package com.tolgahan.chat_app.controller.interfaces;

import com.tolgahan.chat_app.model.Message;
import com.tolgahan.chat_app.request.MessageDeleteRequest;
import com.tolgahan.chat_app.request.MessageRequest;
import com.tolgahan.chat_app.response.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public interface IMessageController {
    @MessageMapping("/chat.sendMessage/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    String sendMessage(@DestinationVariable UUID conversationId, @Payload @Valid MessageRequest messageRequest, Principal principal);

    @DeleteMapping("/{conversationId}")
    String deleteMessage(@PathVariable UUID conversationId, @RequestBody @Valid MessageDeleteRequest request);

    @GetMapping("/topic/conversation/{conversationId}")
    List<MessageResponse> getMessages(@PathVariable UUID conversationId);
}
