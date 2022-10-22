package org.helmo.sd_projet.storage.connection;

public class MySQLConnectionData implements ConnectionData {
    @Override
    public String getDBPath() {
        return "jdbc:mysql://192.168.128.13:3306/in21sd2021?useSSL=false&serverTimezone=UTC";
    }

    @Override
    public String getUsername() {
        return "in21sd2021";
    }

    @Override
    public String getPassword() {
        return "ue12b2";
    }

    @Override
    public String getDriverName() {
        return "com.mysql.cj.jdbc.Driver";
    }
}
