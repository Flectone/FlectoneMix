package net.flectone.mix.server;

import javafx.concurrent.Task;
import net.flectone.mix.model.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileDownloader extends Task<Void> {

    private final String downloadUrl;
    private final File outputFile;

    private final boolean unzip;
    private final boolean isFile;

    public FileDownloader(Component component, String outputDirectory) {
        this(component, outputDirectory, false);
    }

    public FileDownloader(Component component, String outputDirectory, boolean unzip) {
        this.unzip = unzip;
        this.isFile = true;

        String fileExtension = "";
        int extensionIndex = component.link().lastIndexOf(".");
        if (extensionIndex != -1) fileExtension = component.link().substring(extensionIndex);

        this.downloadUrl = component.link();
        this.outputFile = new File(outputDirectory + component.key() + fileExtension);
    }

    public FileDownloader(String downloadUrl, File outputDir) {
        this.downloadUrl = downloadUrl;
        this.outputFile = outputDir;
        this.unzip = false;
        this.isFile = false;
    }

    @Override
    protected Void call() throws Exception {
        URL url = new URL(downloadUrl);

        Path outputPath = isFile ? outputFile.toPath() : Paths.get(outputFile.toPath() + File.separator + Paths.get(url.getPath()).getFileName());

        System.out.println(downloadUrl + " " + outputPath);

        Files.createDirectories(outputPath.getParent());

        try (InputStream in = url.openStream()) {
            Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
        }

        if (unzip && outputFile.getName().toLowerCase().endsWith(".zip")) {
            unzipFile(outputFile, outputPath.getParent().toFile());
            Files.delete(outputFile.toPath());
        }

        return null;
    }

    private void unzipFile(File zipFile, File outputFolder) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path entryPath = outputFolder.toPath().resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.copy(zipInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipInputStream.closeEntry();
            }
        }
    }
}

