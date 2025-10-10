package hexlet.code.repositories;

import hexlet.code.models.Url;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            stmt.setTimestamp(2, url.getCreatedAt());
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            }
        }
    }

    public static Optional<Url> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Url url = new Url(rs.getString("name"), rs.getTimestamp("created_at"));
                url.setId(rs.getLong("id"));
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static List<Url> findAll() throws SQLException {
        String sql = "SELECT * FROM urls";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            List<Url> urls = new ArrayList<>();
            while (rs.next()) {
                Url url = new Url(rs.getString("name"), rs.getTimestamp("created_at"));
                url.setId(rs.getLong("id"));
                urls.add(url);
            }
            return urls;
        }
    }

    public static Optional<Url> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Url url = new Url(rs.getString("name"), rs.getTimestamp("created_at"));
                url.setId(rs.getLong("id"));
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }
}
