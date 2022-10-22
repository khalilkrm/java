package org.helmo.sd_projet.storage.utility.exceptions;

public class UnableToPopulateException extends RuntimeException {

    private static final long serialVersionUID = 4893020842067270431L;

    public UnableToPopulateException(final String message, final Exception cause) {
        super(message, cause);
    }
}
