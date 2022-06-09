package MindAssistant.graphics.render;

import MindAssistant.graphics.draw.BaseBuildDrawer;
import arc.Core;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;

/**
 * @author wangsen
 */
public class BuildRender<T extends Building> extends BaseRender<BaseBuildDrawer<T>> {
    private final Seq<Building> buildings = new Seq<>();

    @Override
    public void globalRender(Seq<BaseBuildDrawer<T>> validDrawers) {
        Vars.state.teams.getActive().each(team -> {
            if (team.buildings == null) return;
            team.buildings.getObjects(buildings);
            buildings.each(b -> validDrawers.each(d -> d.draw(b)));
            buildings.clear();
        });
    }

    @Override
    public void cameraRender(Seq<BaseBuildDrawer<T>> validDrawers) {
        Rect bounds = Core.camera.bounds(Tmp.r1);
        Vars.state.teams.getActive().each(teamData -> {
            if (teamData.buildings == null) return;
            teamData.buildings.intersect(bounds, b -> validDrawers.each(d -> d.draw((T) b)));
        });
    }
}
