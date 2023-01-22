package MindAssistant.modules;

import MindAssistant.modules.spawn.*;
import MindAssistant.ui.settings.SettingsMenuDialog.SettingsTable;
import arc.struct.Seq;

public class Modules {
    private static final Seq<BaseModule> ALL_MODULE = new Seq<>();

    public static void init() {
        ALL_MODULE.add(new WaveSpawnerModule(), new MinimapSpawnerModule(), new WidelyZoomModule());
        Modules.load();
    }

    public static void load() {
        ALL_MODULE.each(BaseModule::load);
    }

    public static void loadSettings(SettingsTable st) {
        ALL_MODULE.each((m) -> m.setPrefTo(st));
    }
}

