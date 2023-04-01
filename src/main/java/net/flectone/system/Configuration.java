package net.flectone.system;

import net.flectone.utils.Dialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Configuration {

    private static final Map<String, String> configMap = new HashMap<>();

    //load file from folder configs/
    public void load(String path) {

        //read file
        try(Scanner scanner = new Scanner(new File(SystemInfo.getInstance().getPath() + path), "UTF-8")) {
            //if next line exist
            while(scanner.hasNextLine()) {
                //get line
                String line = scanner.nextLine();
                if(line == null || line.startsWith("#") || !line.contains(": ")) continue;

                String[] words = line.split(": ");

                //put to map with key
                configMap.put(words[0], words[1].replaceAll("\"", ""));
            }

        } catch (Exception e) {
            Dialog.showException(e);
        }
    }

    public void loadConfig(String configPath) {
        load("configs" + File.separator + configPath);
    }

    //get message
    public static String getValue(String key) {
        return configMap.get(key);
    }

    //get message and split
    public static String[] getValues(String key){
        return configMap.get(key).split(" ");
    }
}
