package com.officehub.websocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class PresenceService {


    private final Set<Long> onlineUsers =
            ConcurrentHashMap.newKeySet();



    public void userOnline(Long userId) {

        onlineUsers.add(userId);

    }



    public void userOffline(Long userId) {

        onlineUsers.remove(userId);

    }



    public boolean isOnline(Long userId) {

        return onlineUsers.contains(userId);

    }



    public Set<Long> getOnlineUsers() {

        return onlineUsers;

    }

}