package org.helmo.sd_projet.storage.exception;

public class UnableToSaveMovieException extends RuntimeException {

    private static final long serialVersionUID = 4169017691649166204L;

    public UnableToSaveMovieException(final String message, final Exception cause) {
        super(message, cause);
    }
}
