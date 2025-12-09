package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

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
            assert response.body() != null;
            //assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }
    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            //assertThat(response.body().string()).contains("ID", "Имя", "Последняя проверка", "Код ответа");
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
    void testShowUrl() {
        JavalinTest.test(app, (server, client) -> {
            var newUrl = new Url("https://ru.hexlet.io/projects/72/members/39734?step=6");
            UrlsRepository.save(newUrl);
            var response = client.get("/urls/" + newUrl.getId());
            var response2 = client.post("/urls/" + newUrl.getId() + "/checks");
            assert response.body() != null;
            assertThat(response.body().string()).contains("https://ru.hexlet.io");
            assertThat(response2.code()).isEqualTo(200);
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

}
