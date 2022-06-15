package MindAssistant.graphics;

import MindAssistant.graphics.draw.build.*;
import MindAssistant.graphics.draw.unit.InfoBar;
import MindAssistant.graphics.draw.unit.UnitAlert;
import MindAssistant.graphics.draw.unit.UnitLogic;
import MindAssistant.graphics.draw.unit.UnitRange;
import MindAssistant.graphics.draw.unit.player.PlayerAim;
import MindAssistant.graphics.render.BaseRender;
import MindAssistant.graphics.render.BuildRender;
import MindAssistant.graphics.render.UnitRender;
import arc.struct.Seq;

/**
 * @author wshon
 */
public class Render {
    private static final Seq<BaseRender<?>> ALL_RENDER = new Seq<>();

    public static void init() {
        ALL_RENDER.add(
                new BuildRender()
                        .addGlobalDrawers(new TurretAlert())
                        .addCameraDrawers(new TurretAmmo(), new HealthBar())
                        .addHoveredDrawers(new BuildRange(), new LogicLine()),
                new UnitRender()
                        .addGlobalDrawers(new UnitAlert(), new PlayerAim())
                        .addCameraDrawers(new InfoBar())
                        .addHoveredDrawers(new UnitRange(), new UnitLogic())
        );
        Render.loadEnabled();
    }

    public static void loadEnabled() {
        ALL_RENDER.each(BaseRender::loadEnabled);
    }

    public static void render() {
        ALL_RENDER.each(BaseRender::render);
    }
}
