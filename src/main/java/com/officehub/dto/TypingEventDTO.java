package com.officehub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypingEventDTO {

    private Long senderId;

    private Long receiverId;

    private boolean typing;

}