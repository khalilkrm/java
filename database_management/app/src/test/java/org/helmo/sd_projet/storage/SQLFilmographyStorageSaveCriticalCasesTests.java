package org.helmo.sd_projet.storage;

import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.storage.connection.ConnectionData;
import org.helmo.sd_projet.storage.connection.ConnectionFactory;
import org.helmo.sd_projet.storage.connection.DerbyConnectionData;
import org.helmo.sd_projet.storage.exception.UnableToSaveCustomerException;
import org.helmo.sd_projet.storage.exception.UnableToSavePersonException;
import org.helmo.sd_projet.storage.utility.FilmographyDBPopulate;
import org.helmo.sd_projet.utility.FilmographyStorageTestUtils;
import org.helmo.sd_projet.utility.data.FakeDomainData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.assertj.db.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLFilmographyStorageSaveCriticalCasesTests {

    private static final ConnectionData connectionData = new DerbyConnectionData();

    final Source source = new Source(connectionData.getDBPath(), connectionData.getUsername(),
            connectionData.getPassword());

    private static Customers customers;
    private static Persons persons;
    private static Movies movies;
    private static Reviews reviews;
    private static Categories categories;
    private static FilmographyDBPopulate populater;

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
        populater.reset();
    }

    @AfterAll
    static void afterAll() throws Exception {
        populater.reset();
        if (populater != null)
            populater.close();
    }

    @Test
    public void givenProblemConnection_WhenSavePersons_ThenThrowException() {

        try {
            final Connection connection = ConnectionFactory.createConnection(new DerbyConnectionData());
            try (final FilmographyStorage storage = FilmographyStorageTestUtils.from(connection, customers, persons, movies, reviews, categories)) {
                connection.close();
                assertThrows(UnableToSavePersonException.class, () -> FakeDomainData.persons.forEach(storage::addPerson));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenProblemConnection_WhenSaveUsers_ThenThrowException() {

        try {
            final Connection connection = ConnectionFactory.createConnection(new DerbyConnectionData());
            try (final FilmographyStorage storage = FilmographyStorageTestUtils.from(connection, customers, persons, movies, reviews, categories)) {
                connection.close();
                assertThrows(UnableToSaveCustomerException.class, () -> FakeDomainData.customers.forEach(storage::addUser));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenProblemConnection_WhenSaveMovies_ThenThrowException() {
        try {
            final Connection connection = ConnectionFactory.createConnection(new DerbyConnectionData());
            try (final FilmographyStorage storage = FilmographyStorageTestUtils.from(connection, customers, persons, movies, reviews, categories)) {
                FakeDomainData.persons.forEach(storage::addPerson);
                connection.setAutoCommit(true);
                connection.close();
                assertThrows(TransactionNotSupportedException.class, () -> FakeDomainData.movies.forEach(storage::addMovie));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenCategoriesWithDuplication_WhenAddCategories_ThenCategoriesAreSavedToDatabaseWithoutDuplication() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData).newStorage(customers, persons,
                movies, reviews, categories)) {
            // Given categories
            final List<Category> categories = FakeDomainData.categories;

            // Add categories
            categories.forEach(storage::addCategory);

            // Then categories are saved to database
            final Table categoryTable = new Table(source, "CATEGORY");

            assertThat(categoryTable).hasNumberOfRows(categories.size() - 1);

        } catch (final Exception e) {
            e.printStackTrace();
            fail("");
        }
    }

}
