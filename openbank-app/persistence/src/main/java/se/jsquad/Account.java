package se.jsquad;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ACCOUNT")
public class Account implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "BALANCE")
    private Long balance;

    @ManyToOne
    @JoinTable(name = "CLIENT_JOIN_ACCOUNT",
            joinColumns = {@JoinColumn(name = "ACCOUNT_FK", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "CLIENT_FK", referencedColumnName = "id")})
    private Client client;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "ACCOUNT_JOIN_ACCOUNTTRANSACTION", joinColumns = {@JoinColumn(name = "ACCOUNT_FK",
            referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "ACCOUNTTRANSACTION_FK",
            referencedColumnName = "id")})
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

}
