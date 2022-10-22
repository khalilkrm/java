package org.helmo.sd_projet.commands;

import org.helmo.sd_projet.domain.Movie;
import org.helmo.sd_projet.domain.Movies;
import org.helmo.sd_projet.repository.FilmographyRepository;
import org.helmo.sd_projet.repository.exceptions.CouldNotImportJSONFileException;
import org.helmo.sd_projet.repository.exceptions.ExportException;
import org.helmo.sd_projet.storage.FilmographyStorage;

import java.util.List;

public class ExportMovieCommand extends Command {
    private final FilmographyRepository filmographyRepository;
    private final FilmographyStorage filmographyStorage;
    private final Movies movies;

    /**
     * Initialise la partie de base d'une commande à l'aide du nom et de la
     * description.
     * Le nom sera utilisée pour chercher la commande à executer.
     * La description sera affichée à l'utilisateur lorsque ce dernier souhaiter
     * lister les commandes.
     *
     * @param filmographyRepository
     * @param filmographyStorage
     * @param movies
     */
    public ExportMovieCommand(FilmographyRepository filmographyRepository, FilmographyStorage filmographyStorage,
                              Movies movies) {
        super("exportMovie", "Export a movie, its directors, casting and reviews into json file");
        this.filmographyRepository = filmographyRepository;
        this.filmographyStorage = filmographyStorage;
        this.movies = movies;
    }

    @Override
    public void execute() {
        loadFromDatabase();

        Movie movie = getMovie();
        String filepath = readNonEmptyString("Encode filepath: ");
        try {
            filmographyRepository.export(filepath, movie);
        } catch (final ExportException exception) {
            System.out.println("Could not export for reason of : " + exception.getCause().getClass() + " " + exception.getMessage());
        }
    }

    private void loadFromDatabase() {
        filmographyStorage.loadPersons();
        filmographyStorage.loadMovies();
        filmographyStorage.loadUsers();
        filmographyStorage.loadReviews();
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
