package gg.jte.generated.ondemand;
import hexlet.code.dto.BasePage;
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,1,1,3,3,5,5,6,6,8,8,8,11,11,37,37,37,38,38,38,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, BasePage page) {
		jteOutput.writeContent("\n");
		gg.jte.generated.ondemand.layout.JtepageGenerated.render(jteOutput, jteHtmlInterceptor, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\n    ");
				if (page.getFlash() != null) {
					jteOutput.writeContent("\n        <div class=\"alert alert-warning\" role=\"alert\">\n            <p class=\"m-0\">");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent(page.getFlash());
					jteOutput.writeContent("</p>\n            <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Закрыть\"></button>\n        </div>\n    ");
				}
				jteOutput.writeContent("\n    <section>\n    <div class=\"container text-center\">\n        <div class=\"row\">\n            <div class=\"col\">\n                <h1>Анализатор страниц</h1>\n                <p class=\"lead\">Бесплатно проверяйте сайты на SEO пригодность</p>\n                <form action=\"/urls\" method=\"post\" class=\"rss-form text-body\">\n                    <div class=\"row\">\n                        <div class=\"col\">\n                            <div class=\"form-floating\">\n                                <input id=\"url-input\" autofocus type=\"text\" required name=\"url\" aria-label=\"url\"\n                                       class=\"form-control w-100\" placeholder=\"ссылка\" autocomplete=\"off\">\n                                <label for=\"url-input\">Ссылка</label>\n                            </div>\n                        </div>\n                        <div class=\"col-auto\">\n                            <button type=\"submit\" class=\"h-100 btn btn-lg btn-primary px-sm-5\">Проверить</button>\n                        </div>\n                    </div>\n                </form>\n                <p class=\"mt-2 mb-0 text-muted\">Пример: https://www.example.com</p>\n            </div>\n        </div>\n    </div>\n    </section>\n");
			}
		}, null);
		jteOutput.writeContent("\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		BasePage page = (BasePage)params.get("page");
		render(jteOutput, jteHtmlInterceptor, page);
	}
}
