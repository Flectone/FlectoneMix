package net.flectone.mix.javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.Getter;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.manager.ConfigManager;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public class SearchPanelController implements Initializable {

    @FXML
    private Pane searchPanel;

    @FXML
    private Pane adPanel;

    @FXML
    private TextField searchField;

    @FXML
    private CheckBox selectAllCheckBox;

    private boolean skipCheckBoxListener;

    private boolean countSelectCheckBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConfigManager config = FlectoneMix.getApp().getConfig();
        searchField.setPromptText(config.getLocaleString("search.prompt"));
        selectAllCheckBox.setText(config.getLocaleString("search.checkbox.select-all"));

        if (url == null) return;

        searchField.textProperty().addListener(e -> {
            ((AppController) FlectoneMix.getApp().getPaneManager().getLoader(PaneType.APP).getController())
                    .getTabSettingController()
                    .updateSearch(searchField.getText());

            setSelectedAllCheckBox(false);
        });

        selectAllCheckBox.selectedProperty().addListener(e -> {
            if (skipCheckBoxListener) return;
            ((AppController) FlectoneMix.getApp().getPaneManager().getLoader(PaneType.APP).getController())
                    .getTabSettingController()
                    .selectAll(selectAllCheckBox.isSelected());
        });

        configureAdPanel();
    }

    private void configureAdPanel() {
        FlectoneMix.getApp().getPaneManager().addPane(PaneType.AD);
        adPanel.getChildren().clear();

        Pane pane = FlectoneMix.getApp().getPaneManager().loadPane(PaneType.AD);
        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setBottomAnchor(pane, 0.0);
        adPanel.getChildren().add(pane);
    }

    public void setSelectedAllCheckBox(boolean selected) {
        if (selectAllCheckBox.isSelected() == selected) return;

        skipCheckBoxListener = true;
        selectAllCheckBox.setSelected(selected);
        skipCheckBoxListener = false;
    }
}
