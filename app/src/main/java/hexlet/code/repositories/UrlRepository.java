package hexlet.code.repositories;

import hexlet.code.models.Url;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UrlRepository extends BaseRepository {
    /**
     * Constructs a new UrlRepository with the specified DataSource.
     *
     * @param dataSource the DataSource to use for database operations
     */
    public UrlRepository(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Finds a URL by its name.
     *
     * @param name the name of the URL to find
     * @return an Optional containing the found URL, or empty if not found
     * @throws SQLException if a database access error occurs
     */
    public Optional<Url> findByName(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getLong("id");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(id, name, createdAt);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    /**
     * Saves a URL to the database.
     *
     * @param url the URL to save
     * @throws SQLException if a database access error occurs
     */
    public void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, new String[]{"id"})) {
            stmt.setString(1, url.getName());
            stmt.setTimestamp(2, url.getCreatedAt());
            stmt.executeUpdate();
            var generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            }
        }
    }

    /**
     * Retrieves all URLs from the database.
     *
     * @return a List of all URLs
     * @throws SQLException if a database access error occurs
     */
    public List<Url> findAll() throws SQLException {
        var sql = "SELECT * FROM urls";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(id, name, createdAt);
                result.add(url);
            }
            return result;
        }
    }

    /**
     * Finds a URL by its ID.
     *
     * @param id the ID of the URL to find
     * @return an Optional containing the found URL, or empty if not found
     * @throws SQLException if a database access error occurs
     */
    public Optional<Url> findById(Long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(id, name, createdAt);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }
}
