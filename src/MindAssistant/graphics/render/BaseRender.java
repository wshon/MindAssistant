package MindAssistant.graphics.render;

import MindAssistant.graphics.draw.BaseDrawer;
import arc.struct.Seq;

/**
 * @author wshon
 */
public abstract class BaseRender<T extends BaseDrawer<?>> {

    private final Seq<T> allGlobalDrawers = new Seq<>();
    private final Seq<T> allCameraDrawers = new Seq<>();
    private Seq<T> enabledGlobalDrawers = new Seq<>();
    private Seq<T> enabledCameraDrawers = new Seq<>();

    @SafeVarargs
    public final BaseRender<T> addGlobalDrawers(T... drawers) {
        allGlobalDrawers.addAll(drawers);
        return this;
    }

    @SafeVarargs
    public final BaseRender<T> addCameraDrawers(T... drawers) {
        allCameraDrawers.addAll(drawers);
        return this;
    }

    public void render() {
        Seq<T> validDrawers;
        if ((validDrawers = enabledGlobalDrawers.select(BaseDrawer::isValid)).any()) {
            globalRender(validDrawers);
        }
        if ((validDrawers = enabledCameraDrawers.select(BaseDrawer::isValid)).any()) {
            cameraRender(validDrawers);
        }
    }

    public void loadEnabled() {
        enabledGlobalDrawers = allGlobalDrawers.select(BaseDrawer::enabled);
        enabledCameraDrawers = allCameraDrawers.select(BaseDrawer::enabled);
    }

    public void loadSettings() {
        allGlobalDrawers.each(BaseDrawer::loadSettings);
        allCameraDrawers.each(BaseDrawer::loadSettings);
    }

    /**
     * Render always
     *
     * @param validDrawers drawers enabled
     */
    public abstract void globalRender(Seq<T> validDrawers);

    /**
     * Render on camera
     *
     * @param validDrawers drawers enabled
     */
    public abstract void cameraRender(Seq<T> validDrawers);
}
