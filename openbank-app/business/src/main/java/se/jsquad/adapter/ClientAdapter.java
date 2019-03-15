package se.jsquad.adapter;

import se.jsquad.Account;
import se.jsquad.AccountTransaction;
import se.jsquad.Client;
import se.jsquad.client.info.AccountApi;
import se.jsquad.client.info.AccountTransactionApi;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.PersonApi;
import se.jsquad.client.info.TransactionTypeApi;
import se.jsquad.repository.ClientRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ClientAdapter {
    Logger logger = Logger.getLogger(ClientAdapter.class.getName());

    @Inject
    private ClientRepository clientRepository;

    public ClientApi getClient(String personIdentification) {
        logger.log(Level.FINE, "getClient(personIdentification: {0})",
                new Object[]{"Secret person identification number parameter"});

        Client client = clientRepository.getClientByPersonIdentification(personIdentification);

        if (client == null) {
            return null;
        }

        ClientApi clientApi = new ClientApi();
        PersonApi personApi = new PersonApi();

        personApi.setFirstName(client.getPerson().getFirstName());
        personApi.setLastName(client.getPerson().getLastName());
        personApi.setPersonIdentification(client.getPerson().getPersonIdentification());

        Set<AccountApi> accountApiSet = new HashSet<>();

        for (Account account : client.getAccountSet()) {
            AccountApi accountApi = new AccountApi();

            accountApi.setBalance(account.getBalance());

            for (AccountTransaction accountTransaction : account.getAccountTransactionSet()) {
                AccountTransactionApi accountTransactionApi = new AccountTransactionApi();

                accountTransactionApi.setMessage(accountTransaction.getMessage());

                TransactionTypeApi transactionTypeApi = TransactionTypeApi.valueOf(accountTransaction.getTransactionType().name());

                accountTransactionApi.setTransactionType(transactionTypeApi);

                accountApi.getAccountTransactionSet().add(accountTransactionApi);
            }

            accountApiSet.add(accountApi);
        }


        clientApi.setPerson(personApi);
        clientApi.getAccountSet().addAll(accountApiSet);

        return clientApi;
    }
}
