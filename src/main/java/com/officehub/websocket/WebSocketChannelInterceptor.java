package com.officehub.websocket;

import java.security.Principal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.officehub.entity.User;
import com.officehub.repository.UserRepository;
import com.officehub.util.JwtUtil;


@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {


    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private UserRepository userRepository;



    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {


        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(message);



        if (StompCommand.CONNECT.equals(accessor.getCommand())) {


            try {


                String authHeader =
                        accessor.getFirstNativeHeader(
                                "Authorization"
                        );


                if (authHeader == null ||
                    !authHeader.startsWith("Bearer ")) {


                    throw new IllegalArgumentException(
                            "Missing Authorization header"
                    );

                }



                String token =
                        authHeader.substring(7);



                String email =
                        jwtUtil.extractUsername(token);



                User user =
                        userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );



                // Store session information

                accessor.getSessionAttributes()
                        .put(
                                "userId",
                                user.getId()
                        );


                accessor.getSessionAttributes()
                        .put(
                                "organizationId",
                                user.getOrganization() != null
                                        ? user.getOrganization().getId()
                                        : null
                        );


                accessor.getSessionAttributes()
                        .put(
                                "role",
                                user.getRole().name()
                        );


                accessor.getSessionAttributes()
                        .put(
                                "email",
                                user.getEmail()
                        );



                // Attach authenticated user to STOMP session

                accessor.setUser(
                        new StompPrincipal(
                                user.getEmail()
                        )
                );



            } catch (Exception ex) {


                throw new IllegalArgumentException(
                        "Invalid or expired WebSocket token",
                        ex
                );


            }

        }


        return MessageBuilder
                .createMessage(
                        message.getPayload(),
                        accessor.getMessageHeaders()
                );

    }

}