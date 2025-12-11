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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {

    public static void root(Context ctx) {
        var page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("index.jte", model("page", page));
    }

    public static void index(Context ctx) throws SQLException {
        var page = new UrlsPage(UrlsRepository.getEntities(),
                UrlChecksRepository.findLatestChecks());
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
        String inputUrl = ctx.formParam("url") == null ? "" : ctx.formParam("url").trim();

        try {
            URL url = new URL(inputUrl);
            String normalizedUrl = normalizeUrl(url);

            if (UrlsRepository.isExist(normalizedUrl)) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flashType", "info");
            } else {
                var newUrl = new Url(normalizedUrl);
                UrlsRepository.save(newUrl);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flashType", "correct");
            }
            ctx.redirect(NamedRoutes.urlsPath());

        } catch (MalformedURLException e) {
            var page = new BasePage();
            page.setFlash("Некорректный URL");
            page.setFlashType("error");
            ctx.status(422);
            ctx.render("index.jte", model("page", page));
        }
    }

    public static String normalizeUrl(URL url) {
        String protocol = url.getProtocol().toLowerCase();
        String host = url.getHost().toLowerCase();
        int port = url.getPort();

        if (port == -1) {
            if ("http".equals(protocol)) {
                port = 80;
            }
            if ("https".equals(protocol)) {
                port = 443;
            }
        }

        String portPart = (port == 80 && "http".equals(protocol)) || (port == 443 && "https".equals(protocol))
                ? ""
                : ":" + port;

        return protocol + "://" + host + portPart;
    }

    public static void check(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            int statusCode = response.getStatus();
            var doc = Jsoup.parse(response.getBody());

            String title = doc.title();
            String h1 = doc.selectFirst("h1") == null ? "" : doc.selectFirst("h1").ownText();
            String description = doc.selectFirst("meta[name=description]") == null
                    ? "" : doc.selectFirst("meta[name=description]").attr("content");

            var check = new UrlCheck(statusCode, title, h1, description);
            check.setUrlId(urlId);
            UrlChecksRepository.save(check);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "correct");

        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flashType", "error");
        }

        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
