/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package net.flectone.swing.panels.installations;

import net.flectone.swing.checkboxes.CustomCheckBox;
import net.flectone.swing.comboboxes.CustomCombobox;
import net.flectone.swing.labels.CustomLabel;
import net.flectone.swing.panels.test.CustomPanel;
import net.flectone.system.Configuration;
import net.flectone.system.Installation;
import net.flectone.system.SystemInfo;
import net.flectone.utils.Dialog;
import net.flectone.utils.SwingUtils;
import net.flectone.utils.WebUtils;

import javax.swing.*;
import java.io.File;
import java.util.*;

/**
 *
 * @author TheFaser
 */
public class OptimizationPanel extends JPanel {

    /**
     * Creates new form OptimizationPanel
     */

    private ArrayList<JCheckBox> unstableList = new ArrayList<>();

    public CustomPanel panel;

    public OptimizationPanel() {
        panel = new CustomPanel("optimization", ".jar");

        initComponents();

        setComboBoxModels();
        addComboBoxListeners();

        panel.addComponentsToInstallPanel(jPanel1);

        updateComponents();

        jButton1.addActionListener(e -> new Thread(() -> {

            jButton1.setEnabled(false);
            jButton2.setEnabled(false);

            Installation installation = new Installation("optimization",
                    ".jar",
                    panel.getUrlToComponents() + "/",
                    "mods" + File.separator,
                    panel.getProgressPanel());


            if(clearCheckBox.isSelected()){
                installation.deleteFilesWithContentType();
            }

            installation.downloadFiles();

            if(settingCheckBox.isSelected()){
                installation.downloadFiles(new ArrayList<>(WebUtils.getWebNames("components/mods/configs/", "")),
                        "https://flectone.net/components/mods/configs/",
                        SystemInfo.getMinecraftPath() + "config" + File.separator);
            }

            if(profileCheckBox.isSelected()){
                installation.addCustomProfile(loadersTypeComboBox.getSelectedItem().toString(), versionComboBox.getSelectedItem().toString());
            }

            installation.close();

            jButton1.setEnabled(true);
            jButton2.setEnabled(true);

        }).start());

        jButton2.addActionListener(e -> new Thread(() ->{

            jButton1.setEnabled(false);
            jButton2.setEnabled(false);

            Installation installation = new Installation("optimization",
                    ".jar",
                    panel.getUrlToComponents() + "/",
                    "mods" + File.separator,
                    panel.getProgressPanel());



            installation.updateExistFiles(panel.getComponentsList());
            installation.close();

            jButton1.setEnabled(true);
            jButton2.setEnabled(true);
        }).start());

        jButton3.addActionListener(e -> Dialog.showSelectMinecraftFolder());
    }

    private void updateComponents(){
        panel.setUrlToComponents("components/optimization/" + versionComboBox.getSelectedItem() + "/" + loadersTypeComboBox.getSelectedItem() + "/" + modsTypeComboBox.getSelectedItem());
        panel.updateComponents((builder, component) -> {
            String modName = component.startsWith("+") ? component.substring(1) : component;

            builder.addIcon("components/" + modName + ".png")
                    .addCheckBox(Configuration.getValue("checkbox.install") + Configuration.getValue("mod." + modName), component,
                            !component.startsWith("+"), unstableList)
                    .addText("label." + modName)
                    .addLine();
        });
    }

    private void setComboBoxModels() {
        versionComboBox.setModel(new DefaultComboBoxModel<>(Configuration.getValues("support.Fabric.Sodium.versions")));
        loadersTypeComboBox.setModel(new DefaultComboBoxModel<>(Configuration.getValues("support.loaders_type")));
        modsTypeComboBox.setModel(new DefaultComboBoxModel<>(Configuration.getValues("support.mods_type")));
    }

    private boolean dontUpdate = false;

    private void addComboBoxListeners() {
        versionComboBox.addActionListener(e -> updateComboBoxModels("version"));
        loadersTypeComboBox.addActionListener(e -> updateComboBoxModels("loader"));
        modsTypeComboBox.addActionListener(e -> {
            if(dontUpdate){
                dontUpdate = false;
                return;
            }
            SwingUtils.clearTabsComponents("optimization");

            updateComponents();
        });
    }

