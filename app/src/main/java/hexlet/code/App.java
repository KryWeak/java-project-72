package hexlet.code;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static Javalin getApp() {
        DatabaseInitializer.init();

        Javalin app = Javalin.create();

        // Устанавливаем Content-Type для всех запросов
        app.before(ctx -> ctx.contentType("text/plain"));

        app.get("/", ctx -> ctx.result("Hello World"));

        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7000"));
        app.start(port);
        LOGGER.info("App started on port " + port);
    }
}
