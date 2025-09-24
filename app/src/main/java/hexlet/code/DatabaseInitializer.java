package hexlet.code;

import hexlet.code.repositories.Database;

public class DatabaseInitializer {

    public static void init() {
        // Просто инициализируем DataSource
        Database.getDataSource();

    }
}
