package se.jsquad.generator;

import se.jsquad.entity.Account;
import se.jsquad.entity.AccountTransaction;
import se.jsquad.entity.Address;
import se.jsquad.entity.Client;
import se.jsquad.entity.Person;
import se.jsquad.entity.PremiumClient;
import se.jsquad.entity.RegularClient;
import se.jsquad.entity.TransactionType;

import java.util.HashSet;
import java.util.Set;

public class DatabaseGenerator {
    public static final String SPECIAL_OFFER_YOU_CAN_NOT_REFUSE = "Special offer you can not refuse.";
    public static final String IN_WITHDRAWAL = "500$ in withdrawal";

    public Set<Client> populateDatabase() {
        Set<Client> clientSet = new HashSet<>();

        Client client1 = new Client();
        client1.setClientType(new RegularClient());
        client1.getClientType().setClient(client1);
        ((RegularClient) client1.getClientType()).setRating(Long.valueOf(500));

        client1.setAccountSet(new HashSet<>());

        Account account = new Account();
        account.setBalance(Long.valueOf(500));
        account.setAccountNumber("1000");
        account.setClient(client1);

        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setMessage("500$ in deposit");
        accountTransaction.setTransactionType(TransactionType.DEPOSIT);
        accountTransaction.setAccount(account);

        account.setAccountTransactionSet(new HashSet<>());
        account.getAccountTransactionSet().add(accountTransaction);

        client1.getAccountSet().add(account);
        client1.setPerson(new Person());

        client1.getPerson().setClient(client1);
        client1.getPerson().setFirstName("John");
        client1.getPerson().setLastName("Doe");
        client1.getPerson().setPersonIdentification("191212121212");
        client1.getPerson().setMail("john.doe@test.se");

        clientSet.add(client1);

        client1 = new Client();
        client1.setClientType(new PremiumClient());
        client1.getClientType().setClient(client1);
        ((PremiumClient) client1.getClientType()).setPremiumRating(Long.valueOf(1000));
        ((PremiumClient) client1.getClientType()).setSpecialOffers(SPECIAL_OFFER_YOU_CAN_NOT_REFUSE);

        client1.setAccountSet(new HashSet<>());

        account = new Account();
        account.setBalance(Long.valueOf(1000));
        account.setAccountNumber("1001");
        account.setClient(client1);

        accountTransaction = new AccountTransaction();
        accountTransaction.setMessage(IN_WITHDRAWAL);
        accountTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        accountTransaction.setAccount(account);

        account.setAccountTransactionSet(new HashSet<>());
        account.getAccountTransactionSet().add(accountTransaction);

        client1.getAccountSet().add(account);

        client1.setPerson(new Person());

        client1.getPerson().setClient(client1);
        client1.getPerson().setFirstName("Alice");
        client1.getPerson().setLastName("Doe");
        client1.getPerson().setPersonIdentification("191212121213");
        client1.getPerson().setMail("alice.doe@test.se");

        Address address = new Address();
        address.setCountry("Sweden");
        address.setMunicipality("Stockholm county");
        address.setPostalCode(17211);
        address.setStreet("Vallhallavägen");
        address.setStreetNumber(12);

        Set<Person> personSet = new HashSet<>();
        personSet.add(client1.getPerson());

        address.setPersonSet(personSet);

        Set<Address> addressSet = new HashSet<>();
        addressSet.add(address);

        client1.getPerson().setAddressSet(addressSet);

        clientSet.add(client1);

        client1 = new Client();
        client1.setClientType(new PremiumClient());
        client1.getClientType().setClient(client1);
        ((PremiumClient) client1.getClientType()).setPremiumRating(Long.valueOf(1000));
        ((PremiumClient) client1.getClientType()).setSpecialOffers(SPECIAL_OFFER_YOU_CAN_NOT_REFUSE);

        client1.setAccountSet(new HashSet<>());

        account = new Account();
        account.setBalance(Long.valueOf(1000));
        account.setAccountNumber("1051");
        account.setClient(client1);

        accountTransaction = new AccountTransaction();
        accountTransaction.setMessage(IN_WITHDRAWAL);
        accountTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        accountTransaction.setAccount(account);

        account.setAccountTransactionSet(new HashSet<>());
        account.getAccountTransactionSet().add(accountTransaction);

        client1.getAccountSet().add(account);

        client1.setPerson(new Person());

        client1.getPerson().setClient(client1);
        client1.getPerson().setFirstName("Alice");
        client1.getPerson().setLastName("Doe");
        client1.getPerson().setPersonIdentification("191212121221");
        client1.getPerson().setMail("alice.doe@test.se");

        clientSet.add(client1);

        client1 = new Client();
        client1.setClientType(new PremiumClient());
        client1.getClientType().setClient(client1);
        ((PremiumClient) client1.getClientType()).setPremiumRating(Long.valueOf(1000));
        ((PremiumClient) client1.getClientType()).setSpecialOffers(SPECIAL_OFFER_YOU_CAN_NOT_REFUSE);

        client1.setAccountSet(new HashSet<>());

        account = new Account();
        account.setBalance(Long.valueOf(500));
        account.setAccountNumber("1050");
        account.setClient(client1);

        accountTransaction = new AccountTransaction();
        accountTransaction.setMessage(IN_WITHDRAWAL);
        accountTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        accountTransaction.setAccount(account);

        account.setAccountTransactionSet(new HashSet<>());
        account.getAccountTransactionSet().add(accountTransaction);

        client1.getAccountSet().add(account);

        client1.setPerson(new Person());

        client1.getPerson().setClient(client1);
        client1.getPerson().setFirstName("John");
        client1.getPerson().setLastName("Doe");
        client1.getPerson().setPersonIdentification("191212121220");
        client1.getPerson().setMail("john.doe@test.se");

        clientSet.add(client1);

        return clientSet;
    }
}
