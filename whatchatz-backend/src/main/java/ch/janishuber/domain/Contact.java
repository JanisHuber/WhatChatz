package ch.janishuber.domain;

import java.time.LocalDateTime;

public record Contact(Long id, String ownerId, String contactId, String contactName, LocalDateTime lastMessage) {
}
