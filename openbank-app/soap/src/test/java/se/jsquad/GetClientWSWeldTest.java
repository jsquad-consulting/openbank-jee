package se.jsquad;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.jsquad.getclientservice.GetClientRequest;
import se.jsquad.getclientservice.GetClientResponse;
import se.jsquad.getclientservice.StatusType;
import se.jsquad.getclientservice.TransactionType;
import se.jsquad.getclientservice.Type;
import se.jsquad.repository.ClientRepository;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.Persistence;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(WeldJunit5Extension.class)
class GetClientWSWeldTest {
    @WeldSetup
    private WeldInitiator weld = WeldInitiator.from(GetClientWS.class, ClientRepository.class)
            .setPersistenceContextFactory(getEntityManager()).build();

    @Inject
    private GetClientWS getClientWS;

    private static Function<InjectionPoint, Object> getEntityManager() {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        return injectionPoint -> Persistence.createEntityManagerFactory("openBankPU", properties)
                .createEntityManager();
    }

    @Test
    public void testGetClientWs() {
        // Given
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
