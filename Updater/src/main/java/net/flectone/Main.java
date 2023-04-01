package net.flectone;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        down();

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "Flectone Installer.jar");
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();


        System.exit(0);

    }

    public static void down(){
        try {
            URL url = new URL("https://flectone.net/components/last/components1.zip");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            InputStream inputStream = connection.getInputStream();

            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry zipEntry;
            byte[] buffer = new byte[1024];
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File file = new File(zipEntry.getName());

                if(file.isFile()){
                    file.delete();
                }
                System.out.println(file.getAbsolutePath());

                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    FileOutputStream fos = new FileOutputStream(file);
                    int count;
                    while ((count = zipInputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                    }
                    fos.close();
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}