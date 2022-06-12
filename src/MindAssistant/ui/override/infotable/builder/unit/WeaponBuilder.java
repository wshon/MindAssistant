package MindAssistant.ui.override.infotable.builder.unit;

import MindAssistant.ui.override.infotable.builder.BaseUnitBuilder;
import arc.graphics.Color;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.Vars;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;
import mindustry.ui.Bar;

/**
 * @author wshon
 */
public class WeaponBuilder extends BaseUnitBuilder {

    @Override
    public boolean canBuild(Unit unit) {
        return unit.type.hasWeapons() && !unit.disarmed;
    }

    @Override
    public void display(Table table, Unit unit) {
        table.table(t -> {
            t.margin(5);
            t.table(Tex.whiteui, tt -> tt.add("Weapons")).color(Color.gray).growX().row();

            float iconSize = Vars.mobile ? Vars.iconSmall : Vars.iconXLarge;

            t.table(weaponsTable -> {
                int index = 0;
                for (WeaponMount mount : unit.mounts()) {
                    Weapon weapon = mount.weapon;

                    Label label = new Label(() -> String.format("%.1f", mount.reload / weapon.reload / 60 * 100) + "s");

                    label.setAlignment(Align.bottom);

                    weaponsTable.table(Tex.pane, weaponTable -> {
                        weaponTable.stack(new Image(weapon.region), label).minSize(iconSize).maxSize(80f, 120f).row();
                        weaponTable.add(new Bar("", Pal.ammo, () -> mount.reload / weapon.reload)).minSize(45f, 18f);
                    }).bottom().growX();

                    if (++index % 4 == 0) weaponsTable.row();
                }
            }).growX();
        }).growX();
    }
}
