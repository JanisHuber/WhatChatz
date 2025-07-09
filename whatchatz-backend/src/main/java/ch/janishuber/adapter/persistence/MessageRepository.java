package ch.janishuber.adapter.persistence;

import ch.janishuber.adapter.persistence.entity.MessageEntity;
import ch.janishuber.domain.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class MessageRepository {
    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public List<Message> getAllMessagesForChat(String chatId) {
        return em.createQuery("SELECT m FROM MessageEntity m WHERE m.chatId = :chatId", Message.class)
                .setParameter("chatId", chatId)
                .getResultList();
    }

    public void save(Message message) {
        MessageEntity entity = new MessageEntity(
                message.chatId(),
                message.senderId(),
                message.receiverId(),
                message.message()
        );
        em.persist(entity);
        em.flush();
    }
}