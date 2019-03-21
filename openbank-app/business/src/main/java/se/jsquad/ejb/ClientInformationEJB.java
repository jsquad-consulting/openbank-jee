package se.jsquad.ejb;

import se.jsquad.Client;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.client.info.ClientApi;
import se.jsquad.repository.ClientRepository;
import se.jsquad.validator.ClientValidator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ClientInformationEJB {
    private static final Logger logger = Logger.getLogger(ClientInformationEJB.class.getName());

    @Inject
    private ClientAdapter clientAdapter;

    @Inject
    private ClientValidator clientValidator;

    @Inject
    private ClientRepository clientRepository;

    public ClientApi getClient(String personIdentification) {
        logger.log(Level.FINE, "getClient(personIdentification: {0})",
                new Object[] {"hidden"});

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
