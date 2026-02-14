package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.utils.ActualTime;
import hexlet.code.utils.NamedRoutes;
import hexlet.code.utils.TestUtils;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static hexlet.code.repository.BaseRepository.dataSource;
import static org.assertj.core.api.Assertions.assertThat;

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
        String urlString = String.format("http://localhost:%d/", mockWebServer.getPort());
        Url url = new Url(urlString);
        UrlsRepository.save(url);

        assertThat(url.getId()).isNotNull();

        mockWebServer.enqueue(new MockResponse()
                .setBody(readResourceFile("test.html"))
                .setResponseCode(200));

        OkHttpClient clientWithoutRedirect = new OkHttpClient.Builder()
                .followRedirects(false)
                .build();

        JavalinTest.test(app, (server, ignored) -> {
            Request postRequest = new Request.Builder()
                    .url("http://localhost:" + server.port() + "/urls/" + url.getId() + "/checks")
                    .post(RequestBody.create("", null))
                    .build();

            Response postResponse = clientWithoutRedirect.newCall(postRequest).execute();

            int status = postResponse.code();
            String location = postResponse.header("Location");

            System.out.println("POST status: " + status);
            System.out.println("POST Location: " + location);

            assertThat(status)
                    .as("POST /checks должен вернуть статус редиректа (3xx)")
                    .isBetween(300, 399);

            assertThat(location)
                    .as("Location должен указывать на страницу URL")
                    .isEqualTo("/urls/" + url.getId());

            Request getRequest = new Request.Builder()
                    .url("http://localhost:" + server.port() + location)
                    .get()
                    .build();

            Response pageResponse = clientWithoutRedirect.newCall(getRequest).execute();
            assertThat(pageResponse.code()).isEqualTo(200);

            String body = pageResponse.body().string();
            System.out.println("GET body length: " + body.length());

            var checks = UrlChecksRepository.getEntitiesByUrlId(url.getId());
            assertThat(checks).isNotEmpty();

            UrlCheck check = checks.getFirst();
            assertThat(check.getH1()).isNotNull();
            assertThat(check.getTitle()).isNotNull();
            assertThat(check.getDescription()).isNotNull();

            assertThat(body).contains(check.getTitle());
            assertThat(body).contains(check.getH1());
            assertThat(body).contains(check.getDescription());
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

    @Test
    void testUrlsRepositoryGetEntitiesNotEmpty() throws SQLException {
        Url url = new Url(testUrl);
        UrlsRepository.save(url);

        var urls = UrlsRepository.getEntities();
        assertThat(urls).isNotEmpty();
        assertThat(urls.get(0).getName()).isEqualTo(testUrl);
    }

    @Test
    void testAppRootContainsIndex() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("<!DOCTYPE html>");
        });
    }

    @Test
    void testActualTimeWithNulls() {
        assertThat(ActualTime.getActualTime((Timestamp) null)).isEqualTo("");
        assertThat(ActualTime.getActualTime((LocalDateTime) null)).isEqualTo("");
        assertThat(ActualTime.getActualTime((Instant) null)).isEqualTo("");
    }

    @Test
    void testGetEntitiesByUrlIdEmpty() throws SQLException {
        assertThat(UrlChecksRepository.getEntitiesByUrlId(9999L)).isEmpty();
    }

    @Test
    void testFindLatestChecksEmpty() throws SQLException {
        assertThat(UrlChecksRepository.findLatestChecks()).isEmpty();
    }
}
