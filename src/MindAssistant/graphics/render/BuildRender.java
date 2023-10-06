package MindAssistant.graphics.render;

import MindAssistant.graphics.draw.BaseBuildDrawer;
import arc.Core;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Tile;

public class BuildRender extends BaseRender<BaseBuildDrawer> {

    @Override
    public void globalRender(Seq<BaseBuildDrawer> validDrawers) {
        Vars.state.teams.getActive().each(team -> {
            if (team.buildings == null) return;
            team.buildings.each(b -> validDrawers.each(d -> d.draw(b)));
        });
    }

    @Override
    public void cameraRender(Seq<BaseBuildDrawer> validDrawers) {
        Rect bounds = Core.camera.bounds(Tmp.r1);
        Vars.state.teams.getActive().each(teamData -> {
            if (teamData.buildingTree == null) return;
            teamData.buildingTree.intersect(bounds, b -> validDrawers.each(d -> d.draw(b)));
        });
    }

    @Override
    public void selectRender(Seq<BaseBuildDrawer> validDrawers) {
        Tile tile = Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
        if (tile == null || tile.build == null) return;
        validDrawers.each(d -> d.draw(tile.build));
    }
}
