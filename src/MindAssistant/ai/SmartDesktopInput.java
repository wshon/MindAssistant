package MindAssistant.ai;

import arc.Core;
import arc.input.KeyCode;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Predict;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.input.DesktopInput;
import mindustry.type.UnitType;
import mindustry.world.Tile;

import static arc.Core.input;
import static mindustry.Vars.player;
import static mindustry.Vars.state;

/**
 * @author wshon
 */
public class SmartDesktopInput extends DesktopInput {
    public @Nullable
    Teamc target;
    public Vec2 targetPos = new Vec2();
    public float crosshairScale;
    public Teamc lastTarget;

    public void init() {
        Vars.control.input = this;
    }

    protected void updateMovement(Unit unit) {
        Rect rect = Tmp.r3;

        UnitType type = unit.type;
        if (type == null) return;

        boolean omni = unit.type.omniMovement;

        float speed = unit.speed();
        float xa = Core.input.axis(Binding.move_x);
        float ya = Core.input.axis(Binding.move_y);
        boolean allowHealing = type.canHeal;
        boolean validHealTarget = allowHealing && target instanceof Building b && b.isValid() && target.team() == unit.team && b.damaged() && target.within(unit, type.range);
        boolean boosted = (unit instanceof Mechc && unit.isFlying());
        float range = unit.hasWeapons() ? unit.range() : 0f;


        //reset target if:
        // - in the editor, or...
        // - it's both an invalid standard target and an invalid heal target
        if ((Units.invalidateTarget(target, unit, type.range) && !validHealTarget) || state.isEditor()) {
            if (target != null) player.shooting = false;
            target = null;
        }

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

        movement.set(xa, ya).nor().scl(speed);
        if (Core.input.keyDown(Binding.mouse_move)) {
            movement.add(input.mouseWorld().sub(player).scl(1f / 25f * speed)).limit(speed);
        }

        float mouseAngle = target != null ? unit.angleTo(unit.aimX(), unit.aimY()) : Angles.mouseAngle(unit.x, unit.y);
        boolean aimCursor = omni && player.shooting && unit.type.hasWeapons() && unit.type.faceTarget && !boosted && unit.type.rotateShooting;

        if (aimCursor) {
            unit.lookAt(mouseAngle);
        } else {
            unit.lookAt(unit.prefRotation());
        }

        unit.movePref(movement);

        if (target != null) {
            targetPos = new Vec2(target.getX(), target.getY());
            if (player.within(targetPos, type.range)) {
                player.shooting = true;
                float bulletSpeed = unit.hasWeapons() ? type.weapons.first().bullet.speed : 0f;
                Vec2 intercept = Predict.intercept(unit, target, bulletSpeed);
                unit.aim(intercept.x, intercept.y);
                unit.controlWeapons(true);
            }
        } else {
            unit.aim(unit.type.faceTarget ? Core.input.mouseWorld() : Tmp.v1.trns(unit.rotation, Core.input.mouseWorld().dst(unit)).add(unit.x, unit.y));
            unit.controlWeapons(true, player.shooting && !boosted);
        }

        player.boosting = Core.input.keyDown(Binding.boost);
        player.mouseX = unit.aimX();
        player.mouseY = unit.aimY();

        //update payload input
        if (unit instanceof Payloadc) {
            if (Core.input.keyTap(Binding.pickupCargo)) {
                tryPickupPayload();
            }

            if (Core.input.keyTap(Binding.dropCargo)) {
                tryDropPayload();
            }
        }

        //update commander unit
        if (Core.input.keyTap(Binding.command) && unit.type.commandLimit > 0) {
            Call.unitCommand(player);
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
