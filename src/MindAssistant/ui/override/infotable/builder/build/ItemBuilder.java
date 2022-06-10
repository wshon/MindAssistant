package MindAssistant.ui.override.infotable.builder.build;

import MindAssistant.ui.override.infotable.builder.BaseBuildBuilder;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import mindustry.core.UI;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.world.modules.ItemModule;

/**
 * @author wshon
 */
public class ItemTable extends BaseBuildBuilder {

    @Override
    public boolean canBuild(Building build) {
        return build.items != null && build.items.any();
    }

    @Override
    public void build(Table table, Building build) {
        ItemModule items = build.items;

        table.table(Tex.pane, t -> {
            t.table(Tex.whiteui, tt -> tt.add("Items")).color(Color.gray).growX().row();

            t.table(itemsTable -> {
                final int[] index = {0};
                items.each(((item, amount) -> {
                    itemsTable.table(itemTable -> {
                        itemTable.image(item.uiIcon);
                        itemTable.label(() -> UI.formatAmount(items.get(item)) + "").padLeft(3f);
                    }).growX().padLeft(4f);

                    if (++index[0] % 3 == 0) itemsTable.row();
                }));
            }).growX();
        }).growX();
    }
}
