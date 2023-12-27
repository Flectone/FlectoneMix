package net.flectone.mix.javafx.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.model.Component;
import net.flectone.mix.util.StringUtil;
import net.flectone.mix.util.WebUtil;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

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

    @FXML
    private Label labelDownload;

    @FXML
    private ImageView star1Image;

    @FXML
    private ImageView star2Image;

    @FXML
    private ImageView star3Image;

    @FXML
    private ImageView star4Image;

    @FXML
    private ImageView star5Image;

    private static final ConcurrentHashMap<String, ImagePattern> IMAGES_MAP = new ConcurrentHashMap<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url == null) return;
    }

    public void initComponent() {
        FlectoneMix.getApp().getThreadPool().execute(() -> {
            try {
                ImagePattern imagePattern = IMAGES_MAP.computeIfAbsent(component.icon(), key -> {
                    Image image = new Image(component.icon());
                    return (!image.isError()) ? new ImagePattern(image) : null;
                });

                if (imagePattern != null) {
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
            updateDownloads(component.downloads());

            component.setController(this);
            setRating(component.avgRating());
        });

        paneBackground.setOnMousePressed(e -> {
            if (e.getPickResult().getIntersectedNode() instanceof ImageView) return;
            if (e.getPickResult().getIntersectedNode() instanceof Rectangle) return;
            clickEvent();
        });

        configureStarImage(star1Image, 1);
        configureStarImage(star2Image, 2);
        configureStarImage(star3Image, 3);
        configureStarImage(star4Image, 4);
        configureStarImage(star5Image, 5);
    }

    public void configureStarImage(ImageView imageView, int value) {
        imageView.setOnMouseEntered(e -> setRating(value));
        imageView.setOnMouseExited(e -> setRating(component.avgRating()));

        imageView.setOnMousePressed(e -> FlectoneMix.getApp().getThreadPool().execute(() -> {
            component.setRatings(value);
            setRating(component.avgRating());
        }));
    }

    public void setRating(double value) {
        Platform.runLater(() -> {
            setStarStyle(star5Image, value, 5);
            setStarStyle(star4Image, value, 4);
            setStarStyle(star3Image, value, 3);
            setStarStyle(star2Image, value, 2);
            setStarStyle(star1Image, value, 1);
        });
    }

    private void setStarStyle(ImageView starImage, double value, int starNumber) {
        if (value >= starNumber) {
            starImage.setStyle("-fx-image: -fx-image-full-star");
            return;
        }

        if (value >= starNumber - 0.5) {
            starImage.setStyle("-fx-image: -fx-image-half-star");
            return;
        }

        starImage.setStyle("-fx-image: -fx-image-empty-star");
    }


    public void updateDownloads(int count) {
        Platform.runLater(() -> {
            String string = StringUtil.convertIntToString(count);
            labelDownload.setText(string);
        });
    }

    public void clickEvent() {
        checkBox.setSelected(!checkBox.isSelected());
    }

    public void openSource() {
        WebUtil.openUrl(component.source());
    }
}
