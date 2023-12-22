package net.flectone.mix.model;

import com.google.gson.annotations.SerializedName;

public record Component(
        @SerializedName("component_id") int id,
        @SerializedName("key_name") String key,
        @SerializedName("icon_url") String icon,
        @SerializedName("author_name") String author,
        @SerializedName("source_name") String source,
        @SerializedName("name_text") String name,
        @SerializedName("description_text") String description,
        @SerializedName("type_name") String type,
        @SerializedName("version_name") String version,
        @SerializedName("link_name") String link
) {}
