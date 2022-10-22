package org.helmo.sd_projet.commands;

import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.storage.FilmographyStorage;

import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AddMovieCommand extends Command {
    private final FilmographyStorage filmographyStorage;
    private final Persons persons;
    private final Movies movies;
    private final Categories categories;

    public AddMovieCommand(final FilmographyStorage filmographyStorage, final Persons persons, final Movies movies, Categories categories) {
        super("addMovie", "Add movie, director et cast");
        this.filmographyStorage = filmographyStorage;
        this.persons = persons;
        this.movies = movies;
        this.categories = categories;
    }

    @Override
    public void execute() {
        filmographyStorage.loadPersons();
        filmographyStorage.loadCategories();

        String title = readNonEmptyString("Enter the movie title: ");

        int year = readInteger("Enter the movie release year: ");
        int minutes = readInteger("Enter the movie runtime in minutes: ");
        System.out.println("Select a director");
        List<Person> directors = new ArrayList<>();
        directors.add(getPerson());

        String text = "";
        do {
            text = readNonEmptyString("Do you want to add a director (yes/no) ?");
            if (text.equalsIgnoreCase("yes")) {
                final Person person = getPerson();
                if (directors.contains(person))
                    System.out.println("Director already added");
                else directors.add(person);
            }
        } while (text.equalsIgnoreCase("yes"));

        List<Person> cast = new ArrayList<>();
        text = "";
        do {
            text = readNonEmptyString("Do you want to add an actor (yes/no) ?");
            if (text.equalsIgnoreCase("yes")) {
                final Person person = getPerson();
                if (cast.contains(person))
                    System.out.println("Actor already added");
                else cast.add(getPerson());

            }
        } while (text.equalsIgnoreCase("yes"));

        List<Category> categories = new ArrayList<>();
        text = "";
        do {
            text = readNonEmptyString("Do you want to add a category (yes/no) ?");
            if (text.equalsIgnoreCase("yes")) {
                final Category category = getCategory();
                if (categories.contains(category))
                    System.out.println("Category already added");
                else categories.add(getCategory());
            }
        } while (text.equalsIgnoreCase("yes"));

        Movie newMovie = new Movie(title, year, Duration.ofMinutes(minutes), directors, cast, new HashSet<>(categories));
        filmographyStorage.loadMovies();
        if (movies.contains(newMovie)) {
            System.out.println("Movie already present in the database");
        } else {
            filmographyStorage.addMovie(newMovie);
            filmographyStorage.loadMovies();
        }
    }

    private Category getCategory() {
        List<Category> results;
        // Get List
        do {
            String text = readNonEmptyString("Select a Category, search by name: ");
            results = categories.searchCategory(text);
            if (results.isEmpty()) {
                System.out.println("There is no result for : " + text);
            }
        } while (results.isEmpty());

        // Display List
        System.out.println("Select a category - Search results :");
        for (int i = 0; i < results.size(); i++) {
            System.out.println(i + " - " + results.get(i));
        }
        final String choice = readNonEmptyString("Enter your choice : ");
        return results.get(Integer.parseInt(choice));
    }

    private Person getPerson() {
        List<Person> results;
        Person chosen = null;
        // Get List
        do {
            String text = readNonEmptyString("Select an Person, search by name: ");
            results = persons.searchPerson(text);
            if (results.isEmpty()) {
                System.out.println("There is no result for : " + text);
            } else {
                // Display List
                System.out.println("Select an Artist - Search results :");
                for (int i = 0; i < results.size(); i++) {
                    System.out.println(i + " - " + results.get(i).getLastname() + " " + results.get(i).getFirstname());
                }
                String choice = readNonEmptyString("Enter your choice : ");
                try {
                    chosen = results.get(Integer.parseInt(choice));
                } catch (IndexOutOfBoundsException | NumberFormatException exception) {
                    System.out.println("Incorrect format");
                }
            }
        } while (results.isEmpty() || chosen == null);
        return chosen;
    }

}
