package se.jsquad.repository;

import se.jsquad.entity.SystemProperty;
import se.jsquad.qualifier.Log;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;

public class SystemPropertyRepository extends EntityManagerProducer {
    @Inject @Log
    private Logger logger;

    List<SystemProperty> findAllUniqueSystemProperties() {
        TypedQuery<SystemProperty> query = getEntityManager().createNamedQuery(SystemProperty
                .FIND_ALL_UNIQUE_SYSTEM_PROPERTIES, SystemProperty.class);

        return query.getResultList();
    }

    void clearSecondaryLevelCache() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    public void refreshSecondaryLevelCache() {
        clearSecondaryLevelCache();
        findAllUniqueSystemProperties();
    }
}
