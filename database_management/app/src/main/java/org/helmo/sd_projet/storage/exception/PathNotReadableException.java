package org.helmo.sd_projet.storage.exception;

public class PathNotReadableException extends Exception {
    private static final long serialVersionUID = -472904076988362738L;

    public PathNotReadableException(final String message, final Exception cause) {
        super(message, cause);
    }

}
