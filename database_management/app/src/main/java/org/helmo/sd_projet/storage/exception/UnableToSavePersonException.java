package org.helmo.sd_projet.storage.exception;

public class UnableToSavePersonException extends RuntimeException {

    private static final long serialVersionUID = 6043286817319430922L;

    public UnableToSavePersonException(final String message, final Exception cause) {
        super(message, cause);
    }
}
