package net.flectone;

import net.flectone.system.Installation;
import net.flectone.utils.SwingUtils;
import net.flectone.swing.frames.MainFrame;
import net.flectone.system.Configuration;
import net.flectone.system.SystemInfo;
import net.flectone.utils.ColorUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {

    public static JFrame frame;

    public static void main(String[] args) {

        //load "configs/config.yml"
        Configuration.loadConfig("config.yml");
        //load "configs/staticWords.yml"
        Configuration.loadConfig("staticWords.yml");

        Configuration.checkSettingsFile();

        String lastColor = Configuration.getValue("last_color");
        SwingUtils.setColors(lastColor != null ? Color.decode(lastColor) : ColorUtils.decode("color.black"));

        // Set the system language and color
        SystemInfo.loadLanguage();

        //load "configs/languages/ .yml"
        Configuration.loadConfig("languages" + File.separator + SystemInfo.getLanguage() + ".yml");

        Installation.checkUpdates();

        frame = new MainFrame();
    }

}
