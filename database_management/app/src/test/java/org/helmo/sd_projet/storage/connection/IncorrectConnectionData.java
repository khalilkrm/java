package org.helmo.sd_projet.storage.connection;

public class IncorrectConnectionData {

    public enum Mistakes {
        DRIVER_NAME_MISTAKE,
        USERNAME_MISTAKE,
        PASSWORD_MISTAKE,
        DBPATH_MISTAKE
    }

    private IncorrectConnectionData() {
    }

    public static ConnectionData withMistake(final Mistakes errorsType) {
        return new ConnectionData() {
            @Override
            public String getDriverName() {
                return errorsType == Mistakes.DRIVER_NAME_MISTAKE
                        ? "This not existing driver name should throw exception"
                        : "org.apache.derby.jdbc.EmbeddedDriver";
            }

            @Override
            public String getUsername() {
                return errorsType == Mistakes.USERNAME_MISTAKE ? "This username should throw exception" : "root";
            }

            @Override
            public String getPassword() {
                return errorsType == Mistakes.PASSWORD_MISTAKE ? "This password should throw exception" : "";
            }

            @Override
            public String getDBPath() {
                return errorsType == Mistakes.DBPATH_MISTAKE ? "This not existing database path should throw exception"
                        : "jdbc:derby:../sd_project_2021";
            }
        };
    }
}
