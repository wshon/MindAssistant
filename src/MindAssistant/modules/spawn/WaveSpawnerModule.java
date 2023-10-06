package MindAssistant.modules.spawn;

import MindAssistant.modules.BaseModule;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.scene.ui.Label;
import arc.util.Align;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Iconc;
import mindustry.graphics.Layer;

public class WaveSpawnerModule extends BaseModule {
    @Override
    public void load() {
        Events.run(EventType.Trigger.draw, () -> {
            if (isEnabled()) {
                this.draw();
            }
        });
    }

    private void draw() {
        Draw.z(Layer.overlayUI);
        Vars.spawner.getSpawns().each(t -> {
            var label = new Label("" + Iconc.blockSpawn);
            label.setFontScale(0.5f);
            label.setPosition(t.worldx(), t.worldy(), Align.center);
            label.draw();
        });
        Draw.reset();
    }
}
