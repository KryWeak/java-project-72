package hexlet.code.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class Database {
    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();

            // Берём URL базы из переменной окружения или H2 по умолчанию
            String dbUrl = System.getenv().getOrDefault(
                    "JDBC_DATABASE_URL",
                    "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
            );

            config.setJdbcUrl(dbUrl);
            config.setMaximumPoolSize(10);
            config.setPoolName("AppConnectionPool");

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    // Чтобы можно было закрыть соединение при завершении приложения
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
