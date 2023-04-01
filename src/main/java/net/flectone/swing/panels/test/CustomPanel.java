/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package net.flectone.swing.panels.test;

import com.formdev.flatlaf.ui.FlatButtonBorder;
import net.flectone.swing.panels.CustomScrollPanel;
import net.flectone.swing.panels.installations.*;
import net.flectone.swing.panels.tabs.BuilderComponent;
import net.flectone.system.Configuration;
import net.flectone.utils.ColorUtils;
import net.flectone.utils.ImageUtils;
import net.flectone.utils.SwingUtils;
import net.flectone.utils.WebUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author TheFaser
 */
public class CustomPanel extends JPanel {

    private String tabName;

    private String componentType;

    private String urlToComponents;

    public CustomPanel(String tabName, String componentType) {
        this.tabName = tabName;
        this.componentType = componentType;

        initComponents();

        componentsPanel.setLayout(new BoxLayout(componentsPanel, BoxLayout.Y_AXIS));
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        installPanel.setLayout(new BoxLayout(installPanel, BoxLayout.Y_AXIS));
        installPanel.add(Box.createVerticalGlue());
        installPanel.setOpaque(false);

        backgroundPanel.setOpaque(false);
        setOpaque(false);

        shadowPanel.setOpaque(false);

        progressLabel.setText(Configuration.getValue("label.waiting"));

        previewLabel.setIcon(ImageUtils.createImageIcon("preview-" + tabName + ".png"));
        previewLabel.setBorder(new FlatButtonBorder());
    }

    public JPanel getProgressPanel() {
        return progressPanel;
    }

    public void setUrlToComponents(String urlToComponents) {
        this.urlToComponents = urlToComponents;
    }

    public String getUrlToComponents() {
        return urlToComponents;
    }

    public void updateComponents(BuilderInterface builderInterface) {
        componentsPanel.removeAll();
        componentsPanel.add(getLoadingPanel());

        new Thread(() -> {

            getComponentsList().stream()
                    .filter(component -> !component.endsWith("litematic"))
                    .forEach(component -> {
                        BuilderComponent builderComponent = new BuilderComponent(tabName);
                        builderInterface.build(builderComponent, component);
                        componentsPanel.add(builderComponent.buildComponent());
                    });

            componentsPanel.updateUI();
            updateSearchLabel();

            componentsPanel.remove(getLoadingPanel());
        }).start();
    }

    private final static Map<String, Box> loadingPanels = new HashMap<>();

    private Box getLoadingPanel(){
        boolean isBigTab = tabName.equals("farms") || tabName.equals("datapacks") || tabName.equals("shaders");
        String typeLoading = isBigTab ? "big" : "small";

        Box loadingPanel = loadingPanels.get(typeLoading);

        if(loadingPanel == null)
            loadingPanel = Box.createVerticalBox();
        else return loadingPanel;

        int countLoading = isBigTab ? 5 : 10;

        for(int x = 0; x < countLoading; x++){

            JPanel panel = new JPanel();
            panel.add(Box.createRigidArea(new Dimension(3, 0)));
            panel.add(new JLabel(ImageUtils.createExtraIcon("loading-" + typeLoading + ".gif")));

            loadingPanel.add(panel);
        }

        loadingPanels.put(typeLoading, loadingPanel);

        return loadingPanel;
    }

    public void updateSearchLabel() {
        ((SearchPanel) searchPanel.getComponents()[0]).updateSearchLabel();
    }

    private HashMap<String, Set<String>> componentsList = new HashMap<>();

    public Set<String> getComponentsList(){

        if(componentsList.get(urlToComponents) != null) return componentsList.get(urlToComponents);

        Set<String> modsList = WebUtils.getWebNames(urlToComponents, componentType);

        componentsList.put(urlToComponents, modsList);

        return modsList;
    }


    public void addComponentsToInstallPanel(JPanel panel){
        installPanel.add(panel);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPanel = new CustomScrollPanel();
        jPanel1 = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        searchPanel.add(new SearchPanel(tabName));
        componentsPanel = new javax.swing.JPanel();
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage backgroundImage = ImageUtils.createBufferedImageTheme(tabName + "-background.png",
                    ColorUtils.makeBrighterOrDarker(SwingUtils.getColor(0), 20));
                backgroundImage.setAccelerationPriority(1.0f);

                int width = Math.min(backgroundImage.getWidth(), getWidth());
                int height = Math.min(backgroundImage.getHeight(), getHeight());
                int x = Math.max(0, (backgroundImage.getWidth() - width) / 2);
                int y = Math.max(0, (backgroundImage.getHeight() - height) / 2);
                g.drawImage(backgroundImage, 0, 0, width, height, x, y, x + width, y + height, null);
            }
        };
        installPanel = new javax.swing.JPanel();
        shadowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image backgroundImage = ImageUtils.createBufferedImage("shadow.png");
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            }
        };
        jPanel2 = new javax.swing.JPanel();
        previewLabel = new javax.swing.JLabel();
        progressPanel = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        progressLabel = new javax.swing.JLabel();

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 422, Short.MAX_VALUE)
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout componentsPanelLayout = new javax.swing.GroupLayout(componentsPanel);
        componentsPanel.setLayout(componentsPanelLayout);
        componentsPanelLayout.setHorizontalGroup(
            componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 822, Short.MAX_VALUE)
        );
        componentsPanelLayout.setVerticalGroup(
            componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 420, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(componentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(componentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        scrollPanel.setViewportView(jPanel1);

        backgroundPanel.setOpaque(false);

        installPanel.setPreferredSize(new java.awt.Dimension(450, 200));

        javax.swing.GroupLayout installPanelLayout = new javax.swing.GroupLayout(installPanel);
        installPanel.setLayout(installPanelLayout);
        installPanelLayout.setHorizontalGroup(
            installPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 441, Short.MAX_VALUE)
        );
        installPanelLayout.setVerticalGroup(
            installPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 159, Short.MAX_VALUE)
        );

        shadowPanel.setPreferredSize(new java.awt.Dimension(15, 10));

        javax.swing.GroupLayout shadowPanelLayout = new javax.swing.GroupLayout(shadowPanel);
        shadowPanel.setLayout(shadowPanelLayout);
        shadowPanelLayout.setHorizontalGroup(
            shadowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );
        shadowPanelLayout.setVerticalGroup(
            shadowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel2.setOpaque(false);

        progressPanel.setOpaque(false);

        jProgressBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        progressLabel.setLabelFor(jProgressBar1);
        progressLabel.setText("jLabel1");

        javax.swing.GroupLayout progressPanelLayout = new javax.swing.GroupLayout(progressPanel);
        progressPanel.setLayout(progressPanelLayout);
        progressPanelLayout.setHorizontalGroup(
            progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(progressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, progressPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(progressLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
                .addContainerGap())
        );
        progressPanelLayout.setVerticalGroup(
            progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(progressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progressPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(previewLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(progressPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(previewLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(installPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(shadowPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(installPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(shadowPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(backgroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(scrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JPanel componentsPanel;
    private javax.swing.JPanel installPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel shadowPanel;
    // End of variables declaration//GEN-END:variables
}
