package hexlet.code.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

public final class Database {
    private static HikariDataSource dataSource;

    private Database() { }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();

            // Получаем DATABASE_URL из переменной окружения
            String dbUrl = System.getenv("DATABASE_URL");
            if (dbUrl == null || dbUrl.isEmpty()) {
                throw new IllegalStateException("DATABASE_URL environment variable is not set");
            }

            try {
                // Парсим DATABASE_URL как URI
                URI dbUri = new URI(dbUrl.replace("postgresql://", "http://")); // Временная замена для парсинга

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String host = dbUri.getHost();
                String path = dbUri.getPath();
                int port = dbUri.getPort() != -1 ? dbUri.getPort() : 5432; // Стандартный порт PostgreSQL

                // Формируем правильный jdbcUrl
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s", host, port, path);
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setPoolName("AppConnectionPool");

                dataSource = new HikariDataSource(config);
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Invalid DATABASE_URL format", e);
            }
        }
        return dataSource;
    }
}
