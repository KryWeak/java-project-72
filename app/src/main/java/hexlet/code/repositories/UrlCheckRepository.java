package hexlet.code.repositories;

import hexlet.code.models.UrlCheck;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck check) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, check.getUrlId());
            stmt.setInt(2, check.getStatusCode());
            stmt.setString(3, check.getTitle());
            stmt.setString(4, check.getH1());
            stmt.setString(5, check.getDescription());
            stmt.setTimestamp(6, check.getCreatedAt());
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                check.setId(generatedKeys.getLong(1));
            }
        }
    }

    public static List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            ResultSet rs = stmt.executeQuery();
            List<UrlCheck> checks = new ArrayList<>();
            while (rs.next()) {
                UrlCheck check = new UrlCheck(
                        rs.getLong("url_id"),
                        rs.getInt("status_code"),
                        rs.getString("title"),
                        rs.getString("h1"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                );
                check.setId(rs.getLong("id"));
                checks.add(check);
            }
            return checks;
        }
    }

    public static Optional<UrlCheck> findLatestByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UrlCheck check = new UrlCheck(
                        rs.getLong("url_id"),
                        rs.getInt("status_code"),
                        rs.getString("title"),
                        rs.getString("h1"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                );
                check.setId(rs.getLong("id"));
                return Optional.of(check);
            }
            return Optional.empty();
        }
    }
}
