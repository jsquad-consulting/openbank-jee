package se.jsquad.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ACCOUNT")
@NamedQuery(name = Account.ACCOUNT_ID, query = "SELECT a FROM Account a WHERE a.accountNumber =:" + Account.PARAM_ACCOUNT_NUMBER)
public class Account implements Serializable {
    public static final String ACCOUNT_ID = "ACCOUNT_ID";
    public static final String PARAM_ACCOUNT_NUMBER = "PARAM_ACCOUNT_NUMBER";

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "BALANCE")
    private Long balance;

    @Column(name = "ACCOUNT_NUMBER")
    private String accountNumber;

    @ManyToOne
    private Client client;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<AccountTransaction> accountTransactionSet;

    public Long getId() {
        return id;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;


    }

    public Set<AccountTransaction> getAccountTransactionSet() {
        if (accountTransactionSet == null) {
            accountTransactionSet = new HashSet<>();
        }
        return accountTransactionSet;
    }

    public void setAccountTransactionSet(Set<AccountTransaction> accountTransactionSet) {
        this.accountTransactionSet = accountTransactionSet;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
