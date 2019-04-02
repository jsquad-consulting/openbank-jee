package se.jsquad.ejb;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;
import se.jsquad.generator.DatabaseGenerator;
import se.jsquad.producer.LoggerProducer;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.EntityManagerProducer;
import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.thread.NumberOfLocks;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SystemStartupEjbWeldTest {
    private boolean runningThreads = true;

    @Test
    public void testConcurrentRefreshTheSecondaryLevelCache() throws NoSuchFieldException, IllegalAccessException {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU", properties);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Weld weld = new Weld();
        weld.disableDiscovery();

        WeldContainer weldContainer = weld.beanClasses(SystemStartupEjb.class, SystemPropertyRepository.class,
                ClientRepository.class, DatabaseGenerator.class)
                .addBeanClass(LoggerProducer.class).initialize();

        SystemStartupEjb systemStartupEjb = weldContainer.select(SystemStartupEjb.class).get();
        ClientRepository clientRepository = weldContainer.select(ClientRepository.class).get();

        Field field = SystemStartupEjb.class.getDeclaredField("clientRepository");
        field.setAccessible(true);

        field.set(systemStartupEjb, clientRepository);

        field = EntityManagerProducer.class.getDeclaredField("entityManager");
        field.setAccessible(true);

        field.set(clientRepository, entityManager);

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

        weldContainer.close();
    }
}
