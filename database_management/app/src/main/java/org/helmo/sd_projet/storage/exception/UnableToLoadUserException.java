package org.helmo.sd_projet.storage.exception;

public class UnableToLoadUserException extends RuntimeException {
    private static final long serialVersionUID = -4600184779407030265L;

    public UnableToLoadUserException(final String message, final Exception cause) {
        super(message, cause);
    }

}
