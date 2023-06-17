package net.flectone.system;

import net.flectone.Main;
import net.flectone.utils.SwingUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

public class SystemInfo {

    public static final String currentPath;

    static {
        try {
            currentPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String siteUrl = "https://flectone.net/";

    public static final String settingsFileName = "settings.txt";

    private static final String os = System.getProperty("os.name").toLowerCase();

    private static final String systemLanguage = System.getProperty("user.language").toLowerCase();

    private static String minecraftPath;

    public static void setMinecraftPath(){
        minecraftPath = Configuration.getValue("last_minecraft_path") != null  ?
                Configuration.getValue("last_minecraft_path") :
                getDefaultFolderPath("minecraft");
    }

    private static String configPath = getDefaultFolderPath("flectonemix");

    private static String language;

    private static String getDefaultFolderPath(String folder){

        String mcDirectory;
        if (os.contains("win")) {
            mcDirectory = System.getenv("APPDATA") +  "\\." + folder + "\\";
        } else if (os.contains("mac")) {
            mcDirectory = System.getProperty("user.home") + "/Library/Application Support/" + folder + "/";
        } else if (os.contains("nux") || os.contains("nix")) {
            mcDirectory = System.getProperty("user.home") + "/." + folder + "/";
        } else {
            mcDirectory = System.getProperty("user.dir") + "/" + folder + "/";
        }

        if(!new File(mcDirectory).exists()){
            new File(mcDirectory).mkdirs();
        }

        return mcDirectory;
    }

    public static String getConfigPath() {
        return configPath;
    }

    public static void loadLanguage(){

        if(Configuration.getValue("last_language") != null){
            SystemInfo.language = Configuration.getValue("last_language");
            return;
        }

        String defaultSystemLanguage = systemLanguage;

        if(!Arrays.asList(Configuration.getValues("support.languages")).contains(defaultSystemLanguage)){
            defaultSystemLanguage = Configuration.getValue("default.language");
        }
        SystemInfo.language = defaultSystemLanguage;
    }

    public static String getLanguage() {
        return language;
    }

    public static String getMinecraftPath() {
        return minecraftPath;
    }

    public static void setMinecraftPath(String minecraftPath) {
        SystemInfo.minecraftPath = minecraftPath;
    }

    public static void setLanguage(String language) {
        SystemInfo.language = language;

        Configuration.loadConfig("languages/" + language + ".yml");

        SwingUtils.updateJComponentText();
    }

}
