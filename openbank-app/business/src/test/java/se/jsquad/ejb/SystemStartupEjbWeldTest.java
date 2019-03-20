package se.jsquad.ejb;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.thread.NumberOfLocks;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.Persistence;
import javax.transaction.TransactionScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(WeldJunit5Extension.class)
@Execution(ExecutionMode.SAME_THREAD)
class SystemStartupEjbWeldTest {
    @WeldSetup
    WeldInitiator weld =
            WeldInitiator.from(SystemStartupEjb.class, SystemPropertyRepository.class).activate(TransactionScoped.class)
            .setPersistenceContextFactory(getEntityManager()).build();

    private static Function<InjectionPoint, Object> getEntityManager() {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        return injectionPoint -> Persistence.createEntityManagerFactory("openBankPU", properties)
                .createEntityManager();
    }

    @Inject
    private SystemStartupEjb systemStartupEjb;

    private boolean runningThreads = true;

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
