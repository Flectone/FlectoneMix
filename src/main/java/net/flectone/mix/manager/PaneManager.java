package net.flectone.mix.manager;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.NonNull;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.javafx.component.FAlert;
import net.flectone.mix.javafx.controller.WindowController;

import java.io.IOException;
import java.util.HashMap;

public class PaneManager {

    private final HashMap<PaneType, FXMLLoader> fxmlLoaderHashMap = new HashMap<>();

    @Getter
    private Scene scene;

    public PaneManager(int width, int height) {
        addPane(PaneType.WINDOW);

        scene = new Scene(loadPane(PaneType.WINDOW), width, height);
        scene.getStylesheets().add(FlectoneMix.getApp().getConfig().getThemePath());

        addPane(PaneType.AUTH);
        activate(PaneType.AUTH);
    }

    public void reloadPanes() {
        fxmlLoaderHashMap.values().forEach(fxmlLoader ->{
            ((Initializable) fxmlLoader.getController()).initialize(null, null);
        });
    }

    public FXMLLoader getLoader(PaneType paneType) {
        return fxmlLoaderHashMap.get(paneType);
    }

    public FXMLLoader addPane(PaneType paneType){
        return addPane(paneType, true);
    }

    public FXMLLoader addPane(@NonNull PaneType paneType, boolean replace) {
        if (!replace && fxmlLoaderHashMap.get(paneType) != null) return fxmlLoaderHashMap.get(paneType);

        FXMLLoader fxmlLoader = getFXMLLoader("/net/flectone/mix/fxml/" + paneType + ".fxml");
        fxmlLoaderHashMap.put(paneType, fxmlLoader);
        return fxmlLoader;
    }

    public void removeScreen(PaneType paneType){
        fxmlLoaderHashMap.remove(paneType);
    }

    public void activate(PaneType paneType){
        WindowController windowController = fxmlLoaderHashMap.get(PaneType.WINDOW).getController();

        windowController.setContentPanel(loadPane(paneType));
    }

    protected FXMLLoader getFXMLLoader(String name) {
        return new FXMLLoader(getClass().getResource(name));
    }

    public Pane loadPane(PaneType paneType) {
        return loadPane(paneType, true);
    }

    public Pane loadPane(PaneType paneType, boolean replace) {
        try {
            FXMLLoader fxmlLoader = fxmlLoaderHashMap.get(paneType);

            if (!replace && fxmlLoader.getRoot() != null) return fxmlLoader.getRoot();
            if (fxmlLoader.getRoot() != null) fxmlLoader = addPane(paneType);

            Pane pane = fxmlLoader.load();
            pane.getStylesheets().add(FlectoneMix.getApp().getConfig().getThemePath());

            return pane;

        } catch (IOException e) {
            FAlert.showException(e, e.getLocalizedMessage());
            return null;
        }
    }
}
