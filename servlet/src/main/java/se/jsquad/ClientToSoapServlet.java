/*
 * Copyright 2020 JSquad AB
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

import com.google.gson.Gson;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import se.jsquad.getclientservice.ClientType;
import se.jsquad.getclientservice.GetClientRequest;
import se.jsquad.getclientservice.GetClientResponse;
import se.jsquad.getclientservice.GetClientServicePort;
import se.jsquad.qualifier.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/ClientToSoapServlet")
public class ClientToSoapServlet extends HttpServlet {
    private Gson gson;

    public ClientToSoapServlet() throws MalformedURLException {
        gson = new Gson();

        Service service = Service.create(URI.create(System.getenv("WEBSERVICE_WSDL_URL")).toURL(),
                new QName(System.getenv("WEBSERVICE_QURL"), System.getenv("WEBSERVICE_QSERVICE")));
        getClientServicePort = service.getPort(GetClientServicePort.class);

        ((BindingProvider) getClientServicePort).getRequestContext().put(
                BindingProvider.USERNAME_PROPERTY, System.getenv("JBOSS_ROOT_USER"));
        ((BindingProvider) getClientServicePort).getRequestContext().put(
                BindingProvider.PASSWORD_PROPERTY, System.getenv("JBOSS_ROOT_PASSWORD"));
    }

    @Inject
    @Log
    private Logger logger;

    private GetClientServicePort getClientServicePort;

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException {
        String personalIdentificationNumber = httpServletRequest.getHeader("personalIdentificationNumber");

        if (personalIdentificationNumber == null || !personalIdentificationNumber.matches("[0-9]{12}")) {
            httpServletResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            httpServletResponse.getWriter().print("Personal identification number must be twelve" +
                    " digits.");
            httpServletResponse.setContentType(MediaType.TEXT_PLAIN);
            return;
        }

        httpServletResponse.setStatus(200);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON);

        try {
            GetClientRequest getClientRequest = new GetClientRequest();

            getClientRequest.setPersonIdentification(personalIdentificationNumber);
            GetClientResponse clientResponse = getClientServicePort.getClient(getClientRequest);

            httpServletResponse.getWriter().print(gson.toJson(clientResponse.getClient(), ClientType.class));
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            httpServletResponse.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            httpServletResponse.setContentType(MediaType.TEXT_PLAIN);
            httpServletResponse.getWriter().print(e.getMessage());
        }
    }
}