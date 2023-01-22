package MindAssistant.graphics.draw.unit;

import MindAssistant.graphics.draw.BaseUtilDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Bits;
import arc.struct.Seq;
import arc.util.Structs;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.gen.PayloadUnit;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.payloads.Payload;

/**
 * Display info bar for Unit
 * @author wshon
 */
public class UnitInfoBarShow extends BaseUtilDrawer {
    public static float healthBarStroke = 1.7f, healthBarAlpha = 0.85f;
    public static float backBarStroke = healthBarStroke + 1.3f, backBarAlpha = healthBarAlpha - 0.25f;

    @Override
    public void draw(Unit unit) {
        doDraw(unit);
    }

    public void doDraw(Unit unit) {
        float startX = unit.x - unit.hitSize / 2f, startY = unit.y + unit.hitSize / 2f;
        float endX = unit.x + unit.hitSize / 2f;

        float iconSize = unit.hitSize / Vars.tilesize;

        Draw.z(Layer.flyingUnit + 0.1f);

        /* HealthBar */
        if (unit.damaged()) {
            /* Background */
            Lines.stroke(backBarStroke, unit.team().color);
            Draw.alpha(backBarAlpha);
            Lines.line(startX, startY, endX, startY);

            Lines.stroke(healthBarStroke, Pal.health);
            Draw.alpha(healthBarAlpha);
            Lines.line(startX, startY, startX + (endX - startX) * Math.max(unit.healthf(), 0), startY);

            startY += backBarStroke;
        }

        Draw.color();

        /* Shield Bar */
        var abilities = unit.abilities;
        if (abilities.length > 0) {
            Ability ability = Structs.find(abilities, a -> a instanceof ForceFieldAbility);

            if (ability instanceof ForceFieldAbility forceFieldAbility) {
                Lines.stroke(healthBarStroke, Pal.shield);
                Draw.alpha(healthBarAlpha);

                Lines.line(startX, startY, startX + (endX - startX) * (unit.shield / forceFieldAbility.max), startY);

                startY += healthBarStroke;
            }


            Draw.color();
        }

        if(!Mathf.zero(unit.drownTime)){
            Lines.stroke(healthBarStroke, Pal.shield);
            Draw.alpha(healthBarAlpha);

            Lines.line(startX, startY, startX + (endX - startX) * (1f - unit.drownTime), startY);

            startY += healthBarStroke + 0.5f;
        }

        /* Status */
        Bits applied = unit.statusBits();
        int statusSize = applied.length();

        if (statusSize != 0) {
            startY += iconSize / 2;

            int index = 0;
            for (StatusEffect effect : Vars.content.statusEffects()) {
                if (applied.get(effect.id) && !effect.isHidden()) {
                    Draw.color(effect.color);
                    Draw.rect(effect.fullIcon, startX + (index++ * iconSize), startY, iconSize, iconSize);
                }
            }

            startY += iconSize / 2;
        }

        Draw.color();

        /* Payloads */
        if (unit instanceof PayloadUnit payloadUnit) {
            Seq<Payload> payloads = payloadUnit.payloads;

            startY += iconSize / 2;

            int index = 0;
            if (payloads.any()) {
                for (Payload payload : payloads) {
                    Draw.rect(payload.icon(), startX + (index++ * iconSize), startY, iconSize, iconSize);
                }
            }

//            startY += iconSize / 2;
        }

        Draw.reset();
    }
}
