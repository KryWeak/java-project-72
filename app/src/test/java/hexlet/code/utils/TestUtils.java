package hexlet.code.utils;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {
    public static void addUrl(HikariDataSource dataSource, String url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, new String[]{"id"})) {
            stmt.setString(1, url);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }
    }

    public static Map<String, Object> getUrlByName(HikariDataSource dataSource, String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Map<String, Object> url = new HashMap<>();
                url.put("id", resultSet.getLong("id"));
                url.put("name", resultSet.getString("name"));
                url.put("created_at", resultSet.getTimestamp("created_at"));
                return url;
            }
            return null;
        }
    }

    public static List<Map<String, Object>> getAllUrls(HikariDataSource dataSource) throws SQLException {
        String sql = "SELECT * FROM urls";
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement();
             var resultSet = stmt.executeQuery(sql)) {
            List<Map<String, Object>> urls = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> url = new HashMap<>();
                url.put("id", resultSet.getLong("id"));
                url.put("name", resultSet.getString("name"));
                url.put("created_at", resultSet.getTimestamp("created_at"));
                urls.add(url);
            }
            return urls;
        }
    }

    public static void addUrlCheck(HikariDataSource dataSource, Long urlId) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            stmt.setInt(2, 200);
            stmt.setString(3, "Test page");
            stmt.setString(4, "Test H1");
            stmt.setString(5, "Test description");
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }
    }

    public static Map<String, Object> getUrlCheck(HikariDataSource dataSource, Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC LIMIT 1";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Map<String, Object> check = new HashMap<>();
                check.put("id", resultSet.getLong("id"));
                check.put("url_id", resultSet.getLong("url_id"));
                check.put("status_code", resultSet.getInt("status_code"));
                check.put("title", resultSet.getString("title"));
                check.put("h1", resultSet.getString("h1"));
                check.put("description", resultSet.getString("description"));
                check.put("created_at", resultSet.getTimestamp("created_at"));
                return check;
            }
            return null;
        }
    }
}
