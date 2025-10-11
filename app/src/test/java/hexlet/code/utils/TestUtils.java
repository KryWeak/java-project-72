package hexlet.code.utils;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestUtils {
    public static void addUrl(HikariDataSource dataSource, String url) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO urls (name) VALUES (?)")) {
            stmt.setString(1, url);
            stmt.executeUpdate();
        }
    }

    public static Map<String, Object> getUrlByName(HikariDataSource dataSource, String url) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM urls WHERE name = ?")) {
            stmt.setString(1, url);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getLong("id"));
                    map.put("name", rs.getString("name"));
                    map.put("created_at", rs.getTimestamp("created_at"));
                    return map;
                }
                return null;
            }
        }
    }

    public static void addUrlCheck(HikariDataSource dataSource, long urlId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO url_checks (url_id, status_code, title, h1, description) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setLong(1, urlId);
            stmt.setInt(2, 200);
            stmt.setString(3, "Test page");
            stmt.setString(4, "Do not expect a miracle, miracles yourself!");
            stmt.setString(5, "statements of great people");
            stmt.executeUpdate();
        }
    }

    public static Map<String, Object> getUrlCheck(HikariDataSource dataSource, long urlId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM url_checks WHERE url_id = ? ORDER BY id DESC")) {
            stmt.setLong(1, urlId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getLong("id"));
                    map.put("url_id", rs.getLong("url_id"));
                    map.put("status_code", rs.getInt("status_code"));
                    map.put("title", rs.getString("title"));
                    map.put("h1", rs.getString("h1"));
                    map.put("description", rs.getString("description"));
                    map.put("created_at", rs.getTimestamp("created_at"));
                    return map;
                }
                return null;
            }
        }
    }
}
