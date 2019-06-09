package se.jsquad;

import se.jsquad.authorization.Authorization;
import se.jsquad.ejb.AccountTransactionEJB;
import se.jsquad.qualifier.Log;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/client")
public class AccountTransferRest {
    @Inject
    @Log
    private Logger logger;

    @EJB
    private AccountTransactionEJB accountTransactionEJB;

    @Inject
    private Authorization authorization;

    @PUT
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Path("/account/transfer/{value}/{fromAccountNumber}/{toAccountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferValueFromAccountToAccount(@PathParam("value") long value,
                                                      @PathParam("fromAccountNumber") String fromAccountNumber,
                                                      @PathParam("toAccountNumber") String toAccountNumber) {
        try {
            if (!authorization.isAuthorized()) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized request.").type(
                        MediaType.TEXT_PLAIN).build();
            }

            accountTransactionEJB.transferValueFromAccountToAccount(value, fromAccountNumber,
                    toAccountNumber);
            return Response.ok().entity("Transaction successful.").type(MediaType.TEXT_PLAIN).build();
        } catch (Exception e) {
            return Response.serverError().entity("Severe system failure has occured!").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
