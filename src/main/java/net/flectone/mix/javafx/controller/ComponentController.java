package net.flectone.mix.javafx.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.model.Component;
import net.flectone.mix.util.WebUtil;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public class ComponentController implements Initializable {

    @Setter
    private Component component;

    @FXML
    private Pane paneBackground;

    @FXML
    private Rectangle rectangleIcon;

    @FXML
    private Label labelName;

    @FXML
    private Label labelDescription;

    @FXML
    private Label labelAuthor;

    @FXML
    private JFXCheckBox checkBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url == null) return;
    }

    public void initComponent() {
        FlectoneMix.getApp().getThreadPool().execute(() -> {
            try {
                Image image = new Image(component.icon());
                if (!image.isError()) {
                    ImagePattern imagePattern = new ImagePattern(image);

                    Platform.runLater(() -> {
                        rectangleIcon.setFill(imagePattern);
                        rectangleIcon.setVisible(true);
                    });
                }
            } catch (IllegalArgumentException ignored) {}
        });

        Platform.runLater(() -> {
            labelName.setText(component.name());
            labelDescription.setText(component.description());
            labelAuthor.setText(FlectoneMix.getApp().getConfig().getLocaleString("components.author") + component.author());
        });
    }

    public void clickEvent() {
        checkBox.setSelected(!checkBox.isSelected());
    }

    public void openSource() {
        WebUtil.openUrl(component.source());
    }
}