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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/ws")
@ApplicationScoped
public class WhatChatzSocket {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Map<String, Set<Session>> chatSubscriptions = new ConcurrentHashMap<>();
    private static final Map<Session, Set<String>> sessionSubscriptions = new ConcurrentHashMap<>();

    private SocketService socketService;

    @PostConstruct
    public void init() {
        socketService = CDIProviderHelper.getBean(SocketService.class);
    }

    @OnOpen
    public void onOpen(Session session) {
        String token = socketService.extractToken(session.getQueryString());
        String uid = socketService.verifyAndExtractUid(token);
        session.getUserProperties().put("uid", uid);

        sessionSubscriptions.put(session, ConcurrentHashMap.newKeySet());
        List<String> chatIds = socketService.getAllChatIdsFor(uid);
        for (String chatId : chatIds) {
            chatSubscriptions.computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet()).add(session);
            sessionSubscriptions.get(session).add(chatId);
        }
    }

    @OnMessage
    public void onMessage(Session sender, String rawJson) {
        try {
            handleChatMessage(sender, rawJson);
        } catch (Exception e) {
            sendError(sender, e.getMessage(), 400);
        }
    }


    private void handleChatMessage(Session sender, String rawJson) throws IOException {
        MessageRequest req = parseMessage(rawJson);
        String senderUid = (String) sender.getUserProperties().get("uid");

        if (senderUid == null) {
            sendError(sender, "Missing authenticated UID", 401);
            return;
        }
        Message message = new Message(
                0L,
                req.chatId(),
                senderUid,
                req.receiverId(),
                req.message(),
                LocalDateTime.now()
        );

        socketService.saveMessage(message);
        broadcastNotification(req.chatId(), sender);
    }

    @OnClose
    public void onClose(Session session) {
        Set<String> subscribedChats = sessionSubscriptions.remove(session);
        if (subscribedChats != null) {
            for (String chatId : subscribedChats) {
                Set<Session> sessions = chatSubscriptions.get(chatId);
                if (sessions != null) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        chatSubscriptions.remove(chatId);
                    }
                }
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sendError(session, throwable.getMessage(), 500);
    }

    private void broadcastNotification(String chatId, Session sender) {
        Set<Session> sessions = chatSubscriptions.getOrDefault(chatId, Set.of());
        for (Session session : sessions) {

            if (!session.equals(sender) && session.isOpen()) {
                try {
                    Map<String, String> payload = Map.of(
                            "type", "NEW_MESSAGE",
                            "chatId", chatId
                    );
                    session.getAsyncRemote().sendText(MAPPER.writeValueAsString(payload));
                } catch (IOException e) {
                    System.err.println("Broadcast failed for session " + session.getId() + ": " + e.getMessage());
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
            System.err.println("Failed to send error: " + e.getMessage());
        }
    }

    private MessageRequest parseMessage(String json) throws IOException {
        return MAPPER.readValue(json, MessageRequest.class);
    }
}
