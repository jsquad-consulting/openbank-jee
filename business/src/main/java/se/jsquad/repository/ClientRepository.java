package se.jsquad.repository;

import se.jsquad.entity.Account;
import se.jsquad.entity.Client;
import se.jsquad.qualifier.Log;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;

public class ClientRepository extends EntityManagerProducer {
    @Inject @Log
    private Logger logger;

    public Client getClientByPersonIdentification(String personIdentification) {
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

    public Account getAccountByNumber(String accountNumber) {
        TypedQuery<Account> query = getEntityManager().createNamedQuery(Account.ACCOUNT_ID, Account.class);
        query.setParameter(Account.PARAM_ACCOUNT_NUMBER, accountNumber);

        List<Account> accountList = query.getResultList();

        if (accountList == null || accountList.isEmpty()) {
            return null;
        } else {
            return accountList.get(0);
        }
    }

    public void createClient(Client client) {
        getEntityManager().persist(client);
    }
}
