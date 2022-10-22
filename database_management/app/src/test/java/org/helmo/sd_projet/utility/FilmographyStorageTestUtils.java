package org.helmo.sd_projet.utility;

import org.helmo.sd_projet.domain.*;
import org.helmo.sd_projet.storage.exception.UnableToConnectException;
import org.helmo.sd_projet.storage.utility.FilmographyDBPopulate;
import org.helmo.sd_projet.storage.FilmographyStorage;
import org.helmo.sd_projet.storage.SQLFilmographyStorage;
import org.helmo.sd_projet.storage.connection.ConnectionData;
import org.helmo.sd_projet.storage.connection.ConnectionFactory;

import java.sql.*;

public final class FilmographyStorageTestUtils {

    public static ConnectionHandler from(final ConnectionData connectionData) {
        return () -> {
            try {
                return new FilmographyDBPopulate(ConnectionFactory.createConnection(connectionData));
            } catch (SQLException | ClassNotFoundException exception) {
                throw new UnableToConnectException(exception.getMessage(), exception);
            }
        };
    }

    public static FilmographyStorage from(final Connection connection, Customers customers, Persons persons,
                                          Movies movies, Reviews reviews, Categories categories) {
        return new SQLFilmographyStorage(customers, persons, movies, reviews, categories, connection);
    }
}