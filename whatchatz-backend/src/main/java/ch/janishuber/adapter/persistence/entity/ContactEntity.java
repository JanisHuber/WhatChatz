package ch.janishuber.adapter.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contacts")
public class ContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "contact_id", nullable = false)
    private String contactId;

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @Column(name = "last_message_timestamp")
    private LocalDateTime lastMessageTimestamp;

    public ContactEntity() {
    }

    public ContactEntity(String ownerId, String contactId, String contactName, LocalDateTime lastMessageTimestamp) {
        this.ownerId = ownerId;
        this.contactId = contactId;
        this.contactName = contactName;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public int getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public LocalDateTime getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(LocalDateTime lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}
