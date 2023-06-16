package net.flectone;

import net.flectone.swing.frames.MainFrame;
import net.flectone.system.Configuration;
import net.flectone.system.Installation;
import net.flectone.system.SystemInfo;
import net.flectone.utils.ColorUtils;
import net.flectone.utils.IOUtils;
import net.flectone.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static JFrame frame;

    public static void main(String[] args) {

        Configuration.loadLocal(IOUtils.getResourceURL("config.yml"));

        Configuration.loadConfig("staticWords.yml");

        Configuration.checkSettingsFile();
        SystemInfo.setMinecraftPath();


        String lastColor = Configuration.getValue("last_color");
        SwingUtils.setColors(lastColor != null ? Color.decode(lastColor) : ColorUtils.decode("color.black"));

        // Set the system language and color
        SystemInfo.loadLanguage();

        Configuration.loadConfig("languages/"+ SystemInfo.getLanguage() + ".yml");

        Installation.checkUpdates();

        frame = new MainFrame();
    }

}
