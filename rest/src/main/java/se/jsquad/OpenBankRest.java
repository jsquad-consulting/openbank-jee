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

import se.jsquad.api.batch.status.BatchStatus;
import se.jsquad.ejb.OpenBankBusinessEJB;
import se.jsquad.qualifier.Log;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Path("/openbank")
public class OpenBankRest {
    @Inject @Log
    private Logger logger;

    @EJB
    private OpenBankBusinessEJB openBankBusinessEJB;

    @GET
    @Path("/hello/world")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHelloWorld() {
        try {
            return Response.ok().entity(openBankBusinessEJB.getHelloWorld()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Severe system failure has occured!").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @GET
    @Path("/start/slow/batch/mock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSlowBatchMock() {
        try {
            Future<BatchStatus> batchStatusFuture = openBankBusinessEJB.startSlowBatch();

            return Response.ok().entity(batchStatusFuture.get()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Severe system failure has occured!").type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