    private void updateComboBoxModels(String changeSource) {
        SwingUtils.clearTabsComponents("optimization");

        String version = (String) versionComboBox.getSelectedItem();
        String loaderType = (String) loadersTypeComboBox.getSelectedItem();
        String[] loadersType = Configuration.getValues("support.loaders_type");
        String[] modsType = Configuration.getValues("support.mods_type");

        Set<String> loadersList = new HashSet<>();
        Set<String> modsList = new HashSet<>();

        for (String lt : loadersType) {
            for (String mt : modsType) {
                String versionsSupport = Configuration.getValue("support." + lt + "." + mt + ".versions");
                if (versionsSupport == null || !versionsSupport.contains(version + " ")) continue;

                loadersList.add(lt);
                modsList.add(mt);
            }
        }

        if (changeSource.equals("version")) {
            loadersTypeComboBox.setModel(new DefaultComboBoxModel<>(loadersList.toArray(new String[0])));
            loadersTypeComboBox.setSelectedItem(loaderType);
            dontUpdate = true;
        }
        modsTypeComboBox.setModel(new DefaultComboBoxModel<>(modsList.toArray(new String[0])));
        modsTypeComboBox.setSelectedItem("Sodium");


        if(loadersTypeComboBox.getSelectedItem().toString().equals("Forge")){
            profileCheckBox.setSelected(false);
            profileCheckBox.setEnabled(false);
        } else profileCheckBox.setEnabled(true);

    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        versionComboBox = new CustomCombobox();
        loadersTypeComboBox = new CustomCombobox();
        modsTypeComboBox = new CustomCombobox();
        profileCheckBox = new CustomCheckBox("create_profile", true);
        unstableCheckBox = new CustomCheckBox("unstable_mods", true);
        jPanel3 = new javax.swing.JPanel();
        minecraftVersionLabel = new CustomLabel("minecraft_label", false);
        loaderLabel = new CustomLabel("loader_type", false);
        modsLabel = new CustomLabel("mods_type", false);
        clearCheckBox = new CustomCheckBox("clear_folder", true);
        settingCheckBox = new CustomCheckBox("setting_minecraft", true);
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setOpaque(false);

        jPanel1.setOpaque(false);

        jPanel2.setOpaque(false);

        versionComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        loadersTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        modsTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        profileCheckBox.setText(Configuration.getValue("checkbox.create_profile"));
        profileCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                profileCheckBoxMouseClicked(evt);
            }
        });

        unstableCheckBox.setText(Configuration.getValue("checkbox.unstable_mods"));
        unstableCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                unstableCheckBoxMouseClicked(evt);
            }
        });
        unstableCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unstableCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(versionComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(loadersTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(profileCheckBox))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(modsTypeComboBox, 0, 124, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(unstableCheckBox)))
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(versionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadersTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(profileCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modsTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unstableCheckBox)))
        );

        jPanel3.setOpaque(false);

        minecraftVersionLabel.setText(Configuration.getValue("label.minecraft_version"));

        loaderLabel.setText(Configuration.getValue("label.loader_type"));

        modsLabel.setText(Configuration.getValue("label.mods_type"));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minecraftVersionLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(loaderLabel, javax.swing.GroupLayout.Alignment.TRAILING)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(modsLabel))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(minecraftVersionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(loaderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modsLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        clearCheckBox.setText(Configuration.getValue("checkbox.clear_folder"));

        settingCheckBox.setText(Configuration.getValue("checkbox.setting_minecraft"));

        jButton1.setText(Configuration.getValue("label.install"));

        jButton2.setText(Configuration.getValue("label.update"));

        jButton3.setText("...");
        jButton3.setPreferredSize(new java.awt.Dimension(23, 22));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(clearCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(settingCheckBox))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addGap(72, 72, 72))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clearCheckBox)
                    .addComponent(settingCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void unstableCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unstableCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_unstableCheckBoxActionPerformed

    private void profileCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_profileCheckBoxMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_profileCheckBoxMouseClicked

    private void unstableCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_unstableCheckBoxMouseClicked
        boolean isSelected = unstableCheckBox.isSelected();
        for(JCheckBox checkBox : unstableList){
            checkBox.setSelected(isSelected);
        }
    }//GEN-LAST:event_unstableCheckBoxMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox clearCheckBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel loaderLabel;
    private javax.swing.JComboBox<String> loadersTypeComboBox;
    private javax.swing.JLabel minecraftVersionLabel;
    private javax.swing.JLabel modsLabel;
    private javax.swing.JComboBox<String> modsTypeComboBox;
    private javax.swing.JCheckBox profileCheckBox;
    private javax.swing.JCheckBox settingCheckBox;
    private javax.swing.JCheckBox unstableCheckBox;
    private javax.swing.JComboBox<String> versionComboBox;
    // End of variables declaration//GEN-END:variables
}
