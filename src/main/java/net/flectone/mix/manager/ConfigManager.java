package net.flectone.mix.manager;

import lombok.Getter;
import lombok.NonNull;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.component.FAlert;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;

@Getter
public class FileManager {

    @Getter
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String SYSTEM_LANGUAGE = System.getProperty("user.language").toLowerCase();

    private String flectonemixFolder;

    private String minecraftFolder;

    private String version;

    private String language;

    private String theme;

    private boolean isPossiblyDecorated;

    private static final HashMap<String, Object> properties = new HashMap<>();

    public FileManager() {
        init();
    }


    public void init() {
        initFlectonemixFolder();
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
            FAlert.showException(e, e.getLocalizedMessage());
        }
    }

    private HashMap<String, Object> getResourceAsMap(@NonNull String name) {
        File file = getResource(name);

        try {
            Yaml yaml = new Yaml();
            return yaml.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            FAlert.showException(e, e.getLocalizedMessage());
        }

        System.out.println("2");
        return null;
    }

    private HashMap<String, Object> getDefaultResourceAsMap(@NonNull String name) {
        try {
            Yaml yaml = new Yaml();
            return yaml.load(FlectoneMix.class.getResource("/net/flectone/mix/" + name).openStream());
        } catch (IOException e) {
            FAlert.showException(e, e.getLocalizedMessage());
            return new HashMap<>();
        }
    }

    public void save() {
        try {
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(getResource("config.yml"), StandardCharsets.UTF_8);
            yaml.dump(properties, writer);
        } catch (IOException e) {
            FAlert.showException(e, e.getLocalizedMessage());
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
}
