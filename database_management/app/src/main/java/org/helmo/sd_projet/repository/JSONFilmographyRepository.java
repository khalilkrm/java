package org.helmo.sd_projet.repository;

import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.repository.exceptions.ExportException;
import org.helmo.sd_projet.repository.exceptions.CouldNotImportJSONFileException;
import org.helmo.sd_projet.repository.exceptions.CouldNotWriteJsonFile;
import org.helmo.sd_projet.repository.exceptions.ImportingExistingMovieException;
import org.helmo.sd_projet.repository.importer.exceptions.FileNotFoundException;
import org.helmo.sd_projet.repository.importer.exceptions.JSONMissingPropertyException;
import org.helmo.sd_projet.storage.FilmographyStorage;
import org.helmo.sd_projet.storage.exception.PathNotReadableException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class JSONFilmographyRepository implements FilmographyRepository {

    private final Reviews reviews;
    private final Movies movies;
    private final Categories categories;
    private final FilmographyStorage filmographyStorage;

    /**
     * Filename in case a movie has a blank title
     */
    public final static String FALLBACK_FILENAME = "untitled";
    public final static String FALLBACK_STRING_VALUE = "empty";
    private final static String IlLEGAL_CHARACTERS = "\\\\/:*?\"<>|";

    public JSONFilmographyRepository(
            final Reviews reviews,
            final Movies movies,
            final Categories categories, final FilmographyStorage filmographyStorage) {
        this.reviews = reviews;
        this.movies = movies;
        this.categories = categories;
        this.filmographyStorage = filmographyStorage;
    }

    /*
        ------ EXPORT ------
    */

    /**
     * Export {@link Movie} to JSON file. The output file will be named with movie's title.
     *
     * @param destination pathname to destination directory
     * @param movie       the movie to export
     * @return a class {@link ExportResult} with information about export destination
     */
    @Override
    public ExportResult export(final String destination, final Movie movie) throws ExportException {
        try {

            final ExportResult pathResult = createDestinationPath(destination, movie);

            JSONObject JSONMovie = movieToJson(movie);
            JSONArray JSONDirectors = getDirectorsToJson(movie);
            JSONArray JSONCastings = getActorsToJson(movie);
            JSONArray JSONReviews = getReviewsToJson(movie);
            JSONArray JSONCategories = getCategoriesToJson(movie);

            JSONMovie.put("directors", JSONDirectors);
            JSONMovie.put("castings", JSONCastings);
            JSONMovie.put("reviews", JSONReviews);
            JSONMovie.put("categories", JSONCategories);

            write(pathResult.getPath(), JSONMovie);

            return pathResult;

        } catch (IOException | InvalidPathException | PathNotReadableException exception) {
            throw new ExportException(exception.getMessage(), exception);
        }
    }

    private JSONArray getCategoriesToJson(final Movie movie) {
        final JSONArray categoriesArray = new JSONArray();
        movie.categoriesIterator().forEachRemaining(category -> categoriesArray.add(category.getName()));
        return categoriesArray;
    }

    private JSONArray getReviewsToJson(final Movie movie) {
        final JSONArray reviewArray = new JSONArray();

        reviews.concernBy(movie).forEach(review -> {
            final JSONObject reviewObject = new JSONObject();

            reviewObject.put("comment", review.getComment());
            reviewObject.put("evaluation", review.getEvaluation());
            reviewObject.put("author", review.getAuthor().getNickname());
            reviewObject.put("creationDate", review.getCreationDate().toString());

            reviewArray.add(reviewObject);
        });

        return reviewArray;
    }

    /**
     * Create a JSON file named by the movie title to export. If the movie has not a title the fallback filename is {@value FALLBACK_FILENAME}
     * <br> All illegal characters {@value IlLEGAL_CHARACTERS} in the filename will be replaced be blank text.
     *
     * @return the filename
     * @throws IOException          if an I/O error occurs or the parent directory does not exist
     * @throws InvalidPathException if the path string cannot be converted to a Path
     */
    private ExportResult createDestinationPath(final String directory, final Movie movie)
            throws IOException, InvalidPathException, PathNotReadableException {
        final String title = replaceIllegalCharacters(movie.getTitle());
        final String filename = !title.isBlank()
                ? title.concat(".json")
                : FALLBACK_FILENAME.concat(".json");
        boolean isReadable = Files.isDirectory(Path.of(directory));

        if (!isReadable) {
            throw new PathNotReadableException("Path : " + directory + " is not a directory", null);
        }

        final Path path = Path.of(directory, filename);

        if (Files.notExists(path)) {
            Files.createFile(path);
        }

        return new ExportResult(filename, path);
    }

    private String replaceIllegalCharacters(final String text) {
        return text.replaceAll("[" + IlLEGAL_CHARACTERS + "]", "");
    }

    public void write(final Path path, final JSONObject object) {
        try {
            try (final BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write(object.toJSONString());
            }
        } catch (IOException e) {
            throw new CouldNotWriteJsonFile(e.getMessage(), e);
        }
    }

    private JSONArray getDirectorsToJson(final Movie movie) {
        final JSONArray directorsArray = new JSONArray();
        final var iterator = movie.directorIterator();
        iterator.forEachRemaining(person -> {

            JSONObject JSONDirector = new JSONObject();

            JSONDirector.put("firstname", person.getFirstname());
            JSONDirector.put("lastname", person.getLastname());
            JSONDirector.put("birthdate", person.getBirthdate().toString());

            directorsArray.add(JSONDirector);
        });

        return directorsArray;
    }

    private JSONArray getActorsToJson(final Movie movie) {
        final JSONArray castingsArray = new JSONArray();
        final var iterator = movie.castingIterator();
        iterator.forEachRemaining(person -> {

            JSONObject JSONActor = new JSONObject();

            JSONActor.put("firstname", person.getFirstname());
            JSONActor.put("lastname", person.getLastname());
            JSONActor.put("birthdate", person.getBirthdate().toString());
            castingsArray.add(JSONActor);
        });

        return castingsArray;
    }

    private JSONObject movieToJson(final Movie movie) {
        final var title = movie.getTitle();
        final var duration = movie.getDuration();
        final var releaseYear = movie.getRelease_year();

        final JSONObject JSONMovie = new JSONObject();

        JSONMovie.put("title", title);
        JSONMovie.put("duration", duration.toMinutes());
        JSONMovie.put("release_year", releaseYear);

        return JSONMovie;
    }

    /*
        ------ IMPORT ------
    */

    @Override
    public ImportResult importFile(final String pathname) {


        final Path path = Path.of(pathname);

        if (Files.notExists(path)) {
            throw new FileNotFoundException("Path does not exist (" + pathname + ")");
        }

        if (pathname.isBlank()) {
            throw new FileNotFoundException("Path does not exist (" + pathname + ")");
        }

/*        if (!Files.isReadable(path)) {
            throw new PathNotReadableException("Extension must be json", null);
        }*/

        try {

            final JSONParser parser = new JSONParser();
            final StringBuilder sb = new StringBuilder();

            try (final Stream<String> stream = Files.lines(path)) {

                stream.forEach(sb::append);
                final JSONObject obj = (JSONObject) parser.parse(sb.toString());

                final String title = getTitleOrElse(obj, () -> {
                    throw new JSONMissingPropertyException("title");
                });

                final Duration duration = getDurationOrElse(obj, () -> Duration.ofMinutes(0));
                final int releaseYear = getReleaseYearOrElse(obj, () -> LocalDate.now().getYear());

                final List<Person> directors = getDirectorsOrElse(obj, ArrayList::new);
                final List<Person> actors = getActorsOrElse(obj, ArrayList::new);

                final Set<Category> categories = getCategoriesOrElse(obj, HashSet::new);

                final Movie movie = new Movie(title, releaseYear, duration, directors, actors, categories);

                final List<Review> reviews = getReviewsOrElse(movie, obj, ArrayList::new);

                filmographyStorage.load();
                save(movie, reviews);

                return new ImportResult(movie, reviews);
            }

        } catch (IOException | ParseException | FileNotFoundException e) {
            throw new CouldNotImportJSONFileException(e.getMessage(), e);
        }
    }

    private Set<Category> getCategoriesOrElse(final JSONObject object, final Supplier<Set<Category>> fallback) {
        try {

            final Object categoriesArray = Objects.requireNonNull(object.get("categories"));
            if (!(categoriesArray instanceof JSONArray)) return fallback.get();

            final Set<Category> categories = new HashSet<>();

            for (final Object o : (JSONArray) categoriesArray) {
                if (!(o instanceof String)) continue;
                final String categoryName = (String) o;
                if (!categoryName.isBlank())
                    categories.add(new Category(categoryName));
            }

            return categories;

        } catch (ClassCastException | NullPointerException exception) {
            return fallback.get();
        }
    }

    private List<Review> getReviewsOrElse(Movie movie, final JSONObject object, final Supplier<List<Review>> fallback) {
        try {
            final Object reviewsArray = Objects.requireNonNull(object.get("reviews"));
            if (!(reviewsArray instanceof JSONArray)) return fallback.get();

            final List<Review> reviews = new ArrayList<>();

            for (final Object o : (JSONArray) reviewsArray) {
                if (!(o instanceof JSONObject)) continue;
                final JSONObject reviewObj = (JSONObject) o;

                final String comment = getCommentOrElse(reviewObj, () -> FALLBACK_STRING_VALUE);
                final int evaluation = getEvaluationOrElse(reviewObj, () -> 0);
                final Customer author = getAuthorOrElse(reviewObj, () -> new Customer(FALLBACK_STRING_VALUE));
                final LocalDate creationDate = getCreationDateOrElse(reviewObj, LocalDate::now);

                reviews.add(new Review(comment, evaluation, author, movie, creationDate));
            }

            return reviews;

        } catch (ClassCastException | NullPointerException exception) {
            return fallback.get();
        }
    }

    private LocalDate getCreationDateOrElse(final JSONObject object, final Supplier<LocalDate> fallback) {
        try {
            final Object creationDateObj = Objects.requireNonNull(object.get("creation_date"));
            return LocalDate.parse((String) creationDateObj);
        } catch (final DateTimeParseException | NullPointerException | ClassCastException ignored) {
            return fallback.get();
        }
    }

    private Customer getAuthorOrElse(final JSONObject object, final Supplier<Customer> fallback) {
        try {
            final Object authorObj = Objects.requireNonNull(object.get("author"));
            final String authorName = getString(authorObj);
            return new Customer(authorName);
        } catch (final DateTimeParseException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private int getEvaluationOrElse(final JSONObject object, final Supplier<Integer> fallback) {
        try {
            final Object evaluationObj = Objects.requireNonNull(object.get("evaluation"));
            if (evaluationObj instanceof String) return Integer.parseInt((String) evaluationObj);
            return (int) (long) evaluationObj;
        } catch (final ClassCastException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private String getCommentOrElse(final JSONObject object, final Supplier<String> fallback) {
        try {
            final Object commentObj = Objects.requireNonNull(object.get("comment"));
            return getString(commentObj);
        } catch (final ClassCastException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private List<Person> getDirectorsOrElse(final JSONObject object, final Supplier<List<Person>> fallback) {
        try {
            final Object directorsObj = Objects.requireNonNull(object.get("directors"));
            return getPeopleOrElse(directorsObj);
        } catch (final ClassCastException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private List<Person> getActorsOrElse(final JSONObject object, final Supplier<List<Person>> fallback) {
        try {
            final Object directorsObj = Objects.requireNonNull(object.get("castings"));
            if (!(directorsObj instanceof JSONArray)) return fallback.get();
            return getPeopleOrElse(directorsObj);
        } catch (final ClassCastException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private List<Person> getPeopleOrElse(final Object peopleObj) {
        final JSONArray JSONPeople = (JSONArray) peopleObj;
        final List<Person> people = new ArrayList<>(JSONPeople.size());

        for (final Object o : JSONPeople) {
            if (!(o instanceof JSONObject)) continue;
            final JSONObject person = (JSONObject) o;
            final String firstname = getFirstnameOrElse(person, () -> FALLBACK_STRING_VALUE);
            final String lastname = getLastnameOrElse(person, () -> FALLBACK_STRING_VALUE);
            final LocalDate birthdate = getBirthdateOrElse(person, LocalDate::now);
            people.add(new Person(firstname, lastname, birthdate));
        }

        return people;
    }

    private LocalDate getBirthdateOrElse(final JSONObject object, final Supplier<LocalDate> fallback) {
        try {
            final Object birthdateObj = Objects.requireNonNull(object.get("birthdate"));
            return LocalDate.parse((String) birthdateObj);
        } catch (final DateTimeParseException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private String getFirstnameOrElse(final JSONObject object, final Supplier<String> fallback) {
        try {
            final Object firstnameObj = Objects.requireNonNull(object.get("firstname"));
            final String firstname = getString(firstnameObj);
            return firstname.isBlank() ? fallback.get() : firstname;
        } catch (final ClassCastException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private String getLastnameOrElse(final JSONObject object, final Supplier<String> fallback) {
        try {
            final Object lastnameObj = Objects.requireNonNull(object.get("lastname"));
            final String lastname = getString(lastnameObj);
            return lastname.isBlank() ? fallback.get() : lastname;
        } catch (final ClassCastException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private String getString(Object firstnameObj) {
        if (firstnameObj instanceof Long) return String.valueOf(firstnameObj);
        return (String) firstnameObj;
    }

    private int getReleaseYearOrElse(final JSONObject object, final Supplier<Integer> fallback) {
        try {
            final Object releaseYearObj = Objects.requireNonNull(object.get("release_year"));
            if (releaseYearObj instanceof String) return Integer.parseInt((String) releaseYearObj);
            return (int) (long) releaseYearObj;
        } catch (final ClassCastException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private Duration getDurationOrElse(final JSONObject object, final Supplier<Duration> fallback) {
        try {
            long intDuration;
            final Object durationObj = Objects.requireNonNull(object.get("duration"));
            if (durationObj instanceof String) intDuration = Long.parseLong((String) durationObj);
            else intDuration = (long) durationObj;
            return Duration.ofMinutes((intDuration));
        } catch (final ClassCastException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private String getTitleOrElse(final JSONObject object, final Supplier<String> fallback) {
        try {
            final Object titleObj = Objects.requireNonNull(object.get("title"));
            return String.valueOf(titleObj);
        } catch (final ClassCastException | NullPointerException ignored) {
            return fallback.get();
        }
    }

    private void save(Movie movie, List<Review> review) {
        if (movies.contains(movie)) {
            throw new ImportingExistingMovieException(String.format("The movie %s already exist", movie.getTitle()));
        }

        Set<Person> persons = new HashSet<>();
        Set<Category> categories = new HashSet<>();

        for (Iterator<Person> it = movie.castingIterator(); it.hasNext(); ) {
            persons.add(it.next());
        }

        for (Iterator<Person> it = movie.directorIterator(); it.hasNext(); ) {
            persons.add(it.next());
        }

        for (var it = movie.categoriesIterator(); it.hasNext(); ) {
            categories.add(it.next());
        }

        categories.forEach(filmographyStorage::addCategory);
        persons.forEach(filmographyStorage::addPerson);
        filmographyStorage.addMovie(movie);
        review.forEach(filmographyStorage::addReview);
    }

    public static class ExportResult {
        private final String filename;
        private final Path path;

        public ExportResult(final String filename, final Path path) {
            this.filename = filename;
            this.path = path;
        }

        public Path getPath() {
            return path;
        }

        public String getFilename() {
            return filename;
        }
    }

    public static class ImportResult {
        private final Movie movie;
        private final List<Review> review;

        public ImportResult(final Movie movie, final List<Review> reviews) {
            this.movie = movie;
            this.review = reviews;
        }

        public Movie getMovie() {
            return movie;
        }

        public List<Review> getReviews() {
            return review;
        }
    }
}
