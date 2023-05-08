package net.flectone;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
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

        //create configuration object
        Configuration configuration = new Configuration();
        //load "configs/config.yml"
        configuration.loadConfig("config.yml");
        //load "configs/staticWords.yml"
        configuration.loadConfig("staticWords.yml");

        if(new File(SystemInfo.getInstance().getPath() + "flectone.installer").exists())
            configuration.load("flectone.installer");

        String lastColor = Configuration.getValue("last_color");

        SwingUtils.setColors(lastColor != null ? Color.decode(lastColor) : ColorUtils.decode("color.black"));

        // Set the system language and color
        SystemInfo.getInstance().loadLanguage();

        //load "configs/languages/ .yml"
        configuration.loadConfig("languages" + File.separator + SystemInfo.getInstance().getLanguage() + ".yml");

        Installation.checkUpdates();

        frame = new MainFrame();
    }

}
