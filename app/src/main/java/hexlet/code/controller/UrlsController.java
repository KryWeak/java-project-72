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
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
        try {
            var name = buildUrl(new URI(ctx.formParamAsClass("url", String.class).getOrDefault("")).toURL());

            if (UrlsRepository.isExist(name)) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flashType", "info");
                ctx.redirect(NamedRoutes.urlsPath());
            } else {
                Url url = new Url(name);
                UrlsRepository.save(url);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flashType", "correct");
                ctx.redirect(NamedRoutes.urlsPath());
            }
        } catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
            var page = new BasePage();
            page.setFlash("Некорректный URL");
            page.setFlashType("error");
            ctx.status(400);
            ctx.render("index.jte", Collections.singletonMap("page", page));
        }
    }
    private static String buildUrl(URL url) {
        var protocol = url.getProtocol().isEmpty() ? "" : url.getProtocol();
        String host = url.getHost().isEmpty() ? "" : url.getHost();
        String port = url.getPort() == -1 ? "" : ":" + url.getPort();
        return String.format("%s://%s%s", protocol, host, port);
    }

    public static void check(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            var codeResponse = response.getStatus();
            var doc = Jsoup.parse(response.getBody());
            var title = doc.title();
            Element h1Element = doc.selectFirst("h1");
            var h1 = h1Element == null ? "" : h1Element.ownText();

            Element descriptionEl = doc.selectFirst("meta[name=description]");
            var description = descriptionEl == null ? "" : descriptionEl.attr("content");

            var urlCheck = new UrlCheck(codeResponse, title, h1, description);
            urlCheck.setUrlId(url.getId());
            UrlChecksRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "correct");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flashType", "error");
        }
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
