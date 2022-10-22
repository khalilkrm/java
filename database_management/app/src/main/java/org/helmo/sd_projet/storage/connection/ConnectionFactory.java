package org.helmo.sd_projet.storage.connection;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {

    private static Class<Driver> driver;

    public static Connection createConnection(final ConnectionData connectionData)
            throws SQLException, ClassNotFoundException {
        loadDriver(connectionData);
        return DriverManager.getConnection(
                connectionData.getDBPath(),
                connectionData.getUsername(),
                connectionData.getPassword());
    }

    private static void loadDriver(final ConnectionData connectionData) throws ClassNotFoundException {
        driver = (Class<Driver>) Class.forName(connectionData.getDriverName());
    }
}
