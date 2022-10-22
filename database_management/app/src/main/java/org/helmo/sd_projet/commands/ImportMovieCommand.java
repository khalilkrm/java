package org.helmo.sd_projet.commands;

import org.helmo.sd_projet.repository.FilmographyRepository;
import org.helmo.sd_projet.repository.exceptions.CouldNotImportJSONFileException;
import org.helmo.sd_projet.repository.exceptions.ImportingExistingMovieException;
import org.helmo.sd_projet.repository.importer.exceptions.FileNotFoundException;
import org.helmo.sd_projet.storage.FilmographyStorage;
import org.helmo.sd_projet.storage.exception.PathNotReadableException;

public class ImportMovieCommand extends Command {

    private final FilmographyRepository filmographyRepository;
    private final FilmographyStorage filmographyStorage;

    /**
     * Initialise la partie de base d'une commande à l'aide du nom et de la
     * description.
     * Le nom sera utilisée pour chercher la commande à executer.
     * La description sera affichée à l'utilisateur lorsque ce dernier souhaiter
     * lister les commandes.
     *
     * @param filmographyRepository
     * @param filmographyStorage
     */
    public ImportMovieCommand(FilmographyRepository filmographyRepository, FilmographyStorage filmographyStorage) {
        super("importMovie", "Import a movie with casting, directors and reviews from Json file to database");
        this.filmographyRepository = filmographyRepository;
        this.filmographyStorage = filmographyStorage;
    }

    @Override
    public void execute() {
        String filepath = readNonEmptyString("Encode filepath: ");
        try {
            loadFromDatabase();
            filmographyRepository.importFile(filepath);
            filmographyStorage.loadMovies();
        } catch (final ImportingExistingMovieException alreadyExistException) {
            System.out.println("The movie to import already exist database");
        } catch (final CouldNotImportJSONFileException exception) {
            System.out.println("Could not import : " + exception.getMessage());
        } catch (final FileNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void loadFromDatabase() {
        filmographyStorage.loadPersons();
        filmographyStorage.loadMovies();
        filmographyStorage.loadUsers();
        filmographyStorage.loadReviews();
        filmographyStorage.loadCategories();
    }

}
