package gg.jte.generated.precompiled;
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,14,14,14,15,15,15,15,16,16,16,18,18,27,27,27,0,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, String flash, String flashType) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"en\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <title>URL Shortener</title>\r\n    <style>\r\n        .alert-success { color: green; }\r\n        .alert-danger { color: red; }\r\n    </style>\r\n</head>\r\n<body>\r\n    ");
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
		jteOutput.writeContent("\r\n\r\n    <h1>Add URL</h1>\r\n    <form action=\"/urls\" method=\"post\">\r\n        <label for=\"url\">URL:</label>\r\n        <input type=\"text\" id=\"url\" name=\"url\" placeholder=\"https://example.com\" required>\r\n        <button type=\"submit\">Submit</button>\r\n    </form>\r\n</body>\r\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		String flash = (String)params.get("flash");
		String flashType = (String)params.get("flashType");
		render(jteOutput, jteHtmlInterceptor, flash, flashType);
	}
}
