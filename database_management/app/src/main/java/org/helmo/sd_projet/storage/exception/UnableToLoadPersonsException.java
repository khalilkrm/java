package org.helmo.sd_projet.storage.exception;

public class UnableToLoadPersonsException extends RuntimeException {

    private static final long serialVersionUID = -1153004784265480393L;

    public UnableToLoadPersonsException(final String message, final Exception cause) {
        super(message, cause);
    }
}
