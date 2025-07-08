package ch.janishuber.domain;

import java.time.LocalDateTime;

public record Contact(String ownerId, String contactId, String contactName, LocalDateTime lastMessage) {
}
