package MindAssistant.graphics.draw.build;

import MindAssistant.MindVars;
import MindAssistant.graphics.draw.BaseBuildDrawer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Pal;

/**
 * Display health bar over building
 * @author wshon
 */
public class HealthBar extends BaseBuildDrawer {
    public static float healthBarStroke = 1.7f, healthBarAlpha = 0.85f;
    public static float backBarStroke = healthBarStroke + 1.3f, backBarAlpha = healthBarAlpha - 0.25f;

    @Override
    public void draw(Building building) {
        if (!building.isValid()) return;
        if (building.team == Team.derelict) return;
        if (!building.damaged()) return;
        doDraw(building);
    }

    private void doDraw(Building build) {
        float startX = build.x - build.hitSize() / 2f + 5f, startY = build.y - build.hitSize() / 2f + backBarStroke;
        float endX = build.x + build.hitSize() / 2f - 5f;

        /* Background */
        Lines.stroke(backBarStroke, build.team().color);
        Draw.alpha(backBarAlpha);
        Lines.line(startX, startY, endX, startY);

        Lines.stroke(healthBarStroke, Pal.health);
        Draw.alpha(healthBarAlpha);
        Lines.line(startX, startY, startX + (endX - startX) * build.healthf(), startY);

        Draw.reset();
    }
}
