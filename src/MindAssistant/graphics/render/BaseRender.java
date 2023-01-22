package MindAssistant.graphics.render;

import MindAssistant.graphics.draw.BaseDrawer;
import arc.struct.Seq;

/**
 * @author wshon
 */
public abstract class BaseRender<T extends BaseDrawer<?>> {

    private final Seq<T> allGlobalDrawers = new Seq<>();
    private final Seq<T> allCameraDrawers = new Seq<>();
    private final Seq<T> allSelectDrawers = new Seq<>();
    private Seq<T> enabledGlobalDrawers = new Seq<>();
    private Seq<T> enabledCameraDrawers = new Seq<>();
    private Seq<T> enabledSelectDrawers = new Seq<>();

    @SafeVarargs
    public final BaseRender<T> addGlobalDrawer(T... drawers) {
        allGlobalDrawers.addAll(drawers);
        return this;
    }
    public final BaseRender<T> addGlobalDrawer(T drawer) {
        allGlobalDrawers.add(drawer);
        return this;
    }

    @SafeVarargs
    public final BaseRender<T> addCameraDrawer(T... drawers) {
        allCameraDrawers.addAll(drawers);
        return this;
    }
    public final BaseRender<T> addCameraDrawer(T drawer) {
        allCameraDrawers.add(drawer);
        return this;
    }

    @SafeVarargs
    public final BaseRender<T> addHoveredDrawer(T... drawers) {
        allSelectDrawers.addAll(drawers);
        return this;
    }
    public final BaseRender<T> addHoveredDrawer(T drawer) {
        allSelectDrawers.add(drawer);
        return this;
    }

    public void render() {
        Seq<T> validDrawers;
        if ((validDrawers = enabledGlobalDrawers.select(BaseDrawer::isValid)).any()) {
            globalRender(validDrawers);
        }
        validDrawers.clear();
        if ((validDrawers = enabledCameraDrawers.select(BaseDrawer::isValid)).any()) {
            cameraRender(validDrawers);
        }
        validDrawers.clear();
        if ((validDrawers = enabledSelectDrawers.select(BaseDrawer::isValid)).any()) {
            selectRender(validDrawers);
        }
    }

    public void loadEnabled() {
        enabledGlobalDrawers = allGlobalDrawers.select(BaseDrawer::enabled);
        enabledCameraDrawers = allCameraDrawers.select(BaseDrawer::enabled);
        enabledSelectDrawers = allSelectDrawers.select(BaseDrawer::enabled);
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

    /**
     * Render on select
     *
     * @param validDrawers drawers enabled
     */
    public abstract void selectRender(Seq<T> validDrawers);
}
