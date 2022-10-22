package org.helmo.sd_projet.storage.exception;

public class UnableToLoadDirectorsException extends RuntimeException {

    private static final long serialVersionUID = 6984960453226169001L;

    public UnableToLoadDirectorsException(final String message, final Exception cause) {
        super(message, cause);
    }
}
