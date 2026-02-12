package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.utils.NamedRoutes;
import hexlet.code.utils.TestUtils;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static hexlet.code.repository.BaseRepository.dataSource;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
            assertThat(response.body()).isNotNull();
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
    void testShowUrl() throws Exception {
        app = App.getApp();

        Url url = new Url("http://localhost:54391/");
        url.setCreatedAt(LocalDateTime.of(2026, 2, 12, 3, 28));
        UrlsRepository.save(url);

        UrlCheck urlCheck = new UrlCheck(
                200,
                "Test Title",
                "Test H1",
                "Test Description"
        );
        urlCheck.setUrlId(url.getId());
        urlCheck.setCreatedAt(Timestamp.valueOf(LocalDateTime.of(2026, 2, 12, 3, 28)));
        UrlChecksRepository.save(urlCheck);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            String body = response.body().string();

            assertThat(body).contains("Test Title");
            assertThat(body).contains("Test H1");
            assertThat(body).contains("Test Description");
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

        Url foundUrl2 = UrlsRepository.find(url2.getId())
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
    void testGetNonExistentUrlCheck() throws SQLException {
        Map<String, Object> check = TestUtils.getUrlCheck(dataSource, 999L);
        assertThat(check).isNull();
    }

}
