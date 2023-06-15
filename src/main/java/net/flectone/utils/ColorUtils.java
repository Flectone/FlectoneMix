package net.flectone.utils;

import net.flectone.system.Configuration;

import java.awt.*;

public class ColorUtils {

    public static Color decode(String colorName){
        return Color.decode(Configuration.getValue(colorName));
    }


    public static Color makeBrighterOrDarker(Color color, int countLight) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int brightness = (r + g + b) / 3;
        int newR = brightness > 128 ? Math.max(0, r - countLight) : Math.min(255, r + countLight);
        int newG = brightness > 128 ? Math.max(0, g - countLight) : Math.min(255, g + countLight);
        int newB = brightness > 128 ? Math.max(0, b - countLight) : Math.min(255, b + countLight);
        return new Color(newR, newG, newB);
    }

    public static Color getComponentColor(Color color) {

        // calculate the luminance of the background color
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;

        if (luminance > 0.5) {
            // use black if the background is light
            return new Color(40, 40, 40);
        } else {
            // use white if the background is dark
            return new Color(225, 225, 225);
        }
    }

    public static Color setAlphaForColor(Color color, int alpha){
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static String toHEX(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
