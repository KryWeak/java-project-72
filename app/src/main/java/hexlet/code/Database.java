package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {
    private static HikariDataSource dataSource;

    public static HikariDataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
            if (jdbcUrl != null && !jdbcUrl.isEmpty()) {
                config.setJdbcUrl(jdbcUrl);
            } else {
                config.setJdbcUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1");
                config.setDriverClassName("org.h2.Driver");
            }
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
