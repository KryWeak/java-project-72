package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.controller.UrlsController;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class AppTest {
    private static MockWebServer mockWebServer;
    private static Javalin app;
    private static String testUrl;

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = AppTest.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    @BeforeEach
    public final void setApp() throws IOException, SQLException {
        app = App.getApp();
    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        mockWebServer.enqueue(new MockResponse().setBody(readResourceFile("test.html")).setResponseCode(200));
        testUrl = mockWebServer.url("/").toString();
    }

    @AfterAll
    public static void shutDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testUrlsShow() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/2");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testUrlsRepository() throws SQLException {
        Url url1 = new Url(testUrl);
        Url url2 = new Url("https://example.com");
        UrlsRepository.save(url1);
        UrlsRepository.save(url2);
        Url foundUrl1 = UrlsRepository.getByName(testUrl)
                .orElseThrow(() -> new AssertionError("URL not found"));
        assertThat(url1.getName()).isEqualTo(foundUrl1.getName());
        Url foundUrl2 = UrlsRepository.find(2L)
                .orElseThrow(() -> new AssertionError("URL not found"));
        assertThat(url2.getName()).isEqualTo(foundUrl2.getName());
        assertThat(UrlsRepository.getEntities().size()).isEqualTo(2);
    }

    @Test
    void testGetNonExistentUrlById() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/9999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testGetNonExistentUrlByName() throws SQLException {
        assertThat(UrlsRepository.getByName("non-existent")).isEmpty();
    }

    @Test
    void testCheckNonExistentUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/9999/checks");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testIndex() {
        JavalinTest.test(app, (server, client) -> {
            Assertions.assertThat(client.get("/").code()).isEqualTo(200);
        });
    }

    @Test
    void testCreateUrlInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", Map.of("url", "это точно не url"));

            assertThat(response.code()).isEqualTo(422);  // у тебя теперь 422!
            assertThat(response.body().string()).contains("Некорректный URL");
            assertThat(UrlsRepository.getEntities()).isEmpty();
        });
    }

    @Test
    void testBuildUrl() throws MalformedURLException {
        assertThat(UrlsController.normalizeUrl(new URL("https://hexlet.io")))
                .isEqualTo("https://hexlet.io");

        assertThat(UrlsController.normalizeUrl(new URL("http://localhost:5000")))
                .isEqualTo("http://localhost:5000");

        assertThat(UrlsController.normalizeUrl(new URL("https://github.com:443")))
                .isEqualTo("https://github.com");

        assertThat(UrlsController.normalizeUrl(new URL("http://example.com:80")))
                .isEqualTo("http://example.com");

        assertThat(UrlsController.normalizeUrl(new URL("HTTPS://GITHUB.COM:443")))
                .isEqualTo("https://github.com"); // проверяем lowerCase
    }
}
