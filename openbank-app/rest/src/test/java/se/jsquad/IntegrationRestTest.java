package se.jsquad;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.client.info.ClientApi;
import se.jsquad.repository.ClientRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationRestTest {

    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    OpenBankRest openBankRest;
    ClientInformationRest clientInformationRESTful;

    @BeforeEach
    void init() throws NoSuchFieldException, IllegalAccessException {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU", properties);
        entityManager = entityManagerFactory.createEntityManager();

        ClientAdapter clientAdapter = Mockito.spy(new ClientAdapter());
        ClientRepository clientRepository = Mockito.spy(new ClientRepository());
        clientInformationRESTful = Mockito.spy(new ClientInformationRest());

        Field field = ClientInformationRest.class.getDeclaredField("clientAdapter");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRESTful, clientAdapter);

        field = ClientAdapter.class.getDeclaredField("clientRepository");
        field.setAccessible(true);

        // Set value
        field.set(clientAdapter, clientRepository);

        field = ClientRepository.class.getDeclaredField("entityManager");
        field.setAccessible(true);

        // Set value
        field.set(clientRepository, entityManager);

        openBankRest = Mockito.spy(new OpenBankRest());
        OpenBankBusiness openBankBusiness = Mockito.spy(new OpenBankBusiness());

        field = OpenBankRest.class.getDeclaredField("openBankBusiness");
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
    public void testGetClient() {
        // Given
        String personIdentification = "191212121212";

        // When
        Response response = clientInformationRESTful.getClientInformtion(personIdentification);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());

        assertEquals(1, clientApi.getAccountSet().size());
        assertEquals(500.0, clientApi.getAccountSet().get(0).getBalance());

        assertEquals(1, clientApi.getAccountSet().get(0).getAccountTransactionSet().size());
        assertEquals("DEPOSIT", clientApi.getAccountSet().get(0).getAccountTransactionSet().get(0).getTransactionType().name());
        assertEquals("500$ in deposit", clientApi.getAccountSet().get(0).getAccountTransactionSet().get(0).getMessage());
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
