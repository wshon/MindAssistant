package MindAssistant.graphics.draw.unit.player;

import MindAssistant.graphics.draw.BaseUtilDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

/**
 * @author wshon
 */
public class PlayerAim extends BaseUtilDrawer {
    @Override
    public void draw(Unit unit) {
        if (!unit.isValid()) return;
        if (!unit.isPlayer()) return;
        doDraw(unit);
    }

    private void doDraw(Unit unit) {
        Draw.z(Layer.flyingUnit + 0.1f);
        if (unit.isShooting()) {
            Draw.color(1f, 0.2f, 0.2f, 0.8f);
        } else {
            Draw.color(1f, 1f, 1f, 0.4f);
        }
        if (unit.mounts().length == 0) {
            Lines.dashLine(unit.aimX, unit.aimY, unit.x, unit.y, 40);
        } else {
            for (WeaponMount m : unit.mounts()) {
                if (Mathf.len(m.aimX - unit.x - m.weapon.x, m.aimY - unit.y - m.weapon.y) < 1800f) {
                    if (m.weapon.controllable) {
                        Lines.dashLine(unit.aimX, unit.aimY, unit.x + Mathf.cos((Mathf.angle(m.weapon.x, m.weapon.y) + unit.rotation() - 90f) / 180f * Mathf.pi) * Mathf.len(m.weapon.x, m.weapon.y), unit.y + Mathf.sin((Mathf.angle(m.weapon.x, m.weapon.y) + unit.rotation() - 90f) / 180f * Mathf.pi) * Mathf.len(m.weapon.x, m.weapon.y), 40);
                    } else {
                        Lines.dashLine(m.aimX, m.aimY, unit.x + Mathf.cos((Mathf.angle(m.weapon.x, m.weapon.y) + unit.rotation() - 90f) / 180f * Mathf.pi) * Mathf.len(m.weapon.x, m.weapon.y), unit.y + Mathf.sin((Mathf.angle(m.weapon.x, m.weapon.y) + unit.rotation() - 90f) / 180f * Mathf.pi) * Mathf.len(m.weapon.x, m.weapon.y), 40);
                    }
                }
            }
        }
        Draw.z(Layer.playerName);
        Drawf.target(unit.aimX, unit.aimY, 4f, 0.6f, Pal.remove);
        Draw.reset();
    }
}
