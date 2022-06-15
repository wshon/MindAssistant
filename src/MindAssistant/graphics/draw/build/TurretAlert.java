package MindAssistant.graphics.draw.build;

import MindAssistant.MindVars;
import MindAssistant.graphics.draw.BaseBuildDrawer;
import MindAssistant.ui.settings.SettingsMenuDialog.SettingsTable;
import arc.func.Floatp;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.turrets.BaseTurret.BaseTurretBuild;
import mindustry.world.blocks.defense.turrets.PowerTurret.PowerTurretBuild;
import mindustry.world.blocks.defense.turrets.TractorBeamTurret;
import mindustry.world.blocks.defense.turrets.TractorBeamTurret.TractorBeamBuild;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;

import static arc.Core.bundle;
import static mindustry.Vars.player;
import static mindustry.Vars.tilesize;

/**
 * Display enemy turrets range and type
 *
 * @author wshon
 */
public class TurretAlert extends BaseBuildDrawer {
    private final Floatp turretAlertRadius = () -> MindVars.settings.getInt("turretAlertRadius") * tilesize;

    @Override
    public void setPrefTo(SettingsTable setting) {
        setting.sliderPref("turretAlertRadius", 10, 5, 50, 1, i -> bundle.format("mind-assistant.blocks", i));
    }

    @Override
    public boolean isValid() {
        return !player.unit().isNull();
    }

    @Override
    public void draw(Building building) {
        if (!building.isValid()) return;
        if (building.team == player.team()) return;
        if (building instanceof TurretBuild turret) {
            Turret block = (Turret) building.block;
            if (building instanceof PowerTurretBuild powerTurret) {
                if (powerTurret.power.status <= 0) return;
            } else {
                if (!turret.hasAmmo()) return;
            }
            if (!(player.unit().isFlying() ? block.targetAir : block.targetGround)) return;
            if (!building.within(player, turretAlertRadius.get() + turret.range())) return;
            doDraw(turret);
        } else if (building instanceof TractorBeamBuild turret) {
            TractorBeamTurret block = (TractorBeamTurret) building.block;
            if (turret.power.status <= 0) return;
            if (!(player.unit().isFlying() ? block.targetAir : block.targetGround)) return;
            if (!building.within(player, turretAlertRadius.get() + turret.range())) return;
            doDraw(turret);
        }
    }

    private void doDraw(BaseTurretBuild turret) {
        Draw.z(Layer.overlayUI);

        Lines.stroke(1.2f);
        Drawf.dashCircle(turret.x, turret.y, turret.range(), turret.team.color);

        Draw.color(turret.team.color);

        float dst = turret.dst(player);
        if (dst > turret.range()) {
            Tmp.v1.set(turret).sub(player).setLength(dst - turret.range());
            Draw.rect(turret.block.fullIcon, player.x + Tmp.v1.x, player.y + Tmp.v1.y, 10f + turret.block.size * 3f, 10f + turret.block.size * 3f, Tmp.v1.angle() - 90f);
        }

        Draw.reset();
    }
}
