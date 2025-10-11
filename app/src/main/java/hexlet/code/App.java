package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.UrlController;
import hexlet.code.controller.UrlCheckController;
import hexlet.code.repositories.UrlRepository;
import hexlet.code.repositories.UrlCheckRepository;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Main application class for setting up and running the Javalin server.
 */
public final class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static Javalin appInstance = null;

    /**
     * Creates and configures the Javalin application.
     *
     * @return the configured Javalin instance
     */
    public static Javalin getApp() {
        if (appInstance == null) {
            // Инициализация DataSource
            DataSource dataSource = Database.getDataSource();

            // Инициализация репозиториев
            UrlRepository urlRepository = new UrlRepository(dataSource);
            UrlCheckRepository urlCheckRepository = new UrlCheckRepository(dataSource);

            // Инициализация контроллеров
            UrlController urlController = new UrlController(urlRepository, urlCheckRepository);
            UrlCheckController urlCheckController = new UrlCheckController(urlRepository, urlCheckRepository);

            // Создание движка шаблонов Jte
            TemplateEngine templateEngine = createTemplateEngine();

            // Настройка Javalin
            appInstance = Javalin.create(config -> {
                config.fileRenderer(new JavalinJte(templateEngine));
                config.bundledPlugins.enableDevLogging();
            });

            // Регистрация маршрутов
            appInstance.get("/", urlController.index);
            appInstance.get("/urls", urlController.list);
            appInstance.post("/urls", urlController.create);
            appInstance.get("/urls/{id}", urlController.show);
            appInstance.post("/urls/{id}/checks", urlCheckController.create);
        }
        return appInstance;
    }

    /**
     * Main entry point to start the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }

    /**
     * Retrieves the port from the environment variable or defaults to 7070.
     *
     * @return the port number
     */
    private static int getPort() {
        String port = System.getenv("PORT");
        if (port == null || port.isBlank()) {
            LOGGER.info("PORT not set, defaulting to 7070");
            return 7070;
        }
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid PORT value: {}, defaulting to 7070", port);
            return 7070;
        }
    }

    /**
     * Creates a Jte template engine.
     *
     * @return the configured TemplateEngine
     */
    private static TemplateEngine createTemplateEngine() {
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", App.class.getClassLoader());
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}
