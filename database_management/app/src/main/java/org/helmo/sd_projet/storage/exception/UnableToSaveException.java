package org.helmo.sd_projet.storage.exception;

public class UnableToSaveException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnableToSaveException(final Exception e) {
        super(e);
    }

}
