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
import se.jsquad.api.client.info.ClientApi;
import se.jsquad.authorization.Authorization;
import se.jsquad.ejb.ClientInformationEJB;
import se.jsquad.generator.MessageGenerator;
import se.jsquad.qualifier.Log;

import javax.ejb.EJB;
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
import java.util.logging.Logger;

@Path("/client")
public class ClientInformationRest {
    @Inject @Log
    private Logger logger;

    private static final String AUTHORIZATION_FAILED = "Authorization failed.";

    @EJB
    private ClientInformationEJB clientInformationEJB;

    @Inject
    private MessageGenerator messageGenerator;

    @Inject
    private Authorization authorization;

    @GET
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login() {
        try {
            if (authorization.isAuthorized()) {
                return Response.ok().entity("Authorization ok").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity(AUTHORIZATION_FAILED)
                        .type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            return Response.serverError().entity("Severe system failure has occured.").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @GET
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
        try {
            authorization.logout();
            return Response.ok().entity("Logout successful.").entity(MediaType.TEXT_PLAIN).build();
        } catch (Exception e) {
            return Response.serverError().entity("Severe system failure has occured.").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @GET
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Path("info/{personIdentification}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get client by person identification number",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "The client", content = @Content(mediaType =
                            MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ClientApi.class))),
                    @ApiResponse(responseCode = "404", description = "Client not found.", content = @Content(mediaType =
                            MediaType.TEXT_PLAIN, schema = @Schema(example = "Client not found."))),
                    @ApiResponse(responseCode = "500", description = "Severe system failure has occured!", content =
                    @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(example = "Severe system failure has occured!")))})
    public Response getClientInformation(@Parameter(description = "The person identification number",
            example = "191212121212", required = true) @PathParam(
            "personIdentification") String personIdentification) {
        try {
            if (!authorization.isAuthorized()) {
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
            return Response.status(Response.Status.NOT_FOUND).entity("Client not found.").type(MediaType.TEXT_PLAIN)
                    .build();

        } catch (Exception e) {
            return Response.serverError().entity("Severe system failure has occured!").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @POST
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createClientInformation(ClientApi clientApi) {
        if (clientApi == null || clientApi.getPerson() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Client must at least contain a person")
                    .type(MediaType.TEXT_PLAIN).build();
        }

        try {
            if (!authorization.isUserInRole(RoleConstants.ADMIN)) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized request.").type(MediaType
                        .TEXT_PLAIN).build();
            }

            clientInformationEJB.createClient(clientApi);
            return Response.ok().entity("Client created successfully.").type(MediaType.TEXT_PLAIN).build();
        } catch (ConstraintViolationException e) {
            String message = messageGenerator.generateClientValidationMessage(e.getConstraintViolations());
            return Response.status(Response.Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (Exception e) {
            return Response.serverError().entity("Severe system failure has occured!").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
