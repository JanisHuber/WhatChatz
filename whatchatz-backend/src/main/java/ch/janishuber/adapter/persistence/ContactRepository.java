package ch.janishuber.adapter.persistence;

import ch.janishuber.adapter.persistence.entity.ContactEntity;
import ch.janishuber.domain.Contact;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@RequestScoped
public class ContactRepository {
    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public List<Contact> getAllContactsFor(String uid) {
        return em.createQuery("FROM ContactEntity c WHERE c.owner_id = :uid", ContactEntity.class)
                .setParameter("uid", uid)
                .getResultList()
                .stream()
                .map(contact -> new Contact(contact.getId(), contact.getOwnerId(), contact.getContactId(), contact.getContactName(), contact.getLastMessageTimestamp()))
                .toList();
    }
}
