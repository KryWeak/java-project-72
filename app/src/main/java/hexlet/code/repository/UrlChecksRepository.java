package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class UrlChecksRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        var sql = "INSERT INTO url_checks (status_code, title, h1, description, url_id, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            preparedStatement.setInt(1, urlCheck.getStatusCode());
            preparedStatement.setString(2, urlCheck.getTitle());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getDescription());
            preparedStatement.setLong(5, urlCheck.getUrlId());
            preparedStatement.setTimestamp(6, createdAt);
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Deque<UrlCheck> getEntitiesByUrlId(Long urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ?";

        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, urlId);
            var resultSet = preparedStatement.executeQuery();
            Deque<UrlCheck> entities = new ArrayDeque<>();

            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");

                var urlCheck = new UrlCheck(id, urlId, statusCode, title, h1, description, createdAt);
                entities.addFirst(urlCheck);
            }

            return entities;
        }
    }
    public static Map<Long, UrlCheck> findLatestChecks() throws SQLException {
        var sql = "SELECT DISTINCT ON (url_id) * from url_checks order by url_id DESC, id DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new HashMap<Long, UrlCheck>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var urlId = resultSet.getLong("url_id");
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var check = new UrlCheck(statusCode, title, h1, description);
                check.setId(id);
                check.setUrlId(urlId);
                check.setCreatedAt(createdAt);
                result.put(urlId, check);
            }
            return result;
        }
    }
}
