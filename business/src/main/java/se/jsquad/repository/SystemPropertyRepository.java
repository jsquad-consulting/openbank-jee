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
