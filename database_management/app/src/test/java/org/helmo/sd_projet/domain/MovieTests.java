package org.helmo.sd_projet.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MovieTests {

    @Test
    public void constructorTest() {
        Person director = new Person("David", "Fincher", LocalDate.of(1937, Calendar.JUNE, 1));
        Person actor1 = new Person("Morgan", "Freeman", LocalDate.of(1937, Calendar.JUNE, 1));
        Person actor2 = new Person("Brad", "Pitt", LocalDate.of(1963, Calendar.DECEMBER, 18));
        Movie movie = new Movie("S7ven", 1995, Duration.ofMinutes(127), List.of(director), List.of(actor1, actor2), new HashSet<>());

        assertEquals("S7ven", movie.getTitle());
        assertEquals("PT2H7M", movie.getDuration().toString());
        assertEquals(1995, movie.getRelease_year());
        assertEquals(director, movie.directorIterator().next());

        var ite = movie.castingIterator();
        assertTrue(ite.hasNext());
        assertEquals(actor1, ite.next());
        assertTrue(ite.hasNext());
        assertEquals(actor2, ite.next());
        assertFalse(ite.hasNext());
    }

    @Test
    public void equalsTest() {
        Person director = new Person("David", "Fincher", LocalDate.of(1937, Calendar.JUNE, 1));
        Person actor1 = new Person("Morgan", "Freeman", LocalDate.of(1937, Calendar.JUNE, 1));
        Person actor2 = new Person("Brad", "Pitt", LocalDate.of(1963, Calendar.DECEMBER, 18));
        Movie movie = new Movie("S7ven", 1995, Duration.ofMinutes(127), List.of(director), List.of(actor1, actor2), new HashSet<>());
        Movie movie2 = new Movie("S7ven", 1995, Duration.ofMinutes(127), List.of(director), List.of(actor1, actor2), new HashSet<>());
        Movie movie3 = new Movie("Seven", 1995, Duration.ofMinutes(127), List.of(director), List.of(actor1, actor2), new HashSet<>());

        assertEquals(movie, movie2);
        assertNotEquals(movie, movie3);
    }

    @Test
    public void moviesContainsTest() {
        Movies movies = new Movies();

        Movie fc = new Movie("Fight Club", 1999, Duration.ofMinutes(139), null, null, new HashSet<>());
        Movie dune84 = new Movie("Dune", 1984, Duration.ofMinutes(180), null, null, new HashSet<>());
        Movie dune = new Movie("Dune", 2021, Duration.ofMinutes(180), null, null, new HashSet<>());

        movies.addMovie(fc, 1);
        movies.addMovie(dune, 2);

        assertTrue(movies.contains(fc));
        assertTrue(movies.contains(dune));
        assertFalse(movies.contains(dune84));
    }

    @Test
    public void moviesSearchTest() {
        Movies movies = new Movies();

        Movie fc = new Movie("Fight Club", 1999, Duration.ofMinutes(139), null, null, new HashSet<>());
        Movie dune84 = new Movie("Dune", 1984, Duration.ofMinutes(180), null, null, new HashSet<>());
        Movie dune = new Movie("Dune", 2021, Duration.ofMinutes(180), null, null, new HashSet<>());

        movies.addMovie(fc, 1);
        movies.addMovie(dune, 2);
        movies.addMovie(dune84, 3);

        var result = movies.searchMovie("dune");

        assertEquals(2, result.size());
        assertEquals(dune, result.get(0));
        assertEquals(dune84, result.get(1));
    }
}
