package org.helmo.sd_projet.storage.connection;

public class LocalhostConnectionData implements ConnectionData {
    @Override
    public String getDBPath() {
        return "jdbc:mysql://localhost/sd_project_local?useSSL=false&serverTimezone=UTC";
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
    public String getDriverName() {
        return "com.mysql.cj.jdbc.Driver";
    }
}
