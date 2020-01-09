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

package se.jsquad;

import javax.ejb.SessionContext;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.transaction.TransactionScoped;
import javax.ws.rs.core.Response;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.api.client.info.AccountApi;
import se.jsquad.api.client.info.AccountTransactionApi;
import se.jsquad.api.client.info.ClientApi;
import se.jsquad.api.client.info.ClientTypeApi;
import se.jsquad.api.client.info.PersonApi;
import se.jsquad.api.client.info.TransactionTypeApi;
import se.jsquad.api.client.info.TypeApi;
import se.jsquad.authorization.Authorization;
import se.jsquad.batch.SlowMockBatch;
import se.jsquad.ejb.AccountTransactionEJB;
import se.jsquad.ejb.AccountTransactionEjbLocal;
import se.jsquad.ejb.ClientInformationEJB;
import se.jsquad.ejb.OpenBankBusinessEJB;
import se.jsquad.ejb.SystemStartupEjb;
import se.jsquad.entity.Client;
import se.jsquad.entity.Person;
import se.jsquad.entity.SystemProperty;
import se.jsquad.generator.DatabaseGenerator;
import se.jsquad.generator.MessageGenerator;
import se.jsquad.jms.MessageSenderSessionJMS;
import se.jsquad.producer.LoggerProducer;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.EntityManagerProducer;
import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.validator.ClientValidator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(WeldJunit5Extension.class)
public class IntegrationRestTest {
    public static final String PERSON_IDENTIFICATION = "191212121212";
    public static final String JOHN_DOE_TEST_SE = "john.doe@test.se";
    public static final String CLIENT_CREATED_SUCCESSFULLY = "Client created successfully.";
    public static final String DEPOSIT_500_$ = "Deposit 500$";

    @WeldSetup
    private WeldInitiator weldInitiator = WeldInitiator.from(ClientInformationRest.class, AccountTransferRest.class,
            OpenBankRest.class, ClientValidator.class, ClientAdapter.class,
            SystemStartupEjb.class,
            ClientInformationEJB.class,
            AccountTransactionEJB.class,
            OpenBankBusinessEJB.class,
            SlowMockBatch.class,
            DatabaseGenerator.class,
            ClientRepository.class, SystemPropertyRepository.class,
            LoggerProducer.class,
            Mockito.mock(JMSContext.class).getClass(),
            TestClassProducer.class,
            MessageGenerator.class, EntityManagerProducer.class)
            .addBeans(MockBean.of(Mockito.mock(Authorization.class), Authorization.class))
            .addBeans(MockBean.of(Mockito.mock(SessionContext.class), SessionContext.class))
            .activate(TransactionScoped.class)
            .setPersistenceContextFactory(getPersistenceContextFactory()).build();


    @Inject
    private Authorization authorization;

    @Inject
    private ClientInformationRest clientInformationRest;

    @Inject
    private AccountTransferRest accountTransferRest;

    @Inject
    private AccountTransactionEjbLocal accountTransactionEjbLocal;

    @Inject
    private OpenBankRest openBankRest;

    @Inject
    private ClientInformationEJB clientInformationEJB;

    @Inject
    private OpenBankBusinessEJB openBankBusinessEJB;

    @Inject
    private MessageSenderSessionJMS messageSenderSessionJMS;

    @Inject
    private SessionContext sessionContext;

    @Inject
    private ClientAdapter clientAdapter;

    private static EntityManager entityManager;

    @BeforeEach
    public void init() throws IOException, ServletException, IllegalAccessException, NoSuchFieldException {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        Mockito.when(authorization.isAuthorized()).thenReturn(true);
        Mockito.when(authorization.isUserInRole(anyString())).thenReturn(true);
        Mockito.doReturn(true).when(sessionContext).isCallerInRole(anyString());

        Field field = ClientInformationRest.class.getDeclaredField("clientInformationEjbLocal");
        field.setAccessible(true);
        field.set(clientInformationRest, clientInformationEJB);

        field = ClientInformationEJB.class.getDeclaredField("messageSenderSessionJMS");
        field.setAccessible(true);
        field.set(clientInformationEJB, messageSenderSessionJMS);

        field = ClientAdapter.class.getDeclaredField("sessionContext");
        field.setAccessible(true);
        field.set(clientAdapter, sessionContext);

        field = ClientInformationEJB.class.getDeclaredField("clientAdapter");
        field.setAccessible(true);
        field.set(clientInformationEJB, clientAdapter);

        field = AccountTransferRest.class.getDeclaredField("accountTransactionEjbLocal");
        field.setAccessible(true);
        field.set(accountTransferRest, accountTransactionEjbLocal);

        field = OpenBankRest.class.getDeclaredField("openBankBusinessEJB");
        field.setAccessible(true);
        field.set(openBankRest, openBankBusinessEJB);
    }

