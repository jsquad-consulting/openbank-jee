package se.jsquad;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.client.info.ClientApi;
import se.jsquad.repository.ClientRepository;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionScoped;
import javax.ws.rs.core.Response;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(WeldJunit5Extension.class)
public class ClientInformationRestWeldTest {
    static EntityManagerFactory entityManagerFactory = null;

    @WeldSetup
    WeldInitiator weld = WeldInitiator.from(ClientInformationRest.class, ClientAdapter.class,
            ClientRepository.class).activate(TransactionScoped.class)
            .setPersistenceContextFactory(getEntityManager()).build();

    @Inject
    private ClientInformationRest clientInformationRest;

    static Function<InjectionPoint, Object> getEntityManager() {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU", properties);

        return injectionPoint -> entityManagerFactory.createEntityManager();
    }

    @Test
    public void testGetClientInformation() {
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

        assertEquals(1, clientApi.getAccountSet().size());
        assertEquals(500.0, clientApi.getAccountSet().get(0).getBalance());

        assertEquals(1, clientApi.getAccountSet().get(0).getAccountTransactionSet().size());
        assertEquals("DEPOSIT", clientApi.getAccountSet().get(0).getAccountTransactionSet().get(0).getTransactionType().name());
        assertEquals("500$ in deposit", clientApi.getAccountSet().get(0).getAccountTransactionSet().get(0).getMessage());
    }
}
