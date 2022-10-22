package org.helmo.sd_projet.commands;

import org.helmo.sd_projet.domain.Customer;
import org.helmo.sd_projet.domain.Customers;
import org.helmo.sd_projet.storage.FilmographyStorage;

public class CreateUserCommand extends Command {

    private final Customers customers;
    FilmographyStorage filmographyStorage;

    /**
     * Initialise la partie de base d'une commande à l'aide du nom et de la
     * description.
     * Le nom sera utilisée pour chercher la commande à executer.
     * La description sera affichée à l'utilisateur lorsque ce dernier souhaiter
     * lister les commandes.
     *
     * @param customers
     * @param filmographyStorage
     */
    public CreateUserCommand(Customers customers, FilmographyStorage filmographyStorage) {
        super("createUser", "Create a user who can encode reviews on movies");
        this.customers = customers;
        this.filmographyStorage = filmographyStorage;
    }

    @Override
    public void execute() {
        Customer newCustomer = new Customer(readNonEmptyString("Enter user's nickname"));
        filmographyStorage.loadUsers();
        if (!customers.contains(newCustomer)) {
            filmographyStorage.addUser(newCustomer);
            filmographyStorage.loadUsers();
            System.out.println("User added !");
        } else
            System.out.println("The user's nickname is already in use");
    }
}
