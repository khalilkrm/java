package org.helmo.sd_projet.commands;

import com.google.protobuf.Enum;
import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.storage.FilmographyStorage;
import org.helmo.sd_projet.storage.exception.UnableToSaveReviewException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddReviewCommand extends Command {
    private final Customers customers;
    private final Movies movies;
    private final Reviews reviews;
    private final FilmographyStorage filmographyStorage;

    /**
     * Initialise la partie de base d'une commande à l'aide du nom et de la
     * description.
     * Le nom sera utilisée pour chercher la commande à executer.
     * La description sera affichée à l'utilisateur lorsque ce dernier souhaite
     * lister les commandes.
     *
     * @param customers
     * @param movies
     * @param reviews
     * @param filmographyStorage
     */
    public AddReviewCommand(Customers customers, Movies movies, Reviews reviews,
                            FilmographyStorage filmographyStorage) {
        super("addReview", "Allow to add user review on a movie");
        this.customers = customers;
        this.movies = movies;
        this.reviews = reviews;
        this.filmographyStorage = filmographyStorage;
    }

    @Override
    public void execute() {
        filmographyStorage.loadUsers();
        filmographyStorage.loadMovies();

        Customer customer;
        do {
            customer = getCustomer();
        } while (customer == null);

        Movie movie = getMovie();
        String comment = readNonEmptyString("Enter review's comment : ");
        int evaluation = -1;
        do {
            evaluation = readInteger("Enter review's evaluation [0-10]: ");
        } while (evaluation < 0 || evaluation > 10);

        Review review = new Review(comment, evaluation, customer, movie, LocalDate.now());

        filmographyStorage.loadReviews();
        if (!reviews.contains(review)) {
            try {
                filmographyStorage.addReview(review);
                filmographyStorage.loadReviews();
            } catch (final UnableToSaveReviewException exception) {
                System.out.println("Could not save reviews for reason : " + exception.getMessage());
            }
        } else {
            System.out.println("A review of this film by this author already exists");
        }
    }

    private Customer getCustomer() {
        List<Customer> results;
        Integer number = 0;
        // Get List
        do {
            String text = readNonEmptyString("Select a Customer, search by nickname: ");
            results = customers.searchCustomer(text);
            if (results.isEmpty()) {
                System.out.println("There is no result for : " + text);
            } else {
                // Display List
                System.out.println("Select a Customer - Search results :");
                for (int i = 0; i < results.size(); i++) {
                    System.out.println(i + " - " + results.get(i));
                }
                number = readIntegerBetween("Enter your choice : ", 0, results.size() - 1);
            }
        } while (results.isEmpty());

        return results.get(number);
    }

    private Movie getMovie() {
        List<Movie> results;
        // Get List
        do {
            String text = readNonEmptyString("Select a movie, search by name: ");
            results = movies.searchMovie(text);
            if (results.isEmpty()) {
                System.out.println("There is no result for : " + text);
            }
        } while (results.isEmpty());

        // Display List
        System.out.println("Select an Artist - Search results :");
        for (int i = 0; i < results.size(); i++) {
            System.out.println(i + " - " + results.get(i).getTitle());
        }
        String choice = readNonEmptyString("Enter your choice : ");
        return results.get(Integer.parseInt(choice));
    }

}
