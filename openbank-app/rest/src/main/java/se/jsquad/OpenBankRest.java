package se.jsquad;

import se.jsquad.ejb.OpenBankBusinessEJB;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Path("/openbank")
public class OpenBankRest {
    private static Logger logger = Logger.getLogger(OpenBankRest.class.getName());

    @Inject
    OpenBankBusinessEJB openBankBusiness;

    @GET
    @Path("/hello/world")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHelloWorld() {
        logger.log(Level.INFO, "getHelloWorld()");

        try {
            return Response.ok().entity(openBankBusiness.getHelloWorld()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Severe system failure has occured!").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

}
