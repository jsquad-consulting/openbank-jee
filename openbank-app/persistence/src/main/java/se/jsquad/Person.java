package se.jsquad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Entity
public class Person implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Pattern(regexp = "\\d{12}")
    @Column(name = "PERSONIDENTIFICATION")
    private String personIdentification;

    @Pattern(regexp = "^\\D*$")
    @Column(name = "FIRSTNAME")
    private String firstName;

    @Pattern(regexp = "^\\D*$")
    @Column(name = "LASTNAME")
    private String lastName;

    @OneToOne
    @JoinColumn(name = "CLIENT_FK")
    private Client client;

    public Long getId() {
        return id;
    }

    public String getPersonIdentification() {
        return personIdentification;
    }

    public void setPersonIdentification(String personIdentification) {
        this.personIdentification = personIdentification;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
