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

public class MinimapSpawnerModule extends BaseModule {
    private boolean showSpawns = true;

    @Override
    public void load() {
        Events.on(EventType.WorldLoadEvent.class, e -> {
            showSpawns = Vars.state.rules.showSpawns;
            if (isEnabled()) {
                Vars.state.rules.showSpawns = true;
            }
        });
    }

    protected void onEnabledChange(boolean enabled) {
        if (enabled) {
            Vars.state.rules.showSpawns = true;
        } else {
            Vars.state.rules.showSpawns = showSpawns;
        }
    }
}
