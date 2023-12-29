package net.flectone.mix.util;

import javafx.application.Platform;
import lombok.NonNull;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.component.FAlert;
import net.flectone.mix.manager.ConfigManager;
import net.flectone.mix.server.FileDownloader;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebUtil {

    private static final String LAST_URL = "https://mix.flectone.net/last/";
    private static final String APP_NAME = "FlectoneMix.";
    private static final String UPDATER_NAME = "updater.jar";

    public static void openUrl(@NonNull String url) {
        try {
            String os = ConfigManager.getOS();

            if (os.contains("win")
                    && Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }

            Runtime rt = Runtime.getRuntime();
            if (os.contains("win")) {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                rt.exec("open " + url);
            } else {
                rt.exec("xdg-open " + url);
            }

        } catch (URISyntaxException | IOException e) {
            new FAlert(FAlert.Type.EXCEPTION, e).show();
        }
    }

    public static String getLatestVersion() {
        try {
            String stringUrl = String.format("https://mix.flectone.net/api/version?id=%s&token=%s",
                    FlectoneMix.getApp().getDiscordUser().id(), FlectoneMix.getApp().getDiscordUser().token());

            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) return null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.readLine();
            }
        } catch (IOException e) {
            return null;
        }
    }

    public static void checkUpdate() {
        FlectoneMix.getApp().getThreadPool().execute(() -> {
            String latestVersion = getLatestVersion();
            if (latestVersion == null) return;

            ConfigManager config = FlectoneMix.getApp().getConfig();
            String currentVersion = config.getVersion();
            System.out.println(currentVersion);
            if (currentVersion == null) return;

            if (config.compareVersions(currentVersion, latestVersion) != ConfigManager.VersionComparison.OLDER) return;

            Platform.runLater(() -> {
                String message = config.getLocaleString("alert.confirmation.message.update")
                        .replace("<version>", latestVersion);
                FAlert fAlert = new FAlert(FAlert.Type.CONFIRMATION, message);
                if (fAlert.showAndWait() == 1) {
                    downloadAndRunUpdater();
                }
            });
        });
    }

    public static void downloadAndRunUpdater() {

        ConfigManager config = FlectoneMix.getApp().getConfig();
        new FileDownloader(LAST_URL + UPDATER_NAME + "?timestamp=" + System.currentTimeMillis(),
                new File(config.getFlectonemixFolder()))
                .run();

        String runningPath = config.getRunningPath();
        String appExtension = runningPath.substring(runningPath.lastIndexOf(".") + 1);

        new FileDownloader(LAST_URL + APP_NAME + appExtension + "?timestamp=" + System.currentTimeMillis(),
                new File(config.getFlectonemixFolder()))
                .run();

        try {
            new ProcessBuilder("java", "-jar",
                    config.getFlectonemixFolder() + UPDATER_NAME,
                    config.getFlectonemixFolder() + APP_NAME + appExtension,
                    runningPath)
                    .inheritIO()
                    .start();
            FlectoneMix.getApp().exit();
        } catch (IOException e) {
            new FAlert(FAlert.Type.EXCEPTION, e).show();
        }
    }
}
