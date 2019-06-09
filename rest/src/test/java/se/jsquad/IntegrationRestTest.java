package se.jsquad;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.authorization.Authorization;
import se.jsquad.client.info.AccountApi;
import se.jsquad.client.info.AccountTransactionApi;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.ClientTypeApi;
import se.jsquad.client.info.PersonApi;
import se.jsquad.client.info.TransactionTypeApi;
import se.jsquad.client.info.TypeApi;
import se.jsquad.ejb.AccountTransactionEJB;
import se.jsquad.ejb.ClientInformationEJB;
import se.jsquad.ejb.OpenBankBusinessEJB;
import se.jsquad.entity.Client;
import se.jsquad.entity.Person;
import se.jsquad.entity.SystemProperty;
import se.jsquad.generator.DatabaseGenerator;
import se.jsquad.generator.MessageGenerator;
import se.jsquad.jms.MessageSenderSessionJMS;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.EntityManagerProducer;
import se.jsquad.validator.ClientValidator;

import javax.ejb.SessionContext;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyObject;

public class IntegrationRestTest {

    public static final String LOGGER = "logger";
    public static final String PERSON_IDENTIFICATION = "191212121212";
    public static final String JOHN_DOE_TEST_SE = "john.doe@test.se";
    public static final String CLIENT_CREATED_SUCCESSFULLY = "Client created successfully.";
    public static final String DEPOSIT_500_$ = "Deposit 500$";
    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    private OpenBankRest openBankRest;
    private ClientInformationRest clientInformationRest;
    private AccountTransferRest accountTransferRest;
    private ClientInformationEJB clientInformationEJB;
    private SessionContext sessionContext;

    @BeforeEach
    void init() throws NoSuchFieldException, IllegalAccessException, IOException, ServletException {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU", properties);
        entityManager = entityManagerFactory.createEntityManager();

        clientInformationEJB = Mockito.spy(new ClientInformationEJB());
        AccountTransactionEJB accountTransactionEJB = Mockito.spy(new AccountTransactionEJB());
        ClientAdapter clientAdapter = Mockito.spy(new ClientAdapter());
        ClientRepository clientRepository = Mockito.spy(new ClientRepository());
        clientInformationRest = Mockito.spy(new ClientInformationRest());
        accountTransferRest = Mockito.spy(new AccountTransferRest());
        ClientValidator clientValidator = Mockito.spy(new ClientValidator());
        MessageGenerator messageGenerator = Mockito.spy(new MessageGenerator());
        MessageSenderSessionJMS messageSenderSessionJMS = Mockito.mock(MessageSenderSessionJMS.class);
        sessionContext = Mockito.mock(SessionContext.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.authenticate(null)).thenReturn(true);
        Mockito.when(request.isUserInRole(anyObject())).thenReturn(true);
        Authorization authorization = Mockito.spy(new Authorization());
        Logger loggerClientInformationRest = Logger.getLogger(ClientInformationRest.class.getName());
        Logger loggerAuthorization = Logger.getLogger(Authorization.class.getName());
        Logger loggerClientInformationEJB = Logger.getLogger(ClientInformationEJB.class.getName());
        Logger loggerClientRepository = Logger.getLogger(ClientRepository.class.getName());
        Logger loggerClientAdapter = Logger.getLogger(ClientAdapter.class.getName());
        Logger loggerClientValidator = Logger.getLogger(ClientValidator.class.getName());
        Logger loggerMessageGenerator = Logger.getLogger(MessageGenerator.class.getName());
        Logger loggerOpenBankRest = Logger.getLogger(OpenBankRest.class.getName());
        Logger loggerOpenBankBusinessEJB = Logger.getLogger(OpenBankBusinessEJB.class.getName());
        Logger loggerAccountTransactionRest = Logger.getLogger(AccountTransferRest.class.getName());
        Logger loggerAccountTransactionEJB = Logger.getLogger(AccountTransactionEJB.class.getName());
        DatabaseGenerator databaseGenerator = new DatabaseGenerator();

        Mockito.when(sessionContext.isCallerInRole(RoleConstants.ADMIN)).thenReturn(true);

        Field field = ClientInformationRest.class.getDeclaredField("clientInformationEJB");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRest, clientInformationEJB);

        field = AccountTransferRest.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(accountTransferRest, loggerAccountTransactionRest);

        field = AccountTransferRest.class.getDeclaredField("authorization");
        field.setAccessible(true);

        // Set value
        field.set(accountTransferRest, authorization);

        field = AccountTransferRest.class.getDeclaredField("accountTransactionEJB");
        field.setAccessible(true);

        // Set value
        field.set(accountTransferRest, accountTransactionEJB);

        field = AccountTransactionEJB.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(accountTransactionEJB, loggerAccountTransactionEJB);

