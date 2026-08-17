package com.officehub.websocket;


import java.util.Map;


import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.officehub.dto.PresenceDTO;

@Component
public class WebSocketEventListener {


    private final SocketSessionRegistry sessionRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(
            SocketSessionRegistry sessionRegistry,
            SimpMessagingTemplate messagingTemplate
    ) {

        this.sessionRegistry = sessionRegistry;
        this.messagingTemplate = messagingTemplate;

    }



    @EventListener
    public void handleWebSocketConnectListener(
            SessionConnectEvent event
    ) {


        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(
                        event.getMessage()
                );


        Map<String,Object> attributes =
                accessor.getSessionAttributes();



        if(attributes == null) {
            return;
        }



        Long userId =
                (Long) attributes.get("userId");


        if(userId != null) {


            SocketUser socketUser =
                    new SocketUser(
                            userId,
                            (Long) attributes.get("organizationId"),
                            (String) attributes.get("role"),
                            (String) attributes.get("email")
                    );


            sessionRegistry.addSession(
                    accessor.getSessionId(),
                    socketUser
            );
            
            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    new PresenceDTO(
                            userId,
                            "ONLINE"
                    )
            );

        }

    }





    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {


        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(
                        event.getMessage()
                );


        String sessionId =
                accessor.getSessionId();


        if(sessionId != null) {


            SocketUser socketUser =
                    sessionRegistry.getSession(sessionId);


            if(socketUser != null) {


                messagingTemplate.convertAndSend(
                        "/topic/presence",
                        new PresenceDTO(
                                socketUser.getUserId(),
                                "OFFLINE"
                        )
                );

            }


            sessionRegistry.removeSession(
                    sessionId
            );

        }

    }

}