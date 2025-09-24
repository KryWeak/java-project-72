package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static HikariDataSource dataSource;

    public static void init() {
        if (dataSource != null) return;

        HikariConfig config = new HikariConfig();

        // Используй Render DATABASE_URL в формате:
        // jdbc:postgresql://host:port/dbname?user=username&password=password
        String dbUrl = System.getenv().getOrDefault(
                "DATABASE_URL",
                "jdbc:postgresql://localhost:5432/java_project_72?user=postgres&password=postgres"
        );

        config.setJdbcUrl(dbUrl);
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);

        // Создаём таблицу users, если не существует
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    password VARCHAR(100) NOT NULL
                );
                """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}
