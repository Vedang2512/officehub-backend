package com.officehub.service;

import java.util.List;
import java.util.Map;

import com.officehub.dto.ChatMessageDTO;
import com.officehub.dto.SendMessageRequest;

public interface ChatService {

    ChatMessageDTO sendMessage(
            String senderEmail,
            SendMessageRequest request
    );

    List<ChatMessageDTO> getConversation(
            String userEmail,
            Long otherUserId
    );

    void markMessagesAsRead(
            String userEmail,
            Long senderId
    );
    
    Map<Long, Long> getUnreadMessageCounts(
            String userEmail
    );
    
    ChatMessageDTO editMessage(
            String userEmail,
            Long messageId,
            String newContent
    );

    ChatMessageDTO deleteMessage(
            String userEmail,
            Long messageId
    );

}