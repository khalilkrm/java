package org.helmo.sd_projet.storage.utility.exceptions;

public class UnableToResetException extends RuntimeException {

    private static final long serialVersionUID = -4016868422879007348L;

    public UnableToResetException(final String message, final Exception cause) {
        super(message, cause);
    }
}
