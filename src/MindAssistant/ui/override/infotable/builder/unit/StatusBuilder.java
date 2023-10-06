package MindAssistant.ui.override.infotable.builder.unit;

import MindAssistant.ui.override.infotable.builder.BaseUnitBuilder;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.struct.Bits;
import arc.util.Align;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

import static mindustry.Vars.player;

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
                        var time = unit.getDuration(effect);
                        Label label = new Label(() -> Float.isInfinite(time) ? "[red]∞" : UI.formatTime(time));
                        label.setAlignment(Align.bottom);
                        t.stack(image, label).size(Vars.iconMed);
                        t.add(effect.localizedName).padLeft(5);
                    }
                }
            }
        }).growX();
    }
}
