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

import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.apache.activemq.artemis.junit.EmbeddedJMSResource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.api.client.info.ClientApi;
import se.jsquad.api.client.info.TypeApi;
import se.jsquad.authorization.Authorization;
import se.jsquad.ejb.ClientInformationEJB;
import se.jsquad.ejb.OpenBankBusinessEJB;
import se.jsquad.entity.Client;
import se.jsquad.entity.SystemProperty;
import se.jsquad.generator.DatabaseGenerator;
import se.jsquad.generator.MessageGenerator;
import se.jsquad.jms.MessageMDB;
import se.jsquad.jms.MessageSenderSessionJMS;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.EntityManagerProducer;
import se.jsquad.validator.ClientValidator;

import javax.ejb.SessionContext;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyObject;

@Execution(ExecutionMode.SAME_THREAD)
@Disabled
public class JmsMDBRestTest {
    public EmbeddedJMSResource jmsServer;

    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    private OpenBankRest openBankRest;
    private ClientInformationRest clientInformationRest;
    private ClientInformationEJB clientInformationEJB;
    private org.apache.activemq.artemis.core.server.Queue queueProducer;
    private org.apache.activemq.artemis.core.server.Queue queueConsumer;
    private Session session;
    private MessageMDB messageMDB;
    private JMSContext jmsContextProducer;
    private JMSContext jmsContextConsumer;
    private Queue jmsProducer;
    private Queue mdbConsumer;

    @BeforeEach
    public void init() throws Exception {
        jmsServer = new EmbeddedJMSResource();
        jmsServer.getJmsServer().start();

        jmsServer.getJmsServer().getJMSServerManager().createQueue(true, "jmsProducer", null, true);

        queueProducer =
                jmsServer.getJmsServer().getActiveMQServer().locateQueue(SimpleString.toSimpleString("jmsProducer"));
        queueConsumer =
                jmsServer.getJmsServer().getActiveMQServer().locateQueue(SimpleString.toSimpleString("jmsProducer"));

        jmsProducer =
                new ActiveMQQueue(queueProducer.getName());
        mdbConsumer =
                new ActiveMQQueue(queueConsumer.getName());

        ConnectionFactory connectionFactory =
                new ActiveMQJMSConnectionFactory(jmsServer.getVmURL());
        QueueConnection queueConnection = ((ActiveMQJMSConnectionFactory) connectionFactory).createQueueConnection();
        queueConnection.createQueueSession(true, 0);

        messageMDB = Mockito.spy(new MessageMDB());

        session = queueConnection.createSession();
        session.createProducer(jmsProducer);
        session.createConsumer(mdbConsumer);
        session.setMessageListener(messageMDB);
        session.run();

        jmsContextProducer = connectionFactory.createContext(JMSContext.SESSION_TRANSACTED);
        jmsContextProducer.setAutoStart(true);
        jmsContextProducer.setClientID("900");

        jmsContextConsumer = connectionFactory.createContext(JMSContext.SESSION_TRANSACTED);
        jmsContextConsumer.setClientID("905");
        jmsContextConsumer.setAutoStart(true);

        DatabaseGenerator databaseGenerator = new DatabaseGenerator();

        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence_test.xml");

        entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU", properties);
        entityManager = entityManagerFactory.createEntityManager();

        Set<Client> clientSet = databaseGenerator.populateDatabase();

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        for (Client client : clientSet) {
            entityManager.persist(client);
        }

        SystemProperty systemProperty = new SystemProperty();
        systemProperty.setName("VERSION");
        systemProperty.setValue("1.0.1");

        entityManager.persist(systemProperty);

        entityTransaction.commit();

        clientInformationEJB = Mockito.spy(new ClientInformationEJB());
        ClientAdapter clientAdapter = Mockito.spy(new ClientAdapter());
        ClientRepository clientRepository = Mockito.spy(new ClientRepository());
        clientInformationRest = Mockito.spy(new ClientInformationRest());
        Logger loggerClientInformationRest = Logger.getLogger(ClientInformationRest.class.getName());
        Logger loggerAuthorization = Logger.getLogger(Authorization.class.getName());
        Logger loggerClientInformationEJB = Logger.getLogger(ClientInformationEJB.class.getName());
        Logger loggerMessageSenderSessionJMS = Logger.getLogger(MessageSenderSessionJMS.class.getName());
        Logger loggerClientRepository = Logger.getLogger(ClientRepository.class.getName());
        Logger loggerClientAdapter = Logger.getLogger(ClientAdapter.class.getName());

        ClientValidator clientValidator = Mockito.spy(new ClientValidator());
        MessageGenerator messageGenerator = Mockito.spy(new MessageGenerator());
        MessageSenderSessionJMS messageSenderSessionJMS = Mockito.spy(new MessageSenderSessionJMS());
        SessionContext sessionContext = Mockito.mock(SessionContext.class);
        Mockito.when(sessionContext.isCallerInRole(RoleConstants.ADMIN)).thenReturn(true);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.authenticate(null)).thenReturn(true);
        Mockito.when(request.isUserInRole(anyObject())).thenReturn(true);
        Authorization authorization = Mockito.spy(new Authorization());


