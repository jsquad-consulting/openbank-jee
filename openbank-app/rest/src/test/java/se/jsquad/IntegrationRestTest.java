package se.jsquad;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.client.info.AccountApi;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.PersonApi;
import se.jsquad.ejb.ClientInformationEJB;
import se.jsquad.ejb.OpenBankBusinessEJB;
import se.jsquad.generator.MessageGenerator;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.EntityManagerProducer;
import se.jsquad.validator.ClientValidator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationRestTest {

    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    private OpenBankRest openBankRest;
    private ClientInformationRest clientInformationRest;

    @BeforeEach
    void init() throws NoSuchFieldException, IllegalAccessException {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU", properties);
        entityManager = entityManagerFactory.createEntityManager();

        ClientInformationEJB clientInformationEJB = Mockito.spy(new ClientInformationEJB());
        ClientAdapter clientAdapter = Mockito.spy(new ClientAdapter());
        ClientRepository clientRepository = Mockito.spy(new ClientRepository());
        clientInformationRest = Mockito.spy(new ClientInformationRest());
        ClientValidator clientValidator = Mockito.spy(new ClientValidator());
        MessageGenerator messageGenerator = Mockito.spy(new MessageGenerator());


        Field field = ClientInformationRest.class.getDeclaredField("clientInformationEJB");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRest, clientInformationEJB);

        field = ClientInformationRest.class.getDeclaredField("messageGenerator");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRest, messageGenerator);

        field = ClientInformationEJB.class.getDeclaredField("clientAdapter");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, clientAdapter);

        field = ClientInformationEJB.class.getDeclaredField("clientValidator");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, clientValidator);

        field = ClientInformationEJB.class.getDeclaredField("clientRepository");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, clientRepository);

        field = EntityManagerProducer.class.getDeclaredField("entityManager");
        field.setAccessible(true);

        // Set value
        field.set(clientRepository, entityManager);

        openBankRest = Mockito.spy(new OpenBankRest());
        OpenBankBusinessEJB openBankBusiness = Mockito.spy(new OpenBankBusinessEJB());

        field = OpenBankRest.class.getDeclaredField("openBankBusinessEJB");
        field.setAccessible(true);

        // Set value
        field.set(openBankRest, openBankBusiness);

        MockitoAnnotations.initMocks(this);

        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
    }

    @AfterEach
    public void tearDownAfterUnitTest() {
        EntityTransaction tx = entityManager.getTransaction();
        tx.commit();

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
    public void testCreateClientWithoutAccount() {
        // Given
        String personIdentification = "191313131313";
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

        // When
        Response response = clientInformationRest.createClientInformation(clientApi);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals("Client created successfully.", response.getEntity());
    }

    @Test
    public void testCreateClientWithAccount() {
        // Given
        String personIdentification = "191313131313";
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

        AccountApi accountApi = new AccountApi();

        accountApi.setBalance(Long.valueOf(1050));
        accountApi.getAccountTransactionList().clear();

        clientApi.getAccountList().add(accountApi);

        // When
        Response response = clientInformationRest.createClientInformation(clientApi);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals("Client created successfully.", response.getEntity());

        // When
        response = clientInformationRest.getClientInformtion(personIdentification);

        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApiResult = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApiResult.getPerson().getPersonIdentification());
        assertEquals(firstName, clientApiResult.getPerson().getFirstName());
        assertEquals(lastName, clientApiResult.getPerson().getLastName());
        assertEquals(mail, clientApiResult.getPerson().getMail());
        assertEquals(0, clientApiResult.getAccountList().size());
    }

    @Test
    public void testGetClient() {
        // Given
        String personIdentification = "191212121212";

        // When
        Response response = clientInformationRest.getClientInformtion(personIdentification);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("john.doe@test.se", clientApi.getPerson().getMail());

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
