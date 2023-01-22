package MindAssistant.modules;

import MindAssistant.MindVars;
import MindAssistant.ui.settings.SettingsMenuDialog.SettingsTable;

/**
 * @author wshon
 */
public abstract class BaseModule {

    private final String moduleName;
    private Boolean enable;

    public BaseModule() {
        this.moduleName = this.getClass().getSimpleName();
        this.syncEnabled();
    }

    public String getModuleName() {
        return moduleName;
    }

    public boolean isEnabled() {
        return enable;
    }

    private void syncEnabled() {
        this.enable = MindVars.settings.getBool("module." + this.getModuleName() + ".enable", true);
        onEnabledChange(enable);
    }

    protected void onEnabledChange(boolean enabled) {
    }

    public void setPrefTo(SettingsTable st) {
        st.checkPref("module." + this.getModuleName() + ".enable", true, b -> syncEnabled());
    }

    public abstract void load();
}
