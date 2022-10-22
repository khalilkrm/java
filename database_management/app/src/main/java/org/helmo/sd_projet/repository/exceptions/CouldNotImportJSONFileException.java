package org.helmo.sd_projet.repository.exceptions;

public class CouldNotImportJSONFileException extends RuntimeException {

    private static final long serialVersionUID = -2680327418668699889L;

    public CouldNotImportJSONFileException(final String message, final Exception cause) {
        super(message, cause);
    }

}
