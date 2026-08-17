package com.officehub.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SocketSessionRegistry {

    // sessionId -> SocketUser
    private final Map<String, SocketUser> sessions =
            new ConcurrentHashMap<>();

    // userId -> sessionId
    private final Map<Long, String> userSessions =
            new ConcurrentHashMap<>();


    public void addSession(String sessionId, SocketUser user) {

        sessions.put(sessionId, user);

        userSessions.put(user.getUserId(), sessionId);
    }


    public void removeSession(String sessionId) {

        SocketUser user = sessions.remove(sessionId);

        if (user != null) {
            userSessions.remove(user.getUserId());
        }
    }


    public SocketUser getSession(String sessionId) {
        return sessions.get(sessionId);
    }


    public SocketUser getUser(Long userId) {

        String sessionId = userSessions.get(userId);

        if (sessionId == null) {
            return null;
        }

        return sessions.get(sessionId);
    }


    public boolean isUserOnline(Long userId) {
        return userSessions.containsKey(userId);
    }


    public String getSessionId(Long userId) {
        return userSessions.get(userId);
    }


    public Map<String, SocketUser> getSessions() {
        return sessions;
    }

}