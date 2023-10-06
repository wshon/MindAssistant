package MindAssistant.graphics;

import MindAssistant.graphics.draw.BaseDrawer;
import MindAssistant.graphics.draw.build.*;
import MindAssistant.graphics.draw.unit.*;
import MindAssistant.graphics.draw.unit.player.PlayerAimNotice;
import MindAssistant.graphics.draw.unit.player.PlayerRangeShow;
import MindAssistant.graphics.render.BaseRender;
import MindAssistant.graphics.render.BuildRender;
import MindAssistant.graphics.render.UnitRender;
import MindAssistant.ui.settings.SettingsMenuDialog;
import arc.struct.Seq;

/**
 * @author wshon
 */
public class Render {
    private static final Seq<BaseRender<?>> ALL_RENDER = new Seq<>();

    public static void init() {
        ALL_RENDER.add(new BuildRender()
                .addGlobalDrawer(new TurretRangeNotice())

                .addCameraDrawer(new TurretAmmoTypeShow())
                .addCameraDrawer(new BuildingHealthBarShow())

                .addHoveredDrawer(new BuildingRangeInspect())
                .addHoveredDrawer(new BuildingLogicLineInspect())
                .addHoveredDrawer(new ItemBridgeLinksInspect())
        );
        ALL_RENDER.add(new UnitRender()
                .addGlobalDrawer(new PlayerAimNotice())
                .addGlobalDrawer(new UnitRangeAlert())

                .addCameraDrawer(new UnitInfoBarShow())
                .addCameraDrawer(new PlayerRangeShow())
                .addCameraDrawer(new UnitTargetShow())

                .addHoveredDrawer(new UnitRangeInspect())
                .addHoveredDrawer(new UnitLogicLineInspect())
        );
        Render.loadEnabled();
    }

    public static void loadEnabled() {
        ALL_RENDER.each(BaseRender::loadEnabled);
    }

    public static void render() {
        ALL_RENDER.each(BaseRender::render);
    }

    public static void loadSettings(SettingsMenuDialog.SettingsTable game) {
        BaseDrawer.loadSettings(game);
    }
}
