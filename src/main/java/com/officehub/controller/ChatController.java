package com.officehub.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.officehub.dto.ChatMessageDTO;
import com.officehub.dto.SendMessageRequest;
import com.officehub.service.ChatService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{userId}")
    public List<ChatMessageDTO> getConversation(
            @PathVariable Long userId,
            Principal principal
    ) {

        return chatService.getConversation(
                principal.getName(),
                userId
        );

    }

    @PostMapping("/send")
    public ChatMessageDTO sendMessage(
            @RequestBody SendMessageRequest request,
            Principal principal
    ) {

        return chatService.sendMessage(
                principal.getName(),
                request
        );

    }

    @PutMapping("/read/{senderId}")
    public void markAsRead(
            @PathVariable Long senderId,
            Principal principal
    ) {

        chatService.markMessagesAsRead(
                principal.getName(),
                senderId
        );

    }
    
    @GetMapping("/unread-counts")
    public Map<Long, Long> getUnreadMessageCounts(
            Principal principal
    ) {

        return chatService.getUnreadMessageCounts(
                principal.getName()
        );
    }
    
    @PutMapping("/edit/{messageId}")
    public ChatMessageDTO editMessage(
            @PathVariable Long messageId,
            @RequestBody Map<String, String> request,
            Principal principal
    ) {

        return chatService.editMessage(
                principal.getName(),
                messageId,
                request.get("content")
        );
    }


    @DeleteMapping("/{messageId}")
    public ChatMessageDTO deleteMessage(
            @PathVariable Long messageId,
            Principal principal
    ) {

        return chatService.deleteMessage(
                principal.getName(),
                messageId
        );
    }

}