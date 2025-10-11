package hexlet.code.repositories;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base repository providing database connection management.
 */
public class BaseRepository {
    private static DataSource dataSource;

    public BaseRepository(DataSource dataSource) {
        BaseRepository.dataSource = dataSource;
    }

    /**
     * Gets a database connection from the data source.
     *
     * @return a database connection
     * @throws SQLException if a database error occurs
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
