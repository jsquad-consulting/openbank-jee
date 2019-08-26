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

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.core.api.configuration.Configuration;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class FlywayDatabaseMigration {
    private static final Logger logger = Logger.getLogger(FlywayDatabaseMigration.class.getName());

    @Resource(lookup = "java:jboss/datasources/OpenBankDS")
    private DataSource dataSource;

    public void migrateToDatabase() {
        if (dataSource == null) {
            logger.log(Level.SEVERE, "No data source found to execute the database migrations.");
            throw new EJBException("No data source found to execute the database migrations on.");
        }

        Configuration configuration = new ClassicConfiguration();

        ((ClassicConfiguration) configuration).setBaselineOnMigrate(true);
        ((ClassicConfiguration) configuration).setDataSource(dataSource);

        Flyway flyway = new Flyway(configuration);

        MigrationInfo migrationInfo = flyway.info().current();

        if (migrationInfo == null) {
            logger.log(Level.FINE, "No existing database at the actual data source.");
        } else {
            logger.log(Level.FINE, String.format("At actual data source an existing database exist with the version " +
                    "%s and description %s", migrationInfo.getVersion(), migrationInfo.getDescription()));
        }

        flyway.migrate();

        logger.log(Level.FINE, String.format("Successfully migrated to database version %s",
                flyway.info().current().getVersion()));
    }
}
