package net.flectone.mix.javafx.builder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.NonNull;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.javafx.component.FAlert;
import net.flectone.mix.javafx.controller.AppController;
import net.flectone.mix.javafx.controller.ComponentController;
import net.flectone.mix.javafx.controller.SearchPanelController;
import net.flectone.mix.model.Component;
import net.flectone.mix.model.DiscordUser;
import net.flectone.mix.thread.CustomThreadPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Getter
public class ComponentBuilder {

    private static final CustomThreadPool BUILD_POOL = new CustomThreadPool(1);

    private final List<Component> componentList = new ArrayList<>();
    private final HashMap<Integer, Pane> paneMap = new HashMap<>();

    private final ObservableList<Node> node;

    private String apiUrl;

    public ComponentBuilder(@NonNull ObservableList<Node> node){
        this.node = node;
    }


    public void build(@NonNull String type, @NonNull String version) {
        System.out.println("build=" + Thread.currentThread());

        DiscordUser discordUser = FlectoneMix.getApp().getDiscordUser();

        setApiUrl("https://mix.flectone.net/api/components?id=" +
                discordUser.id() +
                "&token=" +
                discordUser.token() +
                "&locale=" +
                FlectoneMix.getApp().getConfig().getLanguage() +
                "&type=" +
                type
                +"&version="
                + version);
    }

    public void search(@NonNull String text) {
        String finalText = text.toLowerCase();

        paneMap.values().forEach(pane -> pane.setVisible(false));
        Platform.runLater(node::clear);

        BUILD_POOL.execute(() -> componentList.forEach(component -> {
            if (paneMap.get(component.id()) == null) return;

            boolean isIt = component.description().toLowerCase().contains(finalText)
                    || component.name().toLowerCase().contains(finalText)
                    || component.version().toLowerCase().contains(finalText)
                    || component.type().toLowerCase().contains(finalText);

            paneMap.get(component.id()).setVisible(isIt);

            if (!isIt) return;

            Platform.runLater(() -> {
                if (paneMap.get(component.id()) == null) return;
                if (node.contains(paneMap.get(component.id()))) return;
                node.add(paneMap.get(component.id()));
            });
        }));

        updateSelectAllCheckBox();
    }

    public void updateSelectAllCheckBox() {

        int countVisible = (int) componentList.stream().filter(component -> {
            Pane pane = paneMap.get(component.id());
            return pane != null && pane.isVisible();
        }).count();

        int countSelected = (int) componentList.stream().filter(component -> {
            Pane pane = paneMap.get(component.id());
            return pane != null && pane.isVisible() && ((CheckBox) ((Pane) pane.getChildren().get(0)).getChildren().get(4)).isSelected();
        }).count();

        boolean selected = countSelected == countVisible && countVisible != 0;

        SearchPanelController searchPanelController = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.SEARCH).getController();
        searchPanelController.setSelectedAllCheckBox(selected);
    }

    public void clearAll() {
        Platform.runLater(node::clear);
    }

    public void setApiUrl(@NonNull String apiUrl) {
        this.apiUrl = apiUrl.toLowerCase();
        fill();
    }

    public void selectAll(boolean selected) {
        paneMap.values().stream().filter(Node::isVisible).forEach(pane ->
                ((CheckBox) ((Pane) pane.getChildren().get(0)).getChildren().get(4)).setSelected(selected));
    }

    public void selectAll(@NonNull List<String> filter, boolean selected) {
        componentList.stream().filter(component -> filter.contains(component.key())).forEach(component ->
                ((CheckBox) ((Pane) paneMap.get(component.id()).getChildren().get(0)).getChildren().get(4)).setSelected(selected));
    }

    public void fill() {
        if (!BUILD_POOL.getRunnableQueue().isEmpty()) {
            BUILD_POOL.getRunnableQueue().clear();
            paneMap.values().forEach(pane -> pane.setVisible(false));
            Platform.runLater(node::clear);
        }
        BUILD_POOL.execute(() -> {
            System.out.println(apiUrl);

            Platform.runLater(node::clear);

            try {
                paneMap.clear();
                componentList.clear();
                componentList.addAll(getComponentsFromApi(apiUrl));

                componentList.sort(Comparator.comparing(Component::id));

                componentList.forEach(component -> {
                    FXMLLoader fxmlLoader = FlectoneMix.getApp().getPaneManager().addPane(PaneType.COMPONENT);
                    Pane pane = FlectoneMix.getApp().getPaneManager().loadPane(PaneType.COMPONENT);

                    Platform.runLater(() -> node.add(pane));

                    ComponentController componentController = fxmlLoader.getController();
                    componentController.setComponent(component);
                    componentController.initComponent();

                    if (((AppController) FlectoneMix.getApp().getPaneManager().getLoader(PaneType.APP).getController()).getSelectedTab() == PaneType.OPTIMIZATION) {
                       componentController.getCheckBox().setSelected(!FlectoneMix.getApp().getConfig().getStringList("blacklist.optimization").contains(component.key()));
                    }

                    componentController.getCheckBox().selectedProperty().addListener(e -> updateSelectAllCheckBox());

                    paneMap.put(component.id(), pane);
                });

            } catch (IOException e) {
                FAlert.showException(e, e.getLocalizedMessage());
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            updateSelectAllCheckBox();
        });
    }

    public List<Component> parseJsonResponse(String jsonResponse) {
        List<Component> components = new ArrayList<>();

        Gson gson = new Gson();
        JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();

        for (JsonElement jsonElement : jsonArray) {
            Component component = gson.fromJson(jsonElement, Component.class);
            components.add(component);
        }

        return components;
    }

    public List<Component> getComponentsFromApi(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(),
                            StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            return parseJsonResponse(response.toString());
        }

        return new ArrayList<>();
    }

    public void setVisibleAll(boolean visible) {
        paneMap.values().forEach(pane -> pane.setVisible(visible));
    }
}
