package org.helmo.sd_projet.commands;

import org.helmo.sd_projet.domain.Person;
import org.helmo.sd_projet.domain.Persons;
import org.helmo.sd_projet.storage.FilmographyStorage;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

public class AddPersonCommand extends Command {
    private final Persons persons;
    private final FilmographyStorage filmographyStorage;

    /**
     * Initialise la partie de base d'une commande à l'aide du nom et de la
     * description.
     * Le nom sera utilisée pour chercher la commande à executer.
     * La description sera affichée à l'utilisateur lorsque ce dernier souhaiter
     * lister les commandes.
     *
     * @param persons
     * @param filmographyStorage
     */
    public AddPersonCommand(Persons persons, FilmographyStorage filmographyStorage) {
        super("addPerson", "Add a person who could be an actor or a director, or both");
        this.persons = persons;
        this.filmographyStorage = filmographyStorage;
    }

    @Override
    public void execute() {
        String firstname = readNonEmptyString("Enter person's firstname: ");
        String lastname = readNonEmptyString("Enter person's lastname: ");
        int birthYear = readIntegerBetween("Enter birth's year: ", 1900, LocalDate.now().getYear());
        int birthMonth = readIntegerBetween("Enter birth's month: ", 1, 12);
        int birthDay = readIntegerBetween("Enter birth's day: ", 1, LocalDate.of(birthYear, birthMonth, 1).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth());

        Person newPerson = new Person(firstname, lastname, LocalDate.of(birthYear, birthMonth, birthDay));
        filmographyStorage.loadPersons();
        if (!persons.contains(newPerson)) {
            filmographyStorage.addPerson(newPerson);
            filmographyStorage.loadPersons();
        } else
            System.out.println("This person is already in the database");
    }
}