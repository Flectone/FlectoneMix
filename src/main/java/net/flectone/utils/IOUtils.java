package net.flectone.utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class IOUtils {

    public static void downloadFile(String urlString, String file) throws IOException {

        BufferedInputStream bis = new BufferedInputStream(openConnection(urlString).getInputStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }

        fis.close();
        bis.close();
    }

    public static URLConnection openConnection(String urlString) throws IOException {

        URLConnection urlConnection = new URL(urlString + "?timestamp=" + System.currentTimeMillis()).openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

        return urlConnection;
    }

    public static ProcessBuilder getJarProcess(String arg) {
        return new ProcessBuilder("java", "-jar", arg);
    }

    public static ProcessBuilder getJarProcess(String[] args) {
        return getJarProcess("\"" + String.join("\"", args));
    }
}
