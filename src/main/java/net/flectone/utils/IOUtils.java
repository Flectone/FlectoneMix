package net.flectone.utils;

import net.flectone.system.SystemInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class IOUtils {

    // HashMap to store theme-related BufferedImages
    private static final HashMap<String, BufferedImage> themeBufferedImageHashMap = new HashMap<>();

    // Store a theme BufferedImage in the HashMap
    public static void putThemeBufferedImage(String file, BufferedImage bufferedImage){
        themeBufferedImageHashMap.put(file, bufferedImage);
    }

    // Change the color of all theme icons
    public static void changeThemeIconsColor(Color color){
        themeBufferedImageHashMap.values().forEach(bufferedImage -> {

            Color newColor = color;

            // Make icons with acceleration priority brighter
            if(bufferedImage.getAccelerationPriority() == 1.0f){
                newColor = ColorUtils.makeBrighterOrDarker(SwingUtils.getColor(0), 20);
            }

            changeBufferedImageColor(bufferedImage, newColor);
        });
    }

    // Change the color of a BufferedImage
    public static void changeBufferedImageColor(BufferedImage image, Color color){
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.SrcAtop);
        graphics.setColor(color);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.dispose();
    }

    // Get a themed ImageIcon
    public static ImageIcon getThemeImageIcon(String file){
        if(imageIconHashMap.get(file) != null) return imageIconHashMap.get(file);

        ImageIcon imageIcon = new ImageIcon(getThemeBufferedImage(file, SwingUtils.getColor(2)));
        imageIconHashMap.put(file, imageIcon);

        return imageIcon;
    }

    // Get a themed BufferedImage
    public static BufferedImage getThemeBufferedImage(String file, Color color){
        BufferedImage bufferedImage = themeBufferedImageHashMap.get(file);

        if(bufferedImage == null) {
            bufferedImage = getResourceBufferedImage(file);

            changeBufferedImageColor(bufferedImage, color);

            themeBufferedImageHashMap.put(file, bufferedImage);
        }
        return bufferedImage;
    }

    // HashMap to store resource ImageIcons
    private static final HashMap<String, ImageIcon> imageIconHashMap = new HashMap<>();

    // Get a resource ImageIcon
    public static ImageIcon getResourceImageIcon(String file){
        if(imageIconHashMap.get(file) != null) return imageIconHashMap.get(file);

        ImageIcon imageIcon = new ImageIcon(getResourceBufferedImage(file));
        imageIconHashMap.put(file, imageIcon);

        return imageIcon;
    }

    // Get a web ImageIcon
    public static ImageIcon getWebImageIcon(String file){
        if(imageIconHashMap.get(file) != null) return imageIconHashMap.get(file);

        ImageIcon imageIcon = new ImageIcon(getWebBufferedImage(file));
        imageIconHashMap.put(file, imageIcon);

        return imageIcon;
    }

    // Get a BufferedImage from a web URL
    public static BufferedImage getWebBufferedImage(String file){
        try {
            File image = getWebFile(SystemInfo.siteUrl + "components/configs/images/" + file,
                    SystemInfo.getConfigPath() + file);

            return ImageIO.read(image);

        } catch (IOException exception){
            exception.printStackTrace();
            Dialog.showException(exception);

            return null;
        }
    }

    // Get a Font from a resource file
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

    // Get a BufferedImage from a resource file
    public static BufferedImage getResourceBufferedImage(String file){
        try {
            return ImageIO.read(getResourceURL("images/" + file));
        } catch (IOException exception){

            exception.printStackTrace();
            Dialog.showException(exception);

            return null;
        }

    }

    // Get the URL of a resource file
    public static URL getResourceURL(String file){
        return IOUtils.class.getClassLoader().getResource(file);
    }

    // Get a file from the web
    public static File getWebFile(String urlString, String file) throws IOException {
        if(new File(file).exists()) return new File(file);

        downloadFile(urlString, file);

        return new File(file);
    }

    // Download a file from a URL
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

    // Open a URL connection
    public static URLConnection openConnection(String urlString) throws IOException {
        URLConnection urlConnection = new URL(urlString + "?timestamp=" + System.currentTimeMillis()).openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

        return urlConnection;
    }

    // Get a ProcessBuilder for running a JAR file with one argument
    public static ProcessBuilder getJarProcess(String arg) {
        return new ProcessBuilder("java", "-jar", arg);
    }

    // Get a ProcessBuilder for running a JAR file with multiple arguments
    public static ProcessBuilder getJarProcess(String[] args) {
        return getJarProcess("\"" + String.join("\"", args));
    }
}
