package net.flectone.system;

import net.flectone.Main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.flectone.utils.Dialog;
import net.flectone.utils.ImageUtils;
import net.flectone.utils.SwingUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class Installation {

    private static final String installingLabel = Configuration.getValue("label.installing");

    private static final String urlFlectone = "https://flectone.net/components/";

    private final String urlDirectory;

    private final String destinationDirectory;

    private final String tabName;

    private ArrayList<String> componentsForInstallation;

    private final JPanel progressPanel;

    private final String componentType;

    private final JProgressBar progressBar;

    private final JLabel progressLabel;

    public Installation(String tabName, String componentType, String urlDirectory, String destinationDirectory, JPanel progressPanel){

        progressPanel.setVisible(true);

        this.tabName = tabName;
        this.componentType = componentType;
        this.urlDirectory = "https://flectone.net/" + urlDirectory;
        this.destinationDirectory = SystemInfo.getInstance().getMinecraftDirectory() + destinationDirectory;
        this.progressPanel = progressPanel;
        this.componentsForInstallation = new ArrayList<>();

        this.progressBar = (JProgressBar) progressPanel.getComponent(1);
        this.progressLabel = (JLabel) progressPanel.getComponent(0);

        SwingUtils.getTabsComponents(tabName).stream().filter(component ->
                        component instanceof JCheckBox
                                && ((JCheckBox) component).isSelected()
                                && component.getName() != null)
                .forEach(component -> {
                    componentsForInstallation.add(component.getName() + componentType);
                });
    }

    public void downloadAndUnzipFiles(){
        componentsForInstallation.forEach(component -> {

            String downloadDestinationDirectory = destinationDirectory;
            String unzipDestinationDirectory = destinationDirectory + component.split("\\.")[0];

            if(tabName.equals("farms") && component.contains("litematic")){
                downloadDestinationDirectory = SystemInfo.getInstance().getMinecraftDirectory() + "schematics" + File.separator;
                unzipDestinationDirectory = downloadDestinationDirectory;
            }

            downloadFile(urlDirectory + component, downloadDestinationDirectory + component, component);

            try {
                unzipAndDelete(new File(downloadDestinationDirectory + component), unzipDestinationDirectory);
            } catch (Exception e){
                Dialog.showException(e);
            }
        });
    }

    public void unzipAndDelete(File zipFile, String destDirectory) throws IOException {
        ZipFile zip = new ZipFile(zipFile);
        long totalSize = zip.stream().mapToLong(ZipEntry::getSize).sum();
        long extractedSize = 0;

        byte[] buffer = new byte[1024];
        int bytesRead;

        for (ZipEntry entry : Collections.list(zip.entries())) {

            progressLabel.setText(Configuration.getValue("label.unzipping") + entry);

            File destFile = new File(destDirectory, entry.getName());
            if (entry.isDirectory()) {
                destFile.mkdirs();
            } else {
                new File(destFile.getParent()).mkdirs();
                try (InputStream inputStream = zip.getInputStream(entry);
                     FileOutputStream outputStream = new FileOutputStream(destFile)) {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        extractedSize += bytesRead;
                        progressBar.setValue((int) (extractedSize * 100 / totalSize));
                    }
                }
            }
        }

        zip.close();
        zipFile.delete();
    }

    public void updateExistFiles(Set<String> webComponentsList){

        for(File file : new File(destinationDirectory).listFiles()){
            String fileName = file.getName().split("\\.")[0];

            if(!webComponentsList.contains(fileName)) continue;

            //для того, чтобы обновлять время обновления файла, строчка бессмысленная, но для юзеров
            file.delete();

            downloadFile(urlDirectory + file.getName(), destinationDirectory + file.getName(), file.getName());
        }
    }

    public void downloadFiles(){
        downloadFiles(componentsForInstallation, urlDirectory, destinationDirectory);
    }

    public void downloadFiles(List<String> components, String urlDirectory, String destinationDirectory){
        components.forEach(component
                -> downloadFile(urlDirectory + component,
                destinationDirectory + component,
                component));
    }

    public void deleteFilesWithContentType() {
        File folder = new File(destinationDirectory);
        File[] files = folder.listFiles();
        if (files != null) {
            progressBar.setMaximum(files.length);

            progressLabel.setText(Configuration.getValue("label.clearing"));

            int count = 0;
            progressBar.setValue(count);
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(componentType)) {
                    file.delete();
                    count++;
                    progressBar.setValue(count);
                }
            }
        }
    }

    public void addCustomProfile(String loaderType, String minecraftVersion){
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        String path = SystemInfo.getInstance().getPath();
        String mcDir = SystemInfo.getInstance().getMinecraftDirectory();

        String url = urlFlectone + loaderType.toLowerCase() + ".jar";
        String fileName = loaderType.toLowerCase() + "-installer.jar";
        String command = String.format("java -jar %s%s client -dir %s -noprofile -mcversion %s",
                path, fileName, mcDir, minecraftVersion);
        if (loaderType.equalsIgnoreCase("quilt")) {
            command = String.format("java -jar %s%s install client %s --install-dir=%s --no-profile",
                    path, fileName, minecraftVersion, mcDir);
        }

        try {

            downloadFile(url, path + fileName, fileName);
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            String loaderName = minecraftVersion;
            File folderMinecraftVersions = new File(Paths.get(mcDir, "versions").toString());
            for (File fabricVersion : folderMinecraftVersions.listFiles()) {
                String fileLoaderName = fabricVersion.getName();
                if (fileLoaderName.contains(loaderType.toLowerCase() + "-loader") &&
                        fileLoaderName.contains(minecraftVersion)) {
                    loaderName = fileLoaderName;
                    break;
                }
            }

            File file = new File(mcDir + "launcher_profiles.json");
            if(!file.exists()){
                Dialog.showInformation(Configuration.getValue("error.profile"));
                return;
            }

            FileReader reader = new FileReader(mcDir + "launcher_profiles.json");
            Map<String, Object> json = gson.fromJson(reader, type);
            Map<String, Object> profiles = (Map<String, Object>) json.get("profiles");
            Map<String, Object> customProfile = new LinkedHashMap<>();
            customProfile.put("name", "Flectone " + loaderType + " " + minecraftVersion);
            customProfile.put("lastVersionId", loaderName);
            customProfile.put("javaArgs", "-Xmx2G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:+PerfDisableSharedMem");
            customProfile.put("lastUsed", LocalDateTime.now().toString());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(ImageUtils.createBufferedImage("flectone.png"), "png", outputStream);
            String icon = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            customProfile.put("icon", "data:image/png;base64," + icon);

            Map<String, Object> customLoader = new LinkedHashMap<>();
            customLoader.put("id", "fabric");
            customProfile.put("loader", customLoader);
            profiles.put("Flectone " + loaderType + " " + minecraftVersion, customProfile);
            json.put("selectedProfile", "Flectone " + loaderType + " " + minecraftVersion);

            FileWriter writer = new FileWriter(Paths.get(SystemInfo.getInstance().getMinecraftDirectory() + "launcher_profiles.json").toString());
            gson.toJson(json, writer);
            writer.close();

        } catch (IOException | InterruptedException | NullPointerException e){
            Dialog.showException(e);
            throw new RuntimeException(e);
        }
    }

    public void downloadFile(String urlDirectory, String destinationDirectory, String fileName){
        try {

            progressLabel.setText(installingLabel + fileName);

            File directory = new File(destinationDirectory.substring(0, destinationDirectory.lastIndexOf(File.separator) + 1));
            if (!directory.exists()) {
                directory.mkdirs();
            }

            URL url = new URL(urlDirectory);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            int contentLength = connection.getContentLength();
            InputStream inputStream = connection.getInputStream();

            BufferedInputStream bis = new BufferedInputStream(inputStream);
            FileOutputStream fos = new FileOutputStream(destinationDirectory);

            byte[] buffer = new byte[1024];
            int count, totalCount = 0;
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                fos.write(buffer, 0, count);
                totalCount += count;
                progressBar.setValue((int)((double)totalCount / contentLength * 100));
            }

            fos.close();
            bis.close();
            inputStream.close();
        } catch (IOException e) {
            progressPanel.setVisible(false);
            Dialog.showException(e);
        }
    }

    public void close(){
        Timer timer = new Timer(3, new ActionListener() {
            int progress = progressBar.getValue();

            @Override
            public void actionPerformed(ActionEvent e) {
                progress--;
                progressBar.setValue(progress);

                if (progress <= 0) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });

        timer.start();

        progressLabel.setText(Configuration.getValue("label.waiting"));
        Dialog.showInformation("Installing success!");
    }

    public static void checkUpdates(){
        try {

            double currentVersion = Double.parseDouble(Configuration.getValue("version"));
            Document doc = Jsoup.connect("https://flectone.net/components/last/").get();
            double lastVersion = Double.parseDouble(doc.select("a[href^='v']").get(0).text().replaceFirst("v", ""));

            if(lastVersion <= currentVersion) return;

            if(Dialog.showYesOrNo(Configuration.getValue("label.update_program")) != JOptionPane.YES_OPTION) return;

            String jarFilePath = SystemInfo.getInstance().getPath() + "updater.jar";
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", jarFilePath);
            processBuilder.inheritIO();
            processBuilder.start();
            System.exit(-1);


        } catch (Exception e){
            Dialog.showException(e);
        }

    }

}
