package org.helmo.sd_projet.storage.exception;

public class UnableToLoadCastingsException extends RuntimeException {

    private static final long serialVersionUID = -5008373178201382495L;

    public UnableToLoadCastingsException(final String message, final Exception cause) {
        super(message, cause);
    }
}
