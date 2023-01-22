package MindAssistant.ui.override.infotable.builder;

import arc.math.geom.Position;
import arc.scene.ui.layout.Table;

/**
 * @author wshon
 */
public abstract class BaseBuilder<T extends Position> {
    public abstract boolean canBuild(T entity);

    public abstract void display(Table table, T entity);
}
