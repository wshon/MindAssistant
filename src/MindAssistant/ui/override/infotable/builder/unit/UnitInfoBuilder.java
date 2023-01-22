package MindAssistant.ui.override.infotable.builder.unit;

import MindAssistant.ui.override.infotable.builder.BaseUnitBuilder;
import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import mindustry.ai.types.LogicAI;
import mindustry.content.Blocks;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Iconc;
import mindustry.gen.Payloadc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;

import static mindustry.Vars.iconMed;
import static mindustry.Vars.state;

public class UnitInfoBuilder extends BaseUnitBuilder {

    @Override
    public boolean canBuild(Unit entity) {
        return true;
    }

    /**
     * copy from mindustry.type.UnitType.display
     */
    @Override
    public void display(Table table, Unit unit) {
        table.table(t -> {
            t.left();
            t.add(new Image(unit.type.uiIcon)).size(iconMed).scaling(Scaling.fit);
            t.labelWrap(unit.type.localizedName).left().width(190f).padLeft(5);
        }).growX().left();
        table.row();

        table.table(bars -> {
            bars.defaults().growX().height(20f).pad(4);

            bars.add(new Bar(
                    () -> String.format("%.2f/%.2f(%d%%)", unit.health, unit.maxHealth, (int) (100 * unit.healthf())),
                    () -> Pal.health, unit::healthf
            ).blink(Color.white));
            bars.row();

            if (state.rules.unitAmmo) {
                bars.add(new Bar(unit.type.ammoType.icon() + " " + Core.bundle.get("stat.ammo"), unit.type.ammoType.barColor(), () -> unit.ammo / unit.type.ammoCapacity));
                bars.row();
            }

            for (Ability ability : unit.abilities) {
                ability.displayBars(unit, bars);
            }

            if (unit instanceof Payloadc payload) {
                bars.add(new Bar("stat.payloadcapacity", Pal.items, () -> payload.payloadUsed() / unit.type().payloadCapacity));
                bars.row();

                var count = new float[]{-1};
                bars.table().update(t -> {
                    if (count[0] != payload.payloadUsed()) {
                        payload.contentInfo(t, 8 * 2, 270);
                        count[0] = payload.payloadUsed();
                    }
                }).growX().left().height(0f).pad(0f);
            }
        }).growX();

        if (unit.controller() instanceof LogicAI) {
            table.row();
            table.add(Blocks.microProcessor.emoji() + " " + Core.bundle.get("units.processorcontrol")).growX().wrap().left();
            table.row();
            table.label(() -> Iconc.settings + " " + (long) unit.flag + "").color(Color.lightGray).growX().wrap().left();
        }

        table.row();
    }
}
