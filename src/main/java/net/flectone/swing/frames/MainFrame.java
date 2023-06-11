package net.flectone.swing.frames;

import net.flectone.system.SystemInfo;
import net.flectone.utils.*;
import net.flectone.swing.panels.installations.MainPanel;
import net.flectone.system.Configuration;
import net.flectone.utils.Dialog;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainFrame extends CustomFrame {

    public MainFrame(){

        setSize(new Dimension(1300, 600));
        setMinimumSize(new Dimension(1300, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(ImageUtils.createBufferedImage("flectone.png"));
        setTitle(Configuration.getValue("frame.main") + Configuration.getValue("version"));

        SwingUtils.setFont(FontUtils.createFont("Roboto-Medium.ttf", 12));

        add(new MainPanel());

        setVisible(true);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {


                try {

                    BufferedWriter fileWriter = Files.newBufferedWriter(Paths.get(SystemInfo.getConfigPath() + SystemInfo.settingsFileName));

                    fileWriter.write("last_color: " + ColorUtils.toHEX(SwingUtils.getColor(0)));
                    fileWriter.newLine();

                    fileWriter.write("last_language: " + SystemInfo.getLanguage());
                    fileWriter.newLine();

                    fileWriter.write("last_minecraft_path: " + SystemInfo.getMinecraftPath());
                    fileWriter.newLine();

                    fileWriter.close();

                } catch (IOException exc){
                    Dialog.showException(exc);
                }

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

}
