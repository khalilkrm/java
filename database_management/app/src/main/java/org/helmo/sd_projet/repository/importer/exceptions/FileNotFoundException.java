package org.helmo.sd_projet.repository.importer.exceptions;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(final String message) {
        super(message);
    }
}
