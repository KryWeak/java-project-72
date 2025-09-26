package gg.jte.generated.precompiled.urls;
import java.util.List;
import hexlet.code.models.Url;
import java.time.format.DateTimeFormatter;
public final class JteindexGenerated {
	public static final String JTE_NAME = "urls/index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,4,4,4,21,21,21,22,22,22,22,23,23,23,25,25,37,37,39,39,39,40,40,40,40,40,40,40,41,41,41,43,43,47,47,47,4,5,6,6,6,6};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, List<Url> urls, String flash, String flashType) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"en\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <title>URL List</title>\r\n    <style>\r\n        .alert-success { color: green; }\r\n        .alert-danger { color: red; }\r\n        table { border-collapse: collapse; width: 100%; }\r\n        th, td { border: 1px solid black; padding: 8px; text-align: left; }\r\n    </style>\r\n</head>\r\n<body>\r\n    ");
		if (flash != null) {
			jteOutput.writeContent("\r\n        <div class=\"alert alert-");
			jteOutput.setContext("div", "class");
			jteOutput.writeUserContent(flashType);
			jteOutput.setContext("div", null);
			jteOutput.writeContent("\">\r\n            ");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(flash);
			jteOutput.writeContent("\r\n        </div>\r\n    ");
		}
		jteOutput.writeContent("\r\n\r\n    <h1>URLs</h1>\r\n    <table>\r\n        <thead>\r\n            <tr>\r\n                <th>ID</th>\r\n                <th>Name</th>\r\n                <th>Created At</th>\r\n            </tr>\r\n        </thead>\r\n        <tbody>\r\n            ");
		for (Url url : urls) {
			jteOutput.writeContent("\r\n                <tr>\r\n                    <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(url.getId());
			jteOutput.writeContent("</td>\r\n                    <td><a href=\"/urls/");
			jteOutput.setContext("a", "href");
			jteOutput.writeUserContent(url.getId());
			jteOutput.setContext("a", null);
			jteOutput.writeContent("\">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(url.getName());
			jteOutput.writeContent("</a></td>\r\n                    <td>");
			jteOutput.setContext("td", null);
			jteOutput.writeUserContent(url.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			jteOutput.writeContent("</td>\r\n                </tr>\r\n            ");
		}
		jteOutput.writeContent("\r\n        </tbody>\r\n    </table>\r\n</body>\r\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		List<Url> urls = (List<Url>)params.get("urls");
		String flash = (String)params.get("flash");
		String flashType = (String)params.get("flashType");
		render(jteOutput, jteHtmlInterceptor, urls, flash, flashType);
	}
}
