package se.jsquad.repository;

import se.jsquad.Client;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientRepository extends EntityManagerProducer {
    private static final Logger logger = Logger.getLogger(ClientRepository.class.getName());

    public Client getClientByPersonIdentification(String personIdentification) {
        logger.log(Level.FINE, "getClientByPersonIdentification(personIdentification: {0})",
                new Object[] {"hidden"});

        TypedQuery<Client> query = getEntityManager().createNamedQuery(Client.PERSON_IDENTIFICATION,
                Client.class);
        query.setParameter(Client.PARAM_PERSON_IDENTIFICATION, personIdentification);

        List<Client> clientList = query.getResultList();

        if (clientList == null || clientList.isEmpty()) {
            return null;
        } else {
            return clientList.get(0);
        }
    }

    public void createClient(Client client) {
        logger.log(Level.FINE, "createClient(client: {0})",
                new Object[] {"hidden"});

        client.getAccountSet().clear();
        getEntityManager().persist(client);
    }
}
