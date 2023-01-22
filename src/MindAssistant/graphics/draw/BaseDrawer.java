package MindAssistant.graphics.draw;

import MindAssistant.MindVars;
import MindAssistant.graphics.Render;
import MindAssistant.ui.settings.SettingsMenuDialog.SettingsTable;
import arc.struct.Seq;

/**
 * @author wshon
 */
public abstract class BaseDrawer<T> {
    public static Seq<BaseDrawer<?>> allDrawer = new Seq<>();

    private final String drawerName;

    public BaseDrawer() {
        this.drawerName = this.getClass().getSimpleName();
        allDrawer.add(this);
    }

    public String getDrawerName() {
        return drawerName;
    }

    public void setPrefTo(SettingsTable st) {
        st.checkPref("drawer." + this.getDrawerName() + ".enable", true, (v) -> Render.loadEnabled());
    }

    public boolean enabled() {
        return MindVars.settings.getBool("drawer." + this.getDrawerName() + ".enable", true);
    }

    public boolean isValid() {
        return true;
    }

    public abstract void draw(T type);

    public static void loadSettings(SettingsTable st) {
        BaseDrawer.allDrawer.each((d) -> d.setPrefTo(st));
    }
}
