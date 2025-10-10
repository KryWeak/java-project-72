package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.utils.TestUtils;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import io.javalin.Javalin;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

class AppTest {

    private static MockWebServer mockServer;
    private HikariDataSource dataSource;

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws IOException {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    private static String getDatabaseUrl() {
        return "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        MockResponse mockedResponse = new MockResponse()
                .setBody(readFixture("index.html"));
        mockServer.enqueue(mockedResponse);
        mockServer.start();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    /**
     * Initializes the test environment by setting up the database.
     * @throws IOException if fixture loading fails
     * @throws SQLException if database initialization fails
     */
    @BeforeEach
    public void init() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());
        dataSource = new HikariDataSource(hikariConfig);

        var schema = AppTest.class.getClassLoader().getResource("schema.sql");
        var file = new File(schema.getFile());
        var sql = Files.lines(file.toPath())
                .collect(Collectors.joining("\n"));

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        String url = "https://en.hexlet.io";
        TestUtils.addUrl(dataSource, url);
        Map<String, Object> existingUrl = TestUtils.getUrlByName(dataSource, url);
        TestUtils.addUrlCheck(dataSource, (long) existingUrl.get("id"));
    }

    @AfterEach
    public void tearDown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Nested
    class RootTest {
        @Test
        void testIndex() throws SQLException {
            Javalin app = App.getApp();
            JavalinTest.test(app, (server, client) -> {
                assertThat(client.get("/").code()).isEqualTo(200);
            });
        }
    }

    @Nested
    class UrlTest {
        @Test
        void testIndex() throws SQLException {
            Javalin app = App.getApp();
            Map<String, Object> existingUrl = TestUtils.getUrlByName(dataSource, "https://en.hexlet.io");
            Map<String, Object> existingUrlCheck = TestUtils.getUrlCheck(dataSource, (long) existingUrl.get("id"));
            JavalinTest.test(app, (server, client) -> {
                var response = client.get("/urls");
                assertThat(response.code()).isEqualTo(200);
                assertThat(response.body().string())
                        .contains(existingUrl.get("name").toString())
                        .contains(existingUrlCheck.get("status_code").toString());
            });
        }

        @Test
        void testShow() throws SQLException {
            Javalin app = App.getApp();
            Map<String, Object> existingUrl = TestUtils.getUrlByName(dataSource, "https://en.hexlet.io");
            Map<String, Object> existingUrlCheck = TestUtils.getUrlCheck(dataSource, (long) existingUrl.get("id"));
            JavalinTest.test(app, (server, client) -> {
                var response = client.get("/urls/" + existingUrl.get("id"));
                assertThat(response.code()).isEqualTo(200);
                assertThat(response.body().string())
                        .contains(existingUrl.get("name").toString())
                        .contains(existingUrlCheck.get("status_code").toString());
            });
        }

        @Test
        void testStore() throws SQLException {
            Javalin app = App.getApp();
            String inputUrl = "https://ru.hexlet.io";
            JavalinTest.test(app, (server, client) -> {
                var requestBody = "url=" + inputUrl;
                assertThat(client.post("/urls", requestBody).code()).isEqualTo(302);
                var response = client.get("/urls");
                assertThat(response.code()).isEqualTo(200);
                assertThat(response.body().string()).contains(inputUrl);
                var actualUrl = TestUtils.getUrlByName(dataSource, inputUrl);
                assertThat(actualUrl).isNotNull();
                assertThat(actualUrl.get("name").toString()).isEqualTo(inputUrl);
            });
        }

        @Test
        void testAddDuplicateUrl() throws SQLException {
            Javalin app = App.getApp();
            String inputUrl = "https://en.hexlet.io";
            JavalinTest.test(app, (server, client) -> {
                var requestBody = "url=" + inputUrl;
                assertThat(client.post("/urls", requestBody).code()).isEqualTo(302);
                var response = client.get("/urls");
                assertThat(response.code()).isEqualTo(200);
                var urls = TestUtils.getAllUrls(dataSource);
                assertThat(urls.stream().filter(u -> u.get("name").equals(inputUrl)).count()).isEqualTo(1);
            });
        }

        @Test
        void testInvalidUrl() throws SQLException {
            Javalin app = App.getApp();
            String inputUrl = "invalid-url";
            JavalinTest.test(app, (server, client) -> {
                var requestBody = "url=" + inputUrl;
                assertThat(client.post("/urls", requestBody).code()).isEqualTo(302);
                var response = client.get("/");
                assertThat(response.code()).isEqualTo(200);
                var urls = TestUtils.getAllUrls(dataSource);
                assertThat(urls.stream().noneMatch(u -> u.get("name").equals(inputUrl))).isTrue();
            });
        }
    }

    @Nested
    class UrlCheckTest {
        @Test
        void testStore() throws SQLException {
            Javalin app = App.getApp();
            String url = mockServer.url("/").toString().replaceAll("/$", "");
            JavalinTest.test(app, (server, client) -> {
                var requestBody = "url=" + url;
                assertThat(client.post("/urls", requestBody).code()).isEqualTo(302);
                var actualUrl = TestUtils.getUrlByName(dataSource, url);
                assertThat(actualUrl).isNotNull();
                assertThat(actualUrl.get("name").toString()).isEqualTo(url);
                assertThat(client.post("/urls/" + actualUrl.get("id") + "/checks").code()).isEqualTo(302);
                assertThat(client.get("/urls/" + actualUrl.get("id")).code()).isEqualTo(200);
                var actualCheck = TestUtils.getUrlCheck(dataSource, (long) actualUrl.get("id"));
                assertThat(actualCheck).isNotNull();
                assertThat(actualCheck.get("title")).isEqualTo("Test page");
                assertThat(actualCheck.get("h1")).isEqualTo("Do not expect a miracle, miracles yourself!");
                assertThat(actualCheck.get("description")).isEqualTo("statements of great people");
            });
        }
    }
}
