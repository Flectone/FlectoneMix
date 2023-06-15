package net.flectone.swing.filechooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс, который расширяет меню для выбора файлов
 * и добавляет возможность открывать выбранные файлы
 * <p>
 *
 * @author ezhov_da
 */
public class JFileChooserOpenFiles extends JFileChooserExtend {
    private static final Logger LOG = Logger.getLogger(JFileChooserOpenFiles.class.getName());

    public JFileChooserOpenFiles() {
        super();
    }

    public void addOpenFiles() {
        if (super.popupMenuContextForFileChooser != null) {
            super.popupMenuContextForFileChooser.insert(new AbstractAction() {
                {
                    putValue(NAME, "Открыть выбранный файл(ы)");
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    File[] files = getSelectedFiles();
                    for (File file : files) {
                        if (file.exists()) {
                            try {
                                Desktop.getDesktop().open(file);
                            } catch (IOException ex) {
                                LOG.log(Level.SEVERE, "Не удалось открыть файл:" + file.getAbsolutePath() + "]", ex);
                            }
                        }
                    }
                }
            }, 0);
        }
    }

}
