package net.flectone.mix.javafx.transition;


import javafx.animation.FadeTransition;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class FFadeTransition {

    public FFadeTransition(Region mfxButton) {
        final FadeTransition fadeIn = new FadeTransition(Duration.millis(100));
        fadeIn.setNode(mfxButton);
        fadeIn.setToValue(1);
        mfxButton.setOnMouseExited(e -> {
            fadeIn.playFromStart();
        });

        final FadeTransition fadeOut = new FadeTransition(Duration.millis(100));
        fadeOut.setNode(mfxButton);
        fadeOut.setToValue(0.7);
        mfxButton.setOnMouseEntered(e -> {
            fadeOut.playFromStart();
        });
    }
}
