package net.flectone.swing.filechooser;

import sun.swing.FilePane;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

/**
 * Это класс, который расширет стандартный выбор файлов и
 * имеет возможность работать с контекстным меню выбора файлов
 * <p>
 *
 * @author ezhov_da
 */
public class JFileChooserExtend extends JFileChooser {
    protected JPopupMenu popupMenuContextForFileChooser;

    public JFileChooserExtend() {
        initFilePane();
    }

    public JFileChooserExtend(String currentDirectoryPath) {
        super(currentDirectoryPath);
        initFilePane();
    }

    public JFileChooserExtend(File currentDirectory) {
        super(currentDirectory);
        initFilePane();
    }

    public JFileChooserExtend(FileSystemView fsv) {
        super(fsv);
        initFilePane();
    }

    public JFileChooserExtend(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);
        initFilePane();
    }

    public JFileChooserExtend(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
        initFilePane();
    }

    protected void initFilePane() {
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof FilePane) {
                FilePane filePane = (FilePane) component;
                popupMenuContextForFileChooser = filePane.getComponentPopupMenu();
                break;
            }
        }
    }

    //удалось ли инициализировать контекстное меню
    public boolean isInitPopupMenu() {
        return popupMenuContextForFileChooser != null;
    }


    public void changeGoUp(String newName) {
        if (popupMenuContextForFileChooser != null) {
            JMenuItem menuItem = (JMenuItem) popupMenuContextForFileChooser.getComponent(0);
            if ("Go Up".equals(menuItem.getText())) {
                if (newName != null) {
                    menuItem.setText(newName);
                }
            }
        }
    }

    public JPopupMenu getPopupMenuContextForFileChooser() {
        return popupMenuContextForFileChooser;
    }
}