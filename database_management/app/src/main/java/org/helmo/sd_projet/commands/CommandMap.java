package org.helmo.sd_projet.commands;

import java.util.*;

/**
 * Associe une commande à son nom.
 * Parcours les commandes dans un ordre quelconque.
 */
public class CommandMap implements Iterable<Command> {
	private final Map<String, Command> commands;
	private final Iterable<Command> values;

	/**
	 * Initialise une map de commandes à partir d'un nombre quelconque de Commandes.
	 */
	public CommandMap(final Command... commands) {
		this.commands = new HashMap<>();
		for (final Command c : commands) {
			this.commands.put(c.getName(), c);
		}

		this.values = this.commands.values();
	}

	/**
	 * Retourne la commande associée à key ou null si aucune correspondances
	 * n'existe.
	 */
	public Command get(final String key) {
		return commands.get(key);
	}

	@Override
	public Iterator<Command> iterator() {
		return values.iterator();
	}
}
