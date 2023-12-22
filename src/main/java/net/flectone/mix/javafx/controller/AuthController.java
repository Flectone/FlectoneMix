package net.flectone.mix.javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.javafx.transition.FFadeTransition;
import net.flectone.mix.manager.FileManager;
import net.flectone.mix.manager.PaneManager;
import net.flectone.mix.model.DiscordUser;
import net.flectone.mix.util.WebUtil;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public class AuthController implements Initializable {

    private static final String DISCORD_URL = "https://discord.com/api/oauth2/authorize?client_id=1153781807968428093&response_type=code&redirect_uri=https%3A%2F%2Fmix.flectone.net%2Fapi%2Fdiscord%2Fauth&scope=identify+guilds";

    @FXML
    private Label userName;

    @FXML
    private Rectangle userLogo;

    @FXML
    private Button authButton;

    @FXML
    private Pane authPane;

    @FXML
    private Pane mainPane;

    @Setter
    private static boolean isReady = false;

    @FXML
    protected void authButtonClickEvent() {
        if (!isReady) {
            WebUtil.openUrl(DISCORD_URL);
            return;
        }

        DiscordUser discordUser = FlectoneMix.getApp().getDiscordUser();
        FlectoneMix.getApp().getAuthHandler().authorize(discordUser.id(), discordUser.token(), () -> Platform.runLater(() -> {
            PaneManager paneManager = FlectoneMix.getApp().getPaneManager();

            if (paneManager.getLoader(PaneType.APP) == null) {
                paneManager.addPane(PaneType.APP);
            }
            paneManager.activate(PaneType.APP);
        }));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FileManager config = FlectoneMix.getApp().getConfig();
        userName.setText(config.getLocaleString("auth.label.welcome"));
        authButton.setText(config.getLocaleString("auth.button.authorize"));

        if (url == null) return;

        isReady = false;

        authButton.setDisable(true);
        userLogo.setFill(new ImagePattern(new Image("/net/flectone/mix/images/flectone.png")));

        new FFadeTransition(authButton);
    }

    public void updateData(boolean isReady) {
        setReady(isReady);
        Platform.runLater(() -> {

            DiscordUser discordUser = FlectoneMix.getApp().getDiscordUser();
            userName.setText(discordUser.username());
            userLogo.setFill(new ImagePattern(discordUser.getAvatar()));

            authButton.setText(FlectoneMix.getApp().getConfig().getLocaleString("auth.button.continue"));
            authButton.setGraphic(null);
            authButton.setStyle("-fx-background-color: -fx-color-green");
        });
    }
}