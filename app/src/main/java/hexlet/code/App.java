package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static Javalin getApp() {
        DatabaseInitializer.init();
        UrlRepository urlRepository = new UrlRepository(Database.getDataSource());

        Javalin app = Javalin.create(config -> {
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        // Главная страница с формой
        app.get("/", ctx -> {
            Map<String, Object> model = new HashMap<>();
            model.put("flash", ctx.sessionAttribute("flash"));
            model.put("flashType", ctx.sessionAttribute("flash-type"));
            ctx.sessionAttribute("flash", null); // Очищаем flash после использования
            ctx.sessionAttribute("flash-type", null);
            ctx.render("index.jte", model);
        });

        // Обработка добавления URL
        app.post("/urls", ctx -> {
            String inputUrl = ctx.formParam("url");
            if (inputUrl == null || inputUrl.isBlank()) {
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.redirect("/");
                return;
            }

            // Парсим URL
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

            // Проверяем уникальность
            if (urlRepository.findByName(normalizedUrl).isPresent()) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.redirect("/");
                return;
            }

            // Сохраняем URL
            Url newUrl = new Url();
            newUrl.setName(normalizedUrl);
            urlRepository.save(newUrl);

            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect("/urls");
        });

        // Список всех URL
        app.get("/urls", ctx -> {
            List<Url> urls = urlRepository.findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("urls", urls);
            model.put("flash", ctx.sessionAttribute("flash"));
            model.put("flashType", ctx.sessionAttribute("flash-type"));
            ctx.sessionAttribute("flash", null); // Очищаем flash
            ctx.sessionAttribute("flash-type", null);
            ctx.render("urls/index.jte", model);
        });

        // Страница конкретного URL
        app.get("/urls/{id}", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            Url url = urlRepository.findById(id)
                    .orElseThrow(() -> new NotFoundResponse("URL not found"));
            ctx.render("urls/show.jte", java.util.Collections.singletonMap("url", url));
        });

        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        app.start(port);
        LOGGER.info("App started on port " + port);
    }
}
