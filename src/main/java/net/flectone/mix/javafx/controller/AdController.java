package net.flectone.mix.javafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import net.flectone.mix.javafx.FlectoneMix;
import net.flectone.mix.util.WebUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class AdController implements Initializable {

    private static final String FLECTONECHAT_URL = "https://modrinth.com/plugin/flectonechat";

    @FXML
    private Label adTitle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        adTitle.setText(FlectoneMix.getApp().getConfig().getLocaleString("search.ad.title"));

        if (url == null) return;
    }

    public void clickEvent() {
        WebUtil.openUrl(FLECTONECHAT_URL);
    }
}
