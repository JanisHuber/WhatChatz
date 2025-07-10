package ch.janishuber.adapter.persistence;

import ch.janishuber.adapter.persistence.entity.ContactEntity;
import ch.janishuber.domain.Contact;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@RequestScoped
public class ContactRepository {
    @PersistenceContext(unitName = "whatchatzPU")
    private EntityManager em;

    public List<Contact> getAllContactsFor(String uid) {
        return em.createQuery("FROM ContactEntity c WHERE c.ownerId = :uid", ContactEntity.class)
                .setParameter("uid", uid)
                .getResultList()
                .stream()
                .map(contact -> new Contact(contact.getId(), contact.getOwnerId(), contact.getContactId(), contact.getContactName(), contact.getLastMessageTimestamp()))
                .toList();
    }

    @Transactional
    public void save(Contact contact) {
        ContactEntity entity = new ContactEntity(
                contact.ownerId(),
                contact.contactId(),
                contact.contactName(),
                contact.lastMessage()
        );
        em.persist(entity);
        em.flush();
    }
}
