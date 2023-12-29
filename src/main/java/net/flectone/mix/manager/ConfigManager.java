package net.flectone.mix.manager;

import lombok.Getter;
import lombok.NonNull;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.component.FAlert;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;

@Getter
public class ConfigManager {

    @Getter
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String SYSTEM_LANGUAGE = System.getProperty("user.language").toLowerCase();

    private String flectonemixFolder;

    private String minecraftFolder;

    private String runningPath;

    private String version;

    private String language;

    private String theme;

    private boolean isPossiblyDecorated;

    private static final HashMap<String, Object> properties = new HashMap<>();

    public ConfigManager() {
        init();
    }


    public void init() {
        initFlectonemixFolder();
        initRunningPath();
        initVersion();
        loadConfig();
        initMinecraftFolder();
        initTheme();
        initLanguage();
        initDecoratedFlag();
    }

    private void initVersion() {
        this.version = getClass().getPackage().getImplementationVersion();
    }

    private void initFlectonemixFolder() {
        flectonemixFolder = getDefaultFolderPath("flectonemix");
    }

    private void initRunningPath() {
        try {
            runningPath = new File(FlectoneMix.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
        } catch (URISyntaxException e) {
            new FAlert(FAlert.Type.EXCEPTION, e).show();
        }
    }

    private void loadConfig() {
        var config = getResourceAsMap("config.yml");
        if (config != null) properties.putAll(config);

        getDefaultResourceAsMap("config.yml").forEach(properties::putIfAbsent);
    }

    private void initMinecraftFolder() {
        minecraftFolder = getString("minecraft-folder.selected");
        if (minecraftFolder.isEmpty()) {
            minecraftFolder = getDefaultFolderPath("minecraft");
        }

        setMinecraftFolder(minecraftFolder);
    }

    private void initTheme() {
        theme = getString("theme.selected");
        if (theme.isEmpty()) {
            theme = getStringList("theme.list").get(0);
        }
    }

    private void initLanguage() {
        language = getString("language.selected");
        if (language.isEmpty()) {
            List<String> languageList = getStringList("language.support");
            language = languageList.contains(SYSTEM_LANGUAGE) ? SYSTEM_LANGUAGE : languageList.get(0);
        }
    }

    private void initDecoratedFlag() {
        Boolean decorated = getBoolean("decorated");
        isPossiblyDecorated = decorated == null || decorated;
    }

    public void setTheme(String theme) {
        this.theme = theme;
        put("theme.selected", theme);
    }

    public void setLanguage(String language) {
        this.language = language;
        put("language.selected", language);
    }

    public void setMinecraftFolder(String minecraftFolder) {
        if (!minecraftFolder.endsWith(File.separator)) minecraftFolder += File.separator;

        this.minecraftFolder = minecraftFolder;
        put("minecraft-folder.selected", minecraftFolder);
    }

    public void setPossiblyDecorated(boolean possiblyDecorated) {
        this.isPossiblyDecorated = possiblyDecorated;
        put("decorated", possiblyDecorated);
    }

    public String getThemePath() {
        return "/net/flectone/mix/style/" + theme + "_theme.css";
    }

    public void put(@NonNull String key, Object object) {
        properties.put(key, object);
    }

    public String getLocaleString(@NonNull String key) {
        return getString("language." + language + "." + key);
    }

    public String getString(@NonNull String key) {
        if (get(key) instanceof String string) return string;
        return null;
    }

    public Integer getInteger(@NonNull String key) {
        if (get(key) instanceof Integer integer) return integer;
        return null;
    }

    public Boolean getBoolean(@NonNull String key) {
        if (get(key) instanceof Boolean bool) return bool;
        return null;
    }

    public List<String> getStringList(@NonNull String key) {
        if (get(key) instanceof List<?> list) return list
                .stream()
                .map(String::valueOf)
                .toList();
        return null;
    }

    public Object get(@NonNull String key) {
        return properties.get(key);
    }

    private File getResource(@NonNull String name) {
        File file = new File(flectonemixFolder + name);

        if (!file.exists()) {
            replaceFile(file, name);
        }

        return file;
    }

    private void replaceFile(File file, String name) {
        try {
            Path path = Paths.get(file.getPath());

            InputStream inputStream = FlectoneMix.class.getResourceAsStream("/net/flectone/mix/" + name);
            assert inputStream != null;

            Files.createDirectories(path);
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            new FAlert(FAlert.Type.EXCEPTION, e).show();
        }
    }

    private HashMap<String, Object> getResourceAsMap(@NonNull String name) {
        File file = getResource(name);

        try {
            Yaml yaml = new Yaml();
            return yaml.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            new FAlert(FAlert.Type.EXCEPTION, e).show();
        }

        System.out.println("2");
        return null;
    }

    private HashMap<String, Object> getDefaultResourceAsMap(@NonNull String name) {
        try {
            Yaml yaml = new Yaml();
            return yaml.load(FlectoneMix.class.getResource("/net/flectone/mix/" + name).openStream());
        } catch (IOException e) {
            new FAlert(FAlert.Type.EXCEPTION, e).show();
            return new HashMap<>();
        }
    }

    public void save() {
        try {
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(getResource("config.yml"), StandardCharsets.UTF_8);
            yaml.dump(properties, writer);
        } catch (IOException e) {
            new FAlert(FAlert.Type.EXCEPTION, e).show();
        }
    }

    public boolean isSupportUndecoratedWindow() {
        return OS.contains("win");
    }

    public boolean isUsedUndecoratedWindow() {
        return isSupportUndecoratedWindow() && isPossiblyDecorated;
    }

    private String getDefaultFolderPath(String folder){
        return OS.contains("win") ? System.getenv("APPDATA") +  "\\." + folder + "\\"
                : OS.contains("mac") ? System.getProperty("user.home") + "/Library/Application Support/" + folder + "/"
                : OS.contains("nux") || OS.contains("nix") ? System.getProperty("user.home") + "/." + folder + "/"
                : System.getProperty("user.dir") + "/" + folder + "/";
    }

    public VersionComparison compareVersions(@NotNull String firstVersion, @NotNull String secondVersion) {
        if (firstVersion.isEmpty()) return VersionComparison.OLDER;
        if (secondVersion.isEmpty()) return VersionComparison.NEWER;

        String[] parts1 = firstVersion.split("\\.");
        String[] parts2 = secondVersion.split("\\.");

        for (int x = 0; x < parts1.length; x++) {
            int num1 = Integer.parseInt(parts1[x]);
            int num2 = Integer.parseInt(parts2[x]);

            if (num1 > num2) return VersionComparison.NEWER;
            else if (num1 < num2) return VersionComparison.OLDER;
        }

        return VersionComparison.EQUAL;
    }

    public enum VersionComparison {
        NEWER(1),
        OLDER(-1),
        EQUAL(0);

        private final int value;

        VersionComparison(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
