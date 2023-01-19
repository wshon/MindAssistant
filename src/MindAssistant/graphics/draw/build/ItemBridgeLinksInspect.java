package MindAssistant.graphics.draw.build;

import MindAssistant.graphics.draw.BaseBuildDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.distribution.ItemBridge.ItemBridgeBuild;

/**
 * Display item bridge links
 *
 * @author wshon
 */
public class ItemBridgeLinksInspect extends BaseBuildDrawer {
    private static final Seq<ItemBridgeBuild> currentBridges = new Seq<>();


    @Override
    public void draw(Building building) {
        if (!building.isValid()) return;
        if (building instanceof ItemBridgeBuild ibb) {
            doDraw(ibb);
        }
    }

    private void doDraw(ItemBridgeBuild building) {
        Draw.z(Layer.overlayUI);
        drawLink(building);
        Draw.reset();
        currentBridges.clear();
    }

    private static void drawLink(ItemBridgeBuild build) {
        if (currentBridges.contains(build)) return;
        currentBridges.add(build);
        ItemBridgeBuild otherBuild = getOtherBuild(build);
        if (otherBuild == null) {
            drawProximity(build);
            return;
        }
        drawInput(build, otherBuild);
        drawLink(otherBuild);
    }

    private static ItemBridgeBuild getOtherBuild(ItemBridgeBuild build) {
        if (build.link == -1) return null;
        Tile otherTile = Vars.world.tile(build.link);
        if (!((ItemBridge) (build.block)).linkValid(build.tile, otherTile)) return null;
        return (ItemBridgeBuild) otherTile.build;
    }

    private static void drawProximity(ItemBridgeBuild build) {
        for (Building bridge : build.proximity) {
            if (bridge instanceof ItemBridgeBuild other && !currentBridges.contains(other) && bridge.canDump(other, null)) {
                drawLink(other);
            }
        }
    }

    private static void drawInput(ItemBridgeBuild build, ItemBridgeBuild otherBuild) {
        Tmp.v2.trns(build.angleTo(otherBuild), 2f);
        float tx = build.x, ty = build.y;
        float ox = otherBuild.x, oy = otherBuild.y;
        float alpha = Math.abs(100 - (Time.time * 2f) % 100f) / 100f;
        float x = Mathf.lerp(ox, tx, alpha);
        float y = Mathf.lerp(oy, ty, alpha);

        int rel = otherBuild.tile.absoluteRelativeTo(otherBuild.tileX(), otherBuild.tileY());

        //draw "background"
        Draw.color(Pal.gray);
        Lines.stroke(2.5f);
        Lines.square(ox, oy, 2f, 45f);
        Lines.stroke(2.5f);
        Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

        //draw foreground colors
        Draw.color(Pal.place);
        Lines.stroke(1f);
        Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

        Lines.square(ox, oy, 2f, 45f);
        Draw.mixcol(Draw.getColor(), 1f);
        Draw.color();
        Draw.rect(((ItemBridge) (build.block)).arrowRegion, x, y, rel * 90);
        Draw.mixcol();
    }
}
