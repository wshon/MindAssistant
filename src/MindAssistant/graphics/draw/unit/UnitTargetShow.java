package MindAssistant.graphics.draw.unit;

import MindAssistant.graphics.draw.BaseUtilDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

/**
 * @author wshon
 */
public class UnitTargetShow extends BaseUtilDrawer {

    @Override
    public void draw(Unit unit) {
        if (!unit.isValid()) return;
        if (unit.isPlayer()) return;
        if (unit.aimX == 0 && unit.aimY == 0) return;
        doDraw(unit);
    }

    private void doDraw(Unit unit) {
        Draw.z(Layer.overlayUI);

        Lines.stroke(1.2f, unit.team.color);
        Lines.dashLine(unit.x, unit.y, unit.aimX, unit.aimY, (int) (Mathf.len(unit.aimX - unit.x, unit.aimY - unit.y) / 8));

        Draw.reset();
    }
}
