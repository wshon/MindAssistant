package MindAssistant.ui.override.infotable.builder;

import arc.scene.ui.layout.Table;
import mindustry.gen.Entityc;

/**
 * @author wshon
 */
public abstract class BaseTableBuilder<T extends Entityc> {
    public abstract boolean canBuild(T entity);

    public abstract void build(Table table, T entity);
}
