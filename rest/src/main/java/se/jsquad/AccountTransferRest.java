/*
 * Copyright 2019 JSquad AB
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Transfer money from one account to another account",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Ok message for successful transaction", content = @Content(mediaType =
                            MediaType.TEXT_PLAIN,
                            schema = @Schema(example = "Successful transaction."))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized transaction.", content =
                    @Content(mediaType =
                            MediaType.TEXT_PLAIN, schema = @Schema(example = "Unauthorized transaction."))),
                    @ApiResponse(responseCode = "500", description = "Severe system failure has occured!",
                            content =
                            @Content(mediaType = MediaType.TEXT_PLAIN,
                                    schema = @Schema(example = "Severe system failure has occured!")))})
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferValueFromAccountToAccount(@Parameter(description = "Amount of money to transfer",
            required = true) @PathParam("value") long value,
                                                      @Parameter(description = "Account number to withdrawal from",
                                                              required = true) @PathParam(
                                                              "fromAccountNumber") String fromAccountNumber,
                                                      @Parameter(description = "The account number to deposit to",
                                                              required = true)
                                                      @PathParam(
                                                              "toAccountNumber") String toAccountNumber) {
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
