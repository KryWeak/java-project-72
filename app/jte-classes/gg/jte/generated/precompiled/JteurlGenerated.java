package gg.jte.generated.precompiled;
import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public final class JteurlGenerated {
	public static final String JTE_NAME = "url.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,3,4,6,6,6,16,16,16,16,21,21,21,22,22,23,23,23,23,24,24,24,26,26,27,27,27,28,28,28,29,29,29,29,45,45,47,47,47,48,48,48,49,49,49,50,50,50,51,51,51,52,52,52,54,54,60,60,60,6,7,8,9,9,9,9};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Url url, List<UrlCheck> checks, String flash, String flashType) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"en\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n    <title>URL: ");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(url.getName());
		jteOutput.writeContent("</title>\r\n    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\r\n</head>\r\n<body>\r\n    <div class=\"container\">\r\n        <h1>Сайт: ");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(url.getName());
		jteOutput.writeContent("</h1>\r\n        ");
		if (flash != null) {
			jteOutput.writeContent("\r\n            <div class=\"alert alert-");
			jteOutput.setContext("div", "class");
			jteOutput.writeUserContent(flashType);
			jteOutput.setContext("div", null);
			jteOutput.writeContent("\">\r\n                ");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(flash);
			jteOutput.writeContent("\r\n            </div>\r\n        ");
		}
		jteOutput.writeContent("\r\n        <p>ID: ");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(url.getId());
		jteOutput.writeContent("</p>\r\n        <p>Дата создания: ");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(url.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		jteOutput.writeContent("</p>\r\n        <form action=\"/urls/");
		jteOutput.setContext("form", "action");
		jteOutput.writeUserContent(url.getId());
		jteOutput.setContext("form", null);
		jteOutput.writeContent("/checks\" method=\"post\">\r\n            <button type=\"submit\" class=\"btn btn-primary\">Запустить проверку</button>\r\n        </form>\r\n        <h2>Проверки</h2>\r\n        <table class=\"table\">\r\n            <thead>\r\n                <tr>\r\n                    <th>ID</th>\r\n                    <th>Код ответа</th>\r\n                    <th>Заголовок</th>\r\n                    <th>H1</th>\r\n                    <th>Описание</th>\r\n                    <th>Дата проверки</th>\r\n                </tr>\r\n            </thead>\r\n            <tbody>\r\n                ");
		for (UrlCheck check : checks) {
			jteOutput.writeContent("\r\n                    <tr>\r\n                        <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getId());
			jteOutput.writeContent("</td>\r\n                        <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getStatusCode());
			jteOutput.writeContent("</td>\r\n                        <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getTitle());
			jteOutput.writeContent("</td>\r\n                        <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getH1());
			jteOutput.writeContent("</td>\r\n                        <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getDescription());
			jteOutput.writeContent("</td>\r\n                        <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(check.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			jteOutput.writeContent("</td>\r\n                    </tr>\r\n                ");
		}
		jteOutput.writeContent("\r\n            </tbody>\r\n        </table>\r\n    </div>\r\n    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js\"></script>\r\n</body>\r\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Url url = (Url)params.get("url");
		List<UrlCheck> checks = (List<UrlCheck>)params.get("checks");
		String flash = (String)params.get("flash");
		String flashType = (String)params.get("flashType");
		render(jteOutput, jteHtmlInterceptor, url, checks, flash, flashType);
	}
}
