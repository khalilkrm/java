package org.helmo.sd_projet.storage.exception;

public class UnableToSaveCustomerException extends RuntimeException {
    private static final long serialVersionUID = -5013545608811142616L;

    public UnableToSaveCustomerException(final String message, final Exception cause) {
        super(message, cause);
    }
}
