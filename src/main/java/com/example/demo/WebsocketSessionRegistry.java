package com.example.demo;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashSet;
import java.util.Set;

@Component
public class WebsocketSessionRegistry {

    private final Set<String> activeSessions = new HashSet<>();

    public Integer getActiveSessionCount() {
        return activeSessions.size();
    }

    @EventListener(SessionConnectEvent.class)
    public void handleSessionConnected(SessionConnectEvent event) {
        final String sessionId = SimpAttributesContextHolder.currentAttributes().getSessionId();
        activeSessions.add(sessionId);
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        final String sessionId = SimpAttributesContextHolder.currentAttributes().getSessionId();
        activeSessions.remove(sessionId);
    }
}
