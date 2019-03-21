package se.jsquad.adapter;

import se.jsquad.Account;
import se.jsquad.AccountTransaction;
import se.jsquad.Client;
import se.jsquad.Person;
import se.jsquad.TransactionType;
import se.jsquad.client.info.AccountApi;
import se.jsquad.client.info.AccountTransactionApi;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.PersonApi;
import se.jsquad.client.info.TransactionTypeApi;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientAdapter {
    private static final Logger logger = Logger.getLogger(ClientAdapter.class.getName());

    public ClientApi translateClientToClientApi(Client client) {
        logger.log(Level.FINE, "translateClientToClientApi: {0})",
                new Object[] {"hidden"});

        ClientApi clientApi = new ClientApi();
        PersonApi personApi = new PersonApi();

        personApi.setFirstName(client.getPerson().getFirstName());
        personApi.setLastName(client.getPerson().getLastName());
        personApi.setPersonIdentification(client.getPerson().getPersonIdentification());
        personApi.setMail(client.getPerson().getMail());

        Set<AccountApi> accountApiSet = new HashSet<>();

        for (Account account : client.getAccountSet()) {
            AccountApi accountApi = new AccountApi();

            accountApi.setBalance(account.getBalance());

            for (AccountTransaction accountTransaction : account.getAccountTransactionSet()) {
                AccountTransactionApi accountTransactionApi = new AccountTransactionApi();

                accountTransactionApi.setMessage(accountTransaction.getMessage());

                TransactionTypeApi transactionTypeApi = TransactionTypeApi.valueOf(accountTransaction
                        .getTransactionType().name());

                accountTransactionApi.setTransactionType(transactionTypeApi);

                accountApi.getAccountTransactionList().add(accountTransactionApi);
            }

            accountApiSet.add(accountApi);
        }


        clientApi.setPerson(personApi);
        clientApi.getAccountList().addAll(accountApiSet);

        return clientApi;
    }

    public Client translateClientApiToClient(ClientApi clientApi) {
        logger.log(Level.FINE, "translateClientApiToClient: {0})",
                new Object[] {"hidden"});

        Client client = new Client();
        client.setPerson(new Person());

        client.getPerson().setFirstName(clientApi.getPerson().getFirstName());
        client.getPerson().setLastName(clientApi.getPerson().getLastName());
        client.getPerson().setPersonIdentification(clientApi.getPerson().getPersonIdentification());
        client.getPerson().setMail(clientApi.getPerson().getMail());
        client.getPerson().setClient(client);

        Set<Account> accountSet = new HashSet<>();

        if (clientApi.getAccountList() != null) {
            for(AccountApi accountApi : clientApi.getAccountList()) {
                Account account = new Account();

                account.setBalance(accountApi.getBalance());
                account.setAccountTransactionSet(new HashSet<>());

                if (accountApi.getAccountTransactionList() != null) {
                    for(AccountTransactionApi accountTransactionApi : accountApi.getAccountTransactionList()) {
                        AccountTransaction accountTransaction = new AccountTransaction();
                        accountTransaction.setAccount(account);
                        accountTransaction.setMessage(accountTransactionApi.getMessage());
                        accountTransaction.setTransactionType(TransactionType.valueOf(accountTransactionApi
                                .getTransactionType().value()));

                        account.getAccountTransactionSet().add(accountTransaction);
                    }
                }

                account.setClient(client);

                accountSet.add(account);
            }
        }

        client.setAccountSet(accountSet);

        return client;
    }
}
