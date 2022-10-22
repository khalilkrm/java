package org.helmo.sd_projet.repository.importer.exceptions;

public class JSONMissingPropertyException extends RuntimeException {

    private static final long serialVersionUID = 5762042150779188342L;

    public JSONMissingPropertyException(final String message) {
        super(message);
    }

}
