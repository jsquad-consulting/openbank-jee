package se.jsquad.repository;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Dependent
public class EntityManagerProducer {
    @PersistenceContext(unitName = "openBankPU")
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
