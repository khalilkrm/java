package org.helmo.sd_projet.storage.exception;

public class UnableToLoadCategoriesException extends RuntimeException {

    private static final long serialVersionUID = -7204280937973023786L;

    public UnableToLoadCategoriesException(final String message, final Exception cause) {
        super(message, cause);
    }

}
