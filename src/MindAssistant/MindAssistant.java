package MindAssistant;

import MindAssistant.graphics.Render;
import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.Time;
import mindustry.game.EventType;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.Trigger;
import mindustry.mod.Mod;
import mindustry.ui.dialogs.BaseDialog;

import static MindAssistant.MindVars.playerAI;

public class MindAssistant extends Mod {

    public MindAssistant() {
        Log.info("Loaded MindAssistant constructor.");

        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            //show dialog upon startup
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("frog");
                dialog.cont.add("behold").row();
                //mod sprites are prefixed with the mod name (this mod is called 'example-java-mod' in its config)
                dialog.cont.image(Core.atlas.find("example-java-mod-frog")).pad(20f).row();
                dialog.cont.button("I see", dialog::hide).size(100f, 50f);
                dialog.show();
            });

            MindVars.init();
            Render.init();
        });
        Events.on(EventType.ContentInitEvent.class, e -> MindVars.loadContent());
        Events.run(Trigger.draw, this::draw);
        Events.run(Trigger.update, this::update);
    }

    private void update() {
        playerAI.update();
    }

    private void draw() {
        Render.render();
        playerAI.draw();
    }

    @Override
    public void loadContent() {
        Log.info("Loading some example content.");
    }

}
