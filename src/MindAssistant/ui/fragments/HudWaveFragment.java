package MindAssistant.ui.fragments;

import arc.Events;
import arc.scene.Group;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.game.EventType;
import mindustry.game.SpawnGroup;
import mindustry.gen.Iconc;
import mindustry.type.UnitType;

import java.util.HashMap;
import java.util.Map;

import static mindustry.Vars.state;

public class HudWaveFragment {

    public void build(Group parent) {

        Events.on(EventType.WaveEvent.class, e -> this.rebuild(parent));
        Events.on(EventType.WorldLoadEvent.class, e -> this.rebuild(parent));

        rebuild(parent);
    }

    private void rebuild(Group parent) {
        Map<UnitType, Integer> maps = new HashMap<>();
        Vars.state.rules.spawns.each(spawnGroup -> {
            if (spawnGroup == null) return;
            var count = maps.getOrDefault(spawnGroup.type, 0);
            maps.put(spawnGroup.type, count + spawnGroup.getSpawned(state.wave));
        });
        var c = parent.find("waveinfo");
        if (c != null) parent.removeChild(c);
        parent.fill(t -> {
            t.name = "waveinfo";
            t.left();
            t.top().marginTop(120f);
            t.add("下一波敌人");
            drawWaveGroup(t, state.wave - 1);
            t.row();
        });
    }

    private void drawWaveGroup(Table t, int i) {
        Vars.state.rules.spawns.each(spawnGroup -> {
            if (spawnGroup == null) return;
            this.drawWaveItem(t, spawnGroup, i);
        });
    }

    private void drawWaveItem(Table tt, SpawnGroup group, int i) {
        var count = group.getSpawned(i);
        if (count == 0) return;
        tt.table(g -> {
            g.table(gt -> {
                gt.image(group.type.uiIcon).size(18f);
                gt.add("x" + count).get().setFontScale(0.7f);
            });
            g.row();
            g.add("" + group.getShield(i)).get().setFontScale(0.7f);
            g.row();
            g.table(eip -> {
                if (group.effect != null && group.effect != StatusEffects.none)
                    eip.image(group.effect.uiIcon).size(12f);
                if (group.items != null)
                    eip.stack(new Image(group.items.item.uiIcon), new Label(String.valueOf(group.items.amount)) {{
                        this.setFillParent(true);
                        this.setAlignment(Align.bottomRight);
                        this.setFontScale(0.7f);
                    }}).size(12f);
                if (group.payloads != null && !group.payloads.isEmpty())
                    eip.add("" + Iconc.units).get().setFontScale(0.7f);
            });
        }).pad(2f);
    }
}
