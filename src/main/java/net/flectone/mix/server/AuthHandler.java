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

public class AuthHandler implements HttpHandler {

    private final FlectoneMix app;

    public AuthHandler() {
        app = FlectoneMix.getApp();
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

        String response = "OK";

        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

        String uri = exchange.getRequestURI().getQuery();

        if (uri.isEmpty()) {
            System.out.println("no in discord");
            return;
        }

        HashMap<String, String> userMap = new HashMap<>();

        for (String part : uri.split("&")) {
            String[] parts = part.split("=");
            userMap.put(parts[0], parts[1]);
        }

        DiscordUser discordUser = new DiscordUser(userMap.get("id"),
                userMap.get("username"),
                userMap.get("avatar"),
                userMap.get("token"),
                Boolean.parseBoolean(userMap.get("isInGuild")));

        app.setDiscordUser(discordUser);
        app.getConfig().put("id", discordUser.id());
        app.getConfig().put("token", discordUser.token());

        ((AuthController) app.getPaneManager().getLoader(PaneType.AUTH).getController()).updateData(true);
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
                    .uri(URI.create("https://mix.flectone.net/api/discord/login?id=" + id + "&token=" + token))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    Platform.runLater(() ->
                            FAlert.showWarn(FlectoneMix.getApp().getConfig().getLocaleString("alert.warn.message.bad-response")));
                    runnable.run();
                    return;
                }

                Gson g = new Gson();

                DiscordUser responceUser = g.fromJson(response.body(), DiscordUser.class);
                if (responceUser == null) {
                    Platform.runLater(() ->
                            FAlert.showWarn(FlectoneMix.getApp().getConfig().getLocaleString("alert.warn.message.bad-user")));
                    runnable.run();
                    return;
                }

                if (!responceUser.isInGuild()) {
                    Platform.runLater(() ->
                            FAlert.showWarn(
                                    FlectoneMix.getApp().getConfig().getLocaleString("alert.warn.message.not-in-guild"),
                                    () -> WebUtil.openUrl("https://discord.flectone.net")
                            )
                    );
                    runnable.run();
                    return;
                }

                app.setDiscordUser(responceUser);
                ((AuthController) app.getPaneManager().getLoader(PaneType.AUTH).getController()).updateData(true);

                System.out.println(responceUser.username());

            } catch (IOException | InterruptedException e) {
                FAlert.showException(e, e.getLocalizedMessage());
            }

            runnable.run();
        });
    }
}
