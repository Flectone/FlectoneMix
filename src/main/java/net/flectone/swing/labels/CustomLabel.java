package net.flectone.swing.labels;

import net.flectone.system.Configuration;

import javax.swing.*;

public class CustomLabel extends JLabel {

    public CustomLabel(String name, boolean addToolTip){

        setText(Configuration.getValue("label." + name));

        if(addToolTip) setToolTipText(Configuration.getValue("label.tooltip." + name));
    }

}
