package hexlet.code.repositories;

import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for managing URL checks in the database.
 */
public final class UrlCheckRepository extends BaseRepository {
    public UrlCheckRepository(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Saves a URL check to the database.
     *
     * @param check the URL check to save
     * @throws SQLException if a database error occurs
     */
    public void save(UrlCheck check) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, check.getUrlId());
            stmt.setInt(2, check.getStatusCode());
            stmt.setString(3, check.getTitle());
            stmt.setString(4, check.getH1());
            stmt.setString(5, check.getDescription());
            stmt.setTimestamp(6, Timestamp.valueOf(check.getCreatedAt()));
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                check.setId(generatedKeys.getLong(1));
            }
        }
    }

    /**
     * Finds all URL checks for a given URL ID.
     *
     * @param urlId the ID of the URL
     * @return a list of URL checks
     * @throws SQLException if a database error occurs
     */
    public List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY id DESC";
        List<UrlCheck> checks = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UrlCheck check = new UrlCheck();
                check.setId(rs.getLong("id"));
                check.setUrlId(rs.getLong("url_id"));
                check.setStatusCode(rs.getInt("status_code"));
                check.setTitle(rs.getString("title"));
                check.setH1(rs.getString("h1"));
                check.setDescription(rs.getString("description"));
                check.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                checks.add(check);
            }
        }
        return checks;
    }

    /**
     * Finds all URL checks grouped by URL IDs.
     *
     * @param urls the list of URLs
     * @return a map of URL IDs to their checks
     * @throws SQLException if a database error occurs
     */
    public Map<Long, List<UrlCheck>> findAllByUrls(List<Url> urls) throws SQLException {
        Map<Long, List<UrlCheck>> checksByUrl = new HashMap<>();
        for (Url url : urls) {
            checksByUrl.put(url.getId(), findByUrlId(url.getId()));
        }
        return checksByUrl;
    }

    /**
     * Finds a URL by its ID (used for validation).
     *
     * @param id the ID of the URL
     * @return the URL or null if not found
     * @throws SQLException if a database error occurs
     */
    public Url findUrlById(Long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Url url = new Url();
                url.setId(rs.getLong("id"));
                url.setName(rs.getString("name"));
                url.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return url;
            }
            return null;
        }
    }
}
