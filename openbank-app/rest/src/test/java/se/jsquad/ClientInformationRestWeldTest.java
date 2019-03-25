package se.jsquad;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.client.info.ClientApi;
import se.jsquad.ejb.ClientInformationEJB;
import se.jsquad.generator.MessageGenerator;
import se.jsquad.jms.MessageSenderSessionJMS;
import se.jsquad.repository.ClientRepository;
import se.jsquad.validator.ClientValidator;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.Persistence;
import javax.transaction.TransactionScoped;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyObject;

@ExtendWith(WeldJunit5Extension.class)
public class ClientInformationRestWeldTest {
    @WeldSetup
    private WeldInitiator weld = WeldInitiator.from(ClientInformationRest.class, ClientInformationEJB.class,
            MessageSenderSessionJMS.class, ClientRepository.class, ClientAdapter.class, ClientValidator.class,
            MessageGenerator.class).addBeans(createJMSContextBean())
            .bindResource("java:openBank/jms/callQ", Mockito.mock(Queue.class))
            .activate(TransactionScoped.class)
            .setPersistenceContextFactory(getEntityManager()).build();

    public ClientInformationRestWeldTest() throws JMSException {
    }

    private static Bean<?> createJMSContextBean() throws JMSException {
        JMSContext jmsContext = Mockito.mock(JMSContext.class);
        JMSProducer jmsProducer = Mockito.mock(JMSProducer.class);
        ObjectMessage objectMessage = Mockito.mock(ObjectMessage.class);
        Mockito.doNothing().when(objectMessage).setJMSCorrelationID(anyObject());
        Mockito.doNothing().when(objectMessage).setJMSTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        Mockito.when(jmsContext.createObjectMessage(anyObject())).thenReturn(objectMessage);
        Mockito.when(jmsContext.createProducer()).thenReturn(jmsProducer);
        return MockBean.builder().types(JMSContext.class)
                .creating(jmsContext).build();
    }


    @Inject
    private ClientInformationRest clientInformationRest;

    private static Function<InjectionPoint, Object> getEntityManager() {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        return injectionPoint -> Persistence.createEntityManagerFactory("openBankPU", properties)
                .createEntityManager();
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
    public void testGetClientNotFound() {
        // Given
        String personIdentification = "191212121213";

        // When
        Response response = clientInformationRest.getClientInformtion(personIdentification);

        // Then
        assertEquals(Response.Status.NOT_FOUND, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals("Client not found.", (String) response.getEntity());
    }
}
