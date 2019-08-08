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

package se.jsquad.ejb;

import se.jsquad.adapter.ClientAdapter;
import se.jsquad.api.client.info.ClientApi;
import se.jsquad.entity.Client;
import se.jsquad.jms.MessageSenderSessionJMS;
import se.jsquad.qualifier.Log;
import se.jsquad.repository.ClientRepository;
import se.jsquad.validator.ClientValidator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;
import java.util.Set;
import java.util.logging.Logger;

@Stateless
public class ClientInformationEJB {
    @Inject @Log
    private Logger logger;

    @Inject
    private ClientAdapter clientAdapter;

    @Inject
    private ClientValidator clientValidator;

    @Inject
    private ClientRepository clientRepository;

    @Inject
    private MessageSenderSessionJMS messageSenderSessionJMS;

    public ClientApi getClient(String personIdentification) throws JMSException {
        messageSenderSessionJMS.sendMessage("Client information request with hidden person " +
                "identification acquired.");

        Client client = clientRepository.getClientByPersonIdentification(personIdentification);

        if (client == null) {
            return null;
        }

        return clientAdapter.translateClientToClientApi(client);
    }

    public void createClient(ClientApi clientApi) {
        if (clientRepository.getClientByPersonIdentification(clientApi.getPerson().getPersonIdentification())
                != null) {
            throw new BadRequestException("Client already exist!");
        }

        Client client = clientAdapter.translateClientApiToClient(clientApi);
        Set<ConstraintViolation<Object>> constraintViolationSet = clientValidator.createConstraintViolationSet(client);

        if (!constraintViolationSet.isEmpty()) {
            throw new ConstraintViolationException(constraintViolationSet);
        }

        clientRepository.createClient(client);
    }

}
