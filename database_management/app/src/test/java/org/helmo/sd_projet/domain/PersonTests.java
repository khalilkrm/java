package org.helmo.sd_projet.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTests {

    @Test
    public void constructorTest() {
        Person person = new Person("Brad", "Pitt", LocalDate.of(1963, Calendar.DECEMBER, 18));
        assertEquals("Brad", person.getFirstname());
        assertEquals("Pitt", person.getLastname());
        assertEquals(1963, person.getBirthdate().getYear());
    }

    @Test
    public void equalsTest() {
        Person person = new Person("Brad", "Pitt", LocalDate.of(1963, Calendar.DECEMBER, 18));
        Person person2 = new Person("Brad", "Pitt", LocalDate.of(1963, Calendar.DECEMBER, 18));
        Person person3 = new Person("Brad", "Pit", LocalDate.of(1963, Calendar.DECEMBER, 18));
        Person person4 = new Person("brad", "pit", LocalDate.of(1963, Calendar.DECEMBER, 18));

        assertEquals(person, person2);
        assertNotEquals(person, person3);
        assertEquals(person4, person3);
    }

    @Test
    public void peronsContainsTest() {
        Persons persons = new Persons();
        Person me = new Person("François", "Ludewig", LocalDate.of(1980, 8, 19));
        Person she = new Person("Stéphanie", "Garray", LocalDate.of(1982, 7, 1));
        Person she2 = new Person("Judith", "Ludewig", LocalDate.of(2008, 9, 13));

        persons.addPerson(me, 1);
        persons.addPerson(she, 2);

        assertTrue(persons.contains(me));
        assertTrue(persons.contains(she));
        assertFalse(persons.contains(she2));
    }

    @Test
    public void peronsSearchTest() {
        Persons persons = new Persons();
        Person me = new Person("François", "Ludewig", LocalDate.of(1980, 8, 19));
        Person she = new Person("Stéphanie", "Garray", LocalDate.of(1982, 7, 1));
        Person she2 = new Person("Judith", "Ludewig", LocalDate.of(2008, 9, 13));

        persons.addPerson(me, 1);
        persons.addPerson(she, 2);
        persons.addPerson(she2, 3);

        var result = persons.searchPerson("lud");

        assertEquals(2, result.size());
        assertEquals(me, result.get(0));
        assertEquals(she2, result.get(1));
    }
}
