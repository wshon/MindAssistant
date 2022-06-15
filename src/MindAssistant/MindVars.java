package MindAssistant;

import MindAssistant.ai.SmartDesktopInput;
import MindAssistant.io.MindSettings;
import MindAssistant.ui.UI;
import MindAssistant.ui.override.MoreBuildingBars;
import MindAssistant.ui.settings.SettingsMenuDialog;
import arc.struct.Seq;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.ItemBridge;

import static mindustry.Vars.content;
import static mindustry.Vars.mobile;

/**
 * @author wshon
 */
public class MindVars {
    public static MindSettings settings;
    public static SettingsMenuDialog settingsMenuDialog;
    public static UI ui;

    private static Seq<Block> allBlocks = new Seq<>();
    public static Seq<Block> allVisibleBlocks = new Seq<>();
    private static Seq<Item> allOreItems = new Seq<>();
    public static SmartDesktopInput smartDesktopInput;

    public static void init() {
        settings = new MindSettings();
        ui = new UI();

        if (!mobile) {
            smartDesktopInput = new SmartDesktopInput();
            smartDesktopInput.init();
        }

        settings.init();
    }

    public static void loadContent() {
        for (Block block : content.blocks()) {
            allBlocks.add(block);
            if (block.buildVisibility.visible()) {
                allVisibleBlocks.add(block);
            }
            if (block.itemDrop != null && !allOreItems.contains(block.itemDrop)) {
                allOreItems.add(block.itemDrop);
            }
            if (block instanceof ItemBridge) {
                block.allowConfigInventory = true;
            }
            MoreBuildingBars.addMoreBars(block);
        }
    }
}
