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

package se.jsquad.rest;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.configuration.ApplicationConfiguration;
import se.jsquad.ejb.ClientInformationEJB;
import se.jsquad.ejb.ClientInformationEjbLocal;
import se.jsquad.ejb.SystemStartupEjb;
import se.jsquad.entity.Client;
import se.jsquad.entity.SystemProperty;
import se.jsquad.generator.DatabaseGenerator;
import se.jsquad.generator.MessageGenerator;
import se.jsquad.jms.MessageSenderSessionJMS;
import se.jsquad.producer.LoggerProducer;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.EntityManagerProducer;
import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.validator.ClientValidator;

import javax.ejb.SessionContext;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.jms.JMSContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.TransactionScoped;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@EnableWebMvc
@ContextConfiguration(classes = ApplicationConfiguration.class, loader = AnnotationConfigWebContextLoader.class)
public class GetClientInformationRestControllerTest {
    @WeldSetup
    private WeldInitiator weldInitiator = WeldInitiator.from(ClientInformationEJB.class,
            ClientValidator.class,
            ClientAdapter.class,
            SystemStartupEjb.class,
            DatabaseGenerator.class,
            ClientRepository.class,
            SystemPropertyRepository.class,
            LoggerProducer.class,
            Mockito.mock(JMSContext.class).getClass(),
            TestClassProducer.class,
            MessageGenerator.class,
            EntityManagerProducer.class,
            SessionContext.class)
            .activate(TransactionScoped.class)
            .setPersistenceContextFactory(getPersistenceContextFactory()).build();

    private static Function<InjectionPoint, Object> getPersistenceContextFactory() {
        DatabaseGenerator databaseGenerator = new DatabaseGenerator();

        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence_test.xml");

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

    private static class TestClassProducer {
        @ApplicationScoped
        @Produces
        MessageSenderSessionJMS produceMessageSenderSessionJMS() {
            return Mockito.mock(MessageSenderSessionJMS.class);
        }
    }

    @BeforeEach
    void init() throws NoSuchFieldException, IllegalAccessException {
        SessionContext sessionContext = Mockito.mock(SessionContext.class);

        ClientAdapter clientAdapter =
                weldInitiator.select(ClientAdapter.class).get();

        Field field = ClientAdapter.class.getDeclaredField("sessionContext");
        field.setAccessible(true);

        field.set(clientAdapter, sessionContext);

        ClientInformationEjbLocal clientInformationEjbLocal =
                weldInitiator.select(ClientInformationEjbLocal.class).get();

        field = ClientInformationEJB.class.getDeclaredField("clientAdapter");
        field.setAccessible(true);

        field.set(clientInformationEjbLocal, clientAdapter);

        field = GetClientInformationRestController.class.getDeclaredField("clientInformationEjbLocal");
        field.setAccessible(true);

        field.set(getClientInformationRestController, clientInformationEjbLocal);


        MockitoAnnotations.initMocks(this);
    }

    @Autowired
    private GetClientInformationRestController getClientInformationRestController;


    @Test
    public void testGetClientInformation() {
        // Given
        String personalIdentificationNumber = "191212121212";

        // When
        ResponseEntity responseEntity = getClientInformationRestController
                .getClientInformation(personalIdentificationNumber);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}