package net.flectone.mix.javafx.component;

import javafx.geometry.Bounds;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import lombok.Getter;
import lombok.NonNull;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.manager.ConfigManager;

@Getter
public class FToolTip {

    private final Tooltip tooltip;

    public FToolTip(@NonNull ImageView node, @NonNull String localeString) {
        ConfigManager configManager = FlectoneMix.getApp().getConfig();

        tooltip = new Tooltip(configManager.getLocaleString("tooltip." + localeString));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setStyle("-fx-font-size: 15");

        Tooltip.install(node, tooltip);

        tooltip.setOnShowing(e -> {
            Bounds bounds = node.localToScreen(node.getBoundsInLocal());
            tooltip.setX(bounds.getMaxX());
            tooltip.setY(bounds.getMinY() - 10);
        });
    }

    public FToolTip(@NonNull CheckBox node, @NonNull String localeString) {
        ConfigManager configManager = FlectoneMix.getApp().getConfig();

        tooltip = new Tooltip(configManager.getLocaleString("tooltip." + localeString));
        tooltip.setStyle("-fx-font-size: 12; -fx-background-color: -fx-color-blue");
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(300);

        Tooltip.install(node, tooltip);
    }
}
