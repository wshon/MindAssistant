package MindAssistant.graphics.draw.unit;

import MindAssistant.MindVars;
import MindAssistant.graphics.draw.BaseUtilDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Tmp;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

import static mindustry.Vars.*;

/**
 * Display enemy unit range
 * @author wshon
 */
public class UnitAlert extends BaseUtilDrawer {
    private int alertRadius;

    @Override
    public void loadSettings() {
        alertRadius = MindVars.settings.getInt("unitAlertRadius", 10) * tilesize;
    }

    @Override
    public boolean isValid() {
        return !player.unit().isNull();
    }

    @Override
    public void draw(Unit unit) {
        if (unit.team == player.team()) return;
        if (!unit.type.hasWeapons()) return;
        if (state.rules.unitAmmo && unit.ammo <= 0f) return;
        if (!(player.unit().isFlying() ? unit.type.targetAir : unit.type.targetGround)) return;
        if (!unit.within(player, alertRadius + unit.type.maxRange)) return;
        doDraw(unit);
    }

    private void doDraw(Unit unit) {
        Draw.z(Layer.overlayUI);

        Lines.stroke(1.2f, unit.team.color);
        Lines.dashCircle(unit.x, unit.y, unit.range());

        float dst = unit.dst(player);
        if (dst > unit.range()) {
            Tmp.v1.set(unit).sub(player).setLength(dst - unit.range());
            Draw.rect(unit.type.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f + unit.hitSize / 3f, 10f + unit.hitSize / 3f, Tmp.v1.angle() - 90f);
        }

        Draw.reset();
    }
}
