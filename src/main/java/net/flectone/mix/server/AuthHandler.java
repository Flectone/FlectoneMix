package net.flectone.mix.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Platform;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.javafx.component.FAlert;
import net.flectone.mix.javafx.controller.AuthController;
import net.flectone.mix.model.DiscordUser;
import net.flectone.mix.util.JavaFXUtil;
import net.flectone.mix.util.WebUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class AuthHandler implements HttpHandler {

    private static final String AUTHORIZE_REQUEST_URL = "https://mix.flectone.net/api/discord/login?id=<id>&token=<token>";

    public AuthHandler() {
        startHttpServer();
    }

    private void startHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/", this);
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            FAlert.showException(e, e.getLocalizedMessage());
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handleExchange(exchange);
        String uri = exchange.getRequestURI().getQuery();
        if (uri.isEmpty()) {
            System.out.println("no in discord");
            return;
        }
        Map<String, String> userMap = extractUserMap(uri);
        DiscordUser discordUser = createDiscordUser(userMap);
        updateAppState(discordUser);
    }

    private void handleExchange(HttpExchange exchange) throws IOException {
        String response = "OK";
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private Map<String, String> extractUserMap(String uri) {
        Map<String, String> userMap = new HashMap<>();
        for (String part : uri.split("&")) {
            String[] parts = part.split("=");
            userMap.put(parts[0], parts[1]);
        }
        return userMap;
    }

    private DiscordUser createDiscordUser(Map<String, String> userMap) {
        return new DiscordUser(
                userMap.get("id"),
                userMap.get("username"),
                userMap.get("avatar"),
                userMap.get("token"),
                Boolean.parseBoolean(userMap.get("isInGuild"))
        );
    }

    private void updateAppState(DiscordUser discordUser) {
        FlectoneMix app = FlectoneMix.getApp();
        app.setDiscordUser(discordUser);
        app.getConfig().put("id", discordUser.id());
        app.getConfig().put("token", discordUser.token());
        AuthController authController = app.getPaneManager().getLoader(PaneType.AUTH).getController();
        Platform.runLater(() -> authController.updateData(true));
        JavaFXUtil.focusApp();
    }

    public void authorize(String id, String token, Runnable runnable) {
        FlectoneMix.getApp().getThreadPool().execute(() -> {
            if (id == null || token == null) {
                runnable.run();
                return;
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AUTHORIZE_REQUEST_URL.replace("<id>", id).replace("<token>", token)))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                handleAuthorizeResponse(response, runnable);
            } catch (IOException | InterruptedException e) {
                FAlert.showException(e, e.getLocalizedMessage());
            }

            runnable.run();
        });
    }

    private void handleAuthorizeResponse(HttpResponse<String> response, Runnable runnable) {
        if (response.statusCode() != 200) {
            Platform.runLater(() -> FAlert.showWarn(FlectoneMix.getApp().getConfig().getLocaleString("alert.warn.message.bad-response")));
            runnable.run();
            return;
        }

        Gson g = new Gson();
        DiscordUser responseUser = g.fromJson(response.body(), DiscordUser.class);

        if (responseUser == null) {
            Platform.runLater(() -> FAlert.showWarn(FlectoneMix.getApp().getConfig().getLocaleString("alert.warn.message.bad-user")));
            runnable.run();
            return;
        }

        if (!responseUser.isInGuild()) {
            Platform.runLater(() ->
                    FAlert.showWarn(
                            FlectoneMix.getApp().getConfig().getLocaleString("alert.warn.message.not-in-guild"),
                            () -> WebUtil.openUrl("https://discord.flectone.net")
                    )
            );
            runnable.run();
            return;
        }

        FlectoneMix app = FlectoneMix.getApp();
        app.setDiscordUser(responseUser);
        AuthController authController = app.getPaneManager().getLoader(PaneType.AUTH).getController();
        Platform.runLater(() -> authController.updateData(true));
        System.out.println(responseUser.username());
    }
}
