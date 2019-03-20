package se.jsquad.ejb;

import se.jsquad.Client;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.client.info.ClientApi;
import se.jsquad.repository.ClientRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ClientInformationEJB {
    private static Logger logger = Logger.getLogger(ClientInformationEJB.class.getName());

    @Inject
    private ClientAdapter clientAdapter;

    @Inject
    private ClientRepository clientRepository;

    public ClientApi getClient(String personIdentification) {
        logger.log(Level.FINE, "getClient(personIdentification: {0})",
                new Object[] {"Secret person identification number parameter"});

        Client client = clientRepository.getClientByPersonIdentification(personIdentification);

        if (client == null) {
            return null;
        }

        return clientAdapter.translateClientToClientApi(client);
    }
}
