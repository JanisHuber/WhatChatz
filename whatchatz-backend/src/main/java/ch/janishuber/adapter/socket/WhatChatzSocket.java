package ch.janishuber.adapter.socket;

import ch.janishuber.adapter.dto.MessageRequest;
import ch.janishuber.adapter.helpers.CDIProviderHelper;
import ch.janishuber.domain.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/ws/chat/{chatId}")
@ApplicationScoped
public class WhatChatzSocket {

    private static final Map<String, Set<Session>> chatRooms = new ConcurrentHashMap<>();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private SocketService socketService;

    @PostConstruct
    public void init() {
        socketService = CDIProviderHelper.getBean(SocketService.class);
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New WebSocket connection: " + session.getId());
        String chatId = extractChatIdFromUrl(session.getRequestURI().toString());

        String token = socketService.extractToken(session.getQueryString());
        String uid = socketService.verifyAndExtractUid(token);
        session.getUserProperties().put("uid", uid);
        session.getUserProperties().put("chatId", chatId);

        chatRooms.computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @OnMessage
    public void onMessage(Session sender, String rawJson) {
        try {
            System.out.println("chatId: " + sender.getUserProperties().get("chatId"));
            String chatId = (String) sender.getUserProperties().get("chatId");
            MessageRequest messageRequest = parseMessage(rawJson);
            String senderUid = (String) sender.getUserProperties().get("uid");
            if (senderUid == null) {
                sendError(sender, "Missing authenticated UID", 401);
                return;
            }

            Message message = new Message(
                    0L,
                    chatId,
                    senderUid,
                    messageRequest.receiverId(),
                    messageRequest.message(),
                    LocalDateTime.now()
            );

            socketService.saveMessage(message);

            broadcastNotification(chatId, sender);
        } catch (Exception e) {
            sendError(sender, e.getMessage(), 400);
        }
    }

    @OnClose
    public void onClose(Session session) {
        String chatId = (String) session.getUserProperties().get("chatId");
        Set<Session> sessions = chatRooms.get(chatId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                chatRooms.remove(chatId);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sendError(session, throwable.getMessage(), 500);
    }

    private void broadcastNotification(String chatId, Session sender) {
        Set<Session> sessions = chatRooms.get(chatId);
        if (sessions != null) {
            for (Session session : sessions) {
                if (!session.equals(sender) && session.isOpen()) {
                    try {
                        Map<String, String> payload = Map.of(
                                "type", "NEW_MESSAGE",
                                "chatId", chatId
                        );
                        String json = MAPPER.writeValueAsString(payload);
                        session.getAsyncRemote().sendText(json);
                    } catch (IOException e) {
                        System.err.println("Error sending message to session " + session.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private void sendError(Session session, String reason, int code) {
        String errorJson = String.format(
                "{\"type\": \"ERROR\", \"code\": %d, \"reason\": \"%s\"}",
                code,
                reason.replace("\"", "'")
        );
        try {
            session.getBasicRemote().sendText(errorJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MessageRequest parseMessage(String json) throws IOException {
        return MAPPER.readValue(json, MessageRequest.class);
    }

    private String extractChatIdFromUrl(String url) {
        int idx = url.indexOf("?token=");
        String cuttedUrl = (idx != -1) ? url.substring(0, idx) : url;
        String[] parts = cuttedUrl.split("/");
        return parts[parts.length - 1];
    }
}
