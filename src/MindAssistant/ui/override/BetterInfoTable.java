package MindAssistant.ui.override.infotable;

import MindAssistant.MindVars;
import MindAssistant.ui.override.infotable.builder.BaseUnitBuilder;
import MindAssistant.ui.override.infotable.builder.BaseBuildBuilder;
import MindAssistant.ui.override.infotable.builder.build.ItemBuilder;
import MindAssistant.ui.override.infotable.builder.unit.WeaponBuilder;
import MindAssistant.ui.utils.ElementUtils;
import arc.Core;
import arc.Events;
import arc.func.Boolp;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Reflect;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Units;
import mindustry.game.EventType.UnlockEvent;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Unit;
import mindustry.world.Block;
import mindustry.world.Tile;

public class BetterInfoTable extends Table {
    private final BaseInfoTable<?> unitInfo, buildInfo, tileInfo;
    private static final Seq<BaseBuildBuilder> buildBuilders = Seq.with(new ItemBuilder());
    private static final Seq<BaseUnitBuilder> unitBuilders = Seq.with(new WeaponBuilder());

    private final Seq<BaseInfoTable<?>> infoTables = Seq.with(
            unitInfo = new BaseInfoTable<Unit>() {
                @Override
                public Unit hovered() {
                    return Units.closestOverlap(null, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 5f, Entityc::isAdded);
                }

                @Override
                protected void build() {
                    hover.display(this);

                    var builders = unitBuilders.select(unitBuilder -> unitBuilder.canBuild(hover));
                    builders.each(builder -> builder.build(row(), hover));
                }
            },
            buildInfo = new BaseInfoTable<Building>() {
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

                    var builders = buildBuilders.select(buildBuilder -> buildBuilder.canBuild(hover));
                    if (builders.any()) {
                        for (var builder : builders) {
                            builder.build(row(), hover);
                        }
                    }
                }
            },
            tileInfo = new BaseInfoTable<Tile>() {
                @Override
                public Tile hovered() {
                    return Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
                }

                @Override
                public void build() {
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
    );

    /* For reset override */
    private Table topTable;
    private Block menuHoverBlock;
    private Boolp oldVisible;
    private Cell<?> topTableCell;

    public BetterInfoTable() {
        /* PlacementFragment rebuild event */
        Events.on(WorldLoadEvent.class, event -> Core.app.post(this::tryOverride));

        Events.on(UnlockEvent.class, event -> {
            if (event.content instanceof Block) {
                tryOverride();
            }
        });

//        addSetting();

        setup();
    }

//    private void addSetting(){
//        MindVars.ui.settings.ui.addCategory("overrideInfoTable", setting -> {
//            setting.checkPref("overrideInfoTable", true, b -> tryToggleOverride());
//        });
//    }

    private void setup() {
        update(this::rebuild);
    }

    private void rebuild() {
        clearChildren();

        for (BaseInfoTable<?> table : infoTables) {
            table.update();

            if (table.shouldAdd()) {
                add(table).margin(5).padBottom(-4).padRight(-4).growX().row();
            }
        }
    }

    public void initOverride() {
        topTable = Reflect.get(Vars.ui.hudfrag.blockfrag, "topTable");
    }

    public void tryToggleOverride() {
        initOverride();
        if (MindVars.settings.getBool("overrideInfoTable", true)) {
            doOverride();
        } else {
            resetOverride();
        }
    }

    public void tryOverride() {
        if (MindVars.settings.getBool("overrideInfoTable", true)) {
            initOverride();
            doOverride();
        }
    }

    public void doOverride() {
        oldVisible = topTable.visibility;
        topTable.visible(() -> {
            menuHoverBlock = Reflect.get(Vars.ui.hudfrag.blockfrag, "menuHoverBlock");
            return menuHoverBlock != null || Vars.control.input.block != null;
        });
        Cell<?> cell = ElementUtils.getCell(topTable);
        if (cell != null) {
            topTableCell = cell;

            cell.setElement(new Table(t -> {
                t.add(topTable).growX().row();
                t.add(this).growX();
            }));
        }
    }

    public void resetOverride() {
        if (oldVisible != null) {
            topTable.visible(oldVisible);
        }

        if (topTableCell != null) {
            topTableCell.setElement(topTable);
        }
    }
}
