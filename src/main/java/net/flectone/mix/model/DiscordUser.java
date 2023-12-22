package net.flectone.mix.model;

import javafx.scene.image.Image;

public record DiscordUser(String id, String username, String avatar, String token, boolean isInGuild) {

    private static Image imageAvatar;

    public Image getNewAvatar() {
        return new Image("https://cdn.discordapp.com/avatars/" + id + "/" + avatar + ".png");
    }

    public Image getAvatar() {
        if (imageAvatar == null)
            imageAvatar = getNewAvatar();

        return imageAvatar;
    }
}