    @AfterEach
    public void tearDownAfterUnitTest() {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.commit();

        entityManager.close();
    }

    private static class TestClassProducer {
        @ApplicationScoped
        @Produces
        MessageSenderSessionJMS produceMessageSenderSessionJMS() {
            return Mockito.mock(MessageSenderSessionJMS.class);
        }
    }

    private static Function<InjectionPoint, Object> getPersistenceContextFactory() {
        DatabaseGenerator databaseGenerator = new DatabaseGenerator();

        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence_test.xml");

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU",
                properties);
        entityManager = entityManagerFactory.createEntityManager();

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
    public void testCreateClientWithInvalidFormat() {
        // Given
        String personIdentification = "19111111-111111";
        String firstName = "Mr.1";
        String lastName = "Andersson9";
        String mail = "mr.andersson@matrix";

        ClientApi clientApi = new ClientApi();
        clientApi.setPerson(new PersonApi());
        clientApi.getAccountList().clear();

        clientApi.getPerson().setFirstName(firstName);
        clientApi.getPerson().setLastName(lastName);
        clientApi.getPerson().setMail(mail);
        clientApi.getPerson().setPersonIdentification(personIdentification);

        clientApi.setClientType(new ClientTypeApi());
        clientApi.getClientType().setType(TypeApi.REGULAR);
        clientApi.getClientType().setRating(Long.valueOf(500));

        // When
        Response response = clientInformationRest.createClientInformation(clientApi);
        String responseMessage = (String) response.getEntity();

        // Then
        assertEquals(Response.Status.BAD_REQUEST, Response.Status.fromStatusCode(response.getStatus()));

        assertTrue(responseMessage.contains("personIdentification: must match \"\\d{12}\""));
        assertTrue(responseMessage.contains("firstName: must match \"^\\D*$\""));
        assertTrue(responseMessage.contains("lastName: must match \"^\\D*$\""));
        assertTrue(responseMessage.contains("mail: must match \"" + Person.MAIL_REGEXP + "\""));

    }

