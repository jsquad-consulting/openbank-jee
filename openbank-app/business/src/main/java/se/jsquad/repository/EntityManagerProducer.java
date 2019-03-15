package se.jsquad.repository;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class EntityManagerProducer {
    @PersistenceContext(unitName = "openBankPU")
    EntityManager entityManager;

    EntityManager getEntityManager() {
        return entityManager;
    }
}
