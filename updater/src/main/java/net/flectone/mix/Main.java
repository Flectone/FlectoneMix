package net.flectone.mix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) return;
        System.out.println(args[0] + " " + args[1]);

        Thread.sleep(Duration.ofSeconds(5).toMillis());

        Path fileToMovePath = Paths.get(args[0]);
        Path targetPath = Paths.get(args[1]);

        Path targetFile = Files.move(fileToMovePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        new ProcessBuilder(fileToMovePath.endsWith("jar") ? "java -jar" : "",
                targetFile.toString())
                .inheritIO()
                .start();

        System.exit(0);
    }
}