package com.officehub.mapper;

import org.springframework.stereotype.Component;

import com.officehub.dto.ChatMessageDTO;
import com.officehub.entity.ChatMessage;

@Component
public class ChatMessageMapper {

	public ChatMessageDTO toDTO(ChatMessage message) {

	    if (message == null) {
	        return null;
	    }

	    ChatMessageDTO dto = new ChatMessageDTO();

	    dto.setId(message.getId());

	    dto.setSenderId(message.getSender().getId());
	    dto.setSenderName(message.getSender().getFullName());
	    dto.setSenderProfileImage(message.getSender().getProfileImage());

	    dto.setReceiverId(message.getReceiver().getId());

	    dto.setContent(message.getContent());

	    dto.setStatus(message.getStatus());

	    dto.setSentAt(message.getSentAt());

	    dto.setEdited(message.isEdited());

	    dto.setDeleted(message.isDeleted());

	    return dto;
	}
}