package hexlet.code.controller;

import hexlet.code.Database;
import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.http.Handler;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling URL operations.
 */
public final class UrlController {
    private UrlRepository urlRepository;
    private UrlCheckRepository urlCheckRepository;

    public UrlController() {
        this.urlRepository = new UrlRepository(Database.getDataSource());
        this.urlCheckRepository = new UrlCheckRepository(Database.getDataSource());
    }

    public UrlController(UrlRepository urlRepository, UrlCheckRepository urlCheckRepository) {
        this.urlRepository = urlRepository;
        this.urlCheckRepository = urlCheckRepository;
    }

    /**
     * Handles the rendering of the index page.
     */
    public final Handler index = ctx -> {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("flash", ctx.sessionAttribute("flash"));
        attributes.put("flashType", ctx.sessionAttribute("flash-type"));
        ctx.render("index.jte", attributes);
    };

    /**
     * Handles the listing of all URLs.
     */
    public final Handler list = ctx -> {
        List<Url> urls = urlRepository.findAll();
        Map<Long, List<UrlCheck>> checksByUrl = urlCheckRepository.findAllByUrls(urls);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("urls", urls);
        attributes.put("checksByUrl", checksByUrl);
        attributes.put("flash", ctx.sessionAttribute("flash"));
        attributes.put("flashType", ctx.sessionAttribute("flash-type"));
        ctx.render("urls.jte", attributes);
    };

    /**
     * Handles the creation of a new URL.
     */
    public final Handler create = ctx -> {
        String urlInput = ctx.formParam("url");
        if (urlInput == null || urlInput.isEmpty()) {
            ctx.sessionAttribute("flash", "URL не может быть пустым");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }

        String normalizedUrl;
        try {
            URL url = new URI(urlInput).toURL();
            StringBuilder normalized = new StringBuilder();
            normalized.append(url.getProtocol()).append("://").append(url.getHost());
            if (url.getPort() != -1) {
                normalized.append(":").append(url.getPort());
            }
            normalizedUrl = normalized.toString().toLowerCase();
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }

        Url existing = urlRepository.findByName(normalizedUrl);
        if (existing != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
            ctx.redirect("/urls");
            return;
        }

        Url url = new Url();
        url.setName(normalizedUrl);
        url.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url);

        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");
    };

    /**
     * Handles the display of a specific URL's details.
     */
    public final Handler show = ctx -> {
        long id = Long.parseLong(ctx.pathParam("id"));
        Url url = urlRepository.find(id);

        if (url == null) {
            ctx.status(404).result("URL not found");
            return;
        }

        List<UrlCheck> checks = urlCheckRepository.findByUrlId(id);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("url", url);
        attributes.put("checks", checks);
        attributes.put("flash", ctx.sessionAttribute("flash"));
        attributes.put("flashType", ctx.sessionAttribute("flash-type"));
        ctx.render("url.jte", attributes);
    };
}
