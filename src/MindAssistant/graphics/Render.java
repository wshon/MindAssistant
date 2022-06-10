package MindAssistant.graphics;

import MindAssistant.graphics.draw.build.HealthBar;
import MindAssistant.graphics.draw.build.TurretAlert;
import MindAssistant.graphics.draw.build.TurretAmmo;
import MindAssistant.graphics.draw.unit.InfoBar;
import MindAssistant.graphics.draw.unit.UnitAlert;
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
                        .addCameraDrawers(new TurretAmmo(), new HealthBar()),
                new UnitRender()
                        .addGlobalDrawers(new UnitAlert())
                        .addCameraDrawers(new InfoBar())
        );
        ALL_RENDER.each(BaseRender::loadEnabled);
        ALL_RENDER.each(BaseRender::loadSettings);
    }

    public static void render() {
        ALL_RENDER.each(BaseRender::render);
    }
}
