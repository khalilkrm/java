package org.helmo.sd_projet.storage.utility;

import org.helmo.sd_projet.storage.utility.exceptions.UnableToPopulateException;
import org.helmo.sd_projet.storage.utility.exceptions.UnableToResetException;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class FilmographyDBPopulate implements AutoCloseable {

    private final Connection connection;

    public FilmographyDBPopulate(final Connection connection) {
        this.connection = connection;
    }

    public FilmographyDBPopulate populate(final List<String> data) {
        try (final Statement statement = connection.createStatement()) {
            for (final var sql : data) {
                statement.addBatch(sql);
            }
            statement.executeBatch();
        } catch (final Exception exception) {
            throw new UnableToPopulateException(exception.getMessage(), exception);
        }
        return this;
    }

    public FilmographyDBPopulate reset() {
        try (final Statement statement = connection.createStatement()) {
            statement.addBatch("DELETE FROM REVIEW");
            statement.addBatch("DELETE FROM DIRECTED_BY");
            statement.addBatch("DELETE FROM CASTING");
            statement.addBatch("DELETE FROM MOVIE_CATEGORY");

            statement.addBatch("DELETE FROM CATEGORY");
            statement.addBatch("DELETE FROM CUSTOMER");
            statement.addBatch("DELETE FROM PERSON");
            statement.addBatch("DELETE FROM MOVIE");

            statement.addBatch("ALTER TABLE CUSTOMER ALTER COLUMN ID RESTART WITH 1");
            statement.addBatch("ALTER TABLE PERSON ALTER COLUMN ID RESTART WITH 1");
            statement.addBatch("ALTER TABLE MOVIE ALTER COLUMN ID RESTART WITH 1");

            statement.executeBatch();
        } catch (Exception exception) {
            throw new UnableToResetException(exception.getMessage(), exception);
        }

        return this;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
