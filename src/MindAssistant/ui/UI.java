package MindAssistant.ui;

import MindAssistant.ui.override.BetterInfoTable;
import MindAssistant.ui.fragments.HudWaveFragment;
import MindAssistant.ui.settings.SettingsMenuDialog;
import mindustry.Vars;
import mindustry.ui.fragments.HudFragment;

public class UI {
    public BetterInfoTable betterHover;
    public HudWaveFragment wavefrag;
    public SettingsMenuDialog settingsMenuDialog;

    public void init() {
        betterHover = new BetterInfoTable();
        wavefrag = new HudWaveFragment();
        settingsMenuDialog = new SettingsMenuDialog();;

        wavefrag.build(Vars.ui.hudGroup);
    }
}
