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

            // Используем переменную окружения Render, если есть, иначе fallback на конкретный URL
            String dbUrl = System.getenv().getOrDefault(
                    "JDBC_DATABASE_URL",
                    "jdbc:postgresql://dpg-d378v8er433s73ehhv8g-a.oregon-postgres.render.com/java_project_72_j29o" +
                            "?user=java_project_72_j29o_user&password=t2QqZ9fK4uBaG9XK2rpZQTPhArrgZDPr"
            );

            config.setJdbcUrl(dbUrl);
            // Драйвер указывать не обязательно, Hikari сам определит по URL
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setPoolName("AppConnectionPool");

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
}
