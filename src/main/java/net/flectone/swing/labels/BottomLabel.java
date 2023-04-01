package net.flectone.swing.labels;

import net.flectone.system.Configuration;
import net.flectone.utils.ImageUtils;
import net.flectone.utils.WebUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BottomLabel extends JLabel {

    public BottomLabel(String filePath){
        setIcon(ImageUtils.createThemeImageIcon(filePath));
        setBorder(null);

        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public BottomLabel(String filePath, boolean isUrlLabel){
        this(filePath);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                WebUtils.openUrl(Configuration.getValue("url." + filePath.replace(".png", "")));
            }
        });
    }
}
