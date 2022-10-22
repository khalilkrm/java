package org.helmo.sd_projet;

import org.helmo.sd_projet.commands.Command;
import org.helmo.sd_projet.commands.CommandMap;

import java.util.Scanner;

/**
 * Analyse la commande encodée par l'utilisateur et l'exécute si elle existe.
 */
public class FrontController {
    private final CommandMap map;
    private boolean isRunning;

    /**
     * Initialise ce controleur avec une console et une carte des commandes
     * exécutables.
     */
    public FrontController(final CommandMap map) {
        this.map = map;
        isRunning = true;
    }

    /**
     * Boucle sur les commandes encodées par l'utilisateur.
     * Sort de la boucle quand ce dernier encode la command exit.
     */
    public void loop() {
        System.out.println("Bienvenue !");
        do {
            tryToExecute(readString("Encodez une commande : "));
        } while (isRunning);
    }

    private void tryToExecute(final String cmdName) {
        if ("list".equals(cmdName)) {
            this.list();
        } else if ("exit".equals(cmdName)) {
            isRunning = false;
        } else {
            doExecute(map.get(cmdName));
        }
    }

    private void doExecute(final Command command) {
        if (command != null) {
            command.execute();
        } else {
            System.out.println("Commande non valide !");
        }
    }

    private void list() {
        System.out.printf("%-20s %s", "NOM", "DESCRIPTION");
        System.out.println();
        for (final Command cmd : map) {
            System.out.printf("%-20s %s\n", cmd.getName(), cmd.getDescription());
        }
    }

    private String readString(final String msg) {
        System.out.println(msg);
        var input = new Scanner(System.in);
        return input.nextLine();
    }
}
