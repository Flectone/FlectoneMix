package net.flectone.mix.util;

import lombok.NonNull;
import net.flectone.mix.javafx.component.FAlert;
import net.flectone.mix.manager.FileManager;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebUtil {

    public static void openUrl(@NonNull String url) {
        try {
            String os = FileManager.getOS();

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
            FAlert.showException(e, e.getLocalizedMessage());
        }
    }
}
