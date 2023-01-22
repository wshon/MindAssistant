package MindAssistant.graphics.draw.unit.player;

import MindAssistant.graphics.draw.BaseUtilDrawer;
import arc.graphics.g2d.Draw;
import mindustry.gen.BlockUnitUnit;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.logic.Ranged;

/**
 * Display player range
 *
 * @author wshon
 */
public class PlayerRangeShow extends BaseUtilDrawer {

    @Override
    public void draw(Unit unit) {
        if (!unit.isValid()) return;
        if (!unit.isPlayer()) return;
        doDraw(unit, unit.getPlayer());
    }

    private void doDraw(Unit unit, Player player) {
        Draw.z(Layer.flyingUnitLow - 1f);
        float range = unit.range();
        if (unit instanceof BlockUnitUnit blockUnit) {
            Building building = blockUnit.tile();
            if (building instanceof Ranged ranged) {
                range = ranged.range();
            }
        }
        Drawf.dashCircle(player.x, player.y, range, player.team().color);
        Draw.reset();
    }

}
