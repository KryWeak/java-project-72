package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.utils.ActualTime;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {

    private static Javalin app;
    private MockWebServer server;
    private String testUrl;

    @BeforeAll
    static void initDataSource() throws SQLException {
        org.h2.jdbcx.JdbcDataSource ds = new org.h2.jdbcx.JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUser("sa");
        ds.setPassword("");

        com.zaxxer.hikari.HikariDataSource hikariDS = new com.zaxxer.hikari.HikariDataSource();
        hikariDS.setDataSource(ds);
        BaseRepository.dataSource = hikariDS;
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (BaseRepository.dataSource instanceof com.zaxxer.hikari.HikariDataSource ds) {
            ds.close();
        }
    }

    @BeforeEach
    void setUp() throws IOException, SQLException {
        server = new MockWebServer();
        server.start();
        testUrl = server.url("/").toString();

        app = App.getApp();
    }

    @AfterEach
    void stopServer() throws IOException {
        if (server != null) {
            server.shutdown();
        }
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (javalin, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body()).isNotNull();
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (javalin, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testUrlsShowNonExistent() {
        JavalinTest.test(app, (javalin, client) -> {
            var response = client.get("/urls/9999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testCheckNonExistentUrl() {
        JavalinTest.test(app, (javalin, client) -> {
            var response = client.post("/urls/9999/checks");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testShowUrl() throws Exception {
        Url url = new Url(testUrl);
        UrlsRepository.save(url);
        assertThat(url.getId()).isNotNull();

        String htmlBody = """
            <html>
              <head><title>Test Page Title</title></head>
              <body>
                <h1>Test H1</h1>
                <meta name="description" content="Test description">
              </body>
            </html>
            """;

        server.enqueue(new MockResponse()
                .setBody(htmlBody)
                .setResponseCode(200));

        OkHttpClient clientWithoutRedirect = new OkHttpClient.Builder()
                .followRedirects(false)
                .build();

        JavalinTest.test(app, (javalin, ignored) -> {
            Request postRequest = new Request.Builder()
                    .url("http://localhost:" + javalin.port() + "/urls/" + url.getId() + "/checks")
                    .post(RequestBody.create("", null))
                    .build();

            Response postResponse = clientWithoutRedirect.newCall(postRequest).execute();
            int status = postResponse.code();
            String location = postResponse.header("Location");

            assertThat(status).isBetween(300, 399);
            assertThat(location).isEqualTo("/urls/" + url.getId());

            Request getRequest = new Request.Builder()
                    .url("http://localhost:" + javalin.port() + location)
                    .get()
                    .build();
            Response pageResponse = clientWithoutRedirect.newCall(getRequest).execute();
            assertThat(pageResponse.code()).isEqualTo(200);

            var checks = UrlChecksRepository.getEntitiesByUrlId(url.getId());
            assertThat(checks).hasSize(1);

            UrlCheck check = checks.getFirst();
            assertThat(check.getTitle()).isEqualTo("Test Page Title");
            assertThat(check.getH1()).isEqualTo("Test H1");
            assertThat(check.getDescription()).isEqualTo("Test description");

            String body = pageResponse.body().string();
            assertThat(body).contains(check.getTitle(), check.getH1(), check.getDescription());
        });
    }

    @Test
    void testUrlsRepository() throws SQLException {
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
    void testGetNonExistentUrlCheck() throws SQLException {
        var checks = UrlChecksRepository.getEntitiesByUrlId(999L);
        assertThat(checks).isEmpty();
    }

    @Test
    void testGetEntitiesByUrlIdEmpty() throws SQLException {
        assertThat(UrlChecksRepository.getEntitiesByUrlId(9999L)).isEmpty();
    }

    @Test
    void testFindLatestChecksEmpty() throws SQLException {
        assertThat(UrlChecksRepository.findLatestChecks()).isEmpty();
    }

    @Test
    void testActualTimeWithNulls() {
        assertThat(ActualTime.getActualTime((Timestamp) null)).isEqualTo("");
        assertThat(ActualTime.getActualTime((LocalDateTime) null)).isEqualTo("");
        assertThat(ActualTime.getActualTime((Instant) null)).isEqualTo("");
    }

    @Test
    void testActualTimeWithValues() {
        var now = LocalDateTime.of(2026, 2, 14, 15, 7);
        var ts = Timestamp.valueOf(now);
        var instant = now.atZone(java.time.ZoneId.systemDefault()).toInstant();

        String formattedLocal = ActualTime.getActualTime(now);
        String formattedTimestamp = ActualTime.getActualTime(ts);
        String formattedInstant = ActualTime.getActualTime(instant);

        assertThat(formattedLocal).isEqualTo("14/02/2026 15:07");
        assertThat(formattedTimestamp).isEqualTo(formattedLocal);
        assertThat(formattedInstant).isEqualTo(formattedLocal);
    }
}
