package MindAssistant.graphics.draw.build;

import MindAssistant.graphics.draw.BaseBuildDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.logic.Ranged;

import static mindustry.Vars.player;

/**
 * Display the hovered building effective range
 * @author wshon
 */
public class BuildRange extends BaseBuildDrawer {

    @Override
    public void draw(Building building) {
        if (!building.isValid()) return;
        if (building.team == player.team()) return;
        if (building instanceof Ranged ranged) {
            doDraw(building, ranged);
        }
    }


    private void doDraw(Building building, Ranged ranged) {
        Draw.z(Layer.overlayUI);

        Lines.stroke(1.2f);
        Drawf.dashCircle(building.x, building.y, ranged.range(), building.team.color);

        Draw.color(building.team.color);

        float dst = building.dst(player);
        if (dst > ranged.range()) {
            Tmp.v1.set(building).sub(player).setLength(dst - ranged.range());
            Draw.rect(building.block.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f + building.block.size * 3f, 10f + building.block.size * 3f, Tmp.v1.angle() - 90f);
        }

        Draw.reset();
    }
}
