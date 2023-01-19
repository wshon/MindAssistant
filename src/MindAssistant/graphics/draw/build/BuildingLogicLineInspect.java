package MindAssistant.graphics.draw.build;

import MindAssistant.graphics.draw.BaseBuildDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.ai.types.LogicAI;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.world.blocks.logic.LogicBlock;

/**
 * Display building's login line
 *
 * @author wshon
 */
public class BuildingLogicLineInspect extends BaseBuildDrawer {

    @Override
    public void draw(Building building) {
        if (building.block instanceof LogicBlock) {
            doDraw(building);
        }
    }

    private void doDraw(Building building) {
        Draw.z(Layer.overlayUI);
        Groups.unit.each(u -> u.controller() instanceof LogicAI ai && ai.controller == building, unit -> {
            Lines.dashLine(unit.x, unit.y, building.x, building.y, (int) (Mathf.len(building.x - unit.x, building.y - unit.y) / 8));
        });
        Draw.reset();
    }
}
