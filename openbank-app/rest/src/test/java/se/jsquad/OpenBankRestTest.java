package se.jsquad;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.jsquad.batch.SlowMockBatch;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.batch.status.Status;
import se.jsquad.ejb.OpenBankBusinessEJB;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenBankRestTest {
    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    private OpenBankRest openBankRest;

    @BeforeEach
    void init() throws NoSuchFieldException, IllegalAccessException {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU", properties);
        entityManager = entityManagerFactory.createEntityManager();

        openBankRest = Mockito.spy(new OpenBankRest());
        OpenBankBusinessEJB openBankBusinessEJB = Mockito.spy(new OpenBankBusinessEJB());

        Logger loggerOpenBankBusinessEJB = Logger.getLogger(OpenBankBusinessEJB.class.getName());
        Logger loggerOpenBankRest = Logger.getLogger(OpenBankRest.class.getName());

        Field field = OpenBankRest.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(openBankRest, loggerOpenBankRest);

        field = OpenBankRest.class.getDeclaredField("openBankBusinessEJB");
        field.setAccessible(true);

        // Set value
        field.set(openBankRest, openBankBusinessEJB);

        field = OpenBankBusinessEJB.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(openBankBusinessEJB, loggerOpenBankBusinessEJB);

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
    public void testGetHelloWorld() {
        // When
        Response response = openBankRest.getHelloWorld();

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
        assertEquals("Hello world!", response.getEntity());

    }

    @Test
    public void testStartSlowBatchJob() throws ExecutionException, InterruptedException, NoSuchFieldException,
            IllegalAccessException {
        // Given
        OpenBankRest openBankRest = Mockito.spy(new OpenBankRest());
        OpenBankBusinessEJB openBankBusinessEJB = new OpenBankBusinessEJB();
        SlowMockBatch slowMockBatch = new SlowMockBatch();
        Logger loggerOpenBankRest = Logger.getLogger(OpenBankRest.class.getName());
        Logger loggerSlowMockBatch = Logger.getLogger(SlowMockBatch.class.getName());
        Logger loggerOpenBankBusinessEJB = Logger.getLogger(OpenBankBusinessEJB.class.getName());

        Field field = SlowMockBatch.class.getDeclaredField("sleepTime");
        field.setAccessible(true);

        // Set value
        field.set(slowMockBatch, 0);

        field = SlowMockBatch.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(slowMockBatch, loggerSlowMockBatch);

        field = OpenBankRest.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(openBankRest, loggerOpenBankRest);

        field = OpenBankRest.class.getDeclaredField("openBankBusinessEJB");
        field.setAccessible(true);

        // Set value
        field.set(openBankRest, openBankBusinessEJB);

        field = OpenBankBusinessEJB.class.getDeclaredField("slowMockBatch");
        field.setAccessible(true);

        // Set value
        field.set(openBankBusinessEJB, slowMockBatch);

        field = OpenBankBusinessEJB.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(openBankBusinessEJB, loggerOpenBankBusinessEJB);

        // When
        Response response = openBankRest.getSlowBatchMock();

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        BatchStatus batchStatus = (BatchStatus) response.getEntity();

        assertEquals(Status.SUCCESS, batchStatus.getStatus());
        assertEquals("Batch job went just fine.", batchStatus.getMessage());
    }
}
