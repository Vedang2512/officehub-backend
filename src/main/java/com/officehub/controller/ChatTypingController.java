package com.officehub.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.officehub.dto.TypingEventDTO;


@Controller
public class ChatTypingController {


    private final SimpMessagingTemplate messagingTemplate;


    public ChatTypingController(
            SimpMessagingTemplate messagingTemplate
    ) {

        this.messagingTemplate = messagingTemplate;

    }



    @MessageMapping("/chat.typing")
    public void typing(
            TypingEventDTO typingEvent
    ) {


        messagingTemplate.convertAndSendToUser(
                String.valueOf(
                        typingEvent.getReceiverId()
                ),
                "/queue/typing",
                typingEvent
        );


    }

}