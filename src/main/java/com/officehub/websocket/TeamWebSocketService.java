package com.officehub.websocket;

import com.officehub.dto.TeamEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastTeamEvent(TeamEventDTO event) {

        messagingTemplate.convertAndSend(
                "/topic/organization/" + event.getOrganizationId() + "/teams",
                event
        );
    }
}