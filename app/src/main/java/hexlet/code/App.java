package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import hexlet.code.models.Url;
import hexlet.code.repositories.Database;
import hexlet.code.repositories.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.rendering.template.JavalinJte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static TemplateEngine createTemplateEngine() {
        // Используем precompiled шаблоны для runtime в JRE
        return TemplateEngine.createPrecompiled(ContentType.Html);
    }

    public static Javalin getApp() {
        DatabaseInitializer.init();

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        UrlRepository urlRepository = new UrlRepository(Database.getDataSource());

        app.get("/", ctx -> {
            Map<String, Object> model = new HashMap<>();
            model.put("flash", ctx.sessionAttribute("flash"));
            model.put("flashType", ctx.sessionAttribute("flash-type"));
            ctx.sessionAttribute("flash", null);
            ctx.sessionAttribute("flash-type", null);
            ctx.render("index.jte", model);
        });

        app.post("/urls", ctx -> {
            String inputUrl = ctx.formParam("url");
            if (inputUrl == null || inputUrl.isBlank()) {
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.redirect("/");
                return;
            }

            String normalizedUrl;
            try {
                URI uri = new URI(inputUrl);
                URL url = uri.toURL();
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(url.getProtocol()).append("://").append(url.getHost());
                if (url.getPort() != -1) {
                    urlBuilder.append(":").append(url.getPort());
                }
                normalizedUrl = urlBuilder.toString();
            } catch (Exception e) {
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.redirect("/");
                return;
            }

            if (urlRepository.findByName(normalizedUrl).isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.redirect("/");
                return;
            }

            // Используем конструктор с параметрами вместо конструктора по умолчанию
            Url newUrl = new Url(null, normalizedUrl, Timestamp.from(Instant.now()));
            urlRepository.save(newUrl);

            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect("/urls");
        });

        app.get("/urls", ctx -> {
            List<Url> urls = urlRepository.findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("urls", urls);
            model.put("flash", ctx.sessionAttribute("flash"));
            model.put("flashType", ctx.sessionAttribute("flash-type"));
            ctx.sessionAttribute("flash", null);
            ctx.sessionAttribute("flash-type", null);
            ctx.render("urls/index.jte", model);
        });

        app.get("/urls/{id}", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            Url url = urlRepository.findById(id)
                    .orElseThrow(() -> new NotFoundResponse("URL not found"));
            ctx.render("urls/show.jte", Collections.singletonMap("url", url));
        });

        return app;
    }

    public static void main(String[] args) {
        try {
            Javalin app = getApp();
            int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
            app.start(port);
            LOGGER.info("App started on port " + port);
        } catch (Exception e) {
            LOGGER.error("Failed to start application", e);
        }
    }
}
