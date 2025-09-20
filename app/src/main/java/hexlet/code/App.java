package hexlet.code;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.plugins.enableDevLogging(); // логирование для разработки
        });

        app.get("/", ctx -> ctx.result("Hello World")); // корневой маршрут

        return app;
    }

    public static void main(String[] args) {
        int port = 7000;
        String portEnv = System.getenv("PORT");
        if (portEnv != null) {
            port = Integer.parseInt(portEnv);
        }

        Javalin app = getApp();
        app.start(port);
        LOGGER.info("App started on port {}", port);
    }
}
