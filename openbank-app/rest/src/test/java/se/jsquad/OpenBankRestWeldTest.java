package se.jsquad;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import se.jsquad.batch.SlowMockBatch;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.batch.status.Status;
import se.jsquad.ejb.OpenBankBusinessEJB;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(WeldJunit5Extension.class)
public class OpenBankRestWeldTest {
    @WeldSetup
    private WeldInitiator weld =
            WeldInitiator.from(OpenBankRest.class, OpenBankBusinessEJB.class, SlowMockBatch.class).build();

    @Inject
    private OpenBankRest openBankRest;

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

        Field field = SlowMockBatch.class.getDeclaredField("sleepTime");
        field.setAccessible(true);

        // Set value
        field.set(slowMockBatch, 0);

        field = OpenBankRest.class.getDeclaredField("openBankBusinessEJB");
        field.setAccessible(true);

        // Set value
        field.set(openBankRest, openBankBusinessEJB);

        field = OpenBankBusinessEJB.class.getDeclaredField("slowMockBatch");
        field.setAccessible(true);

        // Set value
        field.set(openBankBusinessEJB, slowMockBatch);

        // When
        Response response = openBankRest.getSlowBatchMock();

        // Then
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));

        BatchStatus batchStatus = (BatchStatus) response.getEntity();

        assertEquals(Status.SUCCESS, batchStatus.getStatus());
        assertEquals("Batch job went just fine.", batchStatus.getMessage());
    }
}
