package org.helmo.sd_projet.storage.utility;

import org.helmo.sd_projet.storage.connection.ConnectionFactory;
import org.helmo.sd_projet.storage.connection.DerbyConnectionData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class RequestTestUtils {

    public static int count(final String tableName) throws SQLException, ClassNotFoundException {
        try (final Connection connection = doCreateConnection();
             final PreparedStatement statement = connection
                     .prepareStatement(String.format("SELECT COUNT(*) FROM %s", tableName));
             final ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public static List<String> column(Function<ResultSet, String> selector, final String tableName)
            throws SQLException, ClassNotFoundException {
        final List<String> currentValues = new ArrayList<>();
        try (final Connection connection = doCreateConnection();
             final PreparedStatement statement = connection
                     .prepareStatement(String.format("SELECT * FROM %s", tableName));
             final ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                currentValues.add(selector.apply(resultSet));
            }
            return currentValues;
        }
    }

    public static int relationCount(final String sql) throws SQLException, ClassNotFoundException {
        try (final Connection connection = doCreateConnection();
             final PreparedStatement statement = connection
                     .prepareStatement(sql);
             final ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public static <T, R> List<T> relation(final String sql, final Function<ResultSet, R> selector,
                                          final Function<R, T> converter) throws SQLException, ClassNotFoundException {
        final List<T> expectedValues = new ArrayList<>();
        try (final Connection connection = doCreateConnection();
             final PreparedStatement statement = connection
                     .prepareStatement(sql);
             final ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                var id = selector.apply(resultSet);
                var converted = converter.apply(id);
                expectedValues.add(converted);
            }
        }
        return expectedValues;
    }


    public static int getCastingPosition(final int movieID, final int personID)
            throws SQLException, ClassNotFoundException {
        try (final Connection connection = doCreateConnection();
             final PreparedStatement statement = connection
                     .prepareStatement("SELECT POSITION FROM CASTING WHERE MOVIE_ID = ? AND ACTOR_ID = ?");) {
            statement.setInt(1, movieID);
            statement.setInt(2, personID);
            try (final ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    private static Connection doCreateConnection() throws SQLException, ClassNotFoundException {
        return ConnectionFactory.createConnection(new DerbyConnectionData());
    }

    public static String stringSelector(final ResultSet resultSet, final String column) {
        try {
            return resultSet.getString(column);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
