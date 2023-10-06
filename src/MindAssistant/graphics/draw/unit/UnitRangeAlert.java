package MindAssistant.graphics.draw.unit;

import MindAssistant.MindVars;
import MindAssistant.ui.settings.SettingsMenuDialog;
import arc.func.Intp;
import mindustry.gen.Unit;

import static arc.Core.bundle;
import static mindustry.Vars.*;

/**
 * Display enemy unit range
 *
 * @author wshon
 */
public class UnitRangeAlert extends UnitRangeInspect {
    private final Intp alertRadius = () -> MindVars.settings.getInt("drawer.unitAlertRadius.range") * tilesize;

    @Override
    public void setPrefTo(SettingsMenuDialog.SettingsTable setting) {
        super.setPrefTo(setting);
        setting.sliderPref("drawer.unitAlertRadius.range", 10, 5, 50, 1, i -> bundle.format("mind-assistant.blocks", i));
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
        if (!unit.within(player, alertRadius.get() + unit.type.maxRange)) return;
        doDraw(unit);
    }
}
