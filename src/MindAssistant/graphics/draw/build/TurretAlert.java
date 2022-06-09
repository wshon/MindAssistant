package MindAssistant.graphics.draw.build;

import MindAssistant.MindVars;
import MindAssistant.graphics.draw.BaseBuildDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.defense.turrets.BaseTurret.BaseTurretBuild;
import mindustry.world.blocks.defense.turrets.TractorBeamTurret;
import mindustry.world.blocks.defense.turrets.TractorBeamTurret.TractorBeamBuild;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;

import static mindustry.Vars.player;
import static mindustry.Vars.tilesize;

/**
 * @author wangsen
 */
public class TurretAlert extends BaseBuildDrawer<BaseTurretBuild> {
    private int turretAlertRadius;

    @Override
    public void loadSettings() {
        turretAlertRadius = MindVars.settings.getInt("turretAlertRadius", 10) * tilesize;
    }

    @Override
    public boolean enabled() {
        return MindVars.settings.getBool("enableTurretAlert", true);
    }

    @Override
    public boolean isValid() {
        return !player.unit().isNull();
    }

    @Override
    public void draw(Building building) {
        if (!(building instanceof BaseTurretBuild baseTurret)) return;
        if (baseTurret.team == player.team()) return;
        if (!baseTurret.isValid()) return;
        if (!baseTurret.within(player, turretAlertRadius + baseTurret.range())) return;
        BaseTurret baseBlock = (BaseTurret) baseTurret.block;
        if (baseTurret instanceof TurretBuild turret) {
            Turret block = (Turret) baseBlock;
            if (!turret.hasAmmo()) return;
            if (!player.unit().isFlying() ? block.targetAir : block.targetGround) return;
        } else if (baseTurret instanceof TractorBeamBuild turret) {
            TractorBeamTurret block = (TractorBeamTurret) baseBlock;
            if (turret.power.status <= 0) return;
            if (!player.unit().isFlying() ? block.targetAir : block.targetGround) return;
        } else {
            return;
        }
        doDraw(baseTurret);
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
