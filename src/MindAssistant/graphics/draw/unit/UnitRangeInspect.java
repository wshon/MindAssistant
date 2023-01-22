package MindAssistant.graphics.draw.unit;

import MindAssistant.graphics.draw.BaseUtilDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Tmp;
import mindustry.core.Renderer;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

import static mindustry.Vars.player;

/**
 * @author wshon
 */
public class UnitRangeInspect extends BaseUtilDrawer {

    @Override
    public void draw(Unit unit) {
        if (!unit.isValid()) return;
        if (unit.team == player.team()) return;
        doDraw(unit);
    }

    protected void doDraw(Unit unit) {
        Draw.z(Layer.overlayUI);

        Draw.alpha(0.5f);
        Lines.stroke(1.2f);
        Drawf.dashCircle(unit.x, unit.y, unit.range(), unit.team.color);

        float dst = unit.dst(player);
        if (dst > unit.range()) {
            Tmp.v1.set(unit).sub(player).setLength(dst - unit.range());
            Draw.rect(unit.type.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f + unit.hitSize / 3f, 10f + unit.hitSize / 3f, Tmp.v1.angle() - 90f);
        }

        Draw.reset();
    }
}
