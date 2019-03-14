package se.jsquad;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
    private Long id;

    @OneToOne(mappedBy = "client",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Person person;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "CLIENT_JOIN_ACCOUNT", joinColumns = {@JoinColumn(name = "CLIENT_FK", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "ACCOUNT_FK", referencedColumnName = "id")})
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
}
