package net.flectone.mix.javafx.builder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class ComponentBuilder {

    private static final CustomThreadPool BUILD_POOL = new CustomThreadPool(1);

    private final List<Component> componentList = new CopyOnWriteArrayList<>();
    private final Map<Integer, Pane> paneMap = new ConcurrentHashMap<>();
    private final ObservableList<Node> node;

    private String apiUrl;

    public ComponentBuilder(@NonNull ObservableList<Node> node) {
        this.node = node;
    }

    public void build(@NonNull String type, @NonNull String version) {
        DiscordUser discordUser = FlectoneMix.getApp().getDiscordUser();
        setApiUrl(String.format("https://mix.flectone.net/api/components?id=%s&token=%s&locale=%s&type=%s&version=%s",
                discordUser.id(), discordUser.token(), FlectoneMix.getApp().getConfig().getLanguage(), type, version));
    }

    public void search(@NonNull String text) {
        String finalText = text.toLowerCase();
        hideAllPanes();
        BUILD_POOL.execute(() -> componentList.forEach(component -> {
            if (paneMap.get(component.id()) == null) return;
            boolean isIt = componentContainsText(component, finalText);
            updatePaneVisibility(component, isIt);
        }));
        updateSelectAllCheckBox();
    }

    public void updateSelectAllCheckBox() {
        long countVisible = countVisibleComponents();
        long countSelected = countSelectedComponents();
        boolean selected = countSelected == countVisible && countVisible != 0;
        updateSearchPanelCheckBox(selected);
    }

    public void clearAll() {
        Platform.runLater(node::clear);
    }

    public void setApiUrl(@NonNull String apiUrl) {
        this.apiUrl = apiUrl.toLowerCase();
        fill();
    }

    public void selectAll(boolean selected) {
        selectAllPanes(selected);
    }

    public void selectAll(@NonNull List<String> filter, boolean selected) {
        selectFilteredPanes(filter, selected);
    }

    public void fill() {
        clearBuildPool();
        BUILD_POOL.execute(() -> {
            try {
                clearNodes();
                loadComponents();
            } catch (IOException e) {
                FAlert.showException(e, e.getLocalizedMessage());
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            updateSelectAllCheckBox();
        });
    }

    private void hideAllPanes() {
        paneMap.values().forEach(pane -> pane.setVisible(false));
        Platform.runLater(node::clear);
    }

    private boolean componentContainsText(Component component, String searchText) {
        return component.description().toLowerCase().contains(searchText)
                || component.name().toLowerCase().contains(searchText);
    }

    private void updatePaneVisibility(Component component, boolean isVisible) {
        Pane pane = paneMap.get(component.id());
        if (pane == null) return;

        Platform.runLater(() -> {
            pane.setVisible(isVisible);
            if (isVisible && !node.contains(pane)) {
                node.add(pane);
            }
        });
    }

    private long countVisibleComponents() {
        return componentList.stream()
                .filter(component -> isPaneVisible(component.id()))
                .count();
    }

    private long countSelectedComponents() {
        return componentList.stream()
                .filter(component -> isPaneVisible(component.id()) && isCheckBoxSelected(component.id()))
                .count();
    }

    private void updateSearchPanelCheckBox(boolean selected) {
        SearchPanelController searchPanelController = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.SEARCH).getController();
        searchPanelController.setSelectedAllCheckBox(selected);
    }

    private void clearBuildPool() {
        if (!BUILD_POOL.getRunnableQueue().isEmpty()) {
            BUILD_POOL.getRunnableQueue().clear();
            hideAllPanes();
        }
    }

    private void clearNodes() {
        Platform.runLater(node::clear);
    }

    private void loadComponents() throws IOException {
        System.out.println(apiUrl);
        List<Component> components = getComponentsFromApi(apiUrl);
        paneMap.clear();
        componentList.clear();
        componentList.addAll(components);
        componentList.sort(Comparator.comparing(Component::id));
        addComponentsToNodes();
    }

    private void addComponentsToNodes() {
        componentList.forEach(this::addPaneToNode);
    }

    private void addPaneToNode(Component component) {
        FXMLLoader fxmlLoader = FlectoneMix.getApp().getPaneManager().addPane(PaneType.COMPONENT);
        Pane pane = FlectoneMix.getApp().getPaneManager().loadPane(PaneType.COMPONENT);
        Platform.runLater(() -> node.add(pane));

        ComponentController componentController = fxmlLoader.getController();
        componentController.setComponent(component);
        componentController.initComponent();

        updateCheckBoxForOptimization(componentController, component);

        componentController.getCheckBox().selectedProperty().addListener(e -> updateSelectAllCheckBox());

        paneMap.put(component.id(), pane);
    }

    private void updateCheckBoxForOptimization(ComponentController componentController, Component component) {
        if (((AppController) FlectoneMix.getApp().getPaneManager().getLoader(PaneType.APP).getController()).getSelectedTab() != PaneType.OPTIMIZATION) return;

        componentController.getCheckBox().setSelected(!FlectoneMix.getApp().getConfig().getStringList("blacklist.optimization").contains(component.key()));
    }

    private boolean isPaneVisible(int componentId) {
        Pane pane = paneMap.get(componentId);
        return pane != null && pane.isVisible();
    }

    private boolean isCheckBoxSelected(int componentId) {
        Pane pane = paneMap.get(componentId);
        return pane != null && pane.isVisible() && ((CheckBox) ((Pane) pane.getChildren().get(0)).getChildren().get(4)).isSelected();
    }

    private void selectAllPanes(boolean selected) {
        paneMap.values().stream()
                .filter(Node::isVisible)
                .forEach(pane ->
                        ((CheckBox) ((Pane) pane.getChildren().get(0)).getChildren().get(4))
                                .setSelected(selected));
    }

    private void selectFilteredPanes(List<String> filter, boolean selected) {
        componentList.stream()
                .filter(component -> filter.contains(component.key()))
                .forEach(component ->
                        ((CheckBox) ((Pane) paneMap.get(component.id()).getChildren().get(0)).getChildren().get(4))
                                .setSelected(selected));
    }

    private List<Component> parseJsonResponse(String jsonResponse) {
        List<Component> components = new ArrayList<>();
        Gson gson = new Gson();
        JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();
        jsonArray.forEach(jsonElement -> components.add(gson.fromJson(jsonElement, Component.class)));
        return components;
    }

    private List<Component> getComponentsFromApi(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return parseJsonResponse(response.toString());
            }
        }
        return new ArrayList<>();
    }

    public void setVisibleAll(boolean visible) {
        paneMap.values().forEach(pane -> pane.setVisible(visible));
    }
}
