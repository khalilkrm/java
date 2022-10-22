package org.helmo.sd_projet.storage;

import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.storage.exception.UnableToConnectException;
import org.helmo.sd_projet.storage.connection.ConnectionData;
import org.helmo.sd_projet.storage.connection.DerbyConnectionData;
import org.helmo.sd_projet.storage.connection.IncorrectConnectionData;
import org.helmo.sd_projet.storage.connection.IncorrectConnectionData.Mistakes;
import org.helmo.sd_projet.storage.utility.FilmographyDBPopulate;
import org.helmo.sd_projet.utility.data.FakeDBData;
import org.helmo.sd_projet.storage.utility.RequestTestUtils;
import org.helmo.sd_projet.utility.FilmographyStorageTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Fail.fail;
import static org.assertj.db.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class SQLFilmographyStorageLoadStandardCaseTests {

    private static final ConnectionData connectionData = new DerbyConnectionData();
    final Source source = new Source(connectionData.getDBPath(), connectionData.getUsername(),
            connectionData.getPassword());
    private static FilmographyDBPopulate populater;

    private static Customers customers;
    private static Persons persons;
    private static Movies movies;
    private static Reviews reviews;
    private static Categories categories;

    @BeforeAll
    static void setup() {
        customers = new Customers();
        persons = new Persons();
        movies = new Movies();
        reviews = new Reviews();
        categories = new Categories();
        populater = FilmographyStorageTestUtils.from(connectionData).connect();
    }

    @BeforeEach
    public void setupEach() {
        // Given data
        populater.reset().populate(FakeDBData.SafeData);
    }

    @AfterAll
    static void teardown() throws Exception {
        populater.reset();
        if (populater != null)
            populater.close();
    }

    /* --- UTILS --- */

    private <T> Stream<T> iteratorToSteam(Iterator<T> iterator) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

    private <T> List<T> iteratorToList(final Iterator<T> iterator) {
        return iteratorToSteam(iterator).collect(Collectors.toUnmodifiableList());
    }

    // from movie's reviews to review's customer
    private Iterator<Customer> reviewCustomers(final List<Review> reviews) {
        return reviews.stream().map(Review::getAuthor).collect(Collectors.toUnmodifiableList()).iterator();
    }

    @Test
    public void givenDerbyConnectionData_WhenConnectAndEnd_ThenConnectionIsClosed() {

        final SQLFilmographyStorageFactory factory = new SQLFilmographyStorageFactory(connectionData);

        try (final FilmographyStorage unused = factory.newStorage(customers, persons, movies, reviews, categories)) {
        } catch (Exception e) {
            fail("Exception occurred while testing connection closing");
        }

        try {
            assertTrue(factory.connectionIsClosed());
        } catch (SQLException e) {
            fail("Could not test connection closing state after use");
        }
    }

    @Test
    public void givenDerbyConnectionData_whenConnect_ThenNothingThrown() {
        assertDoesNotThrow(() -> new SQLFilmographyStorageFactory(connectionData)
                .newStorage(customers, persons, movies, reviews, categories));
    }

    @Test
    public void givenBadDriverNameConnectionData_whenConnect_ThenThrowException() {
        assertThrows(UnableToConnectException.class,
                () -> new SQLFilmographyStorageFactory(
                        IncorrectConnectionData.withMistake(Mistakes.DRIVER_NAME_MISTAKE))
                        .newStorage(customers, persons, movies, reviews, categories));
    }

    @Test
    public void givenBadDBPathConnectionData_whenConnect_ThenThrowException() {
        assertThrows(UnableToConnectException.class,
                () -> new SQLFilmographyStorageFactory(IncorrectConnectionData.withMistake(Mistakes.DBPATH_MISTAKE))
                        .newStorage(customers, persons, movies, reviews, categories));
    }

    @Test
    public void givenBadUsernameConnectionData_WhenConnect_ThenThrowException() {
        assertThrows(UnableToConnectException.class,
                () -> new SQLFilmographyStorageFactory(IncorrectConnectionData.withMistake(Mistakes.USERNAME_MISTAKE))
                        .newStorage(customers, persons, movies, reviews, categories));
    }

    @Test
    public void givenData_WhenPopulate_ThenDatabasePopulated() {
        Table table = new Table(source, "PERSON");
        assertThat(table).hasNumberOfRows(5);
    }

    @Test
    public void givenData_WhenLoadUsers_ThenUsersLoadedToLocalStorage() {
        try (FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When load users
            storage.loadUsers();
            // Then users loaded to local storage
            assertEquals(iteratorToList(customers.getIterator()).size(), RequestTestUtils.count("CUSTOMER"));
            final var expected = RequestTestUtils.column(
                    resultSet -> RequestTestUtils.stringSelector(resultSet, "NICKNAME"),
                    "CUSTOMER");
            assertEquals(expected,
                    iteratorToSteam(customers.getIterator())
                            .map(integerCustomerEntry -> integerCustomerEntry.getValue().getNickname())
                            .collect(Collectors.toUnmodifiableList()));
        } catch (final Exception exception) {
            fail("");
        }
    }

    @Test
    public void givenData_WhenLoadPersons_ThenPersonsLoadedToLocalStorage() {
        try (FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When load persons
            storage.loadPersons();
            // Then data loaded to storage
            assertEquals(iteratorToList(persons.getIterator()).size(), RequestTestUtils.count("PERSON"));
            final var expectedFirstnames = RequestTestUtils
                    .column(resultSet -> RequestTestUtils.stringSelector(resultSet, "FIRSTNAME"),
                            "PERSON");
            assertEquals(expectedFirstnames, iteratorToSteam(persons.getIterator())
                    .map(integerPersonEntry -> integerPersonEntry.getValue().getFirstname())
                    .collect(Collectors.toUnmodifiableList()));

            final var expectedLastnames = RequestTestUtils.column(
                    resultSet -> RequestTestUtils.stringSelector(resultSet, "LASTNAME"),
                    "PERSON");

            assertEquals(expectedLastnames, iteratorToSteam(persons.getIterator())
                    .map(integerPersonEntry -> integerPersonEntry.getValue().getLastname())
                    .collect(Collectors.toUnmodifiableList()));

            final var expectedBirthdate = RequestTestUtils.column(
                    resultSet -> RequestTestUtils.stringSelector(resultSet, "BIRTHDATE"),
                    "PERSON");

            assertEquals(expectedBirthdate, iteratorToSteam(persons.getIterator())
                    .map(integerPersonEntry -> integerPersonEntry.getValue().getBirthdate().toString())
                    .collect(Collectors.toUnmodifiableList()));

        } catch (final Exception exception) {
            fail("Exception occurred while testing persons loading");
        }
    }

    @Test
    public void givenData_WhenLoadMovies_ThenMoviesLoadedToLocalStorage() {
        try (FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When load movies
            storage.loadMovies();
            // Then data loaded to storage
            assertEquals(iteratorToList(movies.getIterator()).size(), RequestTestUtils.count("MOVIE"));

            final var expectedNames = RequestTestUtils.column(
                    resultSet -> RequestTestUtils.stringSelector(resultSet, "NAME"), "MOVIE");

            assertEquals(expectedNames, iteratorToSteam(movies.getIterator())
                    .map(integerMovieEntry -> integerMovieEntry.getValue().getTitle())
                    .collect(Collectors.toUnmodifiableList()));

            final var expectedReleaseYears = RequestTestUtils.column(
                    resultSet -> RequestTestUtils.stringSelector(resultSet, "RELEASE_YEAR"),
                    "MOVIE");

            assertEquals(expectedReleaseYears, iteratorToSteam(movies.getIterator())
                    .map(integerMovieEntry -> String.valueOf(integerMovieEntry.getValue().getRelease_year()))
                    .collect(Collectors.toUnmodifiableList()));

            final var expectedDurations = RequestTestUtils.column(
                    resultSet -> RequestTestUtils.stringSelector(resultSet, "RUNTIME"),
                    "MOVIE");

            assertEquals(expectedDurations, iteratorToSteam(movies.getIterator())
                    .map(integerMovieEntry -> {
                        final long seconds = integerMovieEntry.getValue().getDuration().getSeconds();
                        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
                    })
                    .collect(Collectors.toUnmodifiableList()));

        } catch (final Exception exception) {
            fail("");
        }
    }

    @Test
    public void givenData_WhenLoadMovies_ThenLoadedMovieToLocalStorageHaveDirectors() {
        try (FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When load movies
            storage.loadMovies();
            // Then loaded movies have directors
            final var iterator = movies.getIterator();
            while (iterator.hasNext()) {
                final Movie movie = iterator.next().getValue();
                final var expected = RequestTestUtils.relationCount(
                        String.format("SELECT COUNT(*) FROM MOVIE " +
                                "JOIN DIRECTED_BY DB on MOVIE.ID = DB.MOVIE_ID " +
                                "WHERE MOVIE_ID = %d", movies.getID(movie)));
                assertEquals(expected, iteratorToList(movie.directorIterator()).size());
            }
        } catch (final Exception exception) {
            fail("");
        }
    }

    @Test
    public void givenData_WhenLoadMovies_ThenLoadedMovieToLocalStorageHaveActors() {
        try (FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When load movies
            storage.loadMovies();

            // Then loaded movies have directors
            final var iterator = movies.getIterator();
            while (iterator.hasNext()) {
                final Movie movie = iterator.next().getValue();
                final int expected = RequestTestUtils.relationCount(
                        String.format("SELECT COUNT(*) FROM MOVIE " +
                                "JOIN CASTING C on MOVIE.ID = C.MOVIE_ID " +
                                "WHERE MOVIE_ID = %d", movies.getID(movie)));
                assertEquals(expected, iteratorToList(movie.castingIterator()).size());
            }
        } catch (final Exception exception) {
            fail("");
        }
    }

    @Test
    public void givenData_WhenLoadReviews_ThenReviewsLoadedToLocalStorage() {
        try (FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When load castings
            storage.loadReviews();
            // Then reviews loaded to local storage
            assertEquals(iteratorToList(reviews.getIterator()).size(), RequestTestUtils.count("REVIEW"));

            final var expectedComments = RequestTestUtils.column(
                    resultSet -> RequestTestUtils.stringSelector(resultSet, "COMMENT"),
                    "REVIEW");

            assertEquals(expectedComments, iteratorToSteam(reviews.getIterator())
                    .map(Review::getComment)
                    .collect(Collectors.toUnmodifiableList()));

            final var expectedEvaluations = RequestTestUtils.column(
                    resultSet -> RequestTestUtils.stringSelector(resultSet, "EVALUATION"),
                    "REVIEW");

            assertEquals(expectedEvaluations, iteratorToSteam(reviews.getIterator())
                    .map(review -> String.valueOf(review.getEvaluation()))
                    .collect(Collectors.toUnmodifiableList()));

            final var expectedReviewDates = RequestTestUtils.column(
                    resultSet -> RequestTestUtils.stringSelector(resultSet, "REVIEW_DATE"),
                    "REVIEW");

            assertEquals(expectedReviewDates, iteratorToSteam(reviews.getIterator())
                    .map(review -> String.valueOf(review.getCreationDate()))
                    .collect(Collectors.toUnmodifiableList()));

        } catch (final Exception exception) {
            fail("");
        }
    }

    @Test
    public void givenData_WhenLoadAllData_ThenMoviesDirectorsRelationsAreLoadedToLocalStorage() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When load all data
            storage.load();
            // Then movies relations are loaded
            final var iterator = movies.getIterator();
            while (iterator.hasNext()) {
                final Movie movie = iterator.next().getValue();
                final var expected = RequestTestUtils.relation(
                        String.format("SELECT DIRECTED_BY.PERSON_ID FROM DIRECTED_BY " +
                                "WHERE MOVIE_ID = %d", movies.getID(movie)),
                        resultSet -> Integer.valueOf(RequestTestUtils.stringSelector(resultSet, "PERSON_ID")),
                        integer -> persons.getPerson(integer)
                );
                assertEquals(expected, iteratorToList(movie.directorIterator()));
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
            fail("Exception occurred while testing movies directors relations");
        }
    }

    @Test
    public void givenData_WhenLoadAllData_ThenMoviesCategoriesRelationsAreLoadedToLocalStorage() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When load all data
            storage.load();
            // Then movies relations are loaded
            final var iterator = movies.getIterator();
            while (iterator.hasNext()) {
                final Movie movie = iterator.next().getValue();
                final var expected = RequestTestUtils.relation(
                        String.format("SELECT MOVIE_CATEGORY.CATEGORY_NAME FROM MOVIE_CATEGORY " +
                                "WHERE MOVIE_ID = %d", movies.getID(movie)),
                        resultSet -> RequestTestUtils.stringSelector(resultSet, "CATEGORY_NAME"),
                        string -> categories.get(string)
                );
                assertEquals(expected, iteratorToList(movie.categoriesIterator()));
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
            fail("Exception occurred while testing movies categories relations");
        }
    }

    @Test
    public void givenData_WhenLoadAllData_ThenMoviesCastingsRelationsAreLoadedToLocalStorage() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When load all data
            storage.load();
            // Then movies relations are loaded
            final var iterator = movies.getIterator();
            while (iterator.hasNext()) {
                final Movie movie = iterator.next().getValue();
                final var expected = RequestTestUtils.relation(
                        String.format("SELECT ACTOR_ID FROM CASTING " +
                                "WHERE MOVIE_ID = %d", movies.getID(movie)),
                        resultSet -> Integer.valueOf(RequestTestUtils.stringSelector(resultSet, "ACTOR_ID")),
                        integer -> persons.getPerson(integer)
                );
                assertTrue(expected.containsAll(iteratorToList(movie.castingIterator())));
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
            fail("Exception occurred while testing movies castings relations");
        }
    }

    @Test
    public void givenData_WhenLoadAllData_ThenMoviesReviewsRelationsAreLoadedToLocalStorage() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When load all data
            storage.load();
            // Then movies relations are loaded
            final var iterator = movies.getIterator();
            while (iterator.hasNext()) {
                final Movie movie = iterator.next().getValue();
                final List<Customer> expectedValues = RequestTestUtils.relation(
                        String.format("SELECT CUSTOMER_ID FROM REVIEW " +
                                "WHERE MOVIE_ID = %d", movies.getID(movie)),
                        resultSet -> Integer.valueOf(RequestTestUtils.stringSelector(resultSet, "CUSTOMER_ID")),
                        integer -> customers.get(integer));
                assertEquals(expectedValues, iteratorToList(reviewCustomers(reviews.concernBy(movie))));
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
            fail("Exception occurred while testing movies reviews relations");
        }
    }

    @Test
    public void givenData_WhenLoadMovies_ThenCastingsIndexInCollectionReflectTheirPositionInDatabase() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers, persons, movies, reviews, categories)) {
            //When load movies
            storage.load();
            // Then castings index in collection reflect their position in database
            final var iterator = movies.getIterator();
            while (iterator.hasNext()) {
                final var movie = iterator.next().getValue();
                final var castingsList = iteratorToList(movie.castingIterator());
                IntStream.range(1, castingsList.size() + 1).boxed().forEachOrdered(current -> {
                    try {
                        assertEquals(RequestTestUtils
                                .getCastingPosition(movies.getID(movie), persons.getID(castingsList.get(current - 1))), current);
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                        fail("An exception occurred while testing castings positions", e);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void givenData_WhenLoadCategories_ThenCategoriesAreLoadedToDatabase() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers, persons, movies, reviews, categories)) {
            // When load categories
            storage.loadCategories();
            final var iterator = categories.getIterator();
            assertEquals(iteratorToList(iterator).size(), RequestTestUtils.count("CATEGORY"));

            final var expectedNames = RequestTestUtils.column(
                    resultSet -> RequestTestUtils.stringSelector(resultSet, "NAME"),
                    "CATEGORY");
            expectedNames.sort(String::compareToIgnoreCase);

            assertEquals(expectedNames, iteratorToSteam(categories.getIterator())
                    .map(Category::getName)
                    .sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toUnmodifiableList()));

        } catch (final Exception exception) {
            exception.printStackTrace();
            fail(exception.getMessage());
        }
    }
}