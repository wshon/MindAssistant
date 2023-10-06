package MindAssistant.modules.spawn;

import MindAssistant.modules.BaseModule;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.geom.Geometry;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.util.Reflect;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.game.EventType.*;
import mindustry.gen.BufferItem;
import mindustry.gen.Building;
import mindustry.gen.TimeItem;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.DirectionalItemBuffer;
import mindustry.world.ItemBuffer;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.BufferedItemBridge;
import mindustry.world.blocks.distribution.BufferedItemBridge.BufferedItemBridgeBuild;
import mindustry.world.blocks.distribution.ItemBridge.ItemBridgeBuild;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.distribution.Junction.JunctionBuild;
import mindustry.world.blocks.distribution.Router.RouterBuild;

import java.lang.reflect.Field;

public class ItemBridgeSpy extends BaseModule {

    private static Field itemBridgeBufferField, bufferField, indexField;

    static QuadTree<Tile> tiles;

    @Override
    public void load() {
        try {
            itemBridgeBufferField = BufferedItemBridgeBuild.class.getDeclaredField("buffer");
            bufferField = ItemBuffer.class.getDeclaredField("buffer");
            indexField = ItemBuffer.class.getDeclaredField("index");

            itemBridgeBufferField.setAccessible(true);
            bufferField.setAccessible(true);
            indexField.setAccessible(true);
        } catch (NoSuchFieldException e0) {
            throw new RuntimeException(e0);
        }
        Events.on(WorldLoadEvent.class, e -> {
            tiles = new QuadTree<>(Tmp.r1.set(0, 0, Vars.world.unitWidth(), Vars.world.unitHeight()));
            Vars.world.tiles.eachTile(tile -> tiles.insert(tile));
        });
        Events.run(Trigger.draw, () -> {
            if (isEnabled()) {
                this.draw();
            }
        });
    }

    private void draw() {
        Rect bounds = Core.camera.bounds(Tmp.r1).grow(Vars.tilesize);

        Draw.z(Layer.power + 1f);

        tiles.intersect(bounds, tile -> {
            Building building = tile.build;
            if (building == null || !tile.isCenter()) return;

            if (building instanceof ItemBridgeBuild bridge) {
                drawBridgeItem(bridge);
            } else if (building instanceof JunctionBuild junction) {
                drawHiddenItem(junction);
            } else if (building instanceof RouterBuild router) {
                drawHiddenItem(router);
            }
        });

        Draw.reset();
    }

    private static void getBuffer(ItemBuffer itemBuffer, Item[] returnItems, float[] returnTimes) {
        long[] buffer = Reflect.get(itemBuffer, bufferField);
        int index = Reflect.get(itemBuffer, indexField);

        for (int i = 0; i < index; i++) {
            long l = buffer[i];
            returnItems[i] = Vars.content.item(TimeItem.item(l));
            returnTimes[i] = TimeItem.time(l);
        }
    }

    public static void drawItems(Building building) {
        if (building.items != null) {
            int amount = 0;
            for (int iid = 0; iid < building.items.length(); iid++) {
                if (building.items.get(iid) > 0) {
                    for (int itemID = 1; itemID <= building.items.get(iid); itemID++) {
                        Draw.rect(Vars.content.item(iid).uiIcon, building.x, building.y - Vars.tilesize / 2f + 1f + 0.6f * (float) amount, 4f, 4f);
                        amount++;
                    }
                }
            }
        }
    }

    public static void drawBridgeItem(ItemBridgeBuild bridge) {
        // Draw each item the bridge have
        Draw.color(Color.white, 0.8f);

        drawItems(bridge);

        if (bridge instanceof BufferedItemBridgeBuild bufferedBridge) {
            drawHiddenItem(bufferedBridge);
        }
    }

