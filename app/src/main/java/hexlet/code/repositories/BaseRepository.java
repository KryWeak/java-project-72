package hexlet.code.repositories;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.Database;

public abstract class BaseRepository {
    protected static HikariDataSource dataSource = Database.getDataSource();
}
