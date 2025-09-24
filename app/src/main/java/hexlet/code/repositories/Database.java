package hexlet.code.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class Database {

    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource != null) {
            return dataSource;
        }

        String databaseUrl = System.getenv().getOrDefault("DATABASE_URL",
                "jdbc:postgresql://localhost:5432/java_project_72?user=postgres&password=postgres");

        HikariConfig config = new HikariConfig();

        try {
            if (databaseUrl.startsWith("postgres://")) {
                // Convert DATABASE_URL from Render format to JDBC format
                URI dbUri = new URI(databaseUrl);
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();

                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);

                // SSL для Render
                config.addDataSourceProperty("sslmode", "require");
            } else {
                config.setJdbcUrl(databaseUrl);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid DATABASE_URL", e);
        }

        dataSource = new HikariDataSource(config);
        return dataSource;
    }
}
