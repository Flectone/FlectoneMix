package net.flectone.utils;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import net.flectone.swing.frames.CustomFrame;
import net.flectone.swing.panels.CustomScrollPanel;
import net.flectone.system.Configuration;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class SwingUtils {

    private static Color[] colors = new Color[3];

    public static String theme = "black";

    public static Color getColor(int number){
        return colors[number];
    }


    public static void setColors(Color color) {
        SwingUtils.colors = new Color[]{color, ColorUtils.makeBrighterOrDarker(color, 15), ColorUtils.getComponentColor(color)};

        ImageUtils.changeIconsColor(colors[2]);

        Map<String, String> themeChangeMap = new HashMap();
        themeChangeMap.put("@background", ColorUtils.toHEX(colors[0]));
        themeChangeMap.put("@foreground", ColorUtils.toHEX(colors[2]));
        themeChangeMap.put("@accentColor", "#5cb8ed");
        themeChangeMap.put("@accentFocusColor", "#5baede");
        themeChangeMap.put("CheckBox.icon.style", "filled");
        themeChangeMap.put("CheckBox.icon[filled].selectedBorderColor", " @accentColor");
        themeChangeMap.put("CheckBox.icon[filled].selectedBackground", "@accentColor");
        themeChangeMap.put("CheckBox.icon[filled].checkmarkColor", "@foreground");
        themeChangeMap.put("CheckBox.icon.focusedBackground", "@background");
        themeChangeMap.put("RadioButton.icon.style", "filled");
        themeChangeMap.put("RadioButton.icon[filled].centerDiameter", "6");
        themeChangeMap.put("Button.focusedBackground", "@background");
        themeChangeMap.put("Separator.foreground", "@foreground");
        themeChangeMap.put("CheckBox.icon[filled].focusedCheckmarkColor", "@foreground");
        themeChangeMap.put("TextField.inactiveBackground", ColorUtils.toHEX(colors[1]));

        FlatLaf.setGlobalExtraDefaults(themeChangeMap);
        FlatLightLaf.setup();
        CustomFrame.updateAllFrames();
        CustomScrollPanel.update();
        updateIconBorders();

        System.setProperty("file.encoding", "UTF-8");
    }


    public static void setFont(Font font) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(font));
            }
        }
    }

    public static void updateJComponentText() {

        String[] listComponents = {"AbstractButton.clickText",
                "AbstractDocument.additionText",
                "AbstractDocument.deletionText",
                "AbstractDocument.redoText",
                "AbstractDocument.styleChangeText",
                "AbstractDocument.undoText",
                "AbstractUndoableEdit.redoText",
                "AbstractUndoableEdit.undoText",
                "ColorChooser.cancelText",
                "ColorChooser.hsbBlueText",
                "ColorChooser.hsbBrightnessText",
                "ColorChooser.hsbDisplayedMnemonicIndex",
                "ColorChooser.hsbGreenText",
                "ColorChooser.hsbHueText",
                "ColorChooser.hsbMnemonic",
                "ColorChooser.hsbNameText",
                "ColorChooser.hsbRedText",
                "ColorChooser.hsbSaturationText",
                "ColorChooser.okText",
                "ColorChooser.previewText",
                "ColorChooser.resetMnemonic",
                "ColorChooser.resetText",
                "ColorChooser.rgbBlueMnemonic",
                "ColorChooser.rgbBlueText",
                "ColorChooser.rgbDisplayedMnemonicIndex",
                "ColorChooser.rgbGreenMnemonic",
                "ColorChooser.rgbGreenText",
                "ColorChooser.rgbMnemonic",
                "ColorChooser.rgbNameText",
                "ColorChooser.rgbRedMnemonic",
                "ColorChooser.rgbRedText",
                "ColorChooser.sampleText",
                "ColorChooser.swatchesDisplayedMnemonicIndex",
                "ColorChooser.swatchesMnemonic",
                "ColorChooser.swatchesNameText",
                "ColorChooser.swatchesRecentText",
                "ComboBox.togglePopupText",
                "FileChooser.acceptAllFileFilterText",
                "FileChooser.cancelButtonMnemonic",
                "FileChooser.cancelButtonText",
                "FileChooser.cancelButtonToolTipText",
                "FileChooser.directoryDescriptionText",
                "FileChooser.directoryOpenButtonMnemonic",
                "FileChooser.directoryOpenButtonText",
                "FileChooser.directoryOpenButtonToolTipText",
                "FileChooser.fileDescriptionText",
                "FileChooser.helpButtonMnemonic",
                "FileChooser.helpButtonText",
                "FileChooser.helpButtonToolTipText",
                "FileChooser.newFolderErrorText",
                "FileChooser.openButtonMnemonic",
                "FileChooser.openButtonText",
                "FileChooser.openButtonToolTipText",
                "FileChooser.openDialogTitleText",
                "FileChooser.saveButtonMnemonic",
                "FileChooser.saveButtonText",
                "FileChooser.saveButtonToolTipText",
                "FileChooser.saveDialogTitleText",
                "FileChooser.updateButtonMnemonic",
                "FileChooser.updateButtonText",
                "FileChooser.updateButtonToolTipText",
                "FormView.browseFileButtonText",
                "FormView.resetButtonText",
                "FormView.submitButtonText",
                "InternalFrame.closeButtonToolTip",
                "InternalFrame.iconButtonToolTip",
                "InternalFrame.maxButtonToolTip",
                "InternalFrame.restoreButtonToolTip",
                "InternalFrameTitlePane.closeButtonAccessibleName",
                "InternalFrameTitlePane.closeButtonText",
                "InternalFrameTitlePane.iconifyButtonAccessibleName",
                "InternalFrameTitlePane.maximizeButtonAccessibleName",
                "InternalFrameTitlePane.maximizeButtonText",
                "InternalFrameTitlePane.minimizeButtonText",
                "InternalFrameTitlePane.moveButtonText",
                "InternalFrameTitlePane.restoreButtonText",
                "InternalFrameTitlePane.sizeButtonText",
                "OptionPane.cancelButtonMnemonic",
                "OptionPane.cancelButtonText",
                "OptionPane.inputDialogTitle",
                "OptionPane.messageDialogTitle",
                "OptionPane.noButtonMnemonic",
                "OptionPane.noButtonText",
                "OptionPane.okButtonMnemonic",
                "OptionPane.okButtonText",
                "OptionPane.titleText",
                "OptionPane.yesButtonMnemonic",
                "OptionPane.yesButtonText",
                "PrintingDialog.abortButtonDisplayedMnemonicIndex",
                "PrintingDialog.abortButtonMnemonic",
                "PrintingDialog.abortButtonText",
                "PrintingDialog.abortButtonToolTipText",
                "PrintingDialog.contentAbortingText",
                "PrintingDialog.contentInitialText",
                "PrintingDialog.titleProgressText",
                "ProgressMonitor.progressText",
                "SplitPane.leftButtonText",
                "SplitPane.rightButtonText",
                "FileChooser.detailsViewActionLabelText",
                "FileChooser.detailsViewButtonAccessibleName",
                "FileChooser.detailsViewButtonToolTipText",
                "FileChooser.fileAttrHeaderText",
                "FileChooser.fileDateHeaderText",
                "FileChooser.fileNameHeaderText",
                "FileChooser.fileNameLabelText",
                "FileChooser.fileSizeHeaderText",
                "FileChooser.fileTypeHeaderText",
                "FileChooser.filesOfTypeLabelText",
                "FileChooser.homeFolderAccessibleName",
                "FileChooser.homeFolderToolTipText",
                "FileChooser.listViewActionLabelText",
                "FileChooser.listViewButtonAccessibleName",
                "FileChooser.listViewButtonToolTipText",
                "FileChooser.lookInLabelText",
                "FileChooser.newFolderAccessibleName",
                "FileChooser.newFolderActionLabelText",
                "FileChooser.newFolderToolTipText",
                "FileChooser.refreshActionLabelText",
                "FileChooser.saveInLabelText",
                "FileChooser.upFolderAccessibleName"
        };

        for(String componentName : listComponents){
            UIManager.put(componentName, Configuration.getValue(componentName));
        }
    }

    private static Map<String, ArrayList<JComponent>> tabNameToComponentsMap = new HashMap<>();

    public static void clearTabsComponents(String tabName){
        tabNameToComponentsMap.get(tabName).clear();
    }

    public static void clearAllTabs(){
        tabNameToComponentsMap.clear();
    }

    public static void addTabsComponents(String tabName, ArrayList<JComponent> components){

        if(tabNameToComponentsMap.containsKey(tabName)){
            ArrayList<JComponent> existingComponents = tabNameToComponentsMap.get(tabName);
            existingComponents.addAll(components);
            return;
        }

        tabNameToComponentsMap.put(tabName, components);

    }

    public static ArrayList<JComponent> getTabsComponents(String tabName){
        return tabNameToComponentsMap.get(tabName);
    }

    private static void updateIconBorders(){
        tabNameToComponentsMap.values().forEach(arrayList ->
                arrayList.stream()
                        .filter(c -> c instanceof JLabel)
                        .map(c -> (JLabel) c)
                        .filter(l -> l.getIcon() != null)
                        .forEach(l -> l.setBorder(new FlatButtonBorder())));
    }
}
