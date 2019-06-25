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

import se.jsquad.getclientservice.GetClientRequest;
import se.jsquad.getclientservice.GetClientResponse;
import se.jsquad.getclientservice.GetClientServicePort;
import se.jsquad.qualifier.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebService;
import java.util.logging.Logger;

@Stateless
@WebService(name = "GetClientService")
public class GetClientWS implements GetClientServicePort {
    @Inject @Log
    private Logger logger;

    @Inject
    GetClientWsBusiness getClientWsBusiness;

    @Override
    public GetClientResponse getClient(GetClientRequest request) {
        return getClientWsBusiness.getClientResponse(request);
    }
}
