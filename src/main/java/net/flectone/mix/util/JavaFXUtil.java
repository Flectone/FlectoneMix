package net.flectone.mix.util;

import javafx.application.Platform;
import net.flectone.mix.javafx.FlectoneMix;

public class JavaFXUtil {


    public static void focusApp() {
        Platform.runLater(() -> {
            FlectoneMix app = FlectoneMix.getApp();
            app.getStage().toFront();
            app.getStage().setAlwaysOnTop(true);
            app.getStage().setAlwaysOnTop(false);
        });
    }

}
