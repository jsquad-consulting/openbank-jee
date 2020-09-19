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

import com.fasterxml.jackson.databind.ObjectMapper;
import se.jsquad.ejb.ClientInformationEjbLocal;
import se.jsquad.qualifier.Log;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/ClientInformationServlet")
public class ClientInformationServlet extends HttpServlet {
    private ObjectMapper objectMapper;

    public ClientInformationServlet() {
        objectMapper = new ObjectMapper();
    }

    @Inject @Log
    private Logger logger;

    @EJB
    private ClientInformationEjbLocal clientInformationEjbLocal;

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
            httpServletResponse.getWriter().print(objectMapper.writeValueAsString(clientInformationEjbLocal
                    .getClient(personalIdentificationNumber)));
        } catch (JMSException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            httpServletResponse.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            httpServletResponse.setContentType(MediaType.TEXT_PLAIN);
            httpServletResponse.getWriter().print("Severe system failure has occured!");
        }
    }
}