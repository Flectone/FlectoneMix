package net.flectone.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class WebUtils {

    public static void openUrl(String link) {
        try {
            URI uri = new URI(link);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
            } else {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("xdg-open " + link);
            }
        } catch (IOException | URISyntaxException e) {
            Dialog.showException(e);
        }
    }

    public static Set<String> getWebNames(String url, String filter) {

        Set<String> componentNames = new HashSet<>();

        try {
            Elements links = Jsoup.connect("https://flectone.net/" + url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .get()
                    .select("a[href]");

            for(Element link : links){
                String name = link.attr("href");


                if(filter.equals("")){
                    if(name.contains(".")) componentNames.add(name);
                    continue;
                }

                if(!name.endsWith(filter)) continue;

                componentNames.add(name.split(filter)[0]);
            }

        } catch (IOException exception){
            Dialog.showException(exception);
        }

        return componentNames;
    }

}
