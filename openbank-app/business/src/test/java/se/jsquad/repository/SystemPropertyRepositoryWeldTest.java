package se.jsquad.repository;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import se.jsquad.Client;
import se.jsquad.SystemProperty;
import se.jsquad.generator.DatabaseGenerator;
import se.jsquad.producer.LoggerProducer;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.TransactionScoped;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(WeldJunit5Extension.class)
public class SystemPropertyRepositoryWeldTest {
    @WeldSetup
    private WeldInitiator weldInitiator =
            WeldInitiator.from(SystemPropertyRepository.class, LoggerProducer.class).activate(TransactionScoped.class)
                    .setPersistenceContextFactory(getPersistenceContextFactory()).build();

    @Inject
    private SystemPropertyRepository systemPropertyRepository;

    private static SystemProperty systemProperty;


    private static Function<InjectionPoint, Object> getPersistenceContextFactory() {
        DatabaseGenerator databaseGenerator = new DatabaseGenerator();

        Properties properties = new Properties();

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU",
                properties);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        for (Client client : databaseGenerator.populateDatabase()) {
            entityManager.persist(client);
        }

        systemProperty = new SystemProperty();
        systemProperty.setName("VERSION");
        systemProperty.setValue("1.0.1");

        entityManager.persist(systemProperty);

        entityTransaction.commit();


        return functionPointer -> entityManager;
    }

    @Test
    @Order(1)
    public void testFindAllUniqueSystemPropertiesAndSecondaryCacheLevel() {
        // When
        List<SystemProperty> systemPropertyList = systemPropertyRepository.findAllUniqueSystemProperties();


        // Then
        SystemProperty systemProperty = systemPropertyList.get(0);

        assertEquals("VERSION", systemPropertyList.get(0).getName());
        assertEquals("1.0.1", systemPropertyList.get(0).getValue());
        assertEquals(true,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
    }

    @Test
    @Order(2)
    public void testSecondaryCacheLevelAfterClearAndRefresh() {
        // When
        systemPropertyRepository.clearSecondaryLevelCache();

        // Then
        /*assertEquals(false,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));*/
    }

    @Test
    @Order(3)
    public void testSecondaryCacheLevelAfterClearAndRefreshSecondaryLevelCache() {
        // When
        systemPropertyRepository.findAllUniqueSystemProperties();

        // Then
       /*assertEquals(true,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));*/
    }
}
