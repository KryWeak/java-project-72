package gg.jte.generated.precompiled;
import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter;
public final class JteurlsGenerated {
	public static final String JTE_NAME = "urls.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,3,4,6,6,6,22,22,22,23,23,23,23,24,24,24,26,26,37,37,39,39,39,40,40,40,40,40,40,40,42,42,43,43,43,44,44,46,46,49,49,50,50,50,51,51,53,53,56,56,62,62,62,6,7,8,9,9,9,9};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, List<Url> urls, Map<Long, List<UrlCheck>> checksByUrl, String flash, String flashType) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"en\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n    <title>Список URL</title>\r\n    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\r\n</head>\r\n<body>\r\n    <div class=\"container\">\r\n        <h1>Сайты</h1>\r\n        ");
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
		jteOutput.writeContent("\r\n        <table class=\"table\">\r\n            <thead>\r\n                <tr>\r\n                    <th>ID</th>\r\n                    <th>Имя</th>\r\n                    <th>Дата последней проверки</th>\r\n                    <th>Код ответа</th>\r\n                </tr>\r\n            </thead>\r\n            <tbody>\r\n                ");
		for (Url url : urls) {
			jteOutput.writeContent("\r\n                    <tr>\r\n                        <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(url.getId());
			jteOutput.writeContent("</td>\r\n                        <td><a href=\"/urls/");
			jteOutput.setContext("a", "href");
			jteOutput.writeUserContent(url.getId());
			jteOutput.setContext("a", null);
			jteOutput.writeContent("\">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(url.getName());
			jteOutput.writeContent("</a></td>\r\n                        <td>\r\n                            ");
			if (checksByUrl.get(url.getId()) != null && !checksByUrl.get(url.getId()).isEmpty()) {
				jteOutput.writeContent("\r\n                                ");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(checksByUrl.get(url.getId()).get(0).getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
				jteOutput.writeContent("\r\n                            ");
			} else {
				jteOutput.writeContent("\r\n                                -\r\n                            ");
			}
			jteOutput.writeContent("\r\n                        </td>\r\n                        <td>\r\n                            ");
			if (checksByUrl.get(url.getId()) != null && !checksByUrl.get(url.getId()).isEmpty()) {
				jteOutput.writeContent("\r\n                                ");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(checksByUrl.get(url.getId()).get(0).getStatusCode());
				jteOutput.writeContent("\r\n                            ");
			} else {
				jteOutput.writeContent("\r\n                                -\r\n                            ");
			}
			jteOutput.writeContent("\r\n                        </td>\r\n                    </tr>\r\n                ");
		}
		jteOutput.writeContent("\r\n            </tbody>\r\n        </table>\r\n    </div>\r\n    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js\"></script>\r\n</body>\r\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		List<Url> urls = (List<Url>)params.get("urls");
		Map<Long, List<UrlCheck>> checksByUrl = (Map<Long, List<UrlCheck>>)params.get("checksByUrl");
		String flash = (String)params.get("flash");
		String flashType = (String)params.get("flashType");
		render(jteOutput, jteHtmlInterceptor, urls, checksByUrl, flash, flashType);
	}
}
