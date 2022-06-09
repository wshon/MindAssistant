package MindAssistant;

import MindAssistant.io.MindSettings;
import MindAssistant.world.MoreBars;
import arc.struct.Seq;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.ItemBridge;

import static mindustry.Vars.content;

/**
 * @author wangsen
 */
public class MindVars {
    public static MindSettings settings;
    private static Seq<Block> allBlocks = new Seq<>();
    private static Seq<Block> allVisibleBlocks = new Seq<>();
    private static Seq<Item> allOreItems = new Seq<>();

    public static void init() {
        settings = new MindSettings();
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
            MoreBars.addMoreBars(block);
        }
    }
}
