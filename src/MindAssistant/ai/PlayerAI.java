package MindAssistant.ai;

import arc.Core;
import arc.input.KeyCode;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.entities.Predict;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Mechc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.input.InputHandler;
import mindustry.type.UnitType;
import mindustry.world.Tile;

import static mindustry.Vars.player;
import static mindustry.Vars.state;

/**
 * @author wshon
 */
public class PlayerAI extends InputHandler {
    public @Nullable Teamc target;

    public Vec2 targetPos = new Vec2();
    public float crosshairScale;
    public Teamc lastTarget;

    public void init() {
    }

    @Override
    public void update() {
        Unit unit = player.unit();
        UnitType type = unit.type;
        if (type == null) return;

        float range = unit.hasWeapons() ? unit.range() : 0f;
        boolean omni = unit.type.omniMovement;
        boolean allowHealing = type.canHeal;
        boolean validHealTarget = allowHealing && target instanceof Building b && b.isValid() && target.team() == unit.team && b.damaged() && target.within(unit, type.range);
        boolean boosted = (unit instanceof Mechc && unit.isFlying());

        //reset target if:
        // - in the editor, or...
        // - it's both an invalid standard target and an invalid heal target
        if ((Units.invalidateTarget(target, unit, type.range) && !validHealTarget) || state.isEditor()) {
            target = null;
        }

        float bulletSpeed = unit.hasWeapons() ? type.weapons.first().bullet.speed : 0f;
        boolean aimCursor = omni && player.shooting && type.hasWeapons() && type.faceTarget && !boosted && type.rotateShooting;


        if (Core.input.keyDown(KeyCode.altLeft)) {
            float mouseAngle = Angles.mouseAngle(unit.getX(), unit.getY());
            unit.lookAt(mouseAngle);
            if (Core.input.keyTap(Binding.select)) {
                Tile tile = Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
                if (tile == null) return;
                target = tile.build;
            }
        } else if (target == null) {
            target = Units.closestTarget(unit.team, unit.x, unit.y, range, u -> u.checkTarget(type.targetAir, type.targetGround), u -> type.targetGround);
        }

        if (target != null) {
            targetPos = new Vec2(target.getX(), target.getY());
            if (player.within(targetPos, type.range)) {
                if (aimCursor) {
                    unit.lookAt(targetPos);
                } else {
                    unit.lookAt(unit.prefRotation());
                }
                Vec2 intercept = Predict.intercept(unit, target, bulletSpeed);
                unit.aim(intercept.x, intercept.y);
                unit.controlWeapons(true);
            }
        }
    }

    public void draw() {
        //draw targeting crosshair
        if (target != null && !state.isEditor()) {
            if (target != lastTarget) {
                crosshairScale = 0f;
                lastTarget = target;
            }

            crosshairScale = Mathf.lerpDelta(crosshairScale, 1f, 0.2f);

            Drawf.target(target.getX(), target.getY(), 7f * Interp.swingIn.apply(crosshairScale), 0.5f, Pal.remove);
        }
    }
}
