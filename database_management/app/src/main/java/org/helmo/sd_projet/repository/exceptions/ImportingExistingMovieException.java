package org.helmo.sd_projet.repository.exceptions;

public class ImportingExistingMovieException extends RuntimeException {
    private static final long serialVersionUID = -1393747343859508361L;

    public ImportingExistingMovieException(final String message) {
        super(message);
    }
}
