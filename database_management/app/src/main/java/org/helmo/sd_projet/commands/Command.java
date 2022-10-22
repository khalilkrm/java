package org.helmo.sd_projet.commands;

import java.util.Scanner;

/**
 * Représente une opération sous la forme d'objets.
 */
public abstract class Command {
    private final String name;
    private final String description;

    /**
     * Initialise la partie de base d'une commande à l'aide du nom et de la
     * description.
     * Le nom sera utilisée pour chercher la commande à executer.
     * La description sera affichée à l'utilisateur lorsque ce dernier souhaiter
     * lister les commandes.
     */
    public Command(final String name, final String description) {
        this.name = name.trim();
        this.description = description.trim();
    }

    public final String getName() {
        return this.name;
    }

    public final String getDescription() {
        return this.description;
    }

    /**
     * Retourne true ssi le paramètre name correspond à l'attribut name de cet
     * objet.
     */
    public boolean hasName(final String name) {
        return this.name.equals(name);
    }

    /**
     * Exécute l'opération représentée par cette commande.
     */
    public abstract void execute();

    /**
     * lit un String depuis la console et le retourne.
     */
    protected String readString(final String msg) {
        System.out.println(msg);
        var input = new Scanner(System.in);
        return input.nextLine();
    }

    /**
     * Lit et retourne un String non vide.
     */
    protected String readNonEmptyString(final String msg) {
        String input;
        do {
            input = readString(msg);
        } while (input.isEmpty());
        return input;
    }

    /**
     * Lit et retourne un String non vide.
     */
    protected Integer readInteger(final String msg) {
        String input = readNonEmptyString(msg);
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            System.out.println("Error the encoded value is not an integer");
            return null;
        }
    }

    protected Integer readIntegerBetween(final String msg, int min, int max) {
        Integer chosen;
        boolean isBetween;
        do {
            chosen = readInteger(msg);
            if (chosen == null) return null;
            isBetween = chosen <= max && chosen >= min;
            if (!isBetween)
                System.out.println("The encoded integer is not between " + min + " and " + max);
        } while (!isBetween);
        return chosen;
    }

    @Override
    public int hashCode() {
        return name.hashCode() % 8;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Command)) {
            return false;
        }
        final Command other = (Command) obj;
        return hasName(other.name);
    }
}
