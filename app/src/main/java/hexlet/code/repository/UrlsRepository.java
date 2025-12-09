package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlsRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            preparedStatement.setString(1, url.getName());
            preparedStatement.setTimestamp(2, createdAt);
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }
    public static Optional<Url> find(Long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(name);
                url.setCreatedAt(createdAt);
                url.setId(id);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }
    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT * FROM urls";

        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(id, name);
                url.setCreatedAt(createdAt);
                result.add(url);
            }
            return result;
        }
    }
    public static Optional<Url> getByName(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var url = new Url(name);
                return Optional.of(url);
            } else {
                return Optional.empty();
            }
        }
    }

    public static boolean isExist(String urlName) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, urlName);
            var result = stmt.executeQuery();
            if (result.next()) {
                return true;
            }
            return false;
        }
    }
}
