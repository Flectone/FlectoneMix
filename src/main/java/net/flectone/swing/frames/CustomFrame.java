package net.flectone.swing.frames;

import javax.swing.*;
import java.util.ArrayList;

public class CustomFrame extends JFrame {

    private static final ArrayList<JFrame> listFrames = new ArrayList<>();

    public CustomFrame(){
        listFrames.add(this);
    }

    public static void updateAllFrames(){
        for(JFrame frame : listFrames){

            SwingUtilities.updateComponentTreeUI(frame);

            frame.invalidate();
            frame.validate();
            frame.repaint();
        }
    }
}
