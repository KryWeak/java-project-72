package hexlet.code;

import hexlet.code.models.Url;
import hexlet.code.repositories.Database;
import hexlet.code.repositories.UrlRepository;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import io.javalin.http.NotFoundResponse;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;

public final class App {
    public static Javalin getApp() throws SQLException {
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging(); // Исправлено: используем bundledPlugins
            config.fileRenderer(new JavalinJte());
        });

        app.get("/", ctx -> {
            ctx.render("index.jte", Collections.singletonMap("flash", ctx.consumeSessionAttribute("flash")));
        });

        app.post("/urls", ctx -> {
            var urlName = ctx.formParam("url");
            try {
                var url = new URL(urlName);
                var name = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort());
                var urlRecord = new Url(null, name, Timestamp.from(Instant.now()));
                var urlRepository = new UrlRepository(Database.getDataSource());
                if (urlRepository.findByName(name).isPresent()) {
                    ctx.sessionAttribute("flash", "URL already exists");
                    ctx.sessionAttribute("flashType", "danger");
                } else {
                    urlRepository.save(urlRecord);
                    ctx.sessionAttribute("flash", "URL successfully added");
                    ctx.sessionAttribute("flashType", "success");
                }
                ctx.redirect("/urls");
            } catch (Exception e) {
                ctx.sessionAttribute("flash", "Invalid URL");
                ctx.sessionAttribute("flashType", "danger");
                ctx.redirect("/");
            }
        });

        app.get("/urls", ctx -> {
            var urlRepository = new UrlRepository(Database.getDataSource());
            var urls = urlRepository.findAll();
            ctx.render("urls/index.jte", Collections.singletonMap("urls", urls));
        });

        app.get("/urls/{id}", ctx -> {
            var id = ctx.pathParamAsClass("id", Long.class).get();
            var urlRepository = new UrlRepository(Database.getDataSource());
            var url = urlRepository.findById(id)
                    .orElseThrow(() -> new NotFoundResponse("URL not found"));
            ctx.render("urls/show.jte", Collections.singletonMap("url", url));
        });

        return app;
    }

    public static void main(String[] args) throws SQLException {
        var app = getApp();
        app.start(8080);
    }
}
