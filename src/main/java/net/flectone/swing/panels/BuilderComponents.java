package net.flectone.swing.panels;

import com.formdev.flatlaf.ui.FlatButtonBorder;
import net.flectone.system.Configuration;
import net.flectone.utils.IOUtils;
import net.flectone.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BuilderComponents extends JPanel {
    private Box rightComponents = Box.createVerticalBox();
    private final ArrayList<JComponent> componentList = new ArrayList<>();
    private String tab;

    public BuilderComponents(String tab) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(Box.createRigidArea(new Dimension(3, 0)));
        this.tab = tab;
        componentList.add(this);
    }

    public BuilderComponents addIcon(String configValue) {
        JLabel label = new JLabel();
        label.setBorder(new FlatButtonBorder());

        boolean isBigTab = tab.equals("farms") || tab.equals("datapacks") || tab.equals("shaders");
        String typeLoading = isBigTab ? "big" : "small";

        label.setIcon(new ImageIcon(IOUtils.getResourceURL("images/" + typeLoading + "-null-icon.png")));

        new Thread(() -> {
            label.setIcon(IOUtils.getWebImageIcon(configValue));
        }).start();

        add(label);
        componentList.add(label);
        return this;
    }


    public BuilderComponents addText(String configValue) {


        JLabel label = new JLabel(Configuration.getValue(configValue));

        label.setName(Configuration.getValue(configValue));
        rightComponents.add(label);
        componentList.add(label);

        return this;
    }

    public BuilderComponents addLine() {
        rightComponents.add(new JSeparator());
        return this;
    }

    public BuilderComponents addCheckBox(String configValue) {
        JCheckBox checkBox = new JCheckBox(configValue);
        checkBox.setFont(checkBox.getFont().deriveFont(15f));
        rightComponents.add(checkBox);
        componentList.add(checkBox);
        return this;
    }

    public BuilderComponents addCheckBox(String text, String name) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(checkBox.getFont().deriveFont(15f));
        checkBox.setName(name);
        rightComponents.add(checkBox);
        componentList.add(checkBox);
        return this;
    }

    public BuilderComponents addCheckBox(String text, String name, boolean enable, ArrayList<JCheckBox> unstableList){
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(checkBox.getFont().deriveFont(15f));
        checkBox.setSelected(enable);
        rightComponents.add(checkBox);
        componentList.add(checkBox);
        checkBox.setName(name);

        if(!enable) unstableList.add(checkBox);
        return this;
    }

    public BuilderComponents buildComponent() {
        add(rightComponents);
        SwingUtils.addTabsComponents(tab, componentList);
        return this;
    }
}
