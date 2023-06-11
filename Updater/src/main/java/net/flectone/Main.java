package net.flectone;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {

        String path = args[0];

        new File(path).delete();

        String fileExtension = path.substring(path.lastIndexOf(".") + 1);

        IOUtils.downloadFile("https://flectone.net/components/last/flectonemix." + fileExtension, path);

        IOUtils.getJarProcess(path)
                .inheritIO()
                .start();

        System.exit(0);
    }
}