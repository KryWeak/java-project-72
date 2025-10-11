package hexlet.code;

import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.repositories.UrlRepository;
import hexlet.code.repositories.UrlCheckRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the main application functionality.
 */
public final class AppTest {
    private static Javalin app;
    private static MockWebServer mockServer;
    private static DataSource dataSource;
    private static UrlRepository urlRepository;
    private static UrlCheckRepository urlCheckRepository;

    @BeforeAll
    public static void setUp() throws SQLException, IOException {
        dataSource = Database.getDataSource();
        app = App.getApp();
        mockServer = new MockWebServer();
        mockServer.start();
        urlRepository = new UrlRepository(dataSource);
        urlCheckRepository = new UrlCheckRepository(dataSource);
    }

    @AfterAll
    public static void tearDown() throws IOException {
        app.stop();
        mockServer.shutdown();
    }

    /**
     * Clears the database and resets the Javalin app before each test.
     *
     * @throws SQLException if a database error occurs
     */
    @BeforeEach
    public void clearDatabase() throws SQLException {
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM url_checks; DELETE FROM urls;");
        }
        // Reset Javalin app to prevent KeyAlreadyExistsException
        app.stop();
        app = App.getApp();
    }

    @Test
    public void testIndex() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testCreateUrl() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://example.com");
            assertThat(response.code()).isEqualTo(302); // Expect redirect
            assertThat(response.header("Location")).isEqualTo("/urls");

            // Follow redirect to check flash message
            var redirectedResponse = client.get("/urls");
            assertThat(redirectedResponse.code()).isEqualTo(200);
            assertThat(redirectedResponse.body().string()).contains("Страница успешно добавлена");

            Url url = urlRepository.findByName("https://example.com");
            assertThat(url).isNotNull();
            assertThat(url.getName()).isEqualTo("https://example.com");
        });
    }

    @Test
    public void testCreateUrlInvalid() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=invalid-url");
            assertThat(response.code()).isEqualTo(302); // Expect redirect
            assertThat(response.header("Location")).isEqualTo("/");

            // Follow redirect to check flash message
            var redirectedResponse = client.get("/");
            assertThat(redirectedResponse.code()).isEqualTo(200);
            assertThat(redirectedResponse.body().string()).contains("Некорректный URL");
        });
    }

    @Test
    public void testCreateUrlDuplicate() throws SQLException {
        Url url = new Url();
        url.setName("https://example.com");
        url.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://example.com");
            assertThat(response.code()).isEqualTo(302); // Expect redirect
            assertThat(response.header("Location")).isEqualTo("/urls");

            // Follow redirect to check flash message
            var redirectedResponse = client.get("/urls");
            assertThat(redirectedResponse.code()).isEqualTo(200);
            assertThat(redirectedResponse.body().string()).contains("Страница уже существует");
        });
    }

    @Test
    public void testListUrls() throws SQLException {
        Url url1 = new Url();
        url1.setName("https://example.com");
        url1.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url1);

        Url url2 = new Url();
        url2.setName("https://test.com");
        url2.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url2);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://example.com");
            assertThat(response.body().string()).contains("https://test.com");
        });
    }

    @Test
    public void testShowUrl() throws SQLException {
        Url url = new Url();
        url.setName("https://example.com");
        url.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://example.com");
            assertThat(response.body().string()).contains("Сайт: https://example.com");
        });
    }

    @Test
    public void testShowUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999");
            assertThat(response.code()).isEqualTo(404);
            assertThat(response.body().string()).contains("URL not found");
        });
    }

    @Test
    public void testCreateUrlCheck() throws SQLException {
        String mockHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Test Title</title>
                    <meta name="description" content="Test Description">
                </head>
                <body>
                    <h1>Test H1</h1>
                </body>
                </html>
                """;
        mockServer.enqueue(new MockResponse().setBody(mockHtml).setResponseCode(200));
        String mockUrl = mockServer.url("/").toString();

        Url url = new Url();
        url.setName(mockUrl);
        url.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks");
            assertThat(response.code()).isEqualTo(302); // Expect redirect
            assertThat(response.header("Location")).isEqualTo("/urls/" + url.getId());

            // Follow redirect to check flash message
            var redirectedResponse = client.get("/urls/" + url.getId());
            assertThat(redirectedResponse.code()).isEqualTo(200);
            assertThat(redirectedResponse.body().string()).contains("Страница успешно проверена");

            List<UrlCheck> checks = urlCheckRepository.findByUrlId(url.getId());
            assertThat(checks).hasSize(1);
            UrlCheck check = checks.get(0);
            assertThat(check.getStatusCode()).isEqualTo(200);
            assertThat(check.getTitle()).isEqualTo("Test Title");
            assertThat(check.getH1()).isEqualTo("Test H1");
            assertThat(check.getDescription()).isEqualTo("Test Description");
        });
    }

    @Test
    public void testCreateUrlCheckFailure() throws SQLException {
        mockServer.enqueue(new MockResponse().setResponseCode(500));

        Url url = new Url();
        url.setName(mockServer.url("/").toString());
        url.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks");
            assertThat(response.code()).isEqualTo(302); // Expect redirect
            assertThat(response.header("Location")).isEqualTo("/urls/" + url.getId());

            // Follow redirect to check flash message
            var redirectedResponse = client.get("/urls/" + url.getId());
            assertThat(redirectedResponse.code()).isEqualTo(200);
            assertThat(redirectedResponse.body().string()).contains("Ошибка при проверке страницы");
        });
    }
}
