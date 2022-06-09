package MindAssistant.graphics.draw;

/**
 * @author wangsen
 */
public abstract class BaseDrawer<T> {
    public void loadSettings() {
    }

    public boolean enabled() {
        return false;
    }

    public boolean isValid() {
        return true;
    }

    public void draw(T type) {

    }
}
