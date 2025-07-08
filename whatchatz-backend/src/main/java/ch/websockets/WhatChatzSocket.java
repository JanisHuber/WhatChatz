package ch.websockets;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/websocket")
public class WhatChatzSocket {

    // Sammlung aller verbundenen Sessions
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("WebSocket-Verbindung geöffnet: " + session.getId());

        try {
            session.getBasicRemote().sendText("Willkommen! Sie sind verbunden als: " + session.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Nachricht empfangen von " + session.getId() + ": " + message);

        // Broadcast: Nachricht an alle anderen Clients
        broadcastMessage("Broadcast von " + session.getId() + ": " + message, session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("WebSocket-Verbindung geschlossen: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket-Fehler für " + session.getId() + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void broadcastMessage(String message, Session excludeSession) {
        synchronized (sessions) {
            for (Session session : sessions) {
                if (!session.equals(excludeSession) && session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}