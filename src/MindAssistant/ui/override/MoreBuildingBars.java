package MindAssistant.ui.override;

import arc.Core;
import arc.graphics.Color;
import mindustry.core.UI;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.Reconstructor.ReconstructorBuild;
import mindustry.world.blocks.units.UnitFactory;

/**
 * @author wshon
 */
public class MoreBuildingBars {
    public static void addMoreBars(Block block) {
        block.addBar("health", e -> new Bar(
                () -> String.format("%.2f/%.2f(%d%%)", e.health, e.maxHealth, (int) (100 * e.healthf())),
                () -> Pal.health, e::healthf).blink(Color.white));

        if (block instanceof UnitFactory factory) {
            block.addBar("progress", (UnitFactory.UnitFactoryBuild e) -> new Bar(
                    () -> {
                        float ticks = e.currentPlan == -1 ? 0 : (1 - e.fraction()) * factory.plans.get(e.currentPlan).time / e.timeScale();
                        return Core.bundle.get("bar.progress") + " : " + UI.formatTime(ticks) + "(" + (int) (100 * e.fraction()) + "%" + ")";
                    },
                    () -> Pal.ammo, e::fraction));
        }
        if (block instanceof Reconstructor reconstructor) {
            block.addBar("progress", (ReconstructorBuild e) -> new Bar(
                    () -> {
                        float ticks = (1 - e.fraction()) * reconstructor.constructTime / e.timeScale();
                        return Core.bundle.get("bar.progress") + " : " + UI.formatTime(ticks) + "(" + (int) (100 * e.fraction()) + "%" + ")";
                    },
                    () -> Pal.ammo, e::fraction));
        }
    }
}
