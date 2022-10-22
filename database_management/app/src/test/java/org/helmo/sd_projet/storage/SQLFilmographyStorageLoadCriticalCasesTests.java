package org.helmo.sd_projet.storage;

import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.storage.exception.UnableToLoadMovieException;
import org.helmo.sd_projet.storage.exception.UnableToLoadPersonsException;
import org.helmo.sd_projet.storage.exception.UnableToLoadReviewsException;
import org.helmo.sd_projet.storage.exception.UnableToLoadUserException;
import org.helmo.sd_projet.storage.connection.ConnectionData;
import org.helmo.sd_projet.storage.connection.ConnectionFactory;
import org.helmo.sd_projet.storage.connection.DerbyConnectionData;
import org.helmo.sd_projet.storage.utility.FilmographyDBPopulate;
import org.helmo.sd_projet.utility.data.FakeDBData;
import org.helmo.sd_projet.utility.FilmographyStorageTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLFilmographyStorageLoadCriticalCasesTests {

    private static final ConnectionData connectionData = new DerbyConnectionData();

    private static FilmographyDBPopulate populater;

    private static Customers customers;
    private static Persons persons;
    private static Movies movies;
    private static Reviews reviews;
    private static Categories categories;


    @BeforeAll
    static void setup() {
        // Given data
        populater = FilmographyStorageTestUtils.from(connectionData).connect();
    }

    @BeforeEach
    public void beforeEach() {
        customers = new Customers();
        persons = new Persons();
        movies = new Movies();
        reviews = new Reviews();
        categories = new Categories();
        populater.reset().populate(FakeDBData.SafeData);
    }

    @AfterAll
    static void afterAll() throws Exception {
        populater.reset();
        if (populater != null)
            populater.close();
    }


    @Test
    public void givenData_WhenLoadPersonWithDouble_ThenOnlyOneIsLoadedInLocal() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {

            // When load persons with double
            storage.loadPersons();

            // Then only one is loaded
            final var current = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(persons.getIterator(), Spliterator.ORDERED), false)
                    .map(Map.Entry::getValue).collect(Collectors.toList());
            final var expected = new ArrayList<>(new HashSet<>(current));

            assertThat(current).hasSameElementsAs(expected);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenData_WhenLoadCustomersWithDouble_ThenOnlyOneIsLoadedInLocal() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {

            // When load Customers with double
            storage.loadUsers();

            // Then only one is loaded
            final var current = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(customers.getIterator(), Spliterator.ORDERED), false)
                    .map(Map.Entry::getValue).collect(Collectors.toList());
            final var expected = new ArrayList<>(new HashSet<>(current));

            assertThat(current).hasSameElementsAs(expected);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenData_WhenLoadMovieWithDouble_ThenOnlyOneIsLoadedInLocal() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {

            // When load movies with double
            storage.load();

            // Then only one is loaded
            final var current = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(movies.getIterator(), Spliterator.ORDERED), false)
                    .map(Map.Entry::getValue).collect(Collectors.toList());
            final var expected = new ArrayList<>(new HashSet<>(current));

            assertThat(current).hasSameElementsAs(expected);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenProblemConnection_WhenLoadUser_ThenThrowException() {
        try {
            final Connection connection = ConnectionFactory.createConnection(new DerbyConnectionData());
            try (final FilmographyStorage storage = FilmographyStorageTestUtils.from(connection, customers, persons,
                    movies, reviews, categories)) {
                connection.close();
                assertThrows(UnableToLoadUserException.class, storage::loadUsers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenProblemConnection_WhenLoadPersons_ThenThrowException() {
        try {
            final Connection connection = ConnectionFactory.createConnection(new DerbyConnectionData());
            try (final FilmographyStorage storage = FilmographyStorageTestUtils.from(connection, customers, persons,
                    movies, reviews, categories)) {
                connection.close();
                assertThrows(UnableToLoadPersonsException.class, storage::loadPersons);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenProblemConnection_WhenLoadMovies_ThenThrowException() {
        try {
            final Connection connection = ConnectionFactory.createConnection(new DerbyConnectionData());
            try (final FilmographyStorage storage = FilmographyStorageTestUtils.from(connection, customers, persons,
                    movies, reviews, categories)) {
                storage.loadPersons();
                connection.close();
                assertThrows(UnableToLoadMovieException.class, storage::loadMovies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenProblemConnection_WhenLoadReviews_ThenThrowException() {
        try {
            final Connection connection = ConnectionFactory.createConnection(new DerbyConnectionData());
            try (final FilmographyStorage storage = FilmographyStorageTestUtils.from(connection, customers, persons,
                    movies, reviews, categories)) {
                storage.loadUsers();
                connection.close();
                assertThrows(UnableToLoadReviewsException.class, storage::loadReviews);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
