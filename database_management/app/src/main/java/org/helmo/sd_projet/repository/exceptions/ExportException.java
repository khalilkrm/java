package org.helmo.sd_projet.repository.exceptions;

public class ExportException extends Exception {
    private static final long serialVersionUID = 4514223362180057742L;

    public ExportException(final String message, final Exception cause) {
        super(message, cause);
    }
}
