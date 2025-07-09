package ch.janishuber.domain;

import java.time.LocalDateTime;

public record Message(Long id, String chatId, String senderId, String receiverId, String message,
                      LocalDateTime timeStamp) {
}
