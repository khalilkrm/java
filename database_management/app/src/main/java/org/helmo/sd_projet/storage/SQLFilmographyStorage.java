package org.helmo.sd_projet.storage;

import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.storage.exception.*;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SQLFilmographyStorage implements FilmographyStorage {

    private final Connection connection;
    private final Customers customers;
    private final Persons persons;
    private final Movies movies;
    private final Reviews reviews;
    private final Categories categories;

    public SQLFilmographyStorage(final Customers customers, final Persons persons, final Movies movies, final Reviews reviews,
                                 final Categories categories, final Connection connection) {
        this.connection = connection;
        this.customers = customers;
        this.persons = persons;
        this.movies = movies;
        this.reviews = reviews;
        this.categories = categories;
    }

    private <T> Stream<T> iteratorToStream(final Iterator<T> iterator) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

    @Override
    public void addUser(final Customer customer) {
        try {
            final int key = doAddCustomer(customer, connection);
            customers.addCustomer(customer, key);
        } catch (final SQLException e) {
            throw new UnableToSaveCustomerException(e.getMessage(), e);
        }
    }

    private int doAddCustomer(final Customer person, final Connection connection) throws SQLException {
        if (!customers.contains(person)) {
            try (final PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO CUSTOMER (NICKNAME) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, person.getNickname());
                statement.executeUpdate();
                return getGeneratedKey(statement);
            }
        } else return customers.getID(person);
    }

    @Override
    public void loadUsers() {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM CUSTOMER")) {
            try (final ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    customers.addCustomer(new Customer(
                                    rs.getString("NICKNAME")),
                            rs.getInt("ID"));
                }
            }
        } catch (final SQLException exception) {
            throw new UnableToLoadUserException(exception.getMessage(), exception);
        }
    }

    @Override
    public void addPerson(final Person person) {
        if (!persons.contains(person)) {
            try (final PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO PERSON (FIRSTNAME, LASTNAME, BIRTHDATE) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, person.getFirstname());
                statement.setString(2, person.getLastname());
                statement.setDate(3, java.sql.Date.valueOf(person.getBirthdate()));
                statement.executeUpdate();

                persons.addPerson(person, getGeneratedKey(statement));

            } catch (SQLException ex) {
                throw new UnableToSavePersonException(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void loadPersons() {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM PERSON")) {
            try (final ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    persons.addPerson(new Person(
                            rs.getString("FIRSTNAME"),
                            rs.getString("LASTNAME"),
                            rs.getDate("BIRTHDATE").toLocalDate()), rs.getInt("ID"));
                }
            }
        } catch (final SQLException exception) {
            throw new UnableToLoadPersonsException(exception.getMessage(), exception);
        }
    }

    @Override
    public void addMovie(final Movie movie) {
        if (!movies.contains(movie)) {
            Transaction.from(connection).commit(con -> {
                final int movieID = doAddMovie(movie, con);

                linkDirectors(movieID, iteratorToStream(movie.directorIterator())
                        .collect(Collectors.toUnmodifiableList()));

                linkCasting(movieID, iteratorToStream(movie.castingIterator())
                        .collect(Collectors.toUnmodifiableList()));

                linkCategories(movieID, iteratorToStream(movie.categoriesIterator())
                        .collect(Collectors.toUnmodifiableList()));

                movies.addMovie(movie, movieID);

            }).onRollback(ex -> {
                throw new UnableToSaveMovieException(ex.getMessage(), ex);
            }).execute();
        }
    }

    private int doAddMovie(final Movie movie, final Connection connection) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO MOVIE (NAME, RELEASE_YEAR, RUNTIME) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, movie.getTitle());
            statement.setInt(2, movie.getRelease_year());
            statement.setTime(3, java.sql.Time.valueOf(LocalTime.MIDNIGHT.plus(movie.getDuration())));
            statement.executeUpdate();
            return getGeneratedKey(statement);
        }
    }

    private void linkCategories(final int movieID, final List<Category> categories) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO MOVIE_CATEGORY (MOVIE_ID, CATEGORY_NAME) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, movieID);
            for (final Category category : categories) {
                if (!isMovieLinkedToCategory(movieID, category.getName())) {
                    statement.setString(2, category.getName());
                    statement.executeUpdate();
                }
            }
        }
    }

    private boolean isMovieLinkedToCategory(final int movieID, final String categoryName) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM MOVIE_CATEGORY WHERE MOVIE_ID = ? AND CATEGORY_NAME = ?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, movieID);
            statement.setString(2, categoryName);
            try (final ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private void linkCasting(final int movieID, final List<Person> actors) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO CASTING (MOVIE_ID, ACTOR_ID, POSITION ) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, movieID);
            int position = 1;
            for (Person actor : actors) {
                var actorID = persons.getID(actor);
                if (!actorExists(movieID, actorID)) {
                    statement.setInt(2, actorID);
                    statement.setInt(3, position++);
                    statement.executeUpdate();
                }
            }
        }
    }

    private boolean actorExists(final int movieID, final int actorID) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM CASTING WHERE MOVIE_ID = ? AND ACTOR_ID = ?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, movieID);
            statement.setInt(2, actorID);
            try (final ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private void linkDirectors(final int movieID, final List<Person> directors) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO DIRECTED_BY (MOVIE_ID, PERSON_ID) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, movieID);
            for (Person director : directors) {
                var directorID = persons.getID(director);
                if (!directorExists(movieID, directorID)) {
                    statement.setInt(2, directorID);
                    statement.executeUpdate();
                }
            }
        }
    }

    private boolean directorExists(final int movieID, final int directorID) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM DIRECTED_BY WHERE MOVIE_ID = ? AND PERSON_ID = ?",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, movieID);
            statement.setInt(2, directorID);
            try (final ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public void loadMovies() {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM MOVIE")) {
            try (final ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    movies.addMovie(new Movie(
                                    rs.getString("NAME"),
                                    rs.getInt("RELEASE_YEAR"),
                                    Duration.between(LocalTime.MIDNIGHT, rs.getTime("RUNTIME").toLocalTime()),
                                    directors(rs.getInt("ID")),
                                    castings(rs.getInt("ID")),
                                    categories(rs.getInt("ID"))),
                            rs.getInt("ID"));
                }
            }
        } catch (final SQLException exception) {
            throw new UnableToLoadMovieException(exception.getMessage(), exception);
        }
    }

    private Set<Category> categories(final int movieID) {
        final Set<Category> categoriesFound = new HashSet<>();
        try (final PreparedStatement statement = connection
                .prepareStatement("SELECT * FROM MOVIE_CATEGORY WHERE MOVIE_ID = ?")) {
            statement.setInt(1, movieID);
            try (final ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    final String name = rs.getString("CATEGORY_NAME");
                    final Category category = categories.get(name);
                    categoriesFound.add(category != null ? category : new Category(name));
                }
            }
        } catch (final SQLException exception) {
            throw new UnableToLoadDirectorsException(exception.getMessage(), exception);
        }
        return categoriesFound;
    }

    private List<Person> directors(final int movieID) {
        final List<Person> directors = new ArrayList<>();
        try (final PreparedStatement statement = connection
                .prepareStatement("SELECT * FROM DIRECTED_BY WHERE MOVIE_ID = ?")) {
            statement.setInt(1, movieID);
            try (final ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Person director = persons.getPerson(rs.getInt("PERSON_ID"));
                    directors.add(director != null ? director : new Person("INCONNU", "INCONNU", LocalDate.now()));
                }
            }
        } catch (final SQLException exception) {
            throw new UnableToLoadDirectorsException(exception.getMessage(), exception);
        }
        return directors;
    }

    private List<Person> castings(final int movieID) {
        final Map<Integer, Person> castings = new TreeMap<>();
        try (final PreparedStatement statement = connection
                .prepareStatement("SELECT * FROM CASTING WHERE MOVIE_ID = ?")) {
            statement.setInt(1, movieID);
            try (final ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Person actor = persons.getPerson(rs.getInt("ACTOR_ID"));
                    castings.put(rs.getInt("POSITION"),
                            actor != null ? actor : new Person("INCONNU", "INCONNU", LocalDate.now()));
                }
            }
        } catch (final SQLException exception) {
            throw new UnableToLoadCastingsException(exception.getMessage(), exception);
        }
        return castings
                .values()
                .stream()
                .collect(LinkedList::new, LinkedList::add, List::addAll);
    }

    @Override
    public void addReview(final Review review) {
        if (!reviews.contains(review)) {
            try (final PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO REVIEW (MOVIE_ID, CUSTOMER_ID, COMMENT, EVALUATION, REVIEW_DATE) VALUES (?, ?, ?, ?, ?)")) {
                statement.setInt(1, movies.getID(review.getMovie()));
                statement.setInt(2, getAuthorIdOrAdd(review.getAuthor()));
                statement.setString(3, review.getComment());
                statement.setInt(4, review.getEvaluation());
                statement.setDate(5, java.sql.Date.valueOf(review.getCreationDate()));
                statement.executeUpdate();
                reviews.addReview(review);
            } catch (final SQLException e) {
                throw new UnableToSaveReviewException(e.getMessage(), e);
            }
        }
    }

    public int getAuthorIdOrAdd(final Customer customer) {
        final int id = customers.getID(customer);
        if (id > -1) return id;
        addUser(customer);
        return customers.getID(customer);
    }

    @Override
    public void loadReviews() {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM REVIEW")) {
            try (final ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reviews.addReview(new Review(
                            rs.getString("COMMENT"),
                            rs.getInt("EVALUATION"),
                            customers.get(rs.getInt("CUSTOMER_ID")),
                            movies.get(rs.getInt("MOVIE_ID")),
                            rs.getDate("REVIEW_DATE").toLocalDate()));
                }
            }
        } catch (final SQLException exception) {
            throw new UnableToLoadReviewsException(exception.getMessage(), exception);
        }
    }

    @Override
    public void loadCategories() {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM CATEGORY")) {
            try (final ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    categories.addCategory(new Category(rs.getString("NAME")));
                }
            }
        } catch (final SQLException exception) {
            throw new UnableToLoadCategoriesException(exception.getMessage(), exception);
        }
    }

    @Override
    public void addCategory(final Category category) {
        if (!categories.contains(category)) {
            try (final PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO CATEGORY (NAME) VALUES (?)")) {
                statement.setString(1, category.getName());
                statement.executeUpdate();
                categories.addCategory(category);
            } catch (final SQLException e) {
                throw new UnableToSaveCategoryException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void load() {
        loadUsers();
        loadPersons();
        loadMovies();
        loadReviews();
        loadCategories();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    private int getGeneratedKey(final Statement statement) {
        try (var rs = statement.getGeneratedKeys()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to get generated key: " + e.getMessage());
        }
    }
}
