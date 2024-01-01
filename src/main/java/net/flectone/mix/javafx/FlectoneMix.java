package net.flectone.mix.javafx;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import net.flectone.mix.javafx.component.FStage;
import net.flectone.mix.javafx.component.PaneType;
import net.flectone.mix.javafx.controller.AuthController;
import net.flectone.mix.manager.ConfigManager;
import net.flectone.mix.manager.PaneManager;
import net.flectone.mix.model.DiscordUser;
import net.flectone.mix.server.AuthHandler;
import net.flectone.mix.thread.CustomThreadPool;
import net.flectone.mix.util.WebUtil;

@Getter
@Setter
public class FlectoneMix extends Application {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 500;

    @Getter
    private static final Gson gson = new Gson();

    @Getter
    private static FlectoneMix app;

    private AuthHandler authHandler;
    private ConfigManager config;
    private DiscordUser discordUser;
    private PaneManager paneManager;
    private FStage stage;

    private CustomThreadPool threadPool;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage ignored) {
        app = this;
        threadPool = new CustomThreadPool(6);

        config = new ConfigManager();
        paneManager = new PaneManager(WIDTH, HEIGHT);
        authHandler = new AuthHandler();

        configureStage();
        configureAuthHandler();

        focus();
    }

    private void configureStage() {
        stage = new FStage();
        stage.setMinWidth(WIDTH);
        stage.setMinHeight(HEIGHT);
        stage.setScene(paneManager.getScene());
        stage.setOnCloseRequest(e -> exit());
        stage.setTitle("FlectoneMix " + config.getVersion());
        stage.getIcons().add(new Image("/net/flectone/mix/images/flectone.png"));
        stage.customShow();
    }

    private void configureAuthHandler() {
        authHandler.authorize(
                config.getString("id"),
                config.getString("token"),
                () -> Platform.runLater(() -> {
                    AuthController authController = paneManager.getLoader(PaneType.AUTH).getController();
                    authController.getAuthButton().setDisable(false);
                    WebUtil.checkUpdate();
                })
        );
    }

    public void focus() {
        stage.toFront();
        stage.setAlwaysOnTop(true);
        stage.setAlwaysOnTop(false);
    }

    public void exit() {
        config.save();
        threadPool.shutdown();
        stage.close();
        System.exit(0);
    }
}