package ch.janishuber.adapter.persistence;

import ch.janishuber.adapter.persistence.entity.UserEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@RequestScoped
public class UserRepository {
    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    @Transactional
    public void save(String uid, String name, String info) {
        UserEntity user = new UserEntity(uid, name, info);
        em.persist(user);
        em.flush();
    }
}
