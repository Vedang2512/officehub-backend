package com.officehub.service.impl;

import java.util.List;

import com.officehub.service.NotificationService;

import java.util.Map;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.officehub.mapper.ChatMessageMapper;
import com.officehub.dto.ChatMessageDTO;
import com.officehub.dto.SendMessageRequest;
import com.officehub.entity.ChatMessage;
import com.officehub.entity.MessageStatus;
import com.officehub.entity.User;
import com.officehub.repository.ChatMessageRepository;
import com.officehub.repository.UserRepository;
import com.officehub.service.ChatService;
import java.time.LocalDateTime;
import java.util.Objects;
import java.time.ZoneOffset;
import com.officehub.entity.Organization;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private NotificationService notificationService;

    @Override
    public ChatMessageDTO sendMessage(
            String senderEmail,
            SendMessageRequest request
    ) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() ->
                        new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() ->
                        new RuntimeException("Receiver not found"));

        Organization senderOrganization = sender.getOrganization();
        Organization receiverOrganization = receiver.getOrganization();

        if (senderOrganization == null || receiverOrganization == null) {
            throw new RuntimeException("Organization not found");
        }

        if (!Objects.equals(
                senderOrganization.getId(),
                receiverOrganization.getId())) {

            throw new RuntimeException(
                    "Users belong to different organizations");
        }

        ChatMessage message = new ChatMessage();

        message.setSender(sender);
        message.setReceiver(receiver);
        message.setOrganization(senderOrganization);
        message.setContent(request.getContent());
        message.setStatus(MessageStatus.SENT);
        message.setSentAt(
        	    LocalDateTime.now(ZoneOffset.UTC)
        	);

        message = chatMessageRepository.save(message);

        ChatMessageDTO dto = chatMessageMapper.toDTO(message);

        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                "/queue/messages",
                dto
        );

        notificationService.sendNotification(
                receiver,
                sender,
                sender.getFullName() + " sent you 1 new message",
                "CHAT"
        );

        return dto;
    }

    @Override
    public List<ChatMessageDTO> getConversation(
            String userEmail,
            Long otherUserId
    ) {

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!Objects.equals(
                currentUser.getOrganization().getId(),
                otherUser.getOrganization().getId())) {

            throw new RuntimeException(
                    "Users belong to different organizations");
        }

        return chatMessageRepository
                .findConversation(currentUser, otherUser)
                .stream()
                .map(chatMessageMapper::toDTO)
                .toList();
    }

    @Override
    public void markMessagesAsRead(
            String userEmail,
            Long senderId
    ) {

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<ChatMessage> unreadMessages =
                chatMessageRepository.findByReceiverAndStatus(
                        currentUser,
                        MessageStatus.SENT
                );

        for (ChatMessage message : unreadMessages) {

            if (message.getSender().getId().equals(senderId)) {

                message.setStatus(MessageStatus.READ);

            }

        }

        chatMessageRepository.saveAll(unreadMessages);
    }
    
    @Override
    public Map<Long, Long> getUnreadMessageCounts(
            String userEmail
    ) {

        User currentUser =
                userRepository.findByEmail(userEmail)
                        .orElseThrow(() ->
                                new RuntimeException("User not found"));

        List<Object[]> results =
                chatMessageRepository.countUnreadMessagesBySender(
                        currentUser,
                        MessageStatus.SENT
                );

        Map<Long, Long> unreadCounts =
                new java.util.HashMap<>();

        for (Object[] result : results) {

            Long senderId = (Long) result[0];
            Long count = (Long) result[1];

            unreadCounts.put(
                    senderId,
                    count
            );
        }

        return unreadCounts;
    }
    
    @Override
    public ChatMessageDTO editMessage(
            String userEmail,
            Long messageId,
            String newContent
    ) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found"));

        // Only the sender can edit the message
        if (!message.getSender().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You can only edit your own messages"
            );
        }

        // Deleted messages cannot be edited
        if (message.isDeleted()) {

            throw new RuntimeException(
                    "Deleted messages cannot be edited"
            );
        }

        // Check 5-minute edit window
        long secondsSinceSent =
                Duration.between(
                        message.getSentAt(),
                        LocalDateTime.now(ZoneOffset.UTC)
                ).getSeconds();

        if (secondsSinceSent > 5 * 60) {

            throw new RuntimeException(
                    "Message can only be edited within 5 minutes"
            );
        }

        if (newContent == null || newContent.trim().isEmpty()) {

            throw new RuntimeException(
                    "Message content cannot be empty"
            );
        }

        message.setContent(newContent.trim());
        message.setEdited(true);

        message = chatMessageRepository.save(message);

        ChatMessageDTO dto =
                chatMessageMapper.toDTO(message);

        // Send updated message to receiver
        messagingTemplate.convertAndSendToUser(
                message.getReceiver().getEmail(),
                "/queue/messages",
                dto
        );

        // Send updated message to sender
        messagingTemplate.convertAndSendToUser(
                message.getSender().getEmail(),
                "/queue/messages",
                dto
        );

        return dto;
    }


    @Override
    public ChatMessageDTO deleteMessage(
            String userEmail,
            Long messageId
    ) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException("Message not found"));

        // Only the sender can delete the message
        if (!message.getSender().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You can only delete your own messages"
            );
        }

        // Already deleted
        if (message.isDeleted()) {

            throw new RuntimeException(
                    "Message is already deleted"
            );
        }

        // Check 5-minute delete window
        long secondsSinceSent =
                Duration.between(
                        message.getSentAt(),
                        LocalDateTime.now(ZoneOffset.UTC)
                ).getSeconds();

        if (secondsSinceSent > 5 * 60) {

            throw new RuntimeException(
                    "Message can only be deleted within 5 minutes"
            );
        }

        // Soft delete
        message.setDeleted(true);
        message.setContent("This message was deleted");

        message = chatMessageRepository.save(message);

        ChatMessageDTO dto =
                chatMessageMapper.toDTO(message);

        // Notify receiver
        messagingTemplate.convertAndSendToUser(
                message.getReceiver().getEmail(),
                "/queue/messages",
                dto
        );

        // Notify sender
        messagingTemplate.convertAndSendToUser(
                message.getSender().getEmail(),
                "/queue/messages",
                dto
        );

        return dto;
    }

}