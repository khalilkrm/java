package org.helmo.sd_projet.storage.exception;

public class UnableToConnectException extends RuntimeException {

    private static final long serialVersionUID = -3540091248874597644L;

    public UnableToConnectException(final String message) {
        super(message);
    }

    public UnableToConnectException(final String message, Exception cause) {
        super(message, cause);
    }

}
