package net.flectone.mix.javafx.controller.tab;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.javafx.controller.ComponentPanelController;
import net.flectone.mix.javafx.transition.FFadeTransition;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ModsController extends TabSetting {

    @FXML
    private Label tabLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private Label loaderLabel;

    @FXML
    private Rectangle tabRectangle;

    @FXML
    private ComboBox<String> versionComboBox;

    @FXML
    private ComboBox<String> loaderComboBox;

    @FXML
    private CheckBox profileCheckBox;

    @FXML
    private CheckBox clearCheckBox;

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
        tabLabel.setText(getLocaleString("tab.mods"));
        versionLabel.setText(getLocaleString("tab.mods.label.version"));
        loaderLabel.setText(getLocaleString("tab.mods.label.loader"));

        settingCheckBox(profileCheckBox, "tab.mods.checkbox.profile");
        settingCheckBox(clearCheckBox, "tab.mods.checkbox.clear");

        installButton.setText(getLocaleString("tab.mods.button.install"));
        updateButton.setText(getLocaleString("tab.mods.button.update"));

        if (url == null) return;

        new FFadeTransition(versionComboBox);
        new FFadeTransition(updateButton);
        new FFadeTransition(installButton);


        settingComboBox(versionComboBox, "minecraft.version.list");
        settingComboBox(loaderComboBox, "minecraft.loader.list");

        tabRectangle.setFill(new ImagePattern(new Image("/net/flectone/mix/images/preview-mods.png")));

        setFilterAction(() -> FlectoneMix.getApp().getThreadPool().execute(() -> {
            ComponentPanelController controller = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.COMPONENTS).getController();
            controller.getComponentBuilder().build(String.valueOf(loaderComboBox.getSelectionModel().getSelectedItem()),
                    String.valueOf(versionComboBox.getSelectionModel().getSelectedItem()));
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
    }
}
