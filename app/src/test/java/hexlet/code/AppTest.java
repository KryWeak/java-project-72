package hexlet.code;

import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {
    private static Javalin app;
    private static MockWebServer server;

    @BeforeAll
    public static void setUp() throws SQLException {
        app = App.getApp();
        server = new MockWebServer();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        server.shutdown();
        Database.close();
    }

    @BeforeEach
    public void init() throws SQLException {
        DatabaseInitializer.initialize();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("URL Analyzer"));
        });
    }

    @Test
    public void testAddUrl() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post("/urls", requestBody);
            assertEquals(302, response.code());
            assertEquals("/urls", response.header("Location"));
            List<Url> urls = UrlRepository.findAll();
            assertEquals(1, urls.size());
            assertEquals("https://example.com", urls.get(0).getName());
        });
    }

    @Test
    public void testAddDuplicateUrl() throws SQLException {
        Url url = new Url("https://example.com", new Timestamp(System.currentTimeMillis()));
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post("/urls", requestBody);
            assertEquals(302, response.code());
            assertEquals("/urls", response.header("Location"));
            List<Url> urls = UrlRepository.findAll();
            assertEquals(1, urls.size());
        });
    }

    @Test
    public void testInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=invalid-url";
            var response = client.post("/urls", requestBody);
            assertEquals(302, response.code());
            assertEquals("/", response.header("Location"));
        });
    }

    @Test
    public void testUrlList() throws SQLException {
        Url url = new Url("https://example.com", new Timestamp(System.currentTimeMillis()));
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("https://example.com"));
        });
    }

    @Test
    public void testUrlShow() throws SQLException {
        Url url = new Url("https://example.com", new Timestamp(System.currentTimeMillis()));
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("https://example.com"));
        });
    }

    @Test
    public void testUrlCheck() throws Exception {
        Url url = new Url(server.url("/").toString(), new Timestamp(System.currentTimeMillis()));
        UrlRepository.save(url);
        server.enqueue(new MockResponse()
                .setBody("<html><head><title>Test Title</title><meta name=\"description\" content=\"Test Description\"></head><body><h1>Test H1</h1></body></html>")
                .setResponseCode(200));
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/" + url.getId() + "/checks");
            assertEquals(302, response.code());
            assertEquals("/urls/" + url.getId(), response.header("Location"));
            List<UrlCheck> checks = UrlCheckRepository.findByUrlId(url.getId());
            assertEquals(1, checks.size());
            assertEquals(200, checks.get(0).getStatusCode());
            assertEquals("Test Title", checks.get(0).getTitle());
            assertEquals("Test H1", checks.get(0).getH1());
            assertEquals("Test Description", checks.get(0).getDescription());
        });
    }
}
