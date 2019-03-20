package se.jsquad.ejb;

import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.thread.NumberOfLocks;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class SystemStartupEjb {
    private static final Logger logger = Logger.getLogger(SystemStartupEjb.class.getName());
    private static final Lock lock = new ReentrantLock();

    @Inject
    private SystemPropertyRepository systemPropertyRepository;

    public SystemStartupEjb() {
        logger.log(Level.FINE, "Starting up the application and caching the system properties to the secondary level "
                + "cache.");
    }

    @Schedule(minute = "*/5", hour = "*")
    public void refreshTheSecondaryLevelCache() {
        logger.log(Level.FINE, "refreshTheSecondaryLevelCache() method is being called to refresh the secondary level"
                + " cache for SystemProperty entities.");
        lock.lock();
        NumberOfLocks.increaseNumberOfLocks();

        try {
            systemPropertyRepository.refreshSecondaryLevelCache();
        } finally {
            NumberOfLocks.decreaseNumberOfLocks();
            lock.unlock();
        }
    }
}