        field = AccountTransactionEJB.class.getDeclaredField("clientRepository");
        field.setAccessible(true);

        // Set value
        field.set(accountTransactionEJB, clientRepository);

        field = ClientInformationRest.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRest, loggerClientInformationRest);

        field = ClientInformationRest.class.getDeclaredField("messageGenerator");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRest, messageGenerator);

        field = MessageGenerator.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(messageGenerator, loggerMessageGenerator);

        field = ClientInformationRest.class.getDeclaredField("authorization");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRest, authorization);

        field = Authorization.class.getDeclaredField("request");
        field.setAccessible(true);

        // Set value
        field.set(authorization, request);

        field = Authorization.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(authorization, loggerAuthorization);

        field = ClientInformationEJB.class.getDeclaredField("clientAdapter");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, clientAdapter);

        field = ClientInformationEJB.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, loggerClientInformationEJB);

        field = ClientAdapter.class.getDeclaredField("sessionContext");
        field.setAccessible(true);

        // Set value
        field.set(clientAdapter, sessionContext);

        field = ClientAdapter.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(clientAdapter, loggerClientAdapter);

        field = ClientInformationEJB.class.getDeclaredField("messageSenderSessionJMS");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, messageSenderSessionJMS);

        field = ClientInformationEJB.class.getDeclaredField("clientValidator");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, clientValidator);

        field = ClientValidator.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(clientValidator, loggerClientValidator);

        field = ClientInformationEJB.class.getDeclaredField("clientRepository");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, clientRepository);

        field = EntityManagerProducer.class.getDeclaredField("entityManager");
        field.setAccessible(true);

        // Set value
        field.set(clientRepository, entityManager);

        field = ClientRepository.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(clientRepository, loggerClientRepository);

        openBankRest = Mockito.spy(new OpenBankRest());
        OpenBankBusinessEJB openBankBusinessEJB = Mockito.spy(new OpenBankBusinessEJB());

        field = OpenBankRest.class.getDeclaredField("openBankBusinessEJB");
        field.setAccessible(true);

        // Set value
        field.set(openBankRest, openBankBusinessEJB);

        field = OpenBankBusinessEJB.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(openBankBusinessEJB, loggerOpenBankBusinessEJB);

        field = OpenBankRest.class.getDeclaredField(LOGGER);
        field.setAccessible(true);

        // Set value
        field.set(openBankRest, loggerOpenBankRest);

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        Set<Client> clientSet = databaseGenerator.populateDatabase();

        for (Client client : clientSet) {
            clientRepository.createClient(client);
        }

        SystemProperty systemProperty = new SystemProperty();
        systemProperty.setName("VERSION");
        systemProperty.setValue("1.0.1");

        entityManager.persist(systemProperty);

        entityTransaction.commit();

        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void tearDownAfterUnitTest() {
        entityManager.close();
        entityManagerFactory.close();
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
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        Response response = accountTransferRest.transferValueFromAccountToAccount(value, fromAccountNumber,
                toAccountNumber);

        tx.commit();

        ClientApi fromClientApi = (ClientApi) clientInformationRest.getClientInformtion(
                "191212121220").getEntity();
        ClientApi toClientApi = (ClientApi) clientInformationRest.getClientInformtion("191212121221").getEntity();


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
    public void testGetClientAliceDoeWithAccount() {
        // Given
        String personIdentification = "191212121213";

        // When
        Response response = clientInformationRest.getClientInformtion(personIdentification);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

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
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        Response response = clientInformationRest.createClientInformation(clientApi);

        tx.commit();

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals("Client created successfully.", response.getEntity());

        // When
        response = clientInformationRest.getClientInformtion(personIdentification);

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
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        Response response = clientInformationRest.createClientInformation(clientApi);
        entityTransaction.commit();

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals("Client created successfully.", response.getEntity());

        // When
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        response = clientInformationRest.getClientInformtion(personIdentification);
        tx.commit();

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
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        Response response = clientInformationRest.createClientInformation(clientApi);

        tx.commit();

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals(CLIENT_CREATED_SUCCESSFULLY, response.getEntity());

        // When
        response = clientInformationRest.getClientInformtion(personIdentification);

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
        Response response = clientInformationRest.getClientInformtion(personIdentification);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals(JOHN_DOE_TEST_SE, clientApi.getPerson().getMail());

        assertEquals(0, clientApi.getAccountList().size());
    }

    @Test
    public void testGetClient() {
        // Given
        String personIdentification = PERSON_IDENTIFICATION;

        // When
        Response response = clientInformationRest.getClientInformtion(personIdentification);

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
        Response response = clientInformationRest.getClientInformtion(personIdentification);

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
        Response response = clientInformationRest.getClientInformtion(personIdentification);

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
