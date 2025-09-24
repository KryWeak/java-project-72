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

            // Получаем DATABASE_URL из переменной окружения
            String dbUrl = System.getenv("DATABASE_URL");
            if (dbUrl == null || dbUrl.isEmpty()) {
                throw new IllegalStateException("DATABASE_URL environment variable is not set");
            }

            // Преобразуем postgresql:// в jdbc:postgresql://
            if (dbUrl.startsWith("postgresql://")) {
                dbUrl = "jdbc:" + dbUrl;
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
