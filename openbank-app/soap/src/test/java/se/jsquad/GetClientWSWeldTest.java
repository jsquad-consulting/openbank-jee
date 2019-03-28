package se.jsquad;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.jsquad.getclientservice.GetClientRequest;
import se.jsquad.getclientservice.GetClientResponse;
import se.jsquad.getclientservice.StatusType;
import se.jsquad.getclientservice.TransactionType;
import se.jsquad.getclientservice.Type;
import se.jsquad.producer.LoggerProducer;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.EntityManagerProducer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetClientWSWeldTest {

    private EntityManager entityManager;

    @BeforeEach
    void initEntityManager() {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
                "openBankPU", properties);
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    public void testGetClientWs() throws NoSuchFieldException, IllegalAccessException {
        // Given
        Weld weld = new Weld();
        WeldContainer weldContainer = weld.beanClasses(GetClientWS.class, ClientRepository.class).disableDiscovery()
                .addBeanClass(LoggerProducer.class).initialize();

        GetClientWS getClientWS = weldContainer.select(GetClientWS.class).get();
        ClientRepository clientRepository = weldContainer.select(ClientRepository.class).get();

        Field field = EntityManagerProducer.class.getDeclaredField("entityManager");
        field.setAccessible(true);

        // Set value
        field.set(clientRepository, entityManager);

        field = GetClientWS.class.getDeclaredField("clientRepository");
        field.setAccessible(true);

        // Set value
        field.set(getClientWS, clientRepository);

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
}
