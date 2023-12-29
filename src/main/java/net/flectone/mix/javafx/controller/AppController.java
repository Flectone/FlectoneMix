package net.flectone.mix.javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.component.PaneType;
import net.flectone.mix.javafx.controller.tab.TabSetting;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public class AppController implements Initializable {

    @Setter
    @FXML
    private Pane leftPanel;

    @FXML
    private Pane settingPanel;

    @FXML
    private Pane searchPanel;

    @FXML
    private Pane componentPanel;

    @Setter
    private PaneType selectedTab;
    private TabSetting tabSettingController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url == null) return;

        setPane(leftPanel, PaneType.LEFT, true);
        setPane(searchPanel, PaneType.SEARCH, true);
        setPane(componentPanel, PaneType.COMPONENTS, true);

        if (selectedTab == null) {
            setTab(PaneType.OPTIMIZATION);
        }
    }

    public void setTab(@NonNull PaneType paneType) {
        selectedTab = paneType;
        setPane(settingPanel, selectedTab, false);

        FlectoneMix.getApp().getThreadPool().execute(() -> {
            tabSettingController = FlectoneMix.getApp().getPaneManager().getLoader(selectedTab).getController();
            tabSettingController.updateFilter();
        });
    }

    public void setPane(Pane mainPane, PaneType paneType, boolean replace) {
        FlectoneMix.getApp().getPaneManager().addPane(paneType, replace);
        mainPane.getChildren().clear();

        Pane pane = FlectoneMix.getApp().getPaneManager().loadPane(paneType, replace);
        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setBottomAnchor(pane, 0.0);
        mainPane.getChildren().add(pane);
    }
}
