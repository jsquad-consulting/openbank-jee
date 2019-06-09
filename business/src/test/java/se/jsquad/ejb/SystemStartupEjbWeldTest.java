package se.jsquad.ejb;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.jsquad.entity.Client;
import se.jsquad.entity.SystemProperty;
import se.jsquad.generator.DatabaseGenerator;
import se.jsquad.producer.LoggerProducer;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.EntityManagerProducer;
import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.thread.NumberOfLocks;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(WeldJunit5Extension.class)
public class SystemStartupEjbWeldTest {
    private boolean runningThreads = true;

    @WeldSetup
    private WeldInitiator weldInitiator = WeldInitiator.from(SystemStartupEjb.class, DatabaseGenerator.class,
            ClientRepository.class, SystemPropertyRepository.class, LoggerProducer.class, EntityManagerProducer.class)
            .setPersistenceContextFactory(getPersistenceContextFactory()).build();

    @Inject
    private SystemStartupEjb systemStartupEjb;

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

        SystemProperty systemProperty = new SystemProperty();
        systemProperty.setName("VERSION");
        systemProperty.setValue("1.0.1");

        entityManager.persist(systemProperty);

        entityTransaction.commit();

        return functionPointer -> entityManager;
    }

    @Test
    public void testConcurrentRefreshTheSecondaryLevelCache() {

        List<Integer> numberOfLockList = new ArrayList<>();
        var executorService = Executors.newFixedThreadPool(1001);

        executorService.execute(() -> {
            while (runningThreads) {
                numberOfLockList.add(NumberOfLocks.getCountNumberOfLocks());
            }
        });


        for (int i = 0; i < 1000; ++i) {
            executorService.execute(() -> {
                systemStartupEjb.refreshTheSecondaryLevelCache();
            });
        }
        runningThreads = false;
        executorService.shutdown();

        while (!executorService.isTerminated()) {
        }

        assertTrue(numberOfLockList.stream().noneMatch(n -> n.intValue() < 0 || n.intValue() > 1));

    }
}
