package se.jsquad.adapter;

import se.jsquad.Account;
import se.jsquad.AccountTransaction;
import se.jsquad.Client;
import se.jsquad.client.info.AccountApi;
import se.jsquad.client.info.AccountTransactionApi;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.PersonApi;
import se.jsquad.client.info.TransactionTypeApi;

import java.util.HashSet;
import java.util.Set;

public class ClientAdapter {
    public ClientApi translateClientToClientApi(Client client) {
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

                accountApi.getAccountTransactionSet().add(accountTransactionApi);
            }

            accountApiSet.add(accountApi);
        }


        clientApi.setPerson(personApi);
        clientApi.getAccountSet().addAll(accountApiSet);

        return clientApi;
    }
}
