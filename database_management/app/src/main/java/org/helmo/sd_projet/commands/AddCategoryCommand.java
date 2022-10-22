package org.helmo.sd_projet.commands;

import org.helmo.sd_projet.domain.Categories;
import org.helmo.sd_projet.domain.Category;
import org.helmo.sd_projet.storage.FilmographyStorage;

public class AddCategoryCommand extends Command {

    private final FilmographyStorage filmographyStorage;
    private final Categories categories;

    /**
     * Initialise la partie de base d'une commande à l'aide du nom et de la
     * description.
     * Le nom sera utilisée pour chercher la commande à executer.
     * La description sera affichée à l'utilisateur lorsque ce dernier souhaiter
     * lister les commandes.
     */
    public AddCategoryCommand(final FilmographyStorage filmographyStorage, final Categories categories) {
        super("addCategory", "Allow to add a movie category");
        this.filmographyStorage = filmographyStorage;
        this.categories = categories;
    }

    @Override
    public void execute() {
        String text = "";
        do {
            String categoryName = readNonEmptyString("Enter the category name: ");
            Category category = new Category(categoryName.toLowerCase());
            filmographyStorage.loadCategories();
            if (!categories.contains(category)) {
                filmographyStorage.addCategory(category);
                filmographyStorage.loadCategories();
                System.out.println("Category added !");
                text = "";
            } else {
                System.out.println("This category already exist.");
                text = readNonEmptyString("Do you want to try again (yes/no) ?");
            }
        } while (text.equalsIgnoreCase("yes"));
    }
}
