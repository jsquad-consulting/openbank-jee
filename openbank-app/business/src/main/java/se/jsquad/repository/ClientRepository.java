package se.jsquad.repository;

import se.jsquad.Client;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ClientRepository extends EntityManagerProducer {
    Logger logger = Logger.getLogger(ClientRepository.class.getName());

    public Client getClientByPersonIdentification(String personIdentification) {
        logger.log(Level.FINE, "getClientByPersonIdentification({0})",
                new Object[]{"secret person identification parameter"});

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
}
