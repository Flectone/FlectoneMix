package net.flectone.mix.javafx.component;

import ch.micheljung.fxwindow.FxStage;
import ch.micheljung.fxwindow.StageConfigurer;
import javafx.stage.Stage;
import net.flectone.mix.javafx.FlectoneMix;

public class FStage extends Stage {

    public FStage() {
        super();
    }

    public void makeUndecorated() {
        if (!FlectoneMix.getApp().getConfig().isUsedUndecoratedWindow()) return;

        StageConfigurer fxStage = FxStage.configure(this);
        fxStage.apply();
    }

    public void customShow() {
        makeUndecorated();
        super.show();
    }
}
