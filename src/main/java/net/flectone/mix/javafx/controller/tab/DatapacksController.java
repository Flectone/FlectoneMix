package net.flectone.mix.javafx.controller.tab;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.javafx.component.FAlert;
import net.flectone.mix.javafx.controller.ComponentPanelController;
import net.flectone.mix.javafx.transition.FFadeTransition;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class DatapacksController extends TabSetting {

    @FXML
    private Label tabLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private Label worldLabel;

    @FXML
    private Rectangle tabRectangle;

    @FXML
    private ComboBox versionComboBox;

    @FXML
    private TextField worldField;

    @FXML
    private Button installButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabLabel.setText(getLocaleString("tab.datapacks"));
        versionLabel.setText(getLocaleString("tab.datapacks.label.version"));
        worldLabel.setText(getLocaleString("tab.datapacks.label.world"));
        installButton.setText(getLocaleString("tab.datapacks.button.install"));
        worldField.setPromptText(getLocaleString("tab.datapacks.prompt"));

        if (url == null) return;

        new FFadeTransition(versionComboBox);
        new FFadeTransition(installButton);

        settingComboBox(versionComboBox, "minecraft.version.list");

        tabRectangle.setFill(new ImagePattern(new Image("/net/flectone/mix/images/preview-datapacks.png")));

        setFilterAction(() -> FlectoneMix.getApp().getThreadPool().execute(() -> {
            ComponentPanelController controller = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.COMPONENTS).getController();
            controller.getComponentBuilder().build("datapack", String.valueOf(versionComboBox.getSelectionModel().getSelectedItem()));
        }));

        setProgressBar(progressBar);
        setProgressLabel(progressLabel);

        installButton.setDisable(true);
        installButton.setOnAction(e -> {
            if (getOutputDirectory() == null || worldField.getText().isEmpty()) {
                FAlert.showWarn(getLocaleString("alert.warn.message.null-world"));
                return;
            }
            downloadComponents(installButton);
        });

        worldField.textProperty().addListener(e -> {
            String inputText = worldField.getText();
            installButton.setDisable(false);
            setOutputDirectory("saves" + File.separator + inputText + File.separator);
        });

        Platform.runLater(this::updateFilter);
    }
}