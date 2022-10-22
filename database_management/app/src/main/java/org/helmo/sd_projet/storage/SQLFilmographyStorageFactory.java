package org.helmo.sd_projet.storage;

import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.storage.exception.UnableToConnectException;
import org.helmo.sd_projet.storage.connection.ConnectionData;
import org.helmo.sd_projet.storage.connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLFilmographyStorageFactory {

    private final ConnectionData connectionData;
    private Connection connection;

    public SQLFilmographyStorageFactory(ConnectionData connectionData) {
        this.connectionData = connectionData;
    }

    public FilmographyStorage newStorage(Customers customers, Persons persons, Movies movies, Reviews reviews, Categories categories) {
        try {
            connection = ConnectionFactory.createConnection(connectionData);
            return new SQLFilmographyStorage(customers, persons, movies, reviews, categories, connection);
        } catch (ClassNotFoundException | SQLException exception) {
            throw new UnableToConnectException(exception.getMessage());
        }
    }

    public boolean connectionIsClosed() throws SQLException {
        return connection.isClosed();
    }
}
