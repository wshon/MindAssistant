package MindAssistant.graphics.draw.build;

import MindAssistant.MindVars;
import MindAssistant.graphics.draw.BaseBuildDrawer;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Reflect;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret.ItemEntry;
import mindustry.world.blocks.defense.turrets.ItemTurret.ItemTurretBuild;

import static mindustry.Vars.tilesize;


/**
 * @author wangsen
 */
public class TurretAmmo extends BaseBuildDrawer {

    @Override
    public boolean enabled() {
        return MindVars.settings.getBool("enableTurretAmmo", true);
    }

    @Override
    public void draw(Building building) {
        if (!(building instanceof ItemTurretBuild turret)) return;
        if (!turret.isValid()) return;
        if (!turret.ammo.any()) return;
        doDraw(turret);
    }

    private void doDraw(ItemTurretBuild turret) {
        ItemTurret block = (ItemTurret) turret.block;
        ItemEntry entry = (ItemEntry) turret.ammo.peek();

        Item item = Reflect.get(entry, "item");

        Draw.z(Layer.turret + 0.1f);

        float maxSize = Math.max(6f, block.size * tilesize / 2f);
        float x = turret.x + block.size * tilesize / 3f;
        float y = turret.y + block.size * tilesize / 3f;

        float realSize = Mathf.lerp(3f, maxSize, Math.min(1f, (float) entry.amount / block.maxAmmo));

        Draw.alpha(0.45f);
        Draw.rect(item.uiIcon, x, y, maxSize, maxSize);

        Draw.alpha(1f);
        Draw.rect(item.uiIcon, x, y, realSize, realSize);

        Draw.reset();
    }

}
