package net.flectone.utils;

import net.flectone.system.SystemInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class IOUtils {


    public static Font getResourceFont(String file, float fontSize){
        try {
            InputStream is = getResourceURL("fonts/" + file).openStream();

            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(fontSize);

        } catch (IOException | FontFormatException exception){
            exception.printStackTrace();
            Dialog.showException(exception);
            return null;
        }
    }

    public static BufferedImage getResourceBufferedImage(String file){
        try {
            return ImageIO.read(getResourceURL("images/" + file));
        } catch (IOException exception){
            exception.printStackTrace();
            Dialog.showException(exception);

            return null;
        }

    }

    public static URL getResourceURL(String file){
        return IOUtils.class.getClassLoader().getResource(file);
    }

    public static File getWebFile(String urlString, String file) throws IOException {
        if(new File(file).exists()) return new File(file);

        downloadFile(urlString, file);

        return new File(file);
    }

    public static void downloadFile(String urlString, String file) throws IOException {

        System.out.println(urlString);
        System.out.println(file);

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
