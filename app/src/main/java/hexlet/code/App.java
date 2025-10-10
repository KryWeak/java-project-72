package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.rendering.template.JavalinJte;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static Javalin getApp() throws SQLException {
        DatabaseInitializer.initialize();
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.before(ctx -> {
            ctx.attribute("flash", ctx.sessionAttribute("flash"));
            ctx.attribute("flashType", ctx.sessionAttribute("flashType"));
            ctx.sessionAttribute("flash", null);
            ctx.sessionAttribute("flashType", null);
        });

        app.get("/", ctx -> {
            Map<String, Object> model = Map.of(
                    "flash", ctx.attribute("flash"),
                    "flashType", ctx.attribute("flashType")
            );
            ctx.render("index.jte", model);
        });

        app.post("/urls", ctx -> {
            String inputUrl = ctx.formParam("url");
            if (inputUrl == null || inputUrl.isEmpty()) {
                ctx.sessionAttribute("flash", "URL cannot be empty");
                ctx.sessionAttribute("flashType", "danger");
                ctx.redirect("/");
                return;
            }

            try {
                URL url = new URI(inputUrl).toURL();
                String normalizedUrl = String.format("%s://%s%s", url.getProtocol(), url.getHost(),
                        url.getPort() == -1 ? "" : ":" + url.getPort());
                Optional<Url> existingUrl = UrlRepository.findByName(normalizedUrl);
                if (existingUrl.isPresent()) {
                    ctx.sessionAttribute("flash", "Страница уже существует");
                    ctx.sessionAttribute("flashType", "info");
                } else {
                    Url newUrl = new Url(normalizedUrl, new Timestamp(System.currentTimeMillis()));
                    UrlRepository.save(newUrl);
                    ctx.sessionAttribute("flash", "Страница успешно добавлена");
                    ctx.sessionAttribute("flashType", "success");
                }
                ctx.redirect("/urls");
            } catch (Exception e) {
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.sessionAttribute("flashType", "danger");
                ctx.redirect("/");
            }
        });

        app.get("/urls", ctx -> {
            List<Url> urls = UrlRepository.findAll();
            Map<Long, UrlCheck> latestChecks = new HashMap<>();
            for (Url url : urls) {
                UrlCheckRepository.findLatestByUrlId(url.getId())
                        .ifPresent(check -> latestChecks.put(url.getId(), check));
            }
            Map<String, Object> model = Map.of(
                    "urls", urls,
                    "latestChecks", latestChecks,
                    "flash", ctx.attribute("flash"),
                    "flashType", ctx.attribute("flashType")
            );
            ctx.render("urls/index.jte", model);
        });

        app.get("/urls/{id}", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            Url url = UrlRepository.findById(id).orElseThrow(() -> new NotFoundResponse("URL not found"));
            List<UrlCheck> checks = UrlCheckRepository.findByUrlId(id);
            Map<String, Object> model = Map.of(
                    "url", url,
                    "checks", checks,
                    "flash", ctx.attribute("flash"),
                    "flashType", ctx.attribute("flashType")
            );
            ctx.render("urls/show.jte", model);
        });

        app.post("/urls/{id}/checks", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            Url url = UrlRepository.findById(id).orElseThrow(() -> new NotFoundResponse("URL not found"));
            try {
                HttpResponse<String> response = Unirest.get(url.getName()).asString();
                String html = response.getBody();
                var doc = Jsoup.parse(html);
                String title = doc.title();
                String h1 = doc.selectFirst("h1") != null ? doc.selectFirst("h1").text() : null;
                String description = doc.selectFirst("meta[name=description]") != null
                        ? doc.selectFirst("meta[name=description]").attr("content") : null;
                UrlCheck check = new UrlCheck(id, response.getStatus(), title, h1, description,
                        new Timestamp(System.currentTimeMillis()));
                UrlCheckRepository.save(check);
                ctx.sessionAttribute("flash", "Check completed successfully");
                ctx.sessionAttribute("flashType", "success");
            } catch (Exception e) {
                ctx.sessionAttribute("flash", "Failed to check URL: " + e.getMessage());
                ctx.sessionAttribute("flashType", "danger");
            }
            ctx.redirect("/urls/" + id);
        });

        return app;
    }

    public static void main(String[] args) throws SQLException {
        Javalin app = getApp();
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));
        app.start(port);
    }
}
