package MindAssistant.ui.override.infotable.builder.unit;

import MindAssistant.ui.override.infotable.builder.BaseUnitBuilder;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.Bits;
import mindustry.Vars;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

/**
 * @author wshon
 */
public class StatusBuilder extends BaseUnitBuilder {
    @Override
    public boolean canBuild(Unit unit) {
        return unit.statusBits().length() != 0;
    }

    @Override
    public void display(Table table, Unit unit) {
        table.table(t -> {
            t.top().left().margin(5);
            Bits applied = unit.statusBits();
            int statusSize = applied.length();
            if (statusSize != 0) {
                for (StatusEffect effect : Vars.content.statusEffects()) {
                    if (applied.get(effect.id) && !effect.isHidden()) {
                        Image image = new Image(effect.fullIcon);
                        image.setColor(effect.color);
                        t.add(image).size(Vars.iconMed);
                        t.add(effect.localizedName).padLeft(5);
                    }
                }
            }
        }).growX();
    }
}
