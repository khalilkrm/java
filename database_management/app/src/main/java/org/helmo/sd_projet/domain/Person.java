package org.helmo.sd_projet.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Person {
    private final String firstname;
    private final String lastname;
    private final LocalDate birthdate;

    public Person(String firstname, String lastname, LocalDate birthdate) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Person person = (Person) o;
        return firstname.equalsIgnoreCase(person.firstname) && lastname.equalsIgnoreCase(person.lastname)
                && birthdate.equals(person.birthdate);
    }

    @Override
    public String toString() {
        return String.format(
                "{\"firstname\":\"%s\"," +
                        "\"birthdate\":\"%s\"," +
                        "\"lastname\":\"%s\"}", firstname, birthdate, lastname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, birthdate);
    }

}
