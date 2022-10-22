package org.helmo.sd_projet.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewTests {

    @Test
    public void constructorTest() {
        Person director = new Person("David", "Fincher", null);
        Person actor1 = new Person("Morgan", "Freeman", LocalDate.of(1937, Calendar.JUNE, 1));
        Person actor2 = new Person("Brad", "Pitt", LocalDate.of(1963, Calendar.DECEMBER, 18));
        Movie movie = new Movie("S7ven", 1995, Duration.ofMinutes(127), List.of(director), List.of(actor1, actor2), new HashSet<>());
        Customer customer = new Customer("LUDFR");
        LocalDate creationDate = LocalDate.now();
        Review review = new Review("Huge casting for a great story.", 9, customer, movie, creationDate);

        assertEquals("Huge casting for a great story.", review.getComment());
        assertEquals(customer, review.getAuthor());
        assertEquals(movie, review.getMovie());
        assertEquals(creationDate, review.getCreationDate());
        assertEquals(9, review.getEvaluation());
    }

    @Test
    public void equalsTest() {
        Person director = new Person("David", "Fincher", null);
        Person actor1 = new Person("Morgan", "Freeman", LocalDate.of(1937, Calendar.JUNE, 1));
        Person actor2 = new Person("Brad", "Pitt", LocalDate.of(1963, Calendar.DECEMBER, 18));
        Movie movie = new Movie("S7ven", 1995, Duration.ofMinutes(127), List.of(director), List.of(actor1, actor2), new HashSet<>());
        Customer customer = new Customer("LUDFR");
        LocalDate creationDate = LocalDate.now();
        Review review = new Review("Huge casting for a great story.", 9, customer, movie, creationDate);
        Review review2 = new Review("Huge casting for a great story.", 9, customer, movie, creationDate);
        Review review3 = new Review("Large casting for a great story.", 9, customer, movie, creationDate);

        assertEquals(review, review2);
        assertNotEquals(review, review3);

    }
}
