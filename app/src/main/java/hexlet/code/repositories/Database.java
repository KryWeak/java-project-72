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

            // Проверяем, есть ли JDBC_DATABASE_URL (для продакшена)
            String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
            if (jdbcUrl != null && !jdbcUrl.isEmpty()) {
                // Продакшен: PostgreSQL
                config.setJdbcUrl(jdbcUrl);
                config.setDriverClassName("org.postgresql.Driver");
            } else {
                // Локальная разработка: H2
                config.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1");
                config.setDriverClassName("org.h2.Driver");
            }

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setPoolName("AppConnectionPool");

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
}
