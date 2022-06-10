package MindAssistant.graphics.draw;

import MindAssistant.MindVars;

/**
 * @author wshon
 */
public abstract class BaseDrawer<T> {
    public void loadSettings() {
    }

    public boolean enabled() {
        return MindVars.settings.getBool("enable" + this.getClass().getName(), true);
    }

    public boolean isValid() {
        return true;
    }

    public abstract void draw(T type);
}
