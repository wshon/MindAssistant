package MindAssistant.ui;

import MindAssistant.ui.override.BetterInfoTable;
import MindAssistant.ui.settings.SettingsMenuDialog;

public class UI {
    public BetterInfoTable betterHover;
    public SettingsMenuDialog settingsMenuDialog;

    public void init() {
        betterHover = new BetterInfoTable();
        settingsMenuDialog = new SettingsMenuDialog();
    }
}
