package gg.jte.generated.precompiled.urls;
import hexlet.code.models.Url;
import java.time.format.DateTimeFormatter;
public final class JteshowGenerated {
	public static final String JTE_NAME = "urls/show.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,3,3,3,20,20,20,20,24,24,24,28,28,28,33,33,33,3,3,3,3};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Url url) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"en\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <title>URL Details</title>\r\n    <style>\r\n        table { border-collapse: collapse; width: 100%; }\r\n        th, td { border: 1px solid black; padding: 8px; text-align: left; }\r\n    </style>\r\n</head>\r\n<body>\r\n    <h1>URL Details</h1>\r\n    <table>\r\n        <tr>\r\n            <th>ID</th>\r\n            <td>");
		jteOutput.setContext("td", null);
		jteOutput.writeUserContent(url.getId());
		jteOutput.writeContent("</td>\r\n        </tr>\r\n        <tr>\r\n            <th>Name</th>\r\n            <td>");
		jteOutput.setContext("td", null);
		jteOutput.writeUserContent(url.getName());
		jteOutput.writeContent("</td>\r\n        </tr>\r\n        <tr>\r\n            <th>Created At</th>\r\n            <td>");
		jteOutput.setContext("td", null);
		jteOutput.writeUserContent(url.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		jteOutput.writeContent("</td>\r\n        </tr>\r\n    </table>\r\n    <a href=\"/urls\">Back to URL List</a>\r\n</body>\r\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Url url = (Url)params.get("url");
		render(jteOutput, jteHtmlInterceptor, url);
	}
}
