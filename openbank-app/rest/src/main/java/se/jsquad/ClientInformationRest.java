package se.jsquad;

import se.jsquad.client.info.ClientApi;
import se.jsquad.ejb.ClientInformationEJB;
import se.jsquad.generator.MessageGenerator;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Path("/client")
public class ClientInformationRest {
    private static final Logger logger = Logger.getLogger(ClientInformationRest.class.getName());

    @Inject
    private ClientInformationEJB clientInformationEJB;

    @Inject
    MessageGenerator messageGenerator;

    @GET
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Path("info/{personIdentification}")
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

    @POST
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createClientInformation(ClientApi clientApi) {
        logger.log(Level.FINE, "createClientInformation(clientApi: {0})",
                new Object[] {"hidden"});

        if (clientApi == null || clientApi.getPerson() == null) {
            logger.log(Level.SEVERE, "Client information must contain a person at least.");

            return Response.status(Response.Status.BAD_REQUEST).entity("Client must at least contain a person")
                    .type(MediaType.TEXT_PLAIN).build();
        }

        try {
            clientInformationEJB.createClient(clientApi);
            return Response.ok().entity("Client created successfully.").type(MediaType.TEXT_PLAIN).build();
        } catch (ConstraintViolationException e) {
            String message = messageGenerator.generateClientValidationMessage(e.getConstraintViolations());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build();
        } catch (BadRequestException e) {
          logger.log(Level.SEVERE, e.getMessage(), e);

          return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.APPLICATION_JSON)
                  .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            return Response.serverError().entity("Severe system failure has occured!").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