        Field field = ClientInformationRest.class.getDeclaredField("clientInformationEJB");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRest, clientInformationEJB);

        field = ClientInformationRest.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRest, loggerClientInformationRest);

        field = ClientInformationRest.class.getDeclaredField("messageGenerator");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRest, messageGenerator);

        field = ClientInformationRest.class.getDeclaredField("authorization");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationRest, authorization);

        field = Authorization.class.getDeclaredField("request");
        field.setAccessible(true);

        // Set value
        field.set(authorization, request);

        field = Authorization.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(authorization, loggerAuthorization);

        field = ClientInformationEJB.class.getDeclaredField("clientAdapter");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, clientAdapter);

        field = ClientInformationEJB.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, loggerClientInformationEJB);

        field = ClientAdapter.class.getDeclaredField("sessionContext");
        field.setAccessible(true);

        // Set value
        field.set(clientAdapter, sessionContext);

        field = ClientAdapter.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(clientAdapter, loggerClientAdapter);

        field = ClientInformationEJB.class.getDeclaredField("messageSenderSessionJMS");
        field.setAccessible(true);

        // Set value
        field.set(clientInformationEJB, messageSenderSessionJMS);

        field = MessageSenderSessionJMS.class.getDeclaredField("jmsContext");
        field.setAccessible(true);

        // Set value
        field.set(messageSenderSessionJMS, jmsContextProducer);

        field = MessageSenderSessionJMS.class.getDeclaredField("queue");
        field.setAccessible(true);

        // Set value
        field.set(messageSenderSessionJMS, jmsProducer);

        field = MessageSenderSessionJMS.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(messageSenderSessionJMS, loggerMessageSenderSessionJMS);

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

        field = ClientRepository.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(clientRepository, loggerClientRepository);

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
    public void tearDownAfterUnitTest() throws Exception {
        EntityTransaction tx = entityManager.getTransaction();
        tx.rollback();

        entityManager.close();
        entityManagerFactory.close();

        jmsServer.stop();
    }

    @Test
    public void testGetClientJMSTransactedWithCommit() throws JMSException {
        // Given
        String personIdentification = "191212121212";

        assertEquals(true, jmsServer.getJmsServer().getActiveMQServer().isActive());
        assertEquals(0, queueProducer.getMessageCount());
        assertEquals(0, queueConsumer.getMessageCount());

        // When
        Response response = clientInformationRest.getClientInformation(personIdentification);
        jmsContextProducer.commit();
        entityManager.flush();

        // Then
        assertEquals(queueProducer,
                jmsServer.getJmsServer().getActiveMQServer().locateQueue(SimpleString.toSimpleString("jmsProducer")));
        assertEquals(true, jmsServer.getJmsServer().getActiveMQServer().isActive());
        assertEquals(1, queueProducer.getMessageCount());
        assertEquals(1, queueConsumer.getMessageCount());
        assertEquals(1, queueProducer.getMessagesAdded());
        assertEquals(1, queueConsumer.getMessagesAdded());
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("john.doe@test.se", clientApi.getPerson().getMail());

        assertEquals(1, clientApi.getAccountList().size());
        assertEquals(500, clientApi.getAccountList().get(0).getBalance());

        assertEquals(1, clientApi.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals("DEPOSIT", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getTransactionType().name());
        assertEquals("500$ in deposit", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getMessage());

        assertEquals(TypeApi.REGULAR, clientApi.getClientType().getType());
        assertEquals(500, clientApi.getClientType().getRating());

        // When
        Message message = jmsContextConsumer.createConsumer(mdbConsumer).receive();
        jmsContextConsumer.commit();
        String responseMessage = message.getBody(String.class);

        // Then
        assertEquals("Client information request with hidden person identification acquired.",
               responseMessage);
        assertEquals(0, queueProducer.getMessageCount());
        assertEquals(0, queueConsumer.getMessageCount());
        assertEquals(1, queueProducer.getMessagesAdded());
        assertEquals(1, queueConsumer.getMessagesAdded());
    }

    @Test
    public void testGetClientJMSTransactedWithNoCommit() {
        // Given
        String personIdentification = "191212121212";

        assertEquals(true, jmsServer.getJmsServer().getActiveMQServer().isActive());
        assertEquals(0, queueProducer.getMessageCount());
        assertEquals(0, queueConsumer.getMessageCount());

        // When
        Response response = clientInformationRest.getClientInformation(personIdentification);
        jmsContextProducer.rollback();
        entityManager.flush();

        // Then
        assertEquals(queueProducer,
                jmsServer.getJmsServer().getActiveMQServer().locateQueue(SimpleString.toSimpleString("jmsProducer")));
        assertEquals(true, jmsServer.getJmsServer().getActiveMQServer().isActive());
        assertEquals(0, queueProducer.getMessageCount());
        assertEquals(0, queueConsumer.getMessageCount());
        assertEquals(0, queueProducer.getMessagesAdded());
        assertEquals(0, queueConsumer.getMessagesAdded());
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        ClientApi clientApi = (ClientApi) response.getEntity();

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("john.doe@test.se", clientApi.getPerson().getMail());

        assertEquals(1, clientApi.getAccountList().size());
        assertEquals(500, clientApi.getAccountList().get(0).getBalance());

        assertEquals(1, clientApi.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals("DEPOSIT", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getTransactionType().name());
        assertEquals("500$ in deposit", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getMessage());

        assertEquals(TypeApi.REGULAR, clientApi.getClientType().getType());
        assertEquals(500, clientApi.getClientType().getRating());
    }
}
