package se.jsquad.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "ADDRESS")
public class Address implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @ManyToMany(cascade = {CascadeType.ALL})
    private Set<Person> personSet;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "POSTALCODE")
    private Integer postalCode;

    @Column(name = "MUNICIPALITY")
    private String municipality;

    @Column(name = "STREET")
    private String street;

    @Column(name = "STREETNUMBER")
    private Integer streetNumber;

    public Long getId() {
        return id;
    }

    public Set<Person> getPersonSet() {
        return personSet;
    }

    public void setPersonSet(Set<Person> personSet) {
        this.personSet = personSet;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(Integer streetNumber) {
        this.streetNumber = streetNumber;
    }
}
