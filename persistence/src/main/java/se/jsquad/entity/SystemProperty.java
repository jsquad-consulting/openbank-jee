package se.jsquad.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Cacheable(true)
@Table(name = "SYSTEMPROPERTY")
@NamedQuery(name = SystemProperty.FIND_ALL_UNIQUE_SYSTEM_PROPERTIES, query = "SELECT DISTINCT sp FROM SystemProperty"
        + " as sp")
public class SystemProperty {
    public static final String FIND_ALL_UNIQUE_SYSTEM_PROPERTIES = "FIND_ALL_UNIQUE_SYSTEM_PROPERTIES";

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
    private String value;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
