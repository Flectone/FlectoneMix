package net.flectone.swing.checkboxes;

import net.flectone.system.Configuration;

import javax.swing.*;

public class CustomCheckBox extends JCheckBox {

    public CustomCheckBox(String name, boolean addToolTip){

        setText(Configuration.getValue("checkbox." + name));

        if(addToolTip) setToolTipText(Configuration.getValue("checkbox.tooltip." + name));
    }
}
