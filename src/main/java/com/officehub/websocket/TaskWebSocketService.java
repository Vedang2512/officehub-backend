package com.officehub.websocket;

import com.officehub.dto.TaskEventDTO;
import org.springframework.stereotype.Service;

@Service
public class TaskWebSocketService {

    private final WebSocketService webSocketService;

    public TaskWebSocketService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    public void broadcastTaskEvent(TaskEventDTO event) {

        webSocketService.sendToTopic(
                "/topic/organization/" +
                        event.getOrganizationId() +
                        "/tasks",
                event
        );

    }

}