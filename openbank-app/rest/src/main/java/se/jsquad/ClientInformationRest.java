package se.jsquad;

import se.jsquad.client.info.ClientApi;
import se.jsquad.ejb.ClientInformationEJB;
import se.jsquad.generator.MessageGenerator;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/client")
public class ClientInformationRest {
    private static final Logger logger = Logger.getLogger(ClientInformationRest.class.getName());
    private static final String AUTHORIZATION_FAILED = "Authorization failed.";

    @EJB
    private ClientInformationEJB clientInformationEJB;

    @Inject
    private MessageGenerator messageGenerator;

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    @GET
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login() {
        logger.log(Level.FINE, "login(), request: {0}, response: {1}",
                new Object[]{request, response});

        try {
            boolean authenticated = request.authenticate(response);

            if (authenticated) {
                logger.log(Level.FINE, "Authorization ok.");
                return Response.ok().entity("Authorization ok").type(MediaType.TEXT_PLAIN).build();
            } else {
                logger.log(Level.FINE, AUTHORIZATION_FAILED);
                return Response.status(Response.Status.UNAUTHORIZED).entity(AUTHORIZATION_FAILED)
                        .type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            return Response.serverError().entity("Severe system failure has occured.").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @GET
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
        logger.log(Level.FINE, "logout(), request: {0}, response: {1}",
                new Object[]{request, response});

        try {
            request.logout();
            return Response.ok().entity("Logout successful.").entity(MediaType.TEXT_PLAIN).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            return Response.serverError().entity("Severe system failure has occured.").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @GET
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Path("info/{personIdentification}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientInformtion(@PathParam("personIdentification") String personIdentification) {
        logger.log(Level.FINE, "getClientInformtion(personIdentification: {0})",
                new Object[]{"secret person identification parameter"});

        try {
            if (!request.authenticate(response)) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized request.").type(MediaType
                        .TEXT_PLAIN).build();
            }

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
                new Object[]{"hidden"});

        if (clientApi == null || clientApi.getPerson() == null) {
            logger.log(Level.SEVERE, "Client information must contain a person at least.");

            return Response.status(Response.Status.BAD_REQUEST).entity("Client must at least contain a person")
                    .type(MediaType.TEXT_PLAIN).build();
        }

        try {
            if (!request.isUserInRole(RoleConstants.ADMIN)) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized request.").type(MediaType
                        .TEXT_PLAIN).build();
            }

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
