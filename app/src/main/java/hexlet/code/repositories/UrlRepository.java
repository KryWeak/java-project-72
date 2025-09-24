package hexlet.code.repositories;

import hexlet.code.models.Url;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public UrlRepository(DataSource dataSource) {
        super(dataSource);
    }

    public Optional<Url> findByName(String name) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM urls WHERE name = ?")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Url(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find URL by name", e);
        }
        return Optional.empty();
    }

    public Url save(Url url) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO urls (name, created_at) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    url.setId(rs.getLong(1));
                    url.setCreatedAt(rs.getTimestamp("created_at"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save URL", e);
        }
        return url;
    }

    public List<Url> findAll() {
        List<Url> urls = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM urls");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                urls.add(new Url(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch URLs", e);
        }
        return urls;
    }

    public Optional<Url> findById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM urls WHERE id = ?")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Url(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find URL by id", e);
        }
        return Optional.empty();
    }
}