    public static void drawHiddenItem(BufferedItemBridgeBuild bridge) {
        ItemBuffer buffer = Reflect.get(bridge, itemBridgeBufferField);

        BufferedItemBridge block = (BufferedItemBridge) bridge.block;
        int capacity = block.bufferCapacity;

        Item[] bufferItems = new Item[capacity];
        float[] bufferTimes = new float[capacity];

        getBuffer(buffer, bufferItems, bufferTimes);

        Tile other = Vars.world.tile(bridge.link);

        float begX, begY, endX, endY;
        if (!block.linkValid(bridge.tile, other)) {
            begX = bridge.x - Vars.tilesize / 2f;
            begY = bridge.y - Vars.tilesize / 2f;
            endX = bridge.x + Vars.tilesize / 2f;
            endY = bridge.y - Vars.tilesize / 2f;
        } else {
            int i = bridge.tile.absoluteRelativeTo(other.x, other.y);
            float ex = other.worldx() - bridge.x - Geometry.d4(i).x * Vars.tilesize / 2f, ey = other.worldy() - bridge.y - Geometry.d4(i).y * Vars.tilesize / 2f;
            float warmup = Vars.state.isEditor() ? 1f : bridge.warmup;
            ex *= warmup;
            ey *= warmup;

            begX = bridge.x + Geometry.d4(i).x * Vars.tilesize / 2f;
            begY = bridge.y + Geometry.d4(i).y * Vars.tilesize / 2f;
            endX = bridge.x + ex;
            endY = bridge.y + ey;
        }

        for (int i = 0; i < capacity; i++) {
            if (bufferItems[i] != null) {
                float f = Math.min(((Time.time - bufferTimes[i]) * bridge.timeScale() / block.speed) * capacity, capacity - i - 1) / (float) capacity;
                Draw.rect(bufferItems[i].uiIcon, begX + (endX - begX) * f, begY + (endY - begY) * f, 4f, 4f);
            }
        }
    }

    private static void getDirectionalBuffer(DirectionalItemBuffer buffer, Item[][] items, float[][] times) {
        for (int i = 0; i < 4; i++) {
            for (int ii = 0; ii < buffer.indexes.length; ii++) {
                long l = buffer.buffers[i][ii];
                items[i][ii] = Vars.content.item(BufferItem.item(l));
                times[i][ii] = BufferItem.time(l);
            }
        }
    }

    public static void drawHiddenItem(JunctionBuild junction) {
        DirectionalItemBuffer buffer = junction.buffer;
        Junction block = (Junction) junction.block;
        int capacity = block.capacity;

        Item[][] items = new Item[4][capacity];
        float[][] times = new float[4][capacity];
        getDirectionalBuffer(buffer, items, times);

        float begX, begY, endX, endY;
        for (int i = 0; i < 4; i++) {
            endX = junction.x + Geometry.d4(i).x * Vars.tilesize / 2f + Geometry.d4(Math.floorMod(i + 1, 4)).x * Vars.tilesize / 4f;
            endY = junction.y + Geometry.d4(i).y * Vars.tilesize / 2f + Geometry.d4(Math.floorMod(i + 1, 4)).y * Vars.tilesize / 4f;
            begX = junction.x - Geometry.d4(i).x * Vars.tilesize / 4f + Geometry.d4(Math.floorMod(i + 1, 4)).x * Vars.tilesize / 4f;
            begY = junction.y - Geometry.d4(i).y * Vars.tilesize / 4f + Geometry.d4(Math.floorMod(i + 1, 4)).y * Vars.tilesize / 4f;

            if (buffer.indexes[i] > 0) {
                for (int idi = 0; idi < buffer.indexes[i]; idi++) {
                    if (items[i][idi] != null) {
                        float f = Math.min(((Time.time - times[i][idi]) * junction.timeScale() / block.speed) * capacity, capacity - idi - 1) / (float) capacity;
                        Draw.rect(items[i][idi].uiIcon, begX + (endX - begX) * f, begY + (endY - begY) * f, 4f, 4f);
                    }
                }
            }
        }
    }

    public static void drawHiddenItem(RouterBuild router) {
        Item item = router.lastItem;
        if (item != null) {
            Draw.rect(item.uiIcon, router.x, router.y, 4f, 4f);
        }
    }
}