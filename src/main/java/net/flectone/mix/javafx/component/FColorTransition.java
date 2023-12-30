package net.flectone.mix.javafx.component;

import animatefx.animation.Pulse;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import net.flectone.mix.javafx.FlectoneMix;

public class FColorTransition {

    private boolean isSelected;
    private final ImageView imageView;

    private final ColorAdjust initialColorAdjust = new ColorAdjust();

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;

        if (isSelected) {
            toSelectedColor();

            if (!FlectoneMix.getApp().getConfig().animated()) return;
            Pulse pulse = new Pulse(imageView);
            pulse.setDelay(Duration.ZERO);
            pulse.play();
        } else {
            toUnselectedColor();
        }
    }

    public FColorTransition(ImageView imageView) {
        this.imageView = imageView;

        imageView.setEffect(initialColorAdjust);

        imageView.setOnMouseEntered(e -> toSelectedColor());

        imageView.setOnMouseExited(e -> {
            if(isSelected) return;
            toUnselectedColor();
        });

    }

    private void toSelectedColor() {
        animateColorAdjust(initialColorAdjust, toColor(Color.web("#0000ff")));
    }

    private void toUnselectedColor() {
        animateColorAdjust(initialColorAdjust, toColor(Color.WHITE));
    }

    public void animateColorAdjust(ColorAdjust from, ColorAdjust to) {
        Timeline hoverAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(to.hueProperty(), from.getHue()),
                        new KeyValue(to.brightnessProperty(), from.getBrightness()),
                        new KeyValue(to.saturationProperty(), from.getSaturation())
                ),
                new KeyFrame(Duration.millis(100),
                        new KeyValue(from.hueProperty(), to.getHue()),
                        new KeyValue(from.brightnessProperty(), to.getBrightness()),
                        new KeyValue(from.saturationProperty(), to.getSaturation())
                )
        );

        hoverAnimation.setCycleCount(1);
        hoverAnimation.play();
    }

    private ColorAdjust toColor(Color targetColor) {
        return new ColorAdjust(targetColor.getHue(), targetColor.getSaturation(), 0, 0);
    }
}
