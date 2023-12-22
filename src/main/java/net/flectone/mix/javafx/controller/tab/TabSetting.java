package net.flectone.mix.javafx.controller.tab;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.PaneType;
import net.flectone.mix.javafx.builder.ComponentBuilder;
import net.flectone.mix.javafx.component.FAlert;
import net.flectone.mix.javafx.controller.AppController;
import net.flectone.mix.javafx.controller.ComponentPanelController;
import net.flectone.mix.javafx.transition.FToolTip;
import net.flectone.mix.model.Component;
import net.flectone.mix.server.FileDownloader;
import net.flectone.mix.server.FileUpdater;

import javax.imageio.ImageIO;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public abstract class TabSetting implements Initializable {

    private static final String FABRIC_INSTALLER_URL = "https://mix.flectone.net/files/fabric.jar";
    private static final String QUILT_INSTALLER_URL = "https://mix.flectone.net/files/quilt.jar";

    private static final String SCHEMATICS_OUTPUT = "schematics" + File.separator;
    private static final String DATAPACKS_OUTPUT = "datapacks" + File.separator;

    private String outputDirectory;
    private Label progressLabel;
    private ProgressBar progressBar;

    private Control lastPressedInstallControl;

    private TabFilterAction filterAction;

    private List<Task<Void>> downloadTaskList = new ArrayList<>();

    public void updateSearch(String text) {
        FlectoneMix.getApp().getThreadPool().execute(() -> {
            ComponentPanelController controller = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.COMPONENTS).getController();
            controller.getComponentBuilder().search(text);
        });
    }

    public void updateFilter() {
        if (filterAction == null) return;
        filterAction.apply();
    }

    public void selectAll(boolean selected) {
        FlectoneMix.getApp().getThreadPool().execute(() -> {
            ComponentPanelController controller = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.COMPONENTS).getController();
            controller.getComponentBuilder().selectAll(selected);
        });
    }

    public void settingComboBox(@NonNull ComboBox<String> comboBox, @NonNull String key) {
        Platform.runLater(() -> {
            List<String> list = FlectoneMix.getApp().getConfig().getStringList(key);
            comboBox.setItems(FXCollections.observableArrayList(list));
            comboBox.getSelectionModel().selectFirst();
            comboBox.getSelectionModel().selectedItemProperty()
                    .addListener(e -> updateFilter());
        });
    }

    public void settingCheckBox(@NonNull CheckBox checkBox, @NonNull String key) {
        checkBox.setText(getLocaleString(key));
        new FToolTip(checkBox, key);
    }

    public String getLocaleString(@NonNull String key) {
        return FlectoneMix.getApp().getConfig().getLocaleString(key);
    }

    public void updateComponents(Control control) {
        processComponents(control, new FileUpdaterFactory());
    }

    public void downloadComponents(Control control) {
        processComponents(control, new FileDownloaderFactory());
    }

    private void processComponents(Control control, TaskFactory taskFactory) {
        setupProgressUI(control);

        ComponentPanelController panelController = FlectoneMix.getApp().getPaneManager().getLoader(PaneType.COMPONENTS).getController();
        ComponentBuilder componentBuilder = panelController.getComponentBuilder();

        String minecraftFolder = FlectoneMix.getApp().getConfig().getMinecraftFolder();
        String finalOutputDirectory = minecraftFolder + outputDirectory;
        downloadTaskList.addAll(
                componentBuilder.getPaneMap()
                        .entrySet()
                        .stream()
                        .filter(entry -> {
                            Pane value = entry.getValue();
                            return value.isVisible() && ((CheckBox) ((Pane) value.getChildren().get(0)).getChildren().get(4)).isSelected();
                        })
                        .map(entry -> {
                            int componentId = entry.getKey();
                            return componentBuilder.getComponentList()
                                    .stream()
                                    .filter(component -> component.id() == componentId)
                                    .findFirst()
                                    .map(component -> {
                                        PaneType paneType = ((AppController) FlectoneMix.getApp().getPaneManager().getLoader(PaneType.APP).getController()).getSelectedTab();

                                        return switch (paneType) {
                                            case FARMS -> {
                                                if (component.key().startsWith("litematic"))
                                                    yield taskFactory.createTask(component, minecraftFolder + SCHEMATICS_OUTPUT, true);
                                                yield taskFactory.createTask(component, finalOutputDirectory + component.name() + File.separator, true);
                                            }
                                            case DATAPACKS -> taskFactory.createTask(component, finalOutputDirectory + DATAPACKS_OUTPUT, false);

                                            default -> taskFactory.createTask(component, finalOutputDirectory, false);
                                        };

                                    })
                                    .orElse(null);
                        })
                        .filter(Objects::nonNull)
                        .toList()
        );

        if (downloadTaskList.isEmpty()) {
            FAlert.showWarn(getLocaleString("alert.warn.message.empty"));
            clearDownloadProgress();
            return;
        }

        executeDownloadTasks();
    }

    public void clearFolder(@NonNull String extension) {
        File folder = new File(FlectoneMix.getApp().getConfig().getMinecraftFolder() + outputDirectory);
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(extension)) continue;

            file.delete();
        }
    }

    public void downloadFile(@NonNull String output, @NonNull String url) {
        File outputFile = new File(FlectoneMix.getApp().getConfig().getMinecraftFolder() + output);
        downloadTaskList.add(new FileDownloader(url, outputFile));
    }

    public void downloadProfile(String loaderType, String minecraftVersion){
        String finalLoaderType = loaderType.toLowerCase();
        downloadTaskList.add(new Task<>() {
            @Override
            protected Void call() throws Exception {
                boolean isQuilt = finalLoaderType.equals("quilt");

                String fileName = finalLoaderType + "-installer.jar";

                URL url = new URL(isQuilt ? QUILT_INSTALLER_URL : FABRIC_INSTALLER_URL);
                String outputPathString = FlectoneMix.getApp().getConfig().getFlectonemixFolder() + fileName;
                Path outputPath = Paths.get(outputPathString);
                Files.createDirectories(outputPath.getParent());

                try (InputStream in = url.openStream()) {
                    Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
                }

                String mcDir = FlectoneMix.getApp().getConfig().getMinecraftFolder();

                String command = isQuilt
                        ? String.format("java -jar %s%s install client %s --install-dir=%s --no-profile", "", outputPathString, minecraftVersion, mcDir)
                        : String.format("java -jar %s%s client -dir %s -noprofile -mcversion %s", "", outputPathString, mcDir, minecraftVersion);


                System.out.println(command);
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();

                File file = new File(mcDir + "launcher_profiles.json");
                if (!file.exists()) {
                    Platform.runLater(() ->
                            FAlert.showWarn(getLocaleString("alert.warn.message.null-profiles")));
                    return null;
                }

                File folderMinecraftVersions = new File(Paths.get(mcDir, "versions").toString());

                String loaderName = Arrays.stream(folderMinecraftVersions.listFiles(fileFilter -> {
                    String fileLoaderName = fileFilter.getName();

                    if (!fileLoaderName.contains(finalLoaderType.toLowerCase() + "-loader")) return false;
                    return fileLoaderName.contains(minecraftVersion);
                })).max(Comparator.naturalOrder()).map(File::getName).orElse(minecraftVersion);

                System.out.println(loaderName);

                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                FileReader reader = new FileReader(mcDir + "launcher_profiles.json");
                Map<String, Object> json = gson.fromJson(reader, type);
                Map<String, Object> profiles = (Map<String, Object>) json.get("profiles");
                Map<String, Object> customProfile = new LinkedHashMap<>();
                customProfile.put("name", "FlectoneMix " + finalLoaderType + " " + minecraftVersion);
                customProfile.put("lastVersionId", loaderName);
                customProfile.put("javaArgs", FlectoneMix.getApp().getConfig().getString("profile.javaArgs"));
                customProfile.put("lastUsed", LocalDateTime.now().toString());

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(ImageIO.read(TabSetting.class.getClassLoader().getResource("net/flectone/mix/images/flectone.png")), "png", outputStream);
                String icon = Base64.getEncoder().encodeToString(outputStream.toByteArray());
                customProfile.put("icon", "data:image/png;base64," + icon);

                Map<String, Object> customLoader = new LinkedHashMap<>();
                customLoader.put("id", "fabric");
                customProfile.put("loader", customLoader);
                profiles.put("Flectone " + finalLoaderType + " " + minecraftVersion, customProfile);
                json.put("selectedProfile", "Flectone " + finalLoaderType + " " + minecraftVersion);

                FileWriter writer = new FileWriter(Paths.get(mcDir + "launcher_profiles.json").toString());
                gson.toJson(json, writer);
                writer.close();

                return null;
            }
        });
    }

    private interface TaskFactory {
        Task<Void> createTask(Component component, String outputDirectory, boolean unzip);
    }

    private static class FileUpdaterFactory implements TaskFactory {
        @Override
        public Task<Void> createTask(Component component, String outputDirectory, boolean unzip) {
            return new FileUpdater(component, outputDirectory);
        }
    }

    private static class FileDownloaderFactory implements TaskFactory {
        @Override
        public Task<Void> createTask(Component component, String outputDirectory, boolean unzip) {
            return new FileDownloader(component, outputDirectory, unzip);
        }
    }

    private void setupProgressUI(Control control) {
        progressBar.setVisible(true);
        progressLabel.setVisible(true);
        control.setVisible(false);
        control.requestFocus();
        lastPressedInstallControl = control;
        progressBar.setLayoutY(control.getLayoutY());
        progressBar.setLayoutX(control.getLayoutX());
        progressLabel.setLayoutY(control.getLayoutY());
        progressLabel.setLayoutX(control.getLayoutX());
        progressBar.setProgress(0);
    }

    private void executeDownloadTasks() {
        AtomicInteger completedTasks = new AtomicInteger(0);

        downloadTaskList.forEach(voidTask -> {

            FlectoneMix.getApp().getThreadPool().execute(() -> {
                try {
                    voidTask.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    checkDownloadProgress(completedTasks);
                }
            });

            voidTask.setOnSucceeded(event -> Platform.runLater(() -> {
                checkDownloadProgress(completedTasks);
            }));
        });
    }

    private void checkDownloadProgress(AtomicInteger completedTasks) {
        progressBar.setProgress((double) completedTasks.incrementAndGet() / downloadTaskList.size());

        if (completedTasks.get() == downloadTaskList.size()) {
            String successMessage = getLocaleString("alert.info.message.successful").replace("<installation_path>",
                    FlectoneMix.getApp().getConfig().getMinecraftFolder() + outputDirectory);
            FAlert.showInfo(successMessage);
            clearDownloadProgress();
        }
    }

    private void clearDownloadProgress() {
        lastPressedInstallControl.setVisible(true);
        lastPressedInstallControl.requestFocus();
        progressBar.setVisible(false);
        progressLabel.setVisible(false);
        downloadTaskList.clear();
    }

    @FunctionalInterface
    protected interface TabFilterAction { void apply(); }
}

