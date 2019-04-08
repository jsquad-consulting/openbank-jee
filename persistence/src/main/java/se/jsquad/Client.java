package se.jsquad;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CLIENT")
@NamedQuery(name = Client.PERSON_IDENTIFICATION, query = "SELECT c FROM Client c WHERE " +
        "c.person.personIdentification = :" + Client.PARAM_PERSON_IDENTIFICATION)
public class Client implements Serializable {
    public static final String PERSON_IDENTIFICATION = "PERSON_IDENTIFICATION";
    public static final String PARAM_PERSON_IDENTIFICATION = "personIdentification";

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @OneToOne(mappedBy = "client",
            cascade = {CascadeType.ALL})
    private Person person;

    @OneToOne(mappedBy = "client", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private ClientType clientType;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<Account> accountSet;

    public Long getId() {
        return id;
    }

    public Set<Account> getAccountSet() {
        if (accountSet == null) {
            accountSet = new HashSet<>();
        }

        return accountSet;
    }

    public void setAccountSet(Set<Account> accountSet) {
        this.accountSet = accountSet;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }
}
