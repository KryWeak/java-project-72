package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collections;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {

    public static void root(Context ctx) {
        var page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("index.jte", model("page", page));
    }

    public static void index(Context ctx) throws SQLException {
        var page = new UrlsPage(
                UrlsRepository.getEntities(),
                UrlChecksRepository.findLatestChecks()
        );
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();

        var url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        url.setUrlChecks(UrlChecksRepository.getEntitiesByUrlId(id));

        var page = new UrlPage(url);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));

        ctx.render("urls/show.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException {

        var inputUrl = ctx.formParamAsClass("url", String.class)
                .getOrDefault("");

        URI uri;
        try {
            uri = new URI(inputUrl);
        } catch (URISyntaxException e) {
            renderBadUrl(ctx);
            return;
        }

        String normalizedUrl;
        try {
            normalizedUrl = normalizeUrl(uri);
        } catch (IllegalArgumentException e) {
            renderBadUrl(ctx);
            return;
        }

        if (UrlsRepository.isExist(normalizedUrl)) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flashType", "info");
            ctx.redirect(NamedRoutes.urlsPath());
            return;
        }

        var url = new Url(normalizedUrl);
        UrlsRepository.save(url);

        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flashType", "correct");
        ctx.redirect(NamedRoutes.urlsPath());
    }

    public static void check(Context ctx) throws SQLException {
        Long urlId = null;

        try {
            urlId = ctx.pathParamAsClass("id", Long.class).get();

            var url = UrlsRepository.find(urlId)
                    .orElseThrow(() -> new NotFoundResponse("Url not found"));

            HttpResponse<String> response = Unirest.get(url.getName()).asString();

            int statusCode = response.getStatus();
            var document = Jsoup.parse(response.getBody());

            String title = document.title();
            String h1 = document.selectFirst("h1") != null ? document.selectFirst("h1").text() : null;
            String description = document.selectFirst("meta[name=description]") != null
                    ? document.selectFirst("meta[name=description]").attr("content") : null;

            var urlCheck = new UrlCheck(statusCode, title, h1, description);
            urlCheck.setUrlId(url.getId());

            UrlChecksRepository.save(urlCheck);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("flash", "Ошибка при проверке: " + e.getMessage());
            ctx.sessionAttribute("flashType", "danger");
        } finally {
            if (urlId != null) {
                ctx.redirect(NamedRoutes.urlPath(urlId));
            }
        }
    }

    private static String normalizeUrl(URI uri) {
        var scheme = uri.getScheme();
        var host = uri.getHost();
        var port = uri.getPort();

        if (scheme == null || host == null) {
            throw new IllegalArgumentException("Invalid URL");
        }

        var normalizedPort = port == -1 ? "" : ":" + port;

        return String.format("%s://%s%s", scheme, host, normalizedPort);
    }

    private static void renderBadUrl(Context ctx) {
        var page = new BasePage();
        page.setFlash("Некорректный URL");
        page.setFlashType("error");
        ctx.status(400);
        ctx.render("index.jte", Collections.singletonMap("page", page));
    }
}
