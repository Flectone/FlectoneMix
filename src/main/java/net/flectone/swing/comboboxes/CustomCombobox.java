package net.flectone.swing.comboboxes;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CustomCombobox extends JComboBox {

    public CustomCombobox(){

    }

    public void setIconListRenderer(Map<String, Icon> icons){
        setRenderer(new IconListRenderer(icons));
    }

    class IconListRenderer extends DefaultListCellRenderer {
        private Map<String, Icon> icons = null;

        public IconListRenderer(Map<String, Icon> icons){
            this.icons = icons;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Icon icon = icons.get(value);
            label.setIcon(icon);
            return label;
        }
    }
}
