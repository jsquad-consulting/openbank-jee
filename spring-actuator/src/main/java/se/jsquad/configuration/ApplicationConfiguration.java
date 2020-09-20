/*
 * Copyright 2020 JSquad AB
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

package se.jsquad.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import se.jsquad.component.database.OpenBankDatabaseConfiguration;

@Configuration
@ComponentScan(basePackages = {"se.jsquad"})
@EnableConfigurationProperties(value = {OpenBankDatabaseConfiguration.class})
public class ApplicationConfiguration {
    private OpenBankDatabaseConfiguration openBankDatabaseConfiguration;

    public ApplicationConfiguration(OpenBankDatabaseConfiguration openBankDatabaseConfiguration) {
        this.openBankDatabaseConfiguration = openBankDatabaseConfiguration;
    }

    @Bean("logger")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Logger getLogger(final InjectionPoint injectionPoint) {
        return LogManager.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    @Bean
    @Qualifier("openBankJdbcTemplate")
    public JdbcTemplate openBankJdbcTemplate() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(openBankDatabaseConfiguration.getDriverclassname());
        dataSourceBuilder.url(openBankDatabaseConfiguration.getUrl());
        dataSourceBuilder.username(openBankDatabaseConfiguration.getUsername());
        dataSourceBuilder.password(openBankDatabaseConfiguration.getPassword());

        return new JdbcTemplate(dataSourceBuilder.build(), true);
    }

    @Bean("openbankDatabaseHealthIndicator")
    public HealthIndicator openbankDatabaseHealthIndicator() {
        DataSourceHealthIndicator dataSourceHealthIndicator =
                new DataSourceHealthIndicator(openBankJdbcTemplate().getDataSource());

        dataSourceHealthIndicator.setQuery("SELECT 1");

        return dataSourceHealthIndicator;
    }
}