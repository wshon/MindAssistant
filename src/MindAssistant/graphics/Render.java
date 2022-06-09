package MindAssistant.graphics;

import MindAssistant.graphics.draw.build.TurretAlert;
import MindAssistant.graphics.draw.build.TurretAmmo;
import MindAssistant.graphics.render.BaseRender;
import MindAssistant.graphics.render.BuildRender;
import arc.struct.Seq;

/**
 * @author wangsen
 */
public class Render {
    private static final Seq<BaseRender<?>> ALL_RENDER = new Seq<>();

    public static void init() {
        ALL_RENDER.add(
                new BuildRender()
                        .addGlobalDrawers(new TurretAlert())
                        .addCameraDrawers(new TurretAmmo())
        );
        ALL_RENDER.each(BaseRender::loadEnabled);
        ALL_RENDER.each(BaseRender::loadSettings);
    }

    public static void render() {
        ALL_RENDER.each(BaseRender::render);
    }
}