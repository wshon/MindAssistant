package MindAssistant.graphics.draw;

import MindAssistant.MindVars;
import MindAssistant.ui.settings.SettingsMenuDialog.SettingsTable;
import arc.struct.Seq;

/**
 * @author wshon
 */
public abstract class BaseDrawer<T> {
    private final String drawerName;
    public static Seq<BaseDrawer<?>> allDrawer = new Seq<>();

    public BaseDrawer() {
        this.drawerName = this.getClass().getSimpleName();
        allDrawer.add(this);
    }

    public String getDrawerName() {
        return drawerName;
    }

    public void setPrefTo(SettingsTable setting) {
    }

    public void loadSettings() {
    }

    public boolean enabled() {
        return MindVars.settings.getBool("enable" + this.drawerName, true);
    }

    public boolean isValid() {
        return true;
    }

    public abstract void draw(T type);
}
