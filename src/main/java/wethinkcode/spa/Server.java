package wethinkcode.spa;

/*
 ** DO NOT CHANGE!!
 */

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class Server {
    private static final String PAGES_DIR = "/public";

    private final Javalin appServer;

    public Server() {
        appServer = Javalin.create(config -> config.addStaticFiles(PAGES_DIR, Location.CLASSPATH));
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(5050);
    }

    public void start(int port) {
        this.appServer.start(port);
//        System.setProperty("webdriver.chrome.driver", "/home/wtc/Downloads/chromedriver_linux64/chromedriver");
//        ChromeOptions options = new ChromeOptions();
//// If needed, specify the path to the Chrome binary
//// options.setBinary("/path/to/chrome");
//        WebDriver driver = new ChromeDriver(options);

    }

    public void stop() {
        this.appServer.stop();
    }

    public int port() {
        return appServer.port();
    }
}
