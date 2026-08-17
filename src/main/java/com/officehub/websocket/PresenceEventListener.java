package com.officehub.websocket;


import org.springframework.context.event.EventListener;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;


@Component
public class PresenceEventListener {


    private final PresenceService presenceService;

    private final SimpMessagingTemplate messagingTemplate;



    public PresenceEventListener(
            PresenceService presenceService,
            SimpMessagingTemplate messagingTemplate
    ) {

        this.presenceService = presenceService;
        this.messagingTemplate = messagingTemplate;

    }





    @EventListener
    public void handleConnect(
            SessionConnectedEvent event
    ) {


        Map<String,Object> attributes =
                event.getMessage()
                .getHeaders()
                .get(
                    "simpSessionAttributes",
                    Map.class
                );


        if(attributes == null)
            return;



        Long userId =
                (Long) attributes.get("userId");



        if(userId != null){

            presenceService.userOnline(userId);



            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    userId
            );

        }

    }






    @EventListener
    public void handleDisconnect(
            SessionDisconnectEvent event
    ){

        Map<String,Object> attributes =
                event.getMessage()
                .getHeaders()
                .get(
                    "simpSessionAttributes",
                    Map.class
                );



        if(attributes == null)
            return;



        Long userId =
                (Long) attributes.get("userId");



        if(userId != null){

            presenceService.userOffline(userId);



            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    userId
            );

        }


    }

}