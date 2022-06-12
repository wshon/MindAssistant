package MindAssistant.ui.override.infotable;

import arc.Core;
import mindustry.entities.Units;
import mindustry.gen.Entityc;
import mindustry.gen.Unit;

/**
 * @author wshon
 */
public class UnitInfoTable extends BaseInfoTable<Unit> {
    @Override
    public Unit hovered() {
        return Units.closestOverlap(null, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 5f, Entityc::isAdded);
    }

    @Override
    protected void build() {
//        hover.display(this);
        var builders = allBuilders.select(unitBuilder -> unitBuilder.canBuild(hover));
        builders.each(builder -> builder.display(row(), hover));
    }
}
