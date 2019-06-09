package se.jsquad;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.authorization.Authorization;
import se.jsquad.client.info.ClientApi;
import se.jsquad.ejb.ClientInformationEJB;
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
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.TransactionScoped;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyObject;

@ExtendWith(WeldJunit5Extension.class)
public class ClientInformationRestWeldTest {

    @WeldSetup
    private WeldInitiator weldInitiator = WeldInitiator.from(ClientInformationRest.class,
            ClientInformationEJB.class, ClientValidator.class, ClientAdapter.class, SystemStartupEjb.class,
            DatabaseGenerator.class,
            ClientRepository.class, Authorization.class, SystemPropertyRepository.class,
            LoggerProducer.class,
            MessageGenerator.class, EntityManagerProducer.class,
            MessageSenderSessionJMS.class, JmsContextMock.class, SessionContext.class)
            .activate(TransactionScoped.class)
            .setPersistenceContextFactory(getPersistenceContextFactory()).build();

    @Inject
    private ClientInformationRest clientInformationRest;

    @Inject
    private ClientInformationEJB clientInformationEJB;

    @Inject
    private Authorization authorization;

    @Inject
    private ClientAdapter clientAdapter;


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
    public void testGetClientInformation() throws IOException, ServletException, NoSuchFieldException, IllegalAccessException, JMSException {
        // Given
        String personIdentificationNumber = "191212121212";
        Authorization authorizationSpy = Mockito.spy(authorization);
        MessageSenderSessionJMS messageSenderSessionJMS = Mockito.mock(MessageSenderSessionJMS.class);
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        SessionContext sessionContext = Mockito.mock(SessionContext.class);

        Field field = ClientInformationRest.class.getDeclaredField("authorization");
        field.setAccessible(true);
        field.set(clientInformationRest, authorizationSpy);

        field = Authorization.class.getDeclaredField("request");
        field.setAccessible(true);
        field.set(authorizationSpy, httpServletRequest);

        field = ClientInformationEJB.class.getDeclaredField("messageSenderSessionJMS");
        field.setAccessible(true);
        field.set(clientInformationEJB, messageSenderSessionJMS);

        field = ClientInformationRest.class.getDeclaredField("clientInformationEJB");
        field.setAccessible(true);
        field.set(clientInformationRest, clientInformationEJB);

        field = ClientAdapter.class.getDeclaredField("sessionContext");
        field.setAccessible(true);
        field.set(clientAdapter, sessionContext);

        field = ClientInformationEJB.class.getDeclaredField("clientAdapter");
        field.setAccessible(true);
        field.set(clientInformationEJB, clientAdapter);

        // When
        Mockito.when(sessionContext.isCallerInRole(RoleConstants.ADMIN)).thenReturn(true);
        Mockito.doNothing().when(messageSenderSessionJMS).sendMessage(anyObject());
        Mockito.when(httpServletRequest.authenticate(anyObject())).thenReturn(true);
        Mockito.when(authorizationSpy.isAuthorized()).thenReturn(true);
        Mockito.when(authorizationSpy.isUserInRole(anyObject())).thenReturn(true);
        Response response = clientInformationRest.getClientInformtion(personIdentificationNumber);

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

        assertEquals(personIdentificationNumber, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentificationNumber, clientApi.getPerson().getPersonIdentification());
        assertEquals("john.doe@test.se", clientApi.getPerson().getMail());

        assertEquals(1, clientApi.getAccountList().size());
        assertEquals(500.0, clientApi.getAccountList().get(0).getBalance());

        assertEquals(1, clientApi.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals("DEPOSIT", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getTransactionType().name());
        assertEquals("500$ in deposit", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getMessage());
    }
}
