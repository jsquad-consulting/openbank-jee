/*
 * Copyright 2019 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.ejb;

import se.jsquad.entity.Client;
import se.jsquad.entity.SystemProperty;
import se.jsquad.generator.DatabaseGenerator;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.thread.NumberOfLocks;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

@Singleton
@Startup
public class SystemStartupEjb {
    private static final Logger logger = Logger.getLogger(SystemStartupEjb.class.getName());
    private static final Lock lock = new ReentrantLock();

    @Inject
    private SystemPropertyRepository systemPropertyRepository;

    @Inject
    private ClientRepository clientRepository;

    @Inject
    private DatabaseGenerator databaseGenerator;

    public SystemStartupEjb() {
        // No SONAR
    }

    @Schedule(minute = "*/5", hour = "*")
    public void refreshTheSecondaryLevelCache() {
        lock.lock();
        NumberOfLocks.increaseNumberOfLocks();

        try {
            systemPropertyRepository.refreshSecondaryLevelCache();
        } finally {
            NumberOfLocks.decreaseNumberOfLocks();
            lock.unlock();
        }
    }

    @PostConstruct
    public void populateDatabase() {
        if (clientRepository != null && clientRepository.getEntityManager() != null && clientRepository
                .getClientByPersonIdentification("191212121212") == null) {
            for (Client client : databaseGenerator.populateDatabase()) {
                clientRepository.createClient(client);
            }

            SystemProperty systemProperty = new SystemProperty();
            systemProperty.setName("VERSION");
            systemProperty.setValue("1.0.1");

            systemPropertyRepository.getEntityManager().persist(systemProperty);
        }
    }
}
