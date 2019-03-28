package se.jsquad.ejb;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import se.jsquad.producer.LoggerProducer;
import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.thread.NumberOfLocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
public class SystemStartupEjbWeldTest {
    private boolean runningThreads = true;

    @Test
    public void testConcurrentRefreshTheSecondaryLevelCache() {
        Weld weld = new Weld();
        weld.disableDiscovery();

        WeldContainer weldContainer = weld.beanClasses(SystemStartupEjb.class, SystemPropertyRepository.class)
                .addBeanClass(LoggerProducer.class).initialize();

        SystemStartupEjb systemStartupEjb = weldContainer.select(SystemStartupEjb.class).get();

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
