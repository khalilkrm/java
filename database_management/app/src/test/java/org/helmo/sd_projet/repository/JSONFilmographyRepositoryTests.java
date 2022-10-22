package org.helmo.sd_projet.repository;

import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.repository.exceptions.CouldNotImportJSONFileException;
import org.helmo.sd_projet.repository.exceptions.ExportException;
import org.helmo.sd_projet.repository.importer.exceptions.FileNotFoundException;
import org.helmo.sd_projet.repository.importer.exceptions.JSONMissingPropertyException;
import org.helmo.sd_projet.repository.exceptions.ImportingExistingMovieException;
import org.helmo.sd_projet.storage.FilmographyStorage;
import org.helmo.sd_projet.storage.SQLFilmographyStorageFactory;
import org.helmo.sd_projet.storage.connection.ConnectionData;
import org.helmo.sd_projet.storage.connection.DerbyConnectionData;
import org.helmo.sd_projet.storage.utility.FilmographyDBPopulate;
import org.helmo.sd_projet.utility.FilmographyStorageTestUtils;
import org.helmo.sd_projet.utility.data.FakeDomainData;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JSONFilmographyRepositoryTests {

    private final static ConnectionData connectionData = new DerbyConnectionData();
    private FilmographyRepository repository;
    private static FilmographyDBPopulate populater;
    private static FilmographyStorage storage;

    private static Customers customers;
    private static Persons persons;
    private static Movies movies;
    private static Reviews reviews;
    private static Categories categories;


    private final static Path TEST_RESOURCE_PATH = Paths.get("src", "test", "resources").toAbsolutePath();
    private final static Path TEMP_TEST_PATH = Paths.get(TEST_RESOURCE_PATH.toString(), "temp");

    /*
      ------ FILES ------
    */
    private final File movie = Paths.get(TEST_RESOURCE_PATH.toString(), "movies", "movie.json").toFile();
    private final File existingMovie = Paths.get(TEST_RESOURCE_PATH.toString(), "movies", "existing_movie.json").toFile();
    private final File movieWithMissingTitle = Paths.get(TEST_RESOURCE_PATH.toString(), "movies", "movie_with_missing_title.json").toFile();
    private final File movieWithMissingProperties = Paths.get(TEST_RESOURCE_PATH.toString(), "movies", "movie_with_missing_properties.json").toFile();
    private final File movieWithWrongFormattedProperties = Paths.get(TEST_RESOURCE_PATH.toString(), "movies", "movie_with_wrong_formatted_properties.json").toFile();
    private final File wrongFormattedMovieFile = Paths.get(TEST_RESOURCE_PATH.toString(), "movies", "wrong_formatted_movie_file.json").toFile();
    private final File filmography = Paths.get(TEST_RESOURCE_PATH.toString(), "filmography", "sample_movie.json").toFile();
    /*
        ------ GETTING READY ------
    */

    @BeforeAll
    public static void setupAll() throws IOException {
        populater = FilmographyStorageTestUtils.from(connectionData).connect();
        resetTempResourceDirectory();
    }

    @BeforeEach
    public void setupEach() {
        customers = new Customers();
        persons = new Persons();
        movies = new Movies();
        reviews = new Reviews();
        categories = new Categories();
        storage = new SQLFilmographyStorageFactory(connectionData)
                .newStorage(customers, persons, movies, reviews, categories);
        repository = new JSONFilmographyRepository(reviews, movies, categories, null);
        populater.reset();
    }

    @AfterEach
    public void teardownEach() throws IOException {
        resetTempResourceDirectory();
    }

    @AfterAll
    public static void teardownAll() throws Exception {
        deleteDirectory(TEMP_TEST_PATH.toFile());
        populater.reset();
        if (populater != null)
            populater.close();
        storage.close();
    }

    /*
         ------ DEGRADED TESTS CASES ------
     */

    @Test
    public void givenNotExistingJsonFile_whenImport_ThenExceptionIsThrown() {
        // GIVEN
        final Path notExistMovieFile = Paths.get("src", "test", "resources", "iNotExist.json");
        // THEN
        assertThrows(FileNotFoundException.class, () -> repository.importFile(notExistMovieFile.toAbsolutePath().toString()));
    }

    @Test
    public void givenJsonFileWithMissingTitle_whenImport_ThenExceptionIsThrown() {
        // THEN
        assertThrows(JSONMissingPropertyException.class, () -> repository.importFile(movieWithMissingTitle.getPath()));
    }

    @Test
    public void givenJsonMovieThatAlreadyExists_whenImport_ThenExceptionIsThrown() {
        // GIVEN
        movies.addMovie(FakeDomainData.movies.get(0), 0);
        // THEN
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData)
                .newStorage(customers, persons, movies, reviews, categories)) {
            final FilmographyRepository repository = new JSONFilmographyRepository(reviews, movies, categories, storage);
            assertThrows(ImportingExistingMovieException.class, () -> repository.importFile(existingMovie.getPath()));
        } catch (final Exception exception) {
            exception.printStackTrace();
            fail(exception.getMessage());
        }
    }

    @Test
    public void givenWrongFormattedJSONMovie_WhenImport_ThenExceptionIsThrown() {
        // THEN
        assertThrows(CouldNotImportJSONFileException.class, () -> repository.importFile(wrongFormattedMovieFile.getPath()));
    }

    @Test
    public void givenFileWithWrongFormattedData_WhenImport_ThenWrongFormattedFieldsGotDefaultValue() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData)
                .newStorage(customers, persons, movies, reviews, categories)) {

            storage.load();

            final FilmographyRepository repository = new JSONFilmographyRepository(reviews, movies, categories, storage);
            final JSONFilmographyRepository.ImportResult actual = repository.importFile(movieWithWrongFormattedProperties.getPath());
            final Movie expectedMovie = new Movie(
                    "458",
                    1998,
                    Duration.ofMinutes(145),
                    List.of(
                            new Person("145", JSONFilmographyRepository.FALLBACK_STRING_VALUE, LocalDate.now()),
                            new Person(JSONFilmographyRepository.FALLBACK_STRING_VALUE, JSONFilmographyRepository.FALLBACK_STRING_VALUE, LocalDate.now())),
                    List.of(), Set.of(new Category("Sport")));

            final List<Review> expectedReview = List.of(
                    new Review(JSONFilmographyRepository.FALLBACK_STRING_VALUE, 5, new Customer("458"), actual.getMovie(), LocalDate.now()),
                    new Review("Tim Roth appears impervious and shamelessly kindhearted as the jittery Atoner", 0, new Customer("tmarcoolyn0"), actual.getMovie(), LocalDate.of(2022, 8, 11)));

            Assertions.assertEquals(expectedMovie, actual.getMovie());
            Assertions.assertEquals(expectedReview, actual.getReviews());

        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void givenFileWithMissingProperties_WhenImportFile_ThenMissingFieldsGotDefaultValue() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData)
                .newStorage(customers, persons, movies, reviews, categories)) {

            storage.loadPersons();
            storage.loadUsers();
            storage.loadMovies();
            storage.loadReviews();

            final FilmographyRepository repository = new JSONFilmographyRepository(reviews, movies, categories, storage);
            final JSONFilmographyRepository.ImportResult actual = repository.importFile(movieWithMissingProperties.getPath());
            final Movie expectedMovie = new Movie(
                    "Tai Chi Master (Twin Warriors) (Tai ji: Zhang San Feng)",
                    LocalDate.now().getYear(),
                    Duration.ofMinutes(0),
                    List.of(
                            new Person(JSONFilmographyRepository.FALLBACK_STRING_VALUE, "Woolard", LocalDate.of(2021, 12, 25)),
                            new Person("Jamey", JSONFilmographyRepository.FALLBACK_STRING_VALUE, LocalDate.of(2021, 12, 25)),
                            new Person("Kinnie", "Merryweather", LocalDate.now()),
                            new Person(JSONFilmographyRepository.FALLBACK_STRING_VALUE, JSONFilmographyRepository.FALLBACK_STRING_VALUE, LocalDate.now())),
                    List.of(), new HashSet<>());

            final List<Review> expectedReview = List.of(
                    new Review("Tim Roth appears impervious and shamelessly kindhearted as the jittery Atoner", 0, new Customer("tmarcoolyn0"), actual.getMovie(), LocalDate.of(2022, 8, 11)));

            Assertions.assertEquals(expectedMovie, actual.getMovie());
            Assertions.assertEquals(expectedReview, actual.getReviews());

        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /*
        ------ STANDARD TESTS CASES ------
    */

    @Test
    public void givenMovie_WhenExport_ThenAllPropertiesAreExported() throws ExportException {
        // GIVEN
        // In order for import method to find review for movie 0
        reviews.addReview(FakeDomainData.reviews.get(0));
        final Movie movieToExport = FakeDomainData.movies.get(0);
        // WHEN
        final JSONFilmographyRepository.ExportResult exportResult = repository.export(TEMP_TEST_PATH.toString(), movieToExport);
        // THEN
        Assertions.assertEquals(movieToJSON(movieToExport), read(exportResult.getPath()));
    }

    public String movieToJSON(final Movie movie) {
        /*!! add review in reviews first !!*/
        final String actors = iteratorToStream(movie.castingIterator()).map(Person::toString).collect(Collectors.joining(","));
        final String directors = iteratorToStream(movie.directorIterator()).map(Person::toString).collect(Collectors.joining(","));
        final String movieReviews = reviews.concernBy(movie).stream().map(Review::toString).collect(Collectors.joining(","));
        final String movieCategories = iteratorToStream(movie.categoriesIterator()).map(Category::toString).collect(Collectors.joining("\",\"", "\"", "\""));

        return String.format("{" +
                "\"duration\":%s," +
                "\"castings\":[%s]," +
                "\"reviews\":[%s]," +
                "\"directors\":[%s]," +
                "\"release_year\":%d," +
                "\"categories\":[%s]," +
                "\"title\":\"%s\"}", movie.getDuration().toMinutes(), actors, movieReviews, directors, movie.getRelease_year(), movieCategories, movie.getTitle());
    }

    @Test
    public void givenMovie_WhenExport_ThenFileIsCreated() throws ExportException {
        // GIVEN
        final Movie movieToExport = FakeDomainData.movies.get(0);
        // WHEN
        final JSONFilmographyRepository.ExportResult exportResult = repository.export(TEMP_TEST_PATH.toString(), movieToExport);
        // THEN
        Assertions.assertTrue(Files.exists(exportResult.getPath()));
    }

    @Test
    public void givenJsonFile_whenImport_ThenSavedInDatabase() {
        try (final FilmographyStorage storage = new SQLFilmographyStorageFactory(connectionData)
                .newStorage(customers, persons, movies, reviews, categories)) {

            storage.load();

            final FilmographyRepository repository = new JSONFilmographyRepository(reviews, movies, categories, storage);
            repository.importFile(movie.getPath());

        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Assertions.assertTrue(movies.searchMovie("Airport '77").size() > 0);
        Assertions.assertEquals(1, reviews.concernBy(FakeDomainData.movies.get(0)).size());
        Assertions.assertEquals(3, categories.searchCategory("").size());
    }

    /*
       ------ PRIVATE FUNCTIONS ------
   */

    public String read(final Path path) {
        final StringBuilder sb = new StringBuilder();
        try (final Stream<String> stream = Files.lines(path)) {
            stream.forEach(sb::append);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return sb.toString();
    }

    private static void resetTempResourceDirectory() throws IOException {
        deleteDirectory(TEMP_TEST_PATH.toFile());
        Files.createDirectory(TEMP_TEST_PATH);
    }

    private static void deleteDirectory(final File directoryToBeDeleted) {
        deleteDirectoryFiles(directoryToBeDeleted);
        directoryToBeDeleted.delete();
    }

    private static void deleteDirectoryFiles(File directoryToBeDeleted) {
        final File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (final File file : allContents) {
                deleteDirectory(file);
            }
        }
    }

    private <T> Stream<T> iteratorToStream(final Iterator<T> iterator) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }
}
