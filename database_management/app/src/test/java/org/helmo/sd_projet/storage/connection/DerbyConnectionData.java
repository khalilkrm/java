package org.helmo.sd_projet.storage.connection;

public class DerbyConnectionData implements ConnectionData {
    @Override
    public String getDriverName() {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }

    @Override
    public String getUsername() {
        return "root";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getDBPath() {
        return "jdbc:derby:../sd_project_derby";
    }
}
