package ch.janishuber.adapter.socket;

import ch.janishuber.adapter.dto.MessageRequest;
import ch.janishuber.adapter.helpers.CDIProviderHelper;
import ch.janishuber.domain.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.PathParam;

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
    public void onOpen(Session session, @PathParam("chatId") String chatId) {
        String token = socketService.extractToken(session.getQueryString());
        String uid = socketService.verifyAndExtractUid(token);
        session.getUserProperties().put("uid", uid);

        chatRooms.computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @OnMessage
    public void onMessage(String rawJson, Session sender, @PathParam("chatId") String chatId) {
        try {
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
    public void onClose(Session session, @PathParam("chatId") String chatId) {
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
                    session.getAsyncRemote().sendText("{\"type\":\"NEW_MESSAGE\",\"chatId\":\"" + chatId + "\"}");
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

}
