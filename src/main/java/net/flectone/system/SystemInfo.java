package net.flectone.system;

import net.flectone.Main;
import net.flectone.utils.SwingUtils;

import java.io.File;
import java.util.Arrays;

public class SystemInfo {

    private static SystemInfo instance;

    private final String path;

    private String minecraftDirectory;

    private String language;

    private SystemInfo(){
        //get working directory
        this.path = getJarDirectory() + File.separator;

        this.minecraftDirectory = getDefaultMinecraftDirectory();
    }

    private static String getDefaultMinecraftDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String mcDirectory;
        if (os.contains("win")) {
            mcDirectory = System.getenv("APPDATA") +  "\\.minecraft\\";
        } else if (os.contains("mac")) {
            mcDirectory = System.getProperty("user.home") + "/Library/Application Support/minecraft/";
        } else if (os.contains("nux") || os.contains("nix")) {
            mcDirectory = System.getProperty("user.home") + "/.minecraft/";
        } else {
            mcDirectory = System.getProperty("user.dir") + "/minecraft/";
        }
        return mcDirectory;
    }

    public void setMinecraftDirectory(String minecraftDirectory) {
        this.minecraftDirectory = minecraftDirectory;
    }

    public String getMinecraftDirectory() {
        return minecraftDirectory;
    }

    public void loadLanguage(){

        if(Configuration.getValue("last_language") != null){
            this.language = Configuration.getValue("last_language");
            return;
        }

        //get default system language
        String systemLanguage = getSystemLanguage();

        if(!Arrays.asList(Configuration.getValues("support.languages")).contains(systemLanguage)){
            systemLanguage = Configuration.getValue("default.language");
        }
        this.language = systemLanguage;
    }

    //get only one instance
    public static SystemInfo getInstance() {
        if(instance == null) {
            instance = new SystemInfo();
        }
        return instance;
    }

    public String getPath() {
        return path;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;

        new Configuration().loadConfig("languages" + File.separator + language + ".yml");

        SwingUtils.updateJComponentText();
    }

    private String getJarDirectory() {
        return new File("").getAbsolutePath();
    }

    private String getSystemLanguage() {
        return System.getProperty("user.language").toLowerCase();
    }

}
