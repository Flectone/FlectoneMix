package net.flectone.mix.javafx.controller.alert;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.component.FStage;
import net.flectone.mix.javafx.controller.UndecoratedWindow;
import net.flectone.mix.manager.ConfigManager;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public class AlertConfirmationController implements UndecoratedWindow, AlertWindow {

    @FXML
    private Label errorLabel;

    @FXML
    private Label textLabel;

    @FXML
    private Pane topBarPanel;

    @FXML
    private Pane windowPanel;

    @FXML
    private Pane contentPanel;

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    @Setter
    private FStage stage;

    private int result;

    @Override
    public void checkUndecoratedSupport() {
        if (FlectoneMix.getApp().getConfig().isUsedUndecoratedWindow()) return;

        windowPanel.getChildren().remove(topBarPanel);
        AnchorPane.setTopAnchor(contentPanel, 0.0);
        AnchorPane.setBottomAnchor(contentPanel, 0.0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConfigManager config = FlectoneMix.getApp().getConfig();
        errorLabel.setText(config.getLocaleString("alert.confirmation"));
        yesButton.setText(config.getLocaleString("alert.confirmation.button.yes"));
        noButton.setText(config.getLocaleString("alert.confirmation.button.no"));

        if (url == null) return;

        yesButton.setOnMousePressed(e -> {
            result = 1;
            okButtonEvent();
        });

        noButton.setOnMousePressed(e -> okButtonEvent());

        checkUndecoratedSupport();
    }

    @Override
    public void okButtonEvent() {
        stage.close();
    }
}
