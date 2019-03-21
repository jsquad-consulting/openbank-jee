package se.jsquad;

import se.jsquad.batch.status.BatchStatus;
import se.jsquad.ejb.OpenBankBusinessEJB;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Path("/openbank")
public class OpenBankRest {
    private static final Logger logger = Logger.getLogger(OpenBankRest.class.getName());

    @Inject
    private OpenBankBusinessEJB openBankBusinessEJB;

    @GET
    @Path("/hello/world")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHelloWorld() {
        logger.log(Level.FINE, "getHelloWorld() method called.");

        try {
            return Response.ok().entity(openBankBusinessEJB.getHelloWorld()).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return Response.serverError().entity("Severe system failure has occured!").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @GET
    @Path("/start/slow/batch/mock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSlowBatchMock() {
        logger.log(Level.FINE, "getSlowBatchMock() method called.");
        try {
            Future<BatchStatus> batchStatusFuture = openBankBusinessEJB.startSlowBatch();

            return Response.ok().entity(batchStatusFuture.get()).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return Response.serverError().entity("Severe system failure has occured!").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
