package org.helmo.sd_projet.storage.exception;

public class UnableToSaveCategoryException extends RuntimeException {
    private static final long serialVersionUID = 3561962148827914415L;

    public UnableToSaveCategoryException(final String message, final Exception cause) {
        super(message, cause);
    }

}
