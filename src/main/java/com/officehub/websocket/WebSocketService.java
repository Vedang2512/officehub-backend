package com.officehub.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;



@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Broadcast a message to everyone subscribed to a topic.
     */
    public void sendToTopic(String topic, Object payload) {
        messagingTemplate.convertAndSend(topic, payload);
    }

    /**
     * Send a private message to a specific user.
     */
    public void sendToUser(String username, String destination, Object payload) {

        messagingTemplate.convertAndSendToUser(
                username,
                destination,
                payload
        );
    }
    

}