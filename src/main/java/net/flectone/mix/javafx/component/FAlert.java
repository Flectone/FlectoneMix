package net.flectone.mix.javafx.component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import lombok.Getter;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.controller.alert.AlertConfirmationController;
import net.flectone.mix.javafx.controller.alert.AlertExceptionController;
import net.flectone.mix.javafx.controller.alert.AlertWarnController;
import net.flectone.mix.javafx.controller.alert.AlertWindow;
import net.flectone.mix.manager.PaneManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FAlert {

    private final Type type;
    private final FStage stage;
    private AlertWindow alertWindow;

    public FAlert(@NotNull Type type) {
        this.type = type;
        this.stage = new FStage();
    }

    public FAlert(@NotNull Type type, Object... object) {
        this(type);

        switch (type) {
            case INFO -> configureInfo((String) object[0]);
            case CONFIRMATION -> configureConfirmation((String) object[0]);
            case EXCEPTION -> configureException((Throwable) object[0]);
            case WARN -> configureWarn((String) object[0], null);
        }
    }

    public FAlert(@NotNull Type type, @NotNull String text, @Nullable Runnable runnable) {
        this(type);

        if (type == Type.WARN) {
            configureWarn(text, runnable);
        }
    }

    public void show() {
        stage.customShow();
        stage.setAlwaysOnTop(true);
    }

    public int showAndWait() {
        stage.makeUndecorated();
        stage.setAlwaysOnTop(true);
        stage.showAndWait();

        return type == Type.CONFIRMATION
                ? ((AlertConfirmationController) alertWindow).getResult()
                : 0;
    }

    public void configureException(Throwable e) {
        e.printStackTrace();

        AlertExceptionController paneController = configureAlertWindow(Type.EXCEPTION, e.getLocalizedMessage());

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        TextArea textArea = paneController.getTextArea();
        textArea.setText("   " + stringWriter);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public void configureWarn(String text, Runnable clickTextEvent) {
        AlertWarnController paneController = configureAlertWindow(Type.WARN, text);

        if (clickTextEvent == null) return;
        paneController.getTextLabel().setOnMouseClicked(e -> clickTextEvent.run());
    }

    public void configureInfo(String text) {
        configureAlertWindow(Type.INFO, text);
    }

    public void configureConfirmation(String text) {
        configureAlertWindow(Type.CONFIRMATION, text);
    }

    private <T extends AlertWindow> T configureAlertWindow(Type type, String text) {
        PaneManager paneManager = FlectoneMix.getApp().getPaneManager();

        FXMLLoader fxmlLoader = paneManager.addLoader(type.getPaneType());
        Pane pane = paneManager.loadPane(type.getPaneType());

        T paneController = fxmlLoader.getController();
        this.alertWindow = paneController;
        paneController.getTextLabel().setText(text);

        Scene scene = new Scene(pane, 420, Region.USE_COMPUTED_SIZE);
        scene.getStylesheets().add(FlectoneMix.getApp().getConfig().getThemePath());
        stage.setScene(scene);
        stage.getIcons().add(new Image(type.getIconPath()));
        stage.setTitle(FlectoneMix.getApp().getConfig().getLocaleString(type.getTitleKey()));
        stage.initModality(Modality.APPLICATION_MODAL);

        paneController.setStage(stage);

        return paneController;
    }

    @Getter
    public enum Type {
        EXCEPTION("alert_exception", PaneType.EXCEPTION, "/net/flectone/mix/images/error.png"),
        WARN("alert_warn", PaneType.WARN, "/net/flectone/mix/images/warn.png"),
        INFO("alert_info", PaneType.INFO, "/net/flectone/mix/images/info.png"),
        CONFIRMATION("alert_confirmation", PaneType.CONFIRMATION, "/net/flectone/mix/images/info.png");

        private final String type;
        private final PaneType paneType;
        private final String iconPath;

        Type(String type, PaneType paneType, String iconPath) {
            this.type = type;
            this.paneType = paneType;
            this.iconPath = iconPath;
        }

        public String getTitleKey() {
            return "alert." + type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
