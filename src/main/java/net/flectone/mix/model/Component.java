package net.flectone.mix.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.javafx.controller.ComponentController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class Component {

    @SerializedName("component_id") final int id;
    @SerializedName("key_name") final String key;
    @SerializedName("icon_url") final String icon;
    @SerializedName("author_name") final String author;
    @SerializedName("source_name") final String source;
    @SerializedName("name_text") final String name;
    @SerializedName("description_text") final String description;
    @SerializedName("type_name") final String type;
    @SerializedName("version_name") final String version;
    @NonNull @SerializedName("link_name") String link;
    @NonNull @SerializedName("download_count") int downloads;
    @NonNull @SerializedName("rating_count") int ratings;
    @NonNull @SerializedName("avg_rating") double avgRating;

    @Setter @Accessors(fluent = false) private transient ComponentController controller;

    public void increaseDownloads() {
        FlectoneMix.getApp().getThreadPool().execute(() -> {
            try {
                DiscordUser discordUser = FlectoneMix.getApp().getDiscordUser();
                String stringUrl = String.format("https://mix.flectone.net/api/components/download?id=%s&token=%s&componentId=%s",
                        discordUser.id(), discordUser.token(), id);
                handleDownloadRequest(stringUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setRatings(int value) {
        try {
            DiscordUser discordUser = FlectoneMix.getApp().getDiscordUser();
            String stringUrl = String.format("https://mix.flectone.net/api/components/rating?id=%s&token=%s&componentId=%s&value=%s",
                    discordUser.id(), discordUser.token(), id, value);
            handleRatingRequest(stringUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDownloadRequest(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            controller.updateDownloads(++downloads);
        }
    }

    private void handleRatingRequest(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        int responseCode = connection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) return;

        Rating rating = getRatingFromApi(String.format("https://mix.flectone.net/api/components/rating?id=%s&token=%s&componentId=%s",
                FlectoneMix.getApp().getDiscordUser().id(), FlectoneMix.getApp().getDiscordUser().token(), id));

        if (rating != null) {
            ratings = rating.ratings;
            avgRating = rating.avgRating;
        }
    }

    private Rating getRatingFromApi(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) return null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
            return FlectoneMix.getGson().fromJson(jsonArray.get(0), Rating.class);
        }
    }

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    private static class Rating {
        @NonNull @SerializedName("rating_count") int ratings;
        @NonNull @SerializedName("avg_rating") double avgRating;
    }
}
