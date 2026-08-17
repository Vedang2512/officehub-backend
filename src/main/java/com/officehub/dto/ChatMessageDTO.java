package com.officehub.dto;

import java.time.LocalDateTime;

import com.officehub.entity.MessageStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    private Long id;

    private Long senderId;
    private String senderName;
    private String senderProfileImage;

    private Long receiverId;

    private String content;

    private MessageStatus status;

    private LocalDateTime sentAt;

    private boolean edited;

    private boolean deleted;
}