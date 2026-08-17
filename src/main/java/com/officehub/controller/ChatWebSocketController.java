package com.officehub.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.officehub.dto.ChatMessageDTO;
import com.officehub.dto.SendMessageRequest;
import com.officehub.entity.User;
import com.officehub.repository.UserRepository;
import com.officehub.service.ChatService;

@Controller
public class ChatWebSocketController {


    private final ChatService chatService;

    private final UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate;



    public ChatWebSocketController(
            ChatService chatService,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate
    ) {

        this.chatService = chatService;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;

    }



    @MessageMapping("/chat.send")
    public void sendMessage(
            SendMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor
    ) {


        String senderEmail =
                (String) headerAccessor
                        .getSessionAttributes()
                        .get("email");


        System.out.println(
                "========== CHAT RECEIVED =========="
        );


        System.out.println(
                "Sender: " + senderEmail
        );


        System.out.println(
                "Receiver ID: " + request.getReceiverId()
        );


        System.out.println(
                "Content: " + request.getContent()
        );



        ChatMessageDTO message =
                chatService.sendMessage(
                        senderEmail,
                        request
                );



        User receiver =
                userRepository.findById(
                        request.getReceiverId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Receiver not found"
                        )
                );



        System.out.println(
                "Sending websocket message to: "
                + receiver.getEmail()
        );



        messagingTemplate.convertAndSend(
                "/topic/user/" + receiver.getId(),
                message
        );

    }

}