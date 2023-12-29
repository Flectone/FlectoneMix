package net.flectone.mix.javafx;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;

@AllArgsConstructor
public enum PaneType {

    WINDOW("app/window"),
    AUTH("app/auth"),
    SEARCH("app/search_panel"),
    OPTIMIZATION("tab/optimization_setting"),
    FARMS("tab/farms_setting"),
    MODS("tab/mods_setting"),
    PLUGINS("tab/plugins_setting"),
    DATAPACKS("tab/datapacks_setting"),
    RESOURCEPACKS("tab/resourcepacks_setting"),
    SHADERS("tab/shaders_setting"),
    COMPONENTS("component_panel"),
    COMPONENT("component"),
    APP("app/app"),
    SETTING("tab/setting_setting"),
    EXCEPTION("alert/alert_exception"),
    INFO("alert/alert_info"),
    CONFIRMATION("alert/alert_confirmation"),
    WARN("alert/alert_warn"),
    LEFT("app/left_panel"),
    AD("app/ad");

    public final String name;

    @Override
    public String toString() {
        return name;
    }

    public static PaneType fromString(@NonNull String string) {
        return Arrays.stream(PaneType.values())
                .parallel()
                .filter(paneType -> string.equals(paneType.toString()))
                .findAny()
                .orElse(null);
    }
}
