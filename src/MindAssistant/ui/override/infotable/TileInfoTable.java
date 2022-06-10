package MindAssistant.ui.override.infotable;

import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.world.Tile;

/**
 * @author wshon
 */
public class TileInfoTable extends BaseInfoTable<Tile> {
    @Override
    public Tile hovered() {
        return Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
    }

    @Override
    protected void build() {
        displayContent(this, hover.floor());
        if (hover.overlay() != Blocks.air) displayContent(this, hover.overlay());
        if (hover.block().isStatic()) {
            displayContent(this, hover.block());
        }

    }

    private void displayContent(Table table, UnlockableContent content) {
        table.table(t -> {
            t.top().left().margin(5);
            t.image(content.uiIcon).size(Vars.iconMed);
            t.add(content.localizedName).padLeft(5);
        }).pad(5).growX();
    }
}
