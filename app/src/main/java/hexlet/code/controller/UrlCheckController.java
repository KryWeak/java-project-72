package hexlet.code.controller;

import hexlet.code.Database;
import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.repositories.UrlCheckRepository;
import hexlet.code.repositories.UrlRepository;
import io.javalin.http.Handler;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Controller for handling URL check operations.
 */
public final class UrlCheckController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlCheckController.class);
    private UrlRepository urlRepository;
    private UrlCheckRepository urlCheckRepository;

    public UrlCheckController() {
        this.urlRepository = new UrlRepository(Database.getDataSource());
        this.urlCheckRepository = new UrlCheckRepository(Database.getDataSource());
    }

    public UrlCheckController(UrlRepository urlRepository, UrlCheckRepository urlCheckRepository) {
        this.urlRepository = urlRepository;
        this.urlCheckRepository = urlCheckRepository;
    }

    /**
     * Handles the creation of a URL check.
     */
    public final Handler create = ctx -> {
        long id = Long.parseLong(ctx.pathParam("id"));
        Url url = urlRepository.find(id);

        if (url == null) {
            ctx.status(404).result("URL not found");
            return;
        }

        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            int statusCode = response.getStatus();
            String body = response.getBody();

            Document doc = Jsoup.parse(body);
            String title = doc.title() != null ? doc.title() : "";
            String h1 = doc.selectFirst("h1") != null ? doc.selectFirst("h1").text() : "";
            String description = doc.selectFirst("meta[name=description]") != null
                    ? doc.selectFirst("meta[name=description]").attr("content") : "";

            UrlCheck check = new UrlCheck();
            check.setUrlId(id);
            check.setStatusCode(statusCode);
            check.setTitle(title);
            check.setH1(h1);
            check.setDescription(description);
            check.setCreatedAt(LocalDateTime.now());

            urlCheckRepository.save(check);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
        } catch (Exception e) {
            LOGGER.error("Error checking URL {}: {}", url.getName(), e.getMessage());
            ctx.sessionAttribute("flash", "Ошибка при проверке страницы: " + e.getMessage());
            ctx.sessionAttribute("flash-type", "danger");
        }

        ctx.redirect("/urls/" + id);
    };
}
