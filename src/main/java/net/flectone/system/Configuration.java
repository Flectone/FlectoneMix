package net.flectone.system;

import net.flectone.utils.Dialog;
import net.flectone.utils.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Configuration {

    private static final Map<String, String> configMap = new HashMap<>();

    //load file from folder configs/
    public static void loadWeb(String path) {

        try {
            InputStream inputStream = IOUtils.openConnection(path).getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;

            while ((line = reader.readLine()) != null) {
                if(line.startsWith("#") || !line.contains(": ")) continue;

                String[] words = line.split(": ");

                //put to map with key
                configMap.put(words[0], words[1].replaceAll("\"", ""));
            }

            reader.close();
        } catch (IOException e){
            Dialog.showException(e);
        }
    }

    public static void loadLocal(String file){

        //read file
        try(Scanner scanner = new Scanner(new File(file), "UTF-8")) {
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

    public static void loadLocal(URL url){

        //read file
        try(Scanner scanner = new Scanner(url.openStream(), "UTF-8")) {
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

    public static void loadConfig(String configPath) {
        loadWeb(SystemInfo.siteUrl + "components/configs/"+ configPath);
    }

    //get message
    public static String getValue(String key) {
        return configMap.get(key);
    }

    public static void checkSettingsFile(){

        String path = SystemInfo.getConfigPath();
        if(!new File(path + SystemInfo.settingsFileName).exists()) return;

        loadLocal(path + SystemInfo.settingsFileName);
    }

    //get message and split
    public static String[] getValues(String key){
        return configMap.get(key).split(" ");
    }
}
