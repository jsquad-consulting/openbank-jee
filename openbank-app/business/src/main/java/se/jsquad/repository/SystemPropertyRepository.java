package se.jsquad.repository;

import se.jsquad.SystemProperty;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemPropertyRepository extends EntityManagerProducer {
    private static final Logger logger = Logger.getLogger(SystemProperty.class.getName());

    List<SystemProperty> findAllUniqueSystemProperties() {
        logger.log(Level.FINE, "findAllUniqueSystemProperties() is being called and caching the secondary cache level"
                + " with SYSTEMPROPERTY entities.");

        TypedQuery<SystemProperty> query = getEntityManager().createNamedQuery(SystemProperty
                .FIND_ALL_UNIQUE_SYSTEM_PROPERTIES, SystemProperty.class);

        return query.getResultList();
    }

    void clearSecondaryLevelCache() {
        logger.log(Level.FINE, "clearSecondaryLevelCache() method is called for clearing all of the SystemProperty " +
                "entities from the secondary level JPA cache.");
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    public void refreshSecondaryLevelCache() {
        logger.log(Level.FINE, "refreshSecondaryLevelCache() refreshing the secondary level cache for SYSTEMPROPERTY "
                + "entities.");
        clearSecondaryLevelCache();
        findAllUniqueSystemProperties();
    }
}
