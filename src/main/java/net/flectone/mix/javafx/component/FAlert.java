package net.flectone.mix.javafx.component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.javafx.controller.alert.AlertExceptionController;
import net.flectone.mix.javafx.controller.alert.AlertInfoController;
import net.flectone.mix.javafx.controller.alert.AlertWarnController;
import net.flectone.mix.manager.PaneManager;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FAlert {

    public static void showException(Throwable e, String title) {
        e.printStackTrace();

        PaneManager paneManager = FlectoneMix.getApp().getPaneManager();

        FXMLLoader fxmlLoader = paneManager.addPane(PaneType.EXCEPTION);
        Pane pane = paneManager.loadPane(PaneType.EXCEPTION);

        AlertExceptionController paneController = fxmlLoader.getController();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        TextArea textArea = paneController.getTextArea();
        textArea.setText("   " + stringWriter);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        paneController.getTitleLabel().setText(title);

        Scene scene = new Scene(pane, 600, 350);
        scene.getStylesheets().add(FlectoneMix.getApp().getConfig().getThemePath());
        FStage stage = new FStage();
        stage.setScene(scene);
        stage.getIcons().add(new Image("/net/flectone/mix/images/error.png"));
        stage.setTitle(FlectoneMix.getApp().getConfig().getLocaleString("alert.exception"));
        stage.initModality(Modality.APPLICATION_MODAL);

        paneController.setStage(stage);
        stage.customShow();
    }

    public static void showWarn(String text) {
        PaneManager paneManager = FlectoneMix.getApp().getPaneManager();

        FXMLLoader fxmlLoader = paneManager.addPane(PaneType.WARN);
        Pane pane = paneManager.loadPane(PaneType.WARN);

        AlertWarnController paneController = fxmlLoader.getController();
        paneController.getTextLabel().setText(text);

        Scene scene = new Scene(pane, 420, Region.USE_COMPUTED_SIZE);
        scene.getStylesheets().add(FlectoneMix.getApp().getConfig().getThemePath());
        FStage stage = new FStage();
        stage.setScene(scene);
        stage.getIcons().add(new Image("/net/flectone/mix/images/warn.png"));
        stage.setTitle(FlectoneMix.getApp().getConfig().getLocaleString("alert.warn"));
        stage.initModality(Modality.APPLICATION_MODAL);

        paneController.setStage(stage);
        stage.customShow();
    }

    public static void showInfo(String text) {
        PaneManager paneManager = FlectoneMix.getApp().getPaneManager();

        FXMLLoader fxmlLoader = paneManager.addPane(PaneType.INFO);
        Pane pane = paneManager.loadPane(PaneType.INFO);

        AlertInfoController paneController = fxmlLoader.getController();
        paneController.getTextLabel().setText(text);

        Scene scene = new Scene(pane, 420, Region.USE_COMPUTED_SIZE);
        scene.getStylesheets().add(FlectoneMix.getApp().getConfig().getThemePath());
        FStage stage = new FStage();
        stage.setScene(scene);
        stage.getIcons().add(new Image("/net/flectone/mix/images/info.png"));
        stage.setTitle(FlectoneMix.getApp().getConfig().getLocaleString("alert.info"));
        stage.initModality(Modality.APPLICATION_MODAL);

        paneController.setStage(stage);
        stage.customShow();
    }


    public enum AlertType {
        EXCEPTION("alert_exception"),
        WARN("alert_warn"),
        INFO("alert_info");

        private final String type;

        AlertType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
