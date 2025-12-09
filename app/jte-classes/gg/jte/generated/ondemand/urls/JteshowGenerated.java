package gg.jte.generated.ondemand.urls;
import hexlet.code.dto.UrlPage;
import hexlet.code.utils.BeautyTime;
public final class JteshowGenerated {
	public static final String JTE_NAME = "urls/show.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,2,2,5,5,7,7,9,9,10,10,12,12,12,15,15,17,17,17,20,20,21,21,26,26,26,32,32,32,36,36,36,40,40,40,46,46,46,46,60,60,61,61,64,64,64,68,68,68,71,71,71,74,74,74,77,77,77,80,80,80,83,83,84,84,92,92,92,92,92,2,2,2,2};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, UrlPage page) {
		jteOutput.writeContent("\n\n");
		gg.jte.generated.ondemand.layout.JtepageGenerated.render(jteOutput, jteHtmlInterceptor, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\n    <main class=\"flex-grow-1\">\n        ");
				if (page.getFlash() != null) {
					jteOutput.writeContent("\n            ");
					if (page.getFlash().equals("Некоректный адрес")) {
						jteOutput.writeContent("\n                <div class=\"rounded-0 m-0 alert alert-dismissible fade show alert-danger\" role=\"alert\">\n                    <p class=\"m-0\">");
						jteOutput.setContext("p", null);
						jteOutput.writeUserContent(page.getFlash());
						jteOutput.writeContent("</p>\n                    <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n                </div>\n            ");
					} else {
						jteOutput.writeContent("\n                <div class=\"rounded-0 m-0 alert alert-dismissible fade show alert-success\" role=\"alert\">\n                    <p class=\"m-0\">");
						jteOutput.setContext("p", null);
						jteOutput.writeUserContent(page.getFlash());
						jteOutput.writeContent("</p>\n                    <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n                </div>\n            ");
					}
					jteOutput.writeContent("\n        ");
				}
				jteOutput.writeContent("\n\n        <section>\n\n            <div class=\"container-lg mt-5\">\n                <h1>Сайт: ");
				jteOutput.setContext("h1", null);
				jteOutput.writeUserContent(page.getUrl().getName());
				jteOutput.writeContent("</h1>\n\n                <table class=\"table table-bordered table-hover mt-3\">\n                    <tbody>\n                    <tr>\n                        <td>ID</td>\n                        <td>");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(page.getUrl().getId());
				jteOutput.writeContent("</td>\n                    </tr>\n                    <tr>\n                        <td>Имя</td>\n                        <td>");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(page.getUrl().getName());
				jteOutput.writeContent("</td>\n                    </tr>\n                    <tr>\n                        <td>Дата создания</td>\n                        <td>");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(BeautyTime.getBeautyTime(page.getUrl().getCreatedAt()));
				jteOutput.writeContent("</td>\n                    </tr>\n                    </tbody>\n                </table>\n\n                <h2 class=\"mt-5\">Проверки</h2>\n                <form method=\"post\" action=\"/urls/");
				jteOutput.setContext("form", "action");
				jteOutput.writeUserContent(page.getUrl().getId());
				jteOutput.setContext("form", null);
				jteOutput.writeContent("/checks\">\n                    <button type=\"submit\" class=\"btn btn-primary\">Запустить проверку</button>\n                </form>\n\n                <table class=\"table table-bordered table-hover mt-3\">\n                    <thead>\n                    <th class=\"col-1\">ID</th>\n                    <th class=\"col-1\">Код ответа</th>\n                    <th>title</th>\n                    <th>h1</th>\n                    <th>description</th>\n                    <th class=\"col-2\">Дата проверки</th>\n                    </thead>\n                    <tbody>\n                    ");
				if (!page.getUrl().getUrlChecks().isEmpty()) {
					jteOutput.writeContent("\n                        ");
					for (var urlCheck: page.getUrl().getUrlChecks()) {
						jteOutput.writeContent("\n                            <tr>\n                                <td>\n                                    ");
						jteOutput.setContext("td", null);
						jteOutput.writeUserContent(urlCheck.getId());
						jteOutput.writeContent("\n\n                                </td>\n                                <td>\n                                    ");
						jteOutput.setContext("td", null);
						jteOutput.writeUserContent(urlCheck.getStatusCode());
						jteOutput.writeContent("\n                                </td>\n                                <td>\n                                    ");
						jteOutput.setContext("td", null);
						jteOutput.writeUserContent(urlCheck.getTitle());
						jteOutput.writeContent("\n                                </td>\n                                <td>\n                                    ");
						jteOutput.setContext("td", null);
						jteOutput.writeUserContent(urlCheck.getH1());
						jteOutput.writeContent("\n                                </td>\n                                <td>\n                                    ");
						jteOutput.setContext("td", null);
						jteOutput.writeUserContent(urlCheck.getDescription());
						jteOutput.writeContent("\n                                </td>\n                                <td>\n                                    ");
						jteOutput.setContext("td", null);
						jteOutput.writeUserContent(BeautyTime.getBeautyTime(urlCheck.getCreatedAt()));
						jteOutput.writeContent("\n                                </td>\n                            </tr>\n                        ");
					}
					jteOutput.writeContent("\n                    ");
				}
				jteOutput.writeContent("\n                    </tbody>\n                </table>\n            </div>\n\n        </section>\n    </main>\n\n");
			}
		}, null);
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		UrlPage page = (UrlPage)params.get("page");
		render(jteOutput, jteHtmlInterceptor, page);
	}
}
