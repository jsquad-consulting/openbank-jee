package se.jsquad;

import se.jsquad.getclientservice.AccountTransactionType;
import se.jsquad.getclientservice.AccountType;
import se.jsquad.getclientservice.ClientType;
import se.jsquad.getclientservice.ClientTypeType;
import se.jsquad.getclientservice.GetClientRequest;
import se.jsquad.getclientservice.GetClientResponse;
import se.jsquad.getclientservice.PersonType;
import se.jsquad.getclientservice.StatusType;
import se.jsquad.getclientservice.TransactionType;
import se.jsquad.getclientservice.Type;
import se.jsquad.qualifier.Log;
import se.jsquad.repository.ClientRepository;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebService
public class GetClientWsBusiness {
    @Inject
    @Log
    private Logger logger;

    @Resource
    private WebServiceContext webServiceContext;

    @Inject
    private ClientRepository clientRepository;

    public GetClientResponse getClientResponse(GetClientRequest request) {
        GetClientResponse getClientResponse = new GetClientResponse();
        getClientResponse.setClient(null);
        getClientResponse.setMessage("Client not found.");
        getClientResponse.setStatus(StatusType.ERROR);

        if (request == null) {
            logger.log(Level.FINE, "Request parameter must be set, can't be null.");
            getClientResponse.setMessage("Request must be set with proper person identification number.");
            return getClientResponse;
        }

        if (!webServiceContext.isUserInRole(RoleConstants.ADMIN)
                && !webServiceContext.isUserInRole(RoleConstants.CUSTOMER)) {
            getClientResponse.setMessage("Unauthorized request!");
            return getClientResponse;
        }

        se.jsquad.Client client = clientRepository.getClientByPersonIdentification(request.getPersonIdentification());

        if (client == null) {
            getClientResponse.setStatus(StatusType.WARNING);
        } else {
            getClientResponse.setStatus(StatusType.OK);
            getClientResponse.setMessage("Client found.");

            createClientType(getClientResponse, client);
        }

        return getClientResponse;
    }


    private void createClientType(GetClientResponse getClientResponse, Client client) {
        se.jsquad.getclientservice.ClientType clientType = new ClientType();
        getClientResponse.setClient(clientType);

        clientType.setPerson(new PersonType());
        clientType.getPerson().setFirstName(client.getPerson().getFirstName());
        clientType.getPerson().setLastName(client.getPerson().getLastName());
        clientType.getPerson().setMail(client.getPerson().getMail());
        clientType.getPerson().setPersonIdentification(client.getPerson().getPersonIdentification());

        clientType.setClientType(new ClientTypeType());

        if (client.getClientType() instanceof RegularClient) {
            clientType.getClientType().setRating(((RegularClient) client.getClientType()).getRating());
            clientType.getClientType().setType(Type.REGULAR);
        } else if (client.getClientType() instanceof PremiumClient) {
            clientType.getClientType().setPremiumRating(((PremiumClient) client.getClientType()).getPremiumRating());
            clientType.getClientType().setSpecialOffers(((PremiumClient) client.getClientType()).getSpecialOffers());
            clientType.getClientType().setType(Type.PREMIUM);
        } else {
            clientType.getClientType().setCountry(((ForeignClient) client.getClientType()).getCountry());
            clientType.getClientType().setType(Type.FOREIGN);
        }

        if (webServiceContext.isUserInRole(RoleConstants.ADMIN) && client.getAccountSet() != null) {
            Iterator<Account> accountIterator = client.getAccountSet().iterator();
            while (accountIterator.hasNext()) {
                Account account = accountIterator.next();

                AccountType accountType = new AccountType();
                accountType.setBalance(account.getBalance());

                if (account.getAccountTransactionSet() != null) {
                    Iterator<AccountTransaction> accountTransactionIterator = account.getAccountTransactionSet()
                            .iterator();
                    while (accountTransactionIterator.hasNext()) {
                        AccountTransaction accountTransaction = accountTransactionIterator.next();

                        AccountTransactionType accountTransactionType = new AccountTransactionType();
                        accountTransactionType.setMessage(accountTransaction.getMessage());
                        accountTransactionType.setTransactionType(TransactionType.valueOf(accountTransaction
                                .getTransactionType().name()));

                        accountType.getAccountTransactionList().add(accountTransactionType);
                    }
                }
                clientType.getAccountList().add(accountType);
            }
        }
    }
}
