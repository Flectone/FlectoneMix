package net.flectone.mix.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;
import net.flectone.mix.javafx.component.FStage;
import net.flectone.mix.javafx.controller.AuthController;
import net.flectone.mix.manager.FileManager;
import net.flectone.mix.manager.PaneManager;
import net.flectone.mix.model.DiscordUser;
import net.flectone.mix.server.AuthHandler;
import net.flectone.mix.thread.CustomThreadPool;
import net.flectone.mix.util.JavaFXUtil;

@Getter
@Setter
public class FlectoneMix extends Application {
    private AuthHandler authHandler;
    private FileManager config;
    private DiscordUser discordUser;
    private PaneManager paneManager;
    private FStage stage;

    private CustomThreadPool threadPool;

    @Getter
    private static FlectoneMix app;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage ignored) {
        app = this;
        threadPool = new CustomThreadPool(6);

        config = new FileManager();
        paneManager = new PaneManager(1000, 500);
        authHandler = new AuthHandler();

        stage = new FStage();
        stage.setMinWidth(1000);
        stage.setMinHeight(530);
        stage.setScene(paneManager.getScene());
        stage.setOnCloseRequest(this::exit);
        stage.fullScreenExitHintProperty().addListener(e -> System.out.println(2));
        stage.setTitle("FlectoneMix 3.0.0");
        stage.getIcons().add(new Image("/net/flectone/mix/images/flectone.png"));
        stage.customShow();

        authHandler.authorize(config.getString("id"), config.getString("token"),
                () -> Platform.runLater(() -> ((AuthController) paneManager.getLoader(PaneType.AUTH)
                        .getController())
                        .getAuthButton()
                        .setDisable(false)));

        JavaFXUtil.focusApp();
    }

    public void exit(WindowEvent event) {
        config.save();
        System.exit(0);
        threadPool.shutdown();
    }
}