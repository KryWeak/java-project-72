package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * Database configuration and initialization.
 */
public final class Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
    private static HikariDataSource dataSource;

    /**
     * Gets the configured DataSource.
     *
     * @return the DataSource
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
            if (jdbcUrl != null && !jdbcUrl.isBlank()) {
                config.setJdbcUrl(jdbcUrl);
                LOGGER.info("Using JDBC_DATABASE_URL from environment");
            } else {
                config.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1");
                LOGGER.info("Using in-memory H2 database");
            }
            config.setDriverClassName("org.h2.Driver");
            dataSource = new HikariDataSource(config);

            try {
                String schema = Files.readString(Paths.get("src/main/resources/schema.sql"));
                try (var conn = dataSource.getConnection();
                     var stmt = conn.createStatement()) {
                    stmt.execute(schema);
                    LOGGER.info("Database schema initialized");
                }
            } catch (IOException | SQLException e) {
                LOGGER.error("Failed to initialize database schema", e);
                throw new RuntimeException("Database initialization failed", e);
            }
        }
        return dataSource;
    }
}
