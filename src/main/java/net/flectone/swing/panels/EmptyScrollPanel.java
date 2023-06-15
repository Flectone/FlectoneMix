package net.flectone.swing.panels;

import javax.swing.*;
import java.util.ArrayList;

public class EmptyScrollPanel extends JScrollPane {

    private static final ArrayList<EmptyScrollPanel> listScrollPanels = new ArrayList<>();

    public EmptyScrollPanel(){

        getVerticalScrollBar().setUnitIncrement(8);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setBorder(null);

        listScrollPanels.add(this);
    }

    public static void update(){
        for(EmptyScrollPanel panel : listScrollPanels){
            panel.setBorder(null);
        }
    }
}