    @Test
    public void testTransferValueFromAccountToAccount() {
        // Given
        long value = 250;
        String fromAccountNumber = "1050";
        String toAccountNumber = "1051";

        // When
        Response response = accountTransferRest.transferValueFromAccountToAccount(value, fromAccountNumber,
                toAccountNumber);

        ClientApi fromClientApi = (ClientApi) clientInformationRest.getClientInformation(
                "191212121220").getEntity();
        ClientApi toClientApi = (ClientApi) clientInformationRest.getClientInformation("191212121221").getEntity();


        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals("Transaction successful.", response.getEntity());

        assertEquals(2, fromClientApi.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals(2, toClientApi.getAccountList().get(0).getAccountTransactionList().size());

        assertTrue(fromClientApi.getAccountList().get(0).getAccountTransactionList().stream().anyMatch(ts -> ("250 " +
                "has been withdrawn from the account.").equals(ts.getMessage()) && TransactionTypeApi.WITHDRAWAL.equals(
                ts.getTransactionType())));

        assertTrue(toClientApi.getAccountList().get(0).getAccountTransactionList().stream().anyMatch(ts -> ("250 has" +
                " been deposited to the account.").equals(ts.getMessage()) && TransactionTypeApi.DEPOSIT.equals(
                ts.getTransactionType())));

        assertEquals(250.0, fromClientApi.getAccountList().get(0).getBalance());
        assertEquals(1250.0, toClientApi.getAccountList().get(0).getBalance());
    }

    @Test
    public void testGetClientAliceDoeWithAccountAndAdressList() {
        // Given
        String personIdentification = "191212121213";

        // When
        Response response = clientInformationRest.getClientInformation(personIdentification);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

        assertEquals(1, clientApi.getPerson().getAddressList().size());

        assertEquals("Stockholm county", clientApi.getPerson().getAddressList().get(0).getMunicipality());
        assertEquals("Sweden", clientApi.getPerson().getAddressList().get(0).getCountry());
        assertEquals(17211, clientApi.getPerson().getAddressList().get(0).getPostalCode());
        assertEquals("Vallhallavägen", clientApi.getPerson().getAddressList().get(0).getStreet());
        assertEquals(12, clientApi.getPerson().getAddressList().get(0).getStreetNumber());

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("Alice", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("alice.doe@test.se", clientApi.getPerson().getMail());

        assertEquals(1, clientApi.getAccountList().size());
        assertEquals(1000.0, clientApi.getAccountList().get(0).getBalance());

        assertEquals(1, clientApi.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals("WITHDRAWAL", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getTransactionType().name());
        assertEquals("500$ in withdrawal", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getMessage());

        assertEquals(TypeApi.PREMIUM, clientApi.getClientType().getType());
        assertEquals(1000, clientApi.getClientType().getPremiumRating());
        assertEquals("Special offer you can not refuse.", clientApi.getClientType().getSpecialOffers());
    }

    @Test
    public void testCreateClientWithoutAccount() {
        // Given
        String personIdentification = "191313131314";
        String firstName = "Mr.";
        String lastName = "Andersson";
        String mail = "mr.andersson@matrix.com";

        ClientApi clientApi = new ClientApi();
        clientApi.setPerson(new PersonApi());
        clientApi.getAccountList().clear();

        clientApi.getPerson().setFirstName(firstName);
        clientApi.getPerson().setLastName(lastName);
        clientApi.getPerson().setMail(mail);
        clientApi.getPerson().setPersonIdentification(personIdentification);

        clientApi.setClientType(new ClientTypeApi());
        clientApi.getClientType().setType(TypeApi.REGULAR);
        clientApi.getClientType().setRating(500);

        // When
        Response response = clientInformationRest.createClientInformation(clientApi);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals("Client created successfully.", response.getEntity());

        // When
        response = clientInformationRest.getClientInformation(personIdentification);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        clientApi = (ClientApi) response.getEntity();

        assertEquals(TypeApi.REGULAR, clientApi.getClientType().getType());
        assertEquals(500, clientApi.getClientType().getRating());
    }

    @Test
    public void testCreateClientWithAccount() {
        // Given
        String personIdentification = "191313131313";
        String firstName = "Don";
        String lastName = "Vito";
        String mail = "don.vito@noemail.it";

        ClientApi clientApi = new ClientApi();
        clientApi.setPerson(new PersonApi());
        clientApi.getAccountList().clear();

        clientApi.getPerson().setFirstName(firstName);
        clientApi.getPerson().setLastName(lastName);
        clientApi.getPerson().setMail(mail);
        clientApi.getPerson().setPersonIdentification(personIdentification);

        // Given
        clientApi.setClientType(new ClientTypeApi());
        clientApi.getClientType().setType(TypeApi.PREMIUM);
        clientApi.getClientType().setPremiumRating(9000);
        clientApi.getClientType().setSpecialOffers("Don Vito has an offer we can't refuse");

        AccountApi accountApi = new AccountApi();

        accountApi.setBalance(1050);
        accountApi.getAccountTransactionList().clear();

        AccountTransactionApi accountTransactionApi = new AccountTransactionApi();
        accountTransactionApi.setTransactionType(TransactionTypeApi.DEPOSIT);
        accountTransactionApi.setMessage(DEPOSIT_500_$);
        accountApi.getAccountTransactionList().add(accountTransactionApi);

        clientApi.getAccountList().add(accountApi);

        // When
        Response response = clientInformationRest.createClientInformation(clientApi);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals("Client created successfully.", response.getEntity());

        // When
        response = clientInformationRest.getClientInformation(personIdentification);

        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApiResult = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApiResult.getPerson().getPersonIdentification());
        assertEquals(firstName, clientApiResult.getPerson().getFirstName());
        assertEquals(lastName, clientApiResult.getPerson().getLastName());
        assertEquals(mail, clientApiResult.getPerson().getMail());
        assertEquals(1, clientApiResult.getAccountList().size());
        assertEquals(1, clientApiResult.getAccountList().get(0).getAccountTransactionList().size());

        assertEquals(TransactionTypeApi.DEPOSIT,
                clientApiResult.getAccountList().get(0).getAccountTransactionList().get(0).getTransactionType());
        assertEquals("Deposit 500$",
                clientApiResult.getAccountList().get(0).getAccountTransactionList().get(0).getMessage());

        assertEquals(TypeApi.PREMIUM, clientApi.getClientType().getType());
        assertEquals(9000, clientApi.getClientType().getPremiumRating());
        assertEquals("Don Vito has an offer we can't refuse", clientApi.getClientType().getSpecialOffers());
    }

    @Test
    public void testCreateForeignClientWithAccount() {
        // Given
        String personIdentification = "191313131315";
        String firstName = "Mr.";
        String lastName = "Almighty";
        String mail = "mr.almighty@usa.com";

        ClientApi clientApi = new ClientApi();
        clientApi.setPerson(new PersonApi());
        clientApi.getAccountList().clear();

        clientApi.getPerson().setFirstName(firstName);
        clientApi.getPerson().setLastName(lastName);
        clientApi.getPerson().setMail(mail);
        clientApi.getPerson().setPersonIdentification(personIdentification);

        // Given
        clientApi.setClientType(new ClientTypeApi());
        clientApi.getClientType().setType(TypeApi.FOREIGN);
        clientApi.getClientType().setCountry("United States of America");

        AccountApi accountApi = new AccountApi();

        accountApi.setBalance(1050);
        accountApi.getAccountTransactionList().clear();

        AccountTransactionApi accountTransactionApi = new AccountTransactionApi();
        accountTransactionApi.setTransactionType(TransactionTypeApi.DEPOSIT);
        accountTransactionApi.setMessage(DEPOSIT_500_$);
        accountApi.getAccountTransactionList().add(accountTransactionApi);

        clientApi.getAccountList().add(accountApi);

        // When
        Response response = clientInformationRest.createClientInformation(clientApi);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals(CLIENT_CREATED_SUCCESSFULLY, response.getEntity());

        // When
        response = clientInformationRest.getClientInformation(personIdentification);

        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApiResult = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApiResult.getPerson().getPersonIdentification());
        assertEquals(firstName, clientApiResult.getPerson().getFirstName());
        assertEquals(lastName, clientApiResult.getPerson().getLastName());
        assertEquals(mail, clientApiResult.getPerson().getMail());
        assertEquals(1, clientApiResult.getAccountList().size());

        assertEquals(TypeApi.FOREIGN, clientApi.getClientType().getType());
        assertEquals("United States of America", clientApi.getClientType().getCountry());
    }

    @Test
    public void testGetClientWithoutAccountInformationAsCustomer() {
        // Given
        String personIdentification = PERSON_IDENTIFICATION;

        // When
        Mockito.when(sessionContext.isCallerInRole(RoleConstants.ADMIN)).thenReturn(false);
        Response response = clientInformationRest.getClientInformation(personIdentification);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals(JOHN_DOE_TEST_SE, clientApi.getPerson().getMail());

        assertEquals(0, clientApi.getAccountList().size());
        assertEquals(true, clientApi.getPerson().getAddressList().isEmpty());
    }

    @Test
    public void testGetClient() {
        // Given
        String personIdentification = PERSON_IDENTIFICATION;

        // When
        Response response = clientInformationRest.getClientInformation(personIdentification);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals(JOHN_DOE_TEST_SE, clientApi.getPerson().getMail());

        assertEquals(1, clientApi.getAccountList().size());
        assertEquals(500.0, clientApi.getAccountList().get(0).getBalance());

        assertEquals(1, clientApi.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals("DEPOSIT", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getTransactionType().name());
        assertEquals("500$ in deposit", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getMessage());

        assertEquals(TypeApi.REGULAR, clientApi.getClientType().getType());
        assertEquals(500, clientApi.getClientType().getRating());
    }

    @Test
    public void testGetClientException() throws JMSException {
        // Given
        String personIdentification = PERSON_IDENTIFICATION;

        // When
        Mockito.when(clientInformationEJB.getClient(personIdentification)).thenThrow(new RuntimeException(
                "Severe system failure has occured!"));
        Response response = clientInformationRest.getClientInformation(personIdentification);

        // Then
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals("Severe system failure has occured!", (String) response.getEntity());
    }

    @Test
    public void testGetClientInformationWithAccountIsAdminRole() {
        // Given
        String personIdentification = PERSON_IDENTIFICATION;

        // When
        Mockito.when(sessionContext.isCallerInRole(RoleConstants.ADMIN)).thenReturn(true);
        Response response = clientInformationRest.getClientInformation(personIdentification);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals(JOHN_DOE_TEST_SE, clientApi.getPerson().getMail());

        assertEquals(1, clientApi.getAccountList().size());
        assertEquals(500.0, clientApi.getAccountList().get(0).getBalance());

        assertEquals(1, clientApi.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals("DEPOSIT", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getTransactionType().name());
        assertEquals("500$ in deposit", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getMessage());
    }

    @Test
    public void testGetHelloWorld() {
        // When
        Response response = openBankRest.getHelloWorld();

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        String message = (String) response.getEntity();
        assertEquals("Hello world!", message);
    }
}
