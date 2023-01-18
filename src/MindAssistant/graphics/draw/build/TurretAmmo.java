package MindAssistant.graphics.draw.build;

import MindAssistant.graphics.draw.BaseBuildDrawer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Reflect;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret.ItemEntry;
import mindustry.world.blocks.defense.turrets.ItemTurret.ItemTurretBuild;
import mindustry.world.blocks.defense.turrets.LiquidTurret.LiquidTurretBuild;
import mindustry.world.blocks.defense.turrets.PointDefenseTurret.PointDefenseBuild;
import mindustry.world.blocks.defense.turrets.PowerTurret.PowerTurretBuild;
import mindustry.world.blocks.defense.turrets.TractorBeamTurret.TractorBeamBuild;

import static mindustry.Vars.tilesize;


/**
 * Display turret ammo type
 *
 * @author wshon
 */
public class TurretAmmo extends BaseBuildDrawer {

    @Override
    public void draw(Building building) {
        if (!building.isValid()) return;
        if (building instanceof ItemTurretBuild turret) {
            if (!turret.hasAmmo()) return;
            doDrawItemTurretBuild(turret);
        } else if (building instanceof LiquidTurretBuild turret) {
            if (!turret.hasAmmo()) return;
            doDrawLiquidTurretBuild(turret);
        } else if (building instanceof PowerTurretBuild turret) {
            if (turret.power.status <= 0) return;
            doDrawPowerTurretBuild(turret);
        } else if (building instanceof TractorBeamBuild turret) {
            if (turret.power.status <= 0) return;
            doDrawPowerTurretBuild(turret);
        } else if (building instanceof PointDefenseBuild turret) {
            if (turret.power.status <= 0) return;
            doDrawPowerTurretBuild(turret);
        }
    }

    private void doDrawItemTurretBuild(ItemTurretBuild turret) {
        ItemEntry entry = (ItemEntry) turret.ammo.peek();
        Item item = Reflect.get(entry, "item");
        float size = (float) entry.amount / ((ItemTurret) turret.block).maxAmmo;
        drawBuildAmmoIcon(turret, item.uiIcon, size);
    }

    private void doDrawLiquidTurretBuild(LiquidTurretBuild turret) {
        float size = turret.liquids.currentAmount() / (turret.block).liquidCapacity;
        drawBuildAmmoIcon(turret, turret.liquids.current().uiIcon, size);
    }

    private void doDrawPowerTurretBuild(Building turret) {
        Draw.color(Color.green);
        drawBuildAmmoIcon(turret, Icon.power.getRegion(), 1);
        Draw.reset();
    }

    private void drawBuildAmmoIcon(Building build, TextureRegion icon, float size) {
        Draw.z(Layer.turret + 0.1f);

        float maxSize = Math.max(6f, build.block.size * tilesize / 2f);
        float x = build.x + build.block.size * tilesize / 3f;
        float y = build.y + build.block.size * tilesize / 3f;

        float realSize = Mathf.lerp(3f, maxSize, Math.min(1f, size));

        Draw.alpha(0.45f);
        Draw.rect(icon, x, y, maxSize, maxSize);

        Draw.alpha(1f);
        Draw.rect(icon, x, y, realSize, realSize);

        Draw.reset();
    }

}
