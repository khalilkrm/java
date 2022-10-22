package org.helmo.sd_projet.storage.exception;

public class UnableToLoadMovieException extends RuntimeException {
    private static final long serialVersionUID = 2633906055169717862L;

    public UnableToLoadMovieException(final String message, final Exception cause) {
        super(message, cause);
    }
}
