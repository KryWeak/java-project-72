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

            // Ожидаемый формат: postgresql://user:password@host:port/dbname
            // Удаляем префикс postgresql://
            String cleanedUrl = dbUrl.replace("postgresql://", "");

            // Разделяем на user:password и host:port/dbname
            String[] userInfoAndHost = cleanedUrl.split("@");
            if (userInfoAndHost.length != 2) {
                throw new IllegalStateException("Invalid DATABASE_URL format: missing @ separator");
            }

            // Извлекаем user и password
            String[] userInfo = userInfoAndHost[0].split(":");
            if (userInfo.length != 2) {
                throw new IllegalStateException("Invalid DATABASE_URL format: missing user:password");
            }
            String username = userInfo[0];
            String password = userInfo[1];

            // Извлекаем host, port (если есть) и dbname
            String[] hostAndDb = userInfoAndHost[1].split("/");
            if (hostAndDb.length != 2) {
                throw new IllegalStateException("Invalid DATABASE_URL format: missing /dbname");
            }
            String dbname = hostAndDb[1];

            // Проверяем, есть ли порт в hostAndDb[0]
            String[] hostAndPort = hostAndDb[0].split(":");
            String host = hostAndPort[0];
            int port = 5432; // Стандартный порт PostgreSQL
            if (hostAndPort.length == 2) {
                try {
                    port = Integer.parseInt(hostAndPort[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Invalid DATABASE_URL format: invalid port", e);
                }
            }

            // Формируем правильный jdbcUrl
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbname);
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("org.postgresql.Driver");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setPoolName("AppConnectionPool");

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
}
