package hexlet.code.repositories;

import hexlet.code.models.Url;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing URLs in the database.
 */
public final class UrlRepository extends BaseRepository {
    public UrlRepository(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Saves a URL to the database.
     *
     * @param url the URL to save
     * @throws SQLException if a database error occurs
     */
    public void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            stmt.setTimestamp(2, Timestamp.valueOf(url.getCreatedAt()));
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            }
        }
    }

    /**
     * Finds a URL by its ID.
     *
     * @param id the ID of the URL
     * @return the URL or null if not found
     * @throws SQLException if a database error occurs
     */
    public Url find(Long id) throws SQLException {
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

    /**
     * Finds a URL by its name.
     *
     * @param name the name of the URL
     * @return the URL or null if not found
     * @throws SQLException if a database error occurs
     */
    public Url findByName(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
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

    /**
     * Retrieves all URLs from the database.
     *
     * @return a list of all URLs
     * @throws SQLException if a database error occurs
     */
    public List<Url> findAll() throws SQLException {
        String sql = "SELECT * FROM urls ORDER BY id";
        List<Url> urls = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Url url = new Url();
                url.setId(rs.getLong("id"));
                url.setName(rs.getString("name"));
                url.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                urls.add(url);
            }
        }
        return urls;
    }
}
