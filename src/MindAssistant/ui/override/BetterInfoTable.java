package MindAssistant.ui.override;

import MindAssistant.MindVars;
import MindAssistant.ui.override.infotable.BaseInfoTable;
import MindAssistant.ui.override.infotable.BuildInfoTable;
import MindAssistant.ui.override.infotable.TileInfoTable;
import MindAssistant.ui.override.infotable.UnitInfoTable;
import MindAssistant.ui.override.infotable.builder.build.ItemBuilder;
import MindAssistant.ui.override.infotable.builder.unit.StatusBuilder;
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
import mindustry.game.EventType.UnlockEvent;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.world.Block;

public class BetterInfoTable extends Table {
    private final Seq<BaseInfoTable<?>> infoTables;

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
        infoTables = Seq.with(
                new UnitInfoTable()
                        .addBuilders(new StatusBuilder(), new WeaponBuilder()),
                new BuildInfoTable()
                        .addBuilders(new ItemBuilder()),
                new TileInfoTable()
        );
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
