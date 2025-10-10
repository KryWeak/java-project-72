package hexlet.code.repositories;

import hexlet.code.models.UrlCheck;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck check) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, new String[]{"id"})) {
            stmt.setLong(1, check.getUrlId());
            stmt.setInt(2, check.getStatusCode());
            stmt.setString(3, check.getTitle());
            stmt.setString(4, check.getH1());
            stmt.setString(5, check.getDescription());
            stmt.setTimestamp(6, check.getCreatedAt());
            stmt.executeUpdate();
            var generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                check.setId(generatedKeys.getLong(1));
            }
        }
    }

    public static List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();
            List<UrlCheck> checks = new ArrayList<>();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                int statusCode = resultSet.getInt("status_code");
                String title = resultSet.getString("title");
                String h1 = resultSet.getString("h1");
                String description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var check = new UrlCheck(urlId, statusCode, title, h1, description, createdAt);
                check.setId(id);
                checks.add(check);
            }
            return checks;
        }
    }

    public static Optional<UrlCheck> findLatestByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC LIMIT 1";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                int statusCode = resultSet.getInt("status_code");
                String title = resultSet.getString("title");
                String h1 = resultSet.getString("h1");
                String description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var check = new UrlCheck(urlId, statusCode, title, h1, description, createdAt);
                check.setId(id);
                return Optional.of(check);
            }
            return Optional.empty();
        }
    }
}
