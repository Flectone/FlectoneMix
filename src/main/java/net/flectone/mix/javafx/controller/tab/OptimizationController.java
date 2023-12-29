package net.flectone.mix.javafx.controller.tab;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.component.PaneType;
import net.flectone.mix.javafx.controller.ComponentPanelController;
import net.flectone.mix.javafx.component.FFadeTransition;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class OptimizationController extends TabSetting {

    @FXML
    private Label tabLabel;

    @FXML
    private Rectangle tabRectangle;

    @FXML
    private Label versionLabel;

    @FXML
    private Label loaderLabel;

    @FXML
    private Label modeTypeLabel;

    @FXML
    private CheckBox profileCheckBox;

    @FXML
    private CheckBox unstableCheckBox;

    @FXML
    private CheckBox clearCheckBox;

    @FXML
    private CheckBox settingCheckBox;

    @FXML
    private ComboBox<String> versionComboBox;

    @FXML
    private ComboBox<String> loaderComboBox;

    @FXML
    private ComboBox<String> modComboBox;

    @FXML
    private Button updateButton;

    @FXML
    private Button installButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabLabel.setText(getLocaleString("tab.optimization"));

        versionLabel.setText(getLocaleString("tab.optimization.label.version"));
        loaderLabel.setText(getLocaleString("tab.optimization.label.loader"));
        modeTypeLabel.setText(getLocaleString("tab.optimization.label.mode-type"));

        progressLabel.setText(getLocaleString("tab.optimization.label.progress"));

        settingCheckBox(profileCheckBox, "tab.optimization.checkbox.profile");
        settingCheckBox(unstableCheckBox, "tab.optimization.checkbox.unstable");
        settingCheckBox(clearCheckBox, "tab.optimization.checkbox.clear");
        settingCheckBox(settingCheckBox, "tab.optimization.checkbox.setting");

        installButton.setText(getLocaleString("tab.optimization.button.install"));
        updateButton.setText(getLocaleString("tab.optimization.button.update"));

        if (url == null) return;

        new FFadeTransition(versionComboBox);
        new FFadeTransition(loaderComboBox);
        new FFadeTransition(modComboBox);
        new FFadeTransition(updateButton);
        new FFadeTransition(installButton);

        settingComboBox(versionComboBox, "minecraft.version.list");
        settingComboBox(loaderComboBox, "minecraft.loader.list");
        settingComboBox(modComboBox, "minecraft.mode-type.list");

        tabRectangle.setFill(new ImagePattern(new Image("/net/flectone/mix/images/preview-optimization.png")));
        tabRectangle.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,255,0,0.3), 20, 0.4, 0, 0);");

        setFilterAction(() -> FlectoneMix.getApp().getThreadPool().execute(() -> {
            ComponentPanelController controller = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.COMPONENTS).getController();
            controller.getComponentBuilder().build(loaderComboBox.getSelectionModel().getSelectedItem()
                    + modComboBox.getSelectionModel().getSelectedItem(), String.valueOf(versionComboBox.getSelectionModel().getSelectedItem()));
        }));

        setOutputDirectory("mods" + File.separator);
        setProgressBar(progressBar);
        setProgressLabel(progressLabel);

        installButton.setOnAction(e -> {
            executeCheckBox();
            downloadComponents(installButton);
        });
        updateButton.setOnAction(e -> {
            executeCheckBox();
            updateComponents(updateButton);
        });

        unstableCheckBox.selectedProperty().addListener(e -> FlectoneMix.getApp().getThreadPool().execute(() -> {
            ComponentPanelController controller = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.COMPONENTS).getController();
            controller.getComponentBuilder().selectAll(FlectoneMix.getApp().getConfig().getStringList("blacklist.optimization"), unstableCheckBox.isSelected());
        }));

        Platform.runLater(this::updateFilter);
    }

    private void executeCheckBox() {
        if (clearCheckBox.isSelected()) {
            clearFolder(".jar");
        }

        if (profileCheckBox.isSelected()) {
            FlectoneMix.getApp().getThreadPool().execute(() ->
                    downloadProfile(loaderComboBox.getSelectionModel().getSelectedItem(), versionComboBox.getSelectionModel().getSelectedItem()));
        }

        if (settingCheckBox.isSelected()) {
            FlectoneMix.getApp().getConfig().getStringList("minecraft.config.list").forEach(string ->
                    downloadFile("config" + File.separator, string));
        }
    }
}
