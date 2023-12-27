package net.flectone.mix.javafx.controller.tab;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.javafx.component.FAlert;
import net.flectone.mix.javafx.controller.AuthController;
import net.flectone.mix.javafx.controller.ComponentPanelController;
import net.flectone.mix.javafx.transition.FFadeTransition;
import net.flectone.mix.manager.FileManager;
import net.flectone.mix.manager.PaneManager;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingController extends TabSetting {

    @FXML
    private Label tabLabel;

    @FXML
    private Label languageLabel;

    @FXML
    private Label themeLabel;

    @FXML
    private Label pathLabel;

    @FXML
    private CheckBox decorationCheckBox;

    @FXML
    private Rectangle tabRectangle;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private ComboBox<String> themeComboBox;

    @FXML
    private Button quitButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button pathButton;

    @FXML
    private TextArea pathArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabLabel.setText(getLocaleString("tab.setting"));
        languageLabel.setText(getLocaleString("tab.setting.label.language"));
        themeLabel.setText(getLocaleString("tab.setting.label.theme"));
        pathLabel.setText(getLocaleString("tab.setting.label.path"));
        decorationCheckBox.setText(getLocaleString("tab.setting.checkbox.decoration"));
        saveButton.setText(getLocaleString("tab.setting.button.save"));
        quitButton.setText(getLocaleString("tab.setting.button.quit"));

        if (FlectoneMix.getApp().getConfig().isSupportUndecoratedWindow()) {
            settingCheckBox(decorationCheckBox, "tab.setting.checkbox.decoration");
        } else decorationCheckBox.setDisable(!FlectoneMix.getApp().getConfig().isSupportUndecoratedWindow());

        if (url == null) return;

        new FFadeTransition(languageComboBox);
        new FFadeTransition(quitButton);
        new FFadeTransition(saveButton);

        settingComboBox(languageComboBox, "language.support");
        settingComboBox(themeComboBox, "theme.list");

        decorationCheckBox.setSelected(FlectoneMix.getApp().getConfig().isPossiblyDecorated());

        setFilterAction(() -> {
            ComponentPanelController controller = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.COMPONENTS).getController();
            controller.getComponentBuilder().build("", "");
        });

        Platform.runLater(() -> {
            languageComboBox.getSelectionModel().select(FlectoneMix.getApp().getConfig().getLanguage());
            themeComboBox.getSelectionModel().select(FlectoneMix.getApp().getConfig().getTheme());
        });



        tabRectangle.setFill(new ImagePattern(new Image("/net/flectone/mix/images/preview-setting.png")));
        tabRectangle.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,128,255,0.3), 20, 0.4, 0, 0);");

        pathArea.setText(FlectoneMix.getApp().getConfig().getMinecraftFolder());

        pathButton.setOnAction(e -> openMinecraftFolderChooser());

        Platform.runLater(this::updateFilter);
    }

    public void openMinecraftFolderChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(FlectoneMix.getApp().getConfig().getMinecraftFolder()));
        File file = directoryChooser.showDialog(FlectoneMix.getApp().getStage());
        if (file == null) return;

        pathArea.setText(file.getAbsolutePath() + File.separator);
    }

    public void reloadApp() {
        FileManager config = FlectoneMix.getApp().getConfig();
        String oldTheme = config.getTheme();
        String newTheme = String.valueOf(themeComboBox.getSelectionModel().getSelectedItem());
        if (!oldTheme.equals(newTheme)) {
            String oldThemePath = config.getThemePath();
            config.setTheme(String.valueOf(themeComboBox.getSelectionModel().getSelectedItem()));

            FlectoneMix.getApp().getStage().getScene().getStylesheets().addAll(FlectoneMix.getApp().getConfig().getThemePath());
            FlectoneMix.getApp().getStage().getScene().getStylesheets().removeAll(oldThemePath);
        }

        config.setLanguage(String.valueOf(languageComboBox.getSelectionModel().getSelectedItem()));
        config.setMinecraftFolder(pathArea.getText());

        if (config.isPossiblyDecorated() != decorationCheckBox.isSelected()) {
            FAlert.showWarn(getLocaleString("alert.warn.message.restart-for-decorations"));
        }

        config.setPossiblyDecorated(decorationCheckBox.isSelected());

        FlectoneMix.getApp().getPaneManager().reloadPanes();
    }

    public void quitEvent() {
        FlectoneMix.getApp().setDiscordUser(null);
        PaneManager paneManager = FlectoneMix.getApp().getPaneManager();
        paneManager.addPane(PaneType.AUTH);
        paneManager.activate(PaneType.AUTH);
        ((AuthController) paneManager.getLoader(PaneType.AUTH).getController()).getAuthButton().setDisable(false);
    }
}
