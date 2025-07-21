package ch.janishuber.adapter.persistence;

import ch.janishuber.adapter.persistence.entity.UserEntity;
import ch.janishuber.domain.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

import java.sql.SQLIntegrityConstraintViolationException;

@RequestScoped
public class UserRepository {
    @PersistenceContext(unitName = "whatchatzPU")
    private EntityManager em;

    @Transactional
    public void save(String uid, String name, String info) {
        UserEntity user = new UserEntity(uid, name, info);
        try {
            em.persist(user);
            em.flush();
        } catch (PersistenceException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new IllegalStateException("Benutzername bereits vergeben", e);
            }
        }
    }

    public User findByName(String name) {
        UserEntity userEntity = em.createQuery("SELECT u FROM UserEntity u WHERE u.name = :name", UserEntity.class)
                .setParameter("name", name)
                .getSingleResult();
        return new User(userEntity.getUid(), userEntity.getName(), userEntity.getInfo());
    }

    public User findByUid(String uid) {
        UserEntity userEntity = em.find(UserEntity.class, uid);
        if (userEntity == null) {
            throw new WebApplicationException("Benutzer nicht gefunden", 404);
        }
        return new User(userEntity.getUid(), userEntity.getName(), userEntity.getInfo());
    }
}