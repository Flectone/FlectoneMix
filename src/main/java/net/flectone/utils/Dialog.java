package net.flectone.utils;

import net.flectone.swing.filechooser.JFileChooserExtend;
import net.flectone.system.Configuration;
import net.flectone.system.SystemInfo;

import javax.swing.*;
import java.io.File;

public class Dialog {

    public static int showYesOrNo(String message){

        String[] options = new String[]{Configuration.getValue("label.yes"), Configuration.getValue("label.no")};

        return JOptionPane.showOptionDialog(null, message, Configuration.getValue("label.confirm_dialog"),
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, null);
    }

    public static void showException(Exception e) {

        String message = e.toString();
        for (StackTraceElement ste : e.getStackTrace()) {
            message += "\n\tat " + ste.toString();
        }

        JOptionPane.showMessageDialog(null, message, Configuration.getValue("label.error"), JOptionPane.ERROR_MESSAGE);
    }

    public static void showInformation(String message){
        JOptionPane.showConfirmDialog(null, message, Configuration.getValue("label.information"), JOptionPane.DEFAULT_OPTION);
    }

    public static void showSelectMinecraftFolder(){

        JFileChooser fileChooserExtend = new JFileChooser(SystemInfo.getInstance().getMinecraftDirectory());

        fileChooserExtend.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = fileChooserExtend.showSaveDialog(null);

        if(returnValue != JFileChooserExtend.APPROVE_OPTION) return;

        SystemInfo.getInstance().setMinecraftDirectory(fileChooserExtend.getSelectedFile().getPath() + File.separator);
    }
}
