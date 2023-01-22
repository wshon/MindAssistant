package MindAssistant.graphics.draw.unit;

import MindAssistant.graphics.draw.BaseUtilDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.ai.types.LogicAI;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

public class UnitLogicLineInspect extends BaseUtilDrawer {

    @Override
    public void draw(Unit unit) {
        if (unit.controller() instanceof LogicAI logicai) {
            doDraw(unit, logicai);
        }
    }

    private void doDraw(Unit unit, LogicAI logicai) {

        if (Mathf.len(logicai.moveX - unit.x, logicai.moveY - unit.y) <= 1200f) {
            Draw.z(Layer.overlayUI);
            Lines.stroke(1f);
            Draw.color(0.2f, 0.2f, 1f, 0.9f);
            Lines.dashLine(unit.x, unit.y, logicai.moveX, logicai.moveY, (int) (Mathf.len(logicai.moveX - unit.x, logicai.moveY - unit.y) / 8));
            Lines.dashCircle(logicai.moveX, logicai.moveY, logicai.moveRad);
            Draw.reset();
        }

        if (true) {
            Draw.z(Layer.overlayUI);
            Lines.dashLine(unit.x, unit.y, logicai.controller.x, logicai.controller.y, (int) (Mathf.len(logicai.controller.x - unit.x, logicai.controller.y - unit.y) / 8));
            Draw.reset();
        }

        if (true) {
            Draw.z(Layer.flyingUnit + 0.1f);

            Lines.stroke(2f);
            Draw.color(Pal.heal);
            Lines.line(unit.x - (unit.hitSize() / 2f), unit.y - (unit.hitSize() / 2f), unit.x - (unit.hitSize() / 2f), unit.y + unit.hitSize() * (logicai.controlTimer / LogicAI.logicControlTimeout - 0.5f));
/*
            Lines.stroke(2f);
            Draw.color(Pal.items);
            Lines.line(unit.x - (unit.hitSize() / 2f) - 1f, unit.y - (unit.hitSize() / 2f), unit.x - (unit.hitSize() / 2f) - 1f, unit.y + unit.hitSize() * (logicai.itemTimer / LogicAI.transferDelay - 0.5f));

            Lines.stroke(2f);
            Draw.color(Pal.items);
            Lines.line(unit.x - (unit.hitSize() / 2f) - 1.5f, unit.y - (unit.hitSize() / 2f), unit.x - (unit.hitSize() / 2f) - 1.5f, unit.y + unit.hitSize() * (logicai.payTimer / LogicAI.transferDelay - 0.5f));
*/
            Draw.reset();
        }

        Draw.reset();
    }
}
