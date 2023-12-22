package net.flectone.mix.javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.javafx.builder.ComponentBuilder;
import net.flectone.mix.javafx.transition.SmoothScrolling;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public class ComponentPanelController implements Initializable {

    @FXML
    private Pane componentPanel;

    @FXML
    private Pane TEST;

    @FXML
    private VBox testBox;

    @FXML
    private ScrollPane scrollPane;

    private ComponentBuilder componentBuilder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url == null) return;

        new SmoothScrolling(scrollPane);
        scrollPane.setFitToWidth(true);

        this.componentBuilder = new ComponentBuilder(testBox.getChildren());
    }

    public void setVisibleComponents(boolean visible) {
        componentBuilder.setVisibleAll(visible);
    }

    public void setVisibleSearchPanel(boolean visible) {
        SearchPanelController searchPanelController = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.SEARCH).getController();
        searchPanelController.getSearchPanel().setVisible(visible);
    }
}
