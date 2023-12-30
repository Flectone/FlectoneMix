package net.flectone.mix.server;

import javafx.concurrent.Task;
import net.flectone.mix.javafx.component.FAlert;
import net.flectone.mix.model.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUpdater extends Task<Void> {

    private final String downloadUrl;
    private final File outputFile;
    private Component component;

    public FileUpdater(Component component, String outputDirectory) {
        this.component = component;
        String fileExtension = component.link().substring(component.link().lastIndexOf("."));
        this.downloadUrl = component.link();
        this.outputFile = new File(outputDirectory + "FlectoneMix " + component.key() + fileExtension);
    }

    public FileUpdater(String downloadUrl, File outputFile) {
        this.downloadUrl = downloadUrl;
        this.outputFile = outputFile;
    }

    @Override
    protected Void call() throws Exception {
        URL url = new URL(downloadUrl);
        Path outputPath = outputFile.toPath().getParent();
        if (!outputFile.exists()) return null;

        Files.createDirectories(outputPath);

        try (InputStream in = url.openStream()) {

            Files.copy(in, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            new FAlert(FAlert.Type.EXCEPTION, e).show();
        }

        if (component != null) {
            component.increaseDownloads();
        }


        return null;
    }
}

