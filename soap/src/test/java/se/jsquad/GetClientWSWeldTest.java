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

package se.jsquad;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
import se.jsquad.entity.Client;
import se.jsquad.entity.SystemProperty;
import se.jsquad.generator.DatabaseGenerator;
import se.jsquad.getclientservice.GetClientRequest;
import se.jsquad.getclientservice.GetClientResponse;
import se.jsquad.getclientservice.StatusType;
import se.jsquad.getclientservice.TransactionType;
import se.jsquad.getclientservice.Type;
import se.jsquad.producer.LoggerProducer;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.EntityManagerProducer;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.xml.ws.WebServiceContext;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(WeldJunit5Extension.class)
@Execution(ExecutionMode.SAME_THREAD)
public class GetClientWSWeldTest {

    @WeldSetup
    private WeldInitiator weldInitiator = WeldInitiator.from(GetClientWS.class, GetClientWsBusiness.class,
            ClientRepository.class, LoggerProducer.class,
            EntityManagerProducer.class).setPersistenceContextFactory(getPersistenceContextFactory()).build();

    @Inject
    private GetClientWS getClientWS;

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
    public void testGetClientWs() throws NoSuchFieldException, IllegalAccessException {
        // Given
        GetClientWsBusiness getClientWsBusiness = weldInitiator.select(GetClientWsBusiness.class).get();
        WebServiceContext webServiceContext = Mockito.mock(WebServiceContext.class);
        Mockito.when(webServiceContext.isUserInRole(RoleConstants.ADMIN)).thenReturn(true);
        Mockito.when(webServiceContext.isUserInRole(RoleConstants.CUSTOMER)).thenReturn(false);

        Field field = GetClientWsBusiness.class.getDeclaredField("webServiceContext");
        field.setAccessible(true);

        // Set value
        field.set(getClientWsBusiness, webServiceContext);

        field = GetClientWS.class.getDeclaredField("getClientWsBusiness");
        field.setAccessible(true);

        // Set value
        field.set(getClientWS, getClientWsBusiness);

        String personIdentification = "191212121212";
        GetClientRequest clientRequest = new GetClientRequest();
        clientRequest.setPersonIdentification(personIdentification);

        // When
        GetClientResponse getClientResponse = getClientWS.getClient(clientRequest);

        // Then
        assertEquals(StatusType.OK, getClientResponse.getStatus());
        assertEquals("Client found.", getClientResponse.getMessage());

        assertEquals("John", getClientResponse.getClient().getPerson().getFirstName());
        assertEquals("Doe", getClientResponse.getClient().getPerson().getLastName());
        assertEquals("john.doe@test.se", getClientResponse.getClient().getPerson().getMail());
        assertEquals(personIdentification, getClientResponse.getClient().getPerson().getPersonIdentification());

        assertEquals(1, getClientResponse.getClient().getAccountList().size());
        assertEquals(1, getClientResponse.getClient().getAccountList().get(0).getAccountTransactionList()
                .size());

        assertEquals(500, getClientResponse.getClient().getAccountList().get(0).getBalance());
        assertEquals(TransactionType.DEPOSIT,
                getClientResponse.getClient().getAccountList().get(0).getAccountTransactionList().get(0)
                        .getTransactionType());
        assertEquals("500$ in deposit", getClientResponse.getClient().getAccountList().get(0)
                .getAccountTransactionList().get(0).getMessage());

        assertEquals(Type.REGULAR, getClientResponse.getClient().getClientType().getType());
        assertEquals(500, getClientResponse.getClient().getClientType().getRating());
    }

    @Test
    public void testGetClientWsAddressType() throws NoSuchFieldException, IllegalAccessException {
        // Given
        GetClientWsBusiness getClientWsBusiness = weldInitiator.select(GetClientWsBusiness.class).get();
        WebServiceContext webServiceContext = Mockito.mock(WebServiceContext.class);
        Mockito.when(webServiceContext.isUserInRole(RoleConstants.ADMIN)).thenReturn(true);
        Mockito.when(webServiceContext.isUserInRole(RoleConstants.CUSTOMER)).thenReturn(false);

        Field field = GetClientWsBusiness.class.getDeclaredField("webServiceContext");
        field.setAccessible(true);

        // Set value
        field.set(getClientWsBusiness, webServiceContext);

        field = GetClientWS.class.getDeclaredField("getClientWsBusiness");
        field.setAccessible(true);

        // Set value
        field.set(getClientWS, getClientWsBusiness);

        String personIdentification = "191212121213";
        GetClientRequest clientRequest = new GetClientRequest();
        clientRequest.setPersonIdentification(personIdentification);

        // When
        GetClientResponse getClientResponse = getClientWS.getClient(clientRequest);

        // Then
        assertEquals(StatusType.OK, getClientResponse.getStatus());
        assertEquals("Client found.", getClientResponse.getMessage());

        assertEquals("Alice", getClientResponse.getClient().getPerson().getFirstName());
        assertEquals("Doe", getClientResponse.getClient().getPerson().getLastName());

        assertEquals(1, getClientResponse.getClient().getPerson().getAddressList().size());

        assertEquals("Stockholm county", getClientResponse.getClient().getPerson().getAddressList().get(0).getMunicipality());
        assertEquals("Sweden", getClientResponse.getClient().getPerson().getAddressList().get(0).getCountry());
        assertEquals(17211, getClientResponse.getClient().getPerson().getAddressList().get(0).getPostalCode());
        assertEquals("Vallhallavägen", getClientResponse.getClient().getPerson().getAddressList().get(0).getStreet());
        assertEquals(12, getClientResponse.getClient().getPerson().getAddressList().get(0).getStreetNumber());
    }
}
