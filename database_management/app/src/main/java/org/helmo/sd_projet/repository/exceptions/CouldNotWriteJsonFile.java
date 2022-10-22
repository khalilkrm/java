package org.helmo.sd_projet.repository.exceptions;

public class CouldNotWriteJsonFile extends RuntimeException {

    private static final long serialVersionUID = 5584023381228115171L;

    public CouldNotWriteJsonFile(final String message, final Exception cause) {
        super(message, cause);
    }

}
