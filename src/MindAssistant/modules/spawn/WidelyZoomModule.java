package MindAssistant.modules.spawn;

import MindAssistant.modules.BaseModule;
import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;

public class WidelyZoomModule extends BaseModule {
    @Override
    public void load() {
        Events.on(EventType.WorldLoadEvent.class, e -> {
            if (isEnabled()) {
                setWidelyZoom();
            }
        });
    }

    protected void onEnabledChange(boolean enabled) {
        if (enabled) {
            setWidelyZoom();
        } else {
            restoreDefault();
        }
    }

    private void restoreDefault() {
        Vars.renderer.maxZoom = 6.0f;
        Vars.renderer.minZoom = 1.5f;
    }

    private void setWidelyZoom() {
        Vars.renderer.maxZoom = 25.0f;
        Vars.renderer.minZoom = 0.3f;
    }
}
