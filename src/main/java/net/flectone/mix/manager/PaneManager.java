package net.flectone.mix.manager;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.NonNull;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.component.FAlert;
import net.flectone.mix.javafx.loader.CachingClassLoader;
import net.flectone.mix.javafx.component.PaneType;
import net.flectone.mix.javafx.controller.WindowController;

import java.io.IOException;
import java.util.HashMap;

public class PaneManager {

    private final HashMap<PaneType, FXMLLoader> fxmlLoaderHashMap = new HashMap<>();

    public static ClassLoader cachingClassLoader = new CachingClassLoader(FXMLLoader.getDefaultClassLoader());

    @Getter
    private final Scene scene;

    public PaneManager(int width, int height) {
        addLoader(PaneType.WINDOW);

        scene = new Scene(loadPane(PaneType.WINDOW), width, height);
        scene.getStylesheets().add(FlectoneMix.getApp().getConfig().getThemePath());

        addLoader(PaneType.AUTH);
        activate(PaneType.AUTH);
    }

    public void reloadPanes() {
        fxmlLoaderHashMap.values().forEach(fxmlLoader ->
                ((Initializable) fxmlLoader.getController()).initialize(null, null));
    }

    public FXMLLoader getLoader(PaneType paneType) {
        return fxmlLoaderHashMap.get(paneType);
    }

    public FXMLLoader addLoader(PaneType paneType){
        return addLoader(paneType, true);
    }

    public FXMLLoader addLoader(@NonNull PaneType paneType, boolean replace) {
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
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setClassLoader(cachingClassLoader);
        fxmlLoader.setLocation(getClass().getResource(name));
        return fxmlLoader;
    }

    public Pane loadPane(PaneType paneType) {
        return loadPane(paneType, true);
    }

    public Pane loadPane(PaneType paneType, boolean replace) {
        try {
            FXMLLoader fxmlLoader = fxmlLoaderHashMap.get(paneType);

            if (!replace && fxmlLoader.getRoot() != null) return fxmlLoader.getRoot();
            if (fxmlLoader.getRoot() != null) fxmlLoader = addLoader(paneType);
            Pane pane = fxmlLoader.load();
            pane.getStylesheets().add(FlectoneMix.getApp().getConfig().getThemePath());

            return pane;

        } catch (IOException e) {
            new FAlert(FAlert.Type.EXCEPTION, e).show();
            return null;
        }
    }
}
