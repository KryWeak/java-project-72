package hexlet.code.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public final class Database {
    private static HikariDataSource dataSource;

    private Database() { }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();

            // Используем переменную окружения DATABASE_URL от Render
            String dbUrl = System.getenv("DATABASE_URL");
            if (dbUrl == null || dbUrl.isEmpty()) {
                throw new IllegalStateException("DATABASE_URL environment variable is not set");
            }

            config.setJdbcUrl(dbUrl);
            config.setDriverClassName("org.postgresql.Driver"); // Явно указываем драйвер
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setPoolName("AppConnectionPool");

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
}
