package org.helmo.sd_projet.storage.exception;

public class UnableToSaveReviewException extends RuntimeException {

    private static final long serialVersionUID = -7742831679166284944L;

    public UnableToSaveReviewException(final String message, final Exception cause) {
        super(message, cause);
    }

}
