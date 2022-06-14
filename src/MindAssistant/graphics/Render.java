package MindAssistant.graphics;

import MindAssistant.graphics.draw.build.*;
import MindAssistant.graphics.draw.unit.*;
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
        ALL_RENDER.each(BaseRender::loadEnabled);
        ALL_RENDER.each(BaseRender::loadSettings);
    }

    public static void render() {
        ALL_RENDER.each(BaseRender::render);
    }
}
