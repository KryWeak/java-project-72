package gg.jte.generated.ondemand;
import hexlet.code.models.Url;
import java.util.List;
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,3,3,3,17,17,17,18,18,18,18,19,19,19,21,21,32,32,32,3,4,4,4,4};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, String flash, String flashType) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"en\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n    <title>Анализатор страниц</title>\r\n    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\r\n</head>\r\n<body>\r\n    <div class=\"container\">\r\n        <h1>Анализатор страниц</h1>\r\n        ");
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
		jteOutput.writeContent("\r\n        <form action=\"/urls\" method=\"post\">\r\n            <div class=\"mb-3\">\r\n                <label for=\"url\" class=\"form-label\">URL</label>\r\n                <input type=\"text\" class=\"form-control\" id=\"url\" name=\"url\" placeholder=\"https://example.com\">\r\n            </div>\r\n            <button type=\"submit\" class=\"btn btn-primary\">Проверить</button>\r\n        </form>\r\n    </div>\r\n    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js\"></script>\r\n</body>\r\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		String flash = (String)params.get("flash");
		String flashType = (String)params.get("flashType");
		render(jteOutput, jteHtmlInterceptor, flash, flashType);
	}
}
