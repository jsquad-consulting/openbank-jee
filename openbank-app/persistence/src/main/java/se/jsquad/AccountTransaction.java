package se.jsquad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ACCOUNTTRANSACTION")
public class AccountTransaction implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "TRANSACTIONTYPE")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "MESSAGE")
    private String message;

    @ManyToOne
    @JoinTable(name = "ACCOUNT_JOIN_ACCOUNTTRANSACTION",
            joinColumns = {@JoinColumn(name = "ACCOUNTTRANSACTION_FK",
                    referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "ACCOUNT_FK",
                    referencedColumnName = "id")})
    private Account account;

    public Long getId() {
        return id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getMessage() {
        return message;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
