package se.jsquad;

import se.jsquad.client.info.ClientApi;
import se.jsquad.ejb.ClientInformationEJB;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Path("/client/info")
public class ClientInformationRest {
    private static Logger logger = Logger.getLogger(ClientInformationRest.class.getName());

    @Inject
    private ClientInformationEJB clientInformationEJB;

    @GET
    @Path("/{personIdentification}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientInformtion(@PathParam("personIdentification") String personIdentification) {
        logger.log(Level.FINE, "getClientInformtion(personIdentification: {0})",
                new Object[]{"secret person identification parameter"});

        try {
            ClientApi clientApi = clientInformationEJB.getClient(personIdentification);

            if (clientApi != null) {
                return Response.ok().entity(clientApi).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Client not found.").type(MediaType
                        .TEXT_PLAIN).build();
            }
        } catch (NoResultException e) {
            logger.log(Level.FINE, e.getMessage(), e);

            return Response.status(Response.Status.NOT_FOUND).entity("Client not found.").type(MediaType.TEXT_PLAIN)
                    .build();

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            return Response.serverError().entity("Severe system failure has occured!").type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
