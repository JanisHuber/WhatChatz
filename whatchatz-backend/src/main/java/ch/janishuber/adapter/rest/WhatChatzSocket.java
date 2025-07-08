package ch.janishuber.adapter.rest;

import ch.janishuber.adapter.persistence.ContactRepository;
import ch.janishuber.adapter.persistence.UserRepository;
import ch.janishuber.adapter.rest.dto.UserCreation;
import ch.janishuber.domain.AuthService;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/websocket")
public class WhatChatzSocket {
    // Sammlung aller verbundenen Sessions
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    @Inject
    private AuthService authService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ContactRepository contactRepository;

    public Response saveUser(HttpHeaders headers, UserCreation userCreation) {
        String authHeader = headers.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String idToken = authHeader.substring("Bearer ".length());
        String uid = authService.extractUidFromToken(idToken);

        userRepository.save(uid, userCreation.name(), userCreation.info());
        return Response.ok("User saved successfully").build();
    }

    public Response getAllContacs(HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String idToken = authHeader.substring("Bearer ".length());
        String uid = authService.extractUidFromToken(idToken);

        return Response.ok(contactRepository.getAllContactsFor(uid)).build();
    }

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