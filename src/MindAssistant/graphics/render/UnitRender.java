package MindAssistant.graphics.render;

import MindAssistant.graphics.draw.BaseUtilDrawer;
import arc.Core;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Entityc;
import mindustry.gen.Unit;

public class UnitRender extends BaseRender<BaseUtilDrawer> {
    @Override
    public void globalRender(Seq<BaseUtilDrawer> validDrawers) {
        Vars.state.teams.getActive().each(team -> {
            team.units.each(u -> validDrawers.each(d -> d.draw(u)));
        });
    }

    @Override
    public void cameraRender(Seq<BaseUtilDrawer> validDrawers) {
        Rect bounds = Core.camera.bounds(Tmp.r1);
        Vars.state.teams.getActive().each(team -> {
            if (team.unitTree == null) return;
            team.unitTree.intersect(bounds, b -> validDrawers.each(d -> d.draw(b)));
        });
    }

    @Override
    public void selectRender(Seq<BaseUtilDrawer> validDrawers) {
        Unit unit = Units.closestOverlap(null, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 5f, Entityc::isAdded);
        if (unit == null) return;
        validDrawers.each(d -> d.draw(unit));
    }
}
