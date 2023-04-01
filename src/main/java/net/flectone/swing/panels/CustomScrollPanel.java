package net.flectone.swing.panels;

import javax.swing.*;
import java.util.ArrayList;

public class CustomScrollPanel extends JScrollPane {

    private static final ArrayList<CustomScrollPanel> listScrollPanels = new ArrayList<>();

    public CustomScrollPanel(){

        getVerticalScrollBar().setUnitIncrement(8);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setBorder(null);

        listScrollPanels.add(this);
    }

    public static void update(){
        for(CustomScrollPanel panel : listScrollPanels){
            panel.setBorder(null);
        }
    }
}
