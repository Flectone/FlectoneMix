package net.flectone.mix.javafx.controller.alert;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.component.FStage;
import net.flectone.mix.javafx.controller.UndecoratedWindow;
import net.flectone.mix.manager.ConfigManager;
import net.flectone.mix.util.WebUtil;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public class AlertExceptionController implements UndecoratedWindow, AlertWindow {

    private static final String DISCORD_FORUM = "https://discord.com/channels/861147957365964810/1125849064903286854";

    @FXML
    private Label errorLabel;

    @FXML
    private Label problemLabel;

    @FXML
    private TextArea textArea;

    @FXML
    private Label titleLabel;

    @FXML
    private Pane topBarPanel;

    @FXML
    private Pane windowPanel;

    @FXML
    private Pane contentPanel;

    @FXML
    private Button skipButton;

    @Setter
    private FStage stage;

    public void checkUndecoratedSupport() {
        if (FlectoneMix.getApp().getConfig().isUsedUndecoratedWindow()) return;

        windowPanel.getChildren().remove(topBarPanel);
        AnchorPane.setTopAnchor(contentPanel, 0.0);
        AnchorPane.setBottomAnchor(contentPanel, 0.0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConfigManager config = FlectoneMix.getApp().getConfig();
        errorLabel.setText(config.getLocaleString("alert.error"));
        titleLabel.setText(config.getLocaleString("alert.error.label.title"));
        problemLabel.setText(config.getLocaleString("alert.error.label.problem"));
        skipButton.setText(config.getLocaleString("alert.error.button.skip"));

        if (url == null) return;

        problemLabel.setOnMousePressed(e -> WebUtil.openUrl(DISCORD_FORUM));

        checkUndecoratedSupport();
    }

    @Override
    public void okButtonEvent() {
        stage.close();
    }

    @Override
    public Label getTextLabel() {
        return titleLabel;
    }
}
