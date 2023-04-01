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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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

                SystemInfo systemInfo = SystemInfo.getInstance();

                try {
                    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(systemInfo.getPath() + "flectone.installer"));

                    fileWriter.write("last_color: " + ColorUtils.toHEX(SwingUtils.getColor(0)));
                    fileWriter.newLine();

                    fileWriter.write("last_language: " + systemInfo.getLanguage());
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
