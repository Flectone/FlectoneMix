package net.flectone.mix.javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.NonNull;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.component.FColorTransition;
import net.flectone.mix.javafx.component.FToolTip;
import net.flectone.mix.javafx.component.PaneType;
import net.flectone.mix.model.DiscordUser;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class LeftPanelController implements Initializable {

    @FXML
    private ImageView optimizationImage;
    @FXML
    private ImageView farmImage;
    @FXML
    private ImageView pluginImage;
    @FXML
    private ImageView modImage;
    @FXML
    private ImageView datapackImage;
    @FXML
    private ImageView resourcepackImage;
    @FXML
    private ImageView shaderImage;
    @FXML
    private ImageView settingImage;

    @FXML
    private Rectangle avatarRectangle;

    @FXML
    private Label discordLabel;

    private static final HashMap<ImageView, Options> TABS = new HashMap<>();

    private ImageView selectedImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TABS.put(optimizationImage, new Options(optimizationImage, PaneType.OPTIMIZATION, "tab.optimization"));
        TABS.put(farmImage, new Options(farmImage, PaneType.FARMS, "tab.farms"));
        TABS.put(pluginImage, new Options(pluginImage, PaneType.PLUGINS, "tab.plugins"));
        TABS.put(modImage, new Options(modImage, PaneType.MODS, "tab.mods"));
        TABS.put(datapackImage, new Options(datapackImage, PaneType.DATAPACKS, "tab.datapacks"));
        TABS.put(resourcepackImage, new Options(resourcepackImage, PaneType.RESOURCEPACKS, "tab.resourcepacks"));
        TABS.put(shaderImage, new Options(shaderImage, PaneType.SHADERS, "tab.shaders"));
        TABS.put(settingImage, new Options(settingImage, PaneType.SETTING, "tab.setting"));

        if (url == null) return;

        DiscordUser discordUser = FlectoneMix.getApp().getDiscordUser();
        discordLabel.setText(discordUser.username());
        avatarRectangle.setFill(new ImagePattern(discordUser.getAvatar()));

        TABS.get(optimizationImage).getFColorTransition().setSelected(true);
        selectedImage = optimizationImage;
    }

    public void tabImageClickEvent(MouseEvent event) {
        ComponentPanelController componentPanelController = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.COMPONENTS).getController();

        var tabValues = TABS.get(selectedImage);

        selectedImage = (ImageView) event.getSource();
        var finalTabValues = TABS.get(selectedImage);

        finalTabValues.uninstallToolTip();

        if (tabValues.equals(finalTabValues)) return;

        tabValues.getFColorTransition().setSelected(false);
        finalTabValues.getFColorTransition().setSelected(true);

        componentPanelController.getComponentBuilder().clearAll();
        Platform.runLater(() -> {
            componentPanelController.getComponentBuilder().clearAll();
            ((AppController) FlectoneMix.getApp().getPaneManager().getLoader(PaneType.APP)
                    .getController())
                    .setTab(finalTabValues.getPaneType());

            if (finalTabValues.getPaneType() != PaneType.SETTING) {
                componentPanelController.getComponentPanel().setVisible(true);
            } else {
                componentPanelController.setVisibleComponents(false);
                componentPanelController.setVisibleSearchPanel(false);
            }
        });

        if (finalTabValues.getPaneType() != PaneType.SETTING && tabValues.getPaneType() == PaneType.SETTING) {
            componentPanelController.setVisibleSearchPanel(true);
        }
    }

    @Getter
    private static class Options {

        private final PaneType paneType;
        private final ImageView imageView;
        private final String localeString;

        private final FColorTransition fColorTransition;

        private final FToolTip toolTip;
        private boolean toolTipRemoved = false;

        public Options(@NonNull ImageView imageView, @NonNull PaneType paneType, @NonNull String localeString) {
            this.imageView = imageView;
            this.localeString = localeString;
            this.paneType = paneType;
            this.fColorTransition = new FColorTransition(imageView);
            this.toolTip = new FToolTip(imageView, localeString);

            imageView.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                if (!toolTipRemoved) return;
                installToolTip();
            });
        }

        public void uninstallToolTip() {
            Tooltip.uninstall(imageView, toolTip.getTooltip());
            toolTipRemoved = true;
        }

        public void installToolTip() {
            Tooltip.install(imageView, toolTip.getTooltip());
            toolTipRemoved = false;
        }
    }
}
