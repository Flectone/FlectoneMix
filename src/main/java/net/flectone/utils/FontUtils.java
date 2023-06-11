package net.flectone.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontUtils {

    public static Font createFont(String fontName, float size){

        Font font = null;

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts" + File.separator + fontName))
                    .deriveFont(size);
        } catch (IOException | FontFormatException exception){
            Dialog.showException(exception);
        }

        return font;
    }
}
