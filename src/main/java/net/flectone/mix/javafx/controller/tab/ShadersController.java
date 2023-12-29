package net.flectone.mix.javafx.controller.tab;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.component.FFadeTransition;
import net.flectone.mix.javafx.component.PaneType;
import net.flectone.mix.javafx.controller.ComponentPanelController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ShadersController extends TabSetting {

    @FXML
    private Label tabLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private Rectangle tabRectangle;

    @FXML
    private ComboBox<String> versionComboBox;

    @FXML
    private Button installButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabLabel.setText(getLocaleString("tab.shaders"));
        versionLabel.setText(getLocaleString("tab.shaders.label.version"));
        installButton.setText(getLocaleString("tab.shaders.button.install"));

        if (url == null) return;

        new FFadeTransition(versionComboBox);
        new FFadeTransition(installButton);

        settingComboBox(versionComboBox, "minecraft.version.list");

        tabRectangle.setFill(new ImagePattern(new Image("/net/flectone/mix/images/preview-shaders.png")));
        tabRectangle.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,0,0,0.3), 20, 0.4, 0, 0);");

        setFilterAction(() -> FlectoneMix.getApp().getThreadPool().execute(() -> {
            ComponentPanelController controller = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.COMPONENTS).getController();
            controller.getComponentBuilder().build("shader", String.valueOf(versionComboBox.getSelectionModel().getSelectedItem()));
        }));

        setOutputDirectory("shaderpacks" + File.separator);
        setProgressBar(progressBar);
        setProgressLabel(progressLabel);

        installButton.setOnAction(e -> downloadComponents(installButton));

        Platform.runLater(this::updateFilter);
    }
}
