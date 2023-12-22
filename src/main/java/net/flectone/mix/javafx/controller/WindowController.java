package net.flectone.mix.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import net.flectone.mix.javafx.FlectoneMix;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowController implements UndecoratedWindow {

    @FXML
    private Pane topBarPanel;

    @FXML
    private Pane contentPanel;

    @FXML
    private Pane windowPanel;

    public void setContentPanel(Pane pane) {
        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setBottomAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);

        contentPanel.getChildren().setAll(pane);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url == null) return;

        checkUndecoratedSupport();
    }

    public void checkUndecoratedSupport() {
        if (FlectoneMix.getApp().getConfig().isUsedUndecoratedWindow()) return;

        windowPanel.getChildren().remove(topBarPanel);
        AnchorPane.setTopAnchor(contentPanel, 0.0);
        AnchorPane.setBottomAnchor(contentPanel, 0.0);
    }
}
