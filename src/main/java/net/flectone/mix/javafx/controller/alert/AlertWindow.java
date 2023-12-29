package net.flectone.mix.javafx.controller.alert;

import javafx.scene.control.Label;
import net.flectone.mix.javafx.component.FStage;

public interface AlertWindow {

    void okButtonEvent();

    void setStage(FStage stage);

    Label getTextLabel();
}
