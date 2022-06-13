package MindAssistant.ui.override.infotable;

import MindAssistant.ui.override.infotable.builder.BaseBuilder;
import arc.math.geom.Position;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.gen.Tex;

/**
 * @author wshon
 */
public abstract class BaseInfoTable<T extends Position> extends Table {
    T hover, lastHover;
    final Seq<BaseBuilder<T>> allBuilders = new Seq<>();

    public BaseInfoTable() {
        background(Tex.pane);
    }

    @SafeVarargs
    public final BaseInfoTable<T> addBuilders(BaseBuilder<T>... builders) {
        allBuilders.addAll(builders);
        return this;
    }

    public void update() {
        hover = hovered();
        if (shouldRebuild()) rebuild();
        lastHover = hover;
    }

    public void rebuild() {
        clearChildren();
        build();
    }

    public boolean shouldAdd() {
        return hover != null;
    }

    public boolean shouldRebuild() {
        return shouldAdd() && hover != lastHover;
    }

    @Nullable
    public abstract T hovered();

    protected abstract void build();
}
