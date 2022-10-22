package org.helmo.sd_projet.storage;

import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.storage.connection.ConnectionData;
import org.helmo.sd_projet.storage.connection.DerbyConnectionData;
import org.helmo.sd_projet.storage.utility.FilmographyDBPopulate;
import org.helmo.sd_projet.utility.data.FakeDomainData;
import org.helmo.sd_projet.utility.FilmographyStorageTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.db.api.Assertions.assertThat;

public class SQLFilmographyStorageSaveStandardCaseTests {

    private static final ConnectionData connectionData = new DerbyConnectionData();
    final Source source = new Source(connectionData.getDBPath(), connectionData.getUsername(),
            connectionData.getPassword());
    private static FilmographyDBPopulate populater;

    private Customers customers;
    private Persons persons;
    private Movies movies;
    private Reviews reviews;
    private Categories categories;

    @BeforeAll
    static void setupAll() {
        // Given data
        populater = FilmographyStorageTestUtils.from(connectionData).connect();
    }

    @BeforeEach
    public void setupEach() {
        customers = new Customers();
        persons = new Persons();
        movies = new Movies();
        reviews = new Reviews();
        categories = new Categories();
        populater.reset();
    }

    @AfterAll
    static void afterAll() throws Exception {
        populater.reset();
        if (populater != null)
            populater.close();
    }

    @Test
    public void givenPersons_WhenAddPersons_ThenPersonAreSavedToDatabase() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When add persons
            FakeDomainData.persons.forEach(storage::addPerson);
            // Then persons are saved to database
            final Table personTable = new Table(source, "PERSON");
            assertThat(personTable)
                    .hasNumberOfRows(FakeDomainData.persons.size() - 1)
                    .column("FIRSTNAME")
                    .hasValues(FakeDomainData.persons.stream().skip(1).map(Person::getFirstname)
                            .collect(Collectors.toUnmodifiableList()).toArray())
                    .column("LASTNAME")
                    .hasValues(FakeDomainData.persons.stream().skip(1).map(Person::getLastname)
                            .collect(Collectors.toUnmodifiableList()).toArray())
                    .column("BIRTHDATE")
                    .hasValues(FakeDomainData.persons.stream().skip(1).map(Person::getBirthdate).map(Date::valueOf)
                            .collect(Collectors.toUnmodifiableList()).toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenCustomers_WhenAddCustomers_ThenCustomersAreSavedToDatabase() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // When add persons
            FakeDomainData.customers.forEach(storage::addUser);
            // Then persons are saved to database
            final Table customerTable = new Table(source, "CUSTOMER");
            assertThat(customerTable)
                    .hasNumberOfRows(FakeDomainData.customers.size() - 1) // -1 cause there is duplication in array that should be added 1 time
                    .column("NICKNAME")
                    .hasValues(FakeDomainData.customers.stream().skip(1).map(Customer::getNickname)
                            .collect(Collectors.toUnmodifiableList()).toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenMovies_WhenAddMovies_ThenMoviesAreSavedToDatabase() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {
            // Given movies

            final List<Movie> movies = FakeDomainData.movies;

            // Save directors
            movies.forEach(movie -> StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(movie.directorIterator(), Spliterator.ORDERED), false)
                    .forEach(storage::addPerson));

            // Save actors
            movies.forEach(movie -> StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(movie.castingIterator(), Spliterator.ORDERED), false)
                    .forEach(storage::addPerson));

            // Save categories
            movies.forEach(movie -> StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(movie.categoriesIterator(), Spliterator.ORDERED), false)
                    .forEach(storage::addCategory));

            // When add movies
            movies.forEach(storage::addMovie);

            // Then movies are saved to database
            final Table movieTable = new Table(source, "MOVIE");

            assertThat(movieTable).hasNumberOfRows(movies.size() - 1)
                    .column("NAME")
                    .hasValues(movies.stream().skip(1).map(Movie::getTitle).toArray())
                    .column("RUNTIME")
                    .hasValues(movies.stream().skip(1).map(Movie::getDuration)
                            .map(duration -> java.sql.Time.valueOf(LocalTime.MIDNIGHT.plus(duration))).toArray())
                    .column("RELEASE_YEAR")
                    .hasValues(movies.stream().skip(1).map(Movie::getRelease_year).toArray());

            final Table directedByTable = new Table(source, "DIRECTED_BY");
            final Table castingByTable = new Table(source, "CASTING");

            final var numberOfCastingRows = movies.stream().skip(1).map(movie -> StreamSupport
                            .stream(Spliterators.spliteratorUnknownSize(movie.castingIterator(), Spliterator.ORDERED), false)
                            .collect(Collectors.toSet()).size())
                    .reduce(0, Integer::sum);

            final var numberOfDirectedByRows = movies.stream().skip(1).map(movie -> StreamSupport
                            .stream(Spliterators.spliteratorUnknownSize(movie.directorIterator(), Spliterator.ORDERED), false)
                            .collect(Collectors.toSet()).size())
                    .reduce(0, Integer::sum);

            assertThat(directedByTable)
                    .hasNumberOfRows(numberOfDirectedByRows);
            assertThat(castingByTable)
                    .hasNumberOfRows(numberOfCastingRows);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void givenReviews_WhenAddReviews_ThenReviewAreSavedToDatabase() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers,
                persons, movies, reviews, categories)) {

            // Given review
            final List<Review> reviews = FakeDomainData.reviews;

            // Save authors, directors, castings and movie
            reviews.stream().skip(1).forEach(review -> {
                storage.addUser(review.getAuthor());
                final Movie movie = review.getMovie();
                StreamSupport
                        .stream(Spliterators.spliteratorUnknownSize(movie.directorIterator(), Spliterator.ORDERED),
                                false)
                        .forEach(storage::addPerson);

                StreamSupport
                        .stream(Spliterators.spliteratorUnknownSize(movie.castingIterator(), Spliterator.ORDERED),
                                false)
                        .forEach(storage::addPerson);

                StreamSupport
                        .stream(Spliterators.spliteratorUnknownSize(movie.categoriesIterator(), Spliterator.ORDERED),
                                false)
                        .forEach(storage::addCategory);


                storage.addMovie(movie);
            });

            // When add reviews
            reviews.forEach(storage::addReview);

            // Then reviews are saved to database
            final Table reviewTable = new Table(source, "REVIEW");

            assertThat(reviewTable).hasNumberOfRows(reviews.size() - 1)
                    .column("COMMENT")
                    .hasValues(reviews.stream().skip(1).map(Review::getComment).toArray())
                    .column("EVALUATION")
                    .hasValues(reviews.stream().skip(1).map(Review::getEvaluation).toArray())
                    .column("REVIEW_DATE")
                    .hasValues(reviews.stream().skip(1).map(Review::getCreationDate).map(Date::valueOf)
                            .collect(Collectors.toUnmodifiableList()).toArray());

        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }
    }

    @Test
    public void givenCategories_WhenAddCategories_ThenCategoriesAreSavedToDatabase() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers, persons,
                movies, reviews, categories)) {
            // Given categories
            final List<Category> categories = FakeDomainData.categories;

            // Add categories
            categories.forEach(storage::addCategory);

            // Then categories are saved to database
            final Table categoryTable = new Table(source, "CATEGORY");

            assertThat(categoryTable)
                    .column("NAME")
                    .hasValues(categories.stream().skip(1).sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName())).map(Category::getName).toArray());

        } catch (final Exception e) {
            e.printStackTrace();
            fail("");
        }
    }


}