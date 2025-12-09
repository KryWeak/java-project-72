package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.UrlsController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
public class App {
    public static Javalin getApp() throws SQLException, IOException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());

        var dataSource = new HikariDataSource(hikariConfig);
        var sql = readResourceFile("schema.sql");
        log.info(sql);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });
        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });

        app.get(NamedRoutes.rootPath(), UrlsController::root);
        app.get(NamedRoutes.urlsPath(), UrlsController::index);
        app.post(NamedRoutes.urlsPath(), UrlsController::create);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
        app.post(NamedRoutes.urlCheckPath("{id}"), UrlsController::check);

        return app;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }
    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL",
                "jdbc:h2:mem:project:DB_CLOSE_DELAY=-1;");
    }
    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.start(getPort());
    }
}

