package ch.janishuber.adapter.persistence;

import ch.janishuber.adapter.persistence.entity.ContactEntity;
import ch.janishuber.domain.Contact;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ContactRepository {
    @PersistenceUnit
    EntityManagerFactory emf;
    @PersistenceContext(unitName = "whatchatzPU")
    private EntityManager em;

    @Transactional
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
