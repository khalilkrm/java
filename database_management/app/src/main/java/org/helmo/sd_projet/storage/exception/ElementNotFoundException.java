package org.helmo.sd_projet.storage.exception;

import java.sql.SQLException;

public class ElementNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ElementNotFoundException(String message, SQLException ex) {
        super(message, ex);
    }
}
