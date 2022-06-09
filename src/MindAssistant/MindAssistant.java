package MindAssistant;

import MindAssistant.graphics.Render;
import arc.*;
import arc.util.*;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;

public class MindAssistant extends Mod{

    public MindAssistant(){
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
        Events.run(Trigger.draw, Render::render);
        Events.on(EventType.ContentInitEvent.class, e -> MindVars.loadContent());
        Events.run(Trigger.update, this::update);
    }

    private void update() {
    }

    @Override
    public void loadContent(){
        Log.info("Loading some example content.");
    }

}
