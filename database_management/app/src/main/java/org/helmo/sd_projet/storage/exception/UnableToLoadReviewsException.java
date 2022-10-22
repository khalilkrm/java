package org.helmo.sd_projet.storage.exception;

import java.sql.SQLException;

public class UnableToLoadReviewsException extends RuntimeException {

    private static final long serialVersionUID = -7707099927688345840L;

    public UnableToLoadReviewsException(String message, SQLException exception) {
        super(message, exception);
    }
}
