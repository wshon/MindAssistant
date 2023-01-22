package MindAssistant.ui.override.infotable;

import arc.Core;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Tile;

/**
 * @author wshon
 */
public class BuildInfoTable extends BaseInfoTable<Building> {
    @Override
    public Building hovered() {
        Tile tile = Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
        if (tile == null) return null;
        return tile.build;
    }

    @Override
    protected void build() {
        Team team = hover.team;
        table(t -> {
            if (team != Vars.player.team()) {
                hover.team(Vars.player.team());
                hover.display(t);
                hover.team(team);
            } else {
                hover.display(t);
            }
        }).margin(5).growX();

        var builders = allBuilders.select(buildBuilder -> buildBuilder.canBuild(hover));
        if (builders.any()) {
            for (var builder : builders) {
                builder.display(row(), hover);
            }
        }
    }
}
