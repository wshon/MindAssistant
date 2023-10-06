package MindAssistant.ai;

import MindAssistant.MindVars;
import arc.Core;
import arc.input.KeyCode;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Predict;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.input.DesktopInput;
import mindustry.input.InputHandler;
import mindustry.world.Tile;

import static arc.Core.input;
import static mindustry.Vars.player;
import static mindustry.Vars.state;

/**
 * Auto shoot and choose target
 *
 * @author wshon
 */
public class SmartDesktopInput extends DesktopInput {
    public Teamc target;
    public Vec2 targetPos = new Vec2();
    public float crosshairScale;
    public Teamc lastTarget;
    private Teamc selectTarget;
    private InputHandler oldInput;

    public void init() {
        oldInput = Vars.control.input;
        enable();
    }

    public void enable() {
        Vars.control.input = this;
    }

    public void disable() {
        Vars.control.input = oldInput;
    }

    public void toggle(boolean enable) {
        if (enable) {
            enable();
        } else {
            disable();
        }
    }

    @Override

    protected void updateMovement(Unit unit) {
        boolean omni = unit.type.omniMovement;

        float speed = unit.speed();
        float xa = Core.input.axis(Binding.move_x);
        float ya = Core.input.axis(Binding.move_y);
        boolean boosted = (unit instanceof Mechc && unit.isFlying());

        float bulletSpeed = unit.hasWeapons() ? unit.type.weapons.first().bullet.speed : 0f;
        boolean allowHealing = unit.type.canHeal;
        boolean validHealTarget = allowHealing && target instanceof Building b && b.isValid() && target.team() == unit.team && b.damaged() && target.within(unit, unit.type.range);
        float range = unit.hasWeapons() ? unit.range() : 0f;

        //reset target if:
        // - in the editor, or...
        // - it's both an invalid standard target and an invalid heal target
        if ((Units.invalidateTarget(target, unit, unit.type.range) && !validHealTarget) || state.isEditor()) {
            if (target != null) player.shooting = false;
            target = null;
        }
        Vec2 intercept = target != null ? Predict.intercept(unit, target, bulletSpeed) : null;

        movement.set(xa, ya).nor().scl(speed);
        if (Core.input.keyDown(Binding.mouse_move)) {
            movement.add(input.mouseWorld().sub(player).scl(1f / 25f * speed)).limit(speed);
        }

        float mouseAngle = intercept != null ? unit.angleTo(intercept) : Angles.mouseAngle(unit.x, unit.y);
        boolean aimCursor = omni && player.shooting && unit.type.hasWeapons() && unit.type.faceTarget && !boosted;

        if (aimCursor) {
            unit.lookAt(mouseAngle);
        } else {
            unit.lookAt(unit.prefRotation());
        }

        unit.movePref(movement);

        if (intercept != null) {
            unit.aim(intercept);
        } else {
            unit.aim(unit.type.faceTarget ? Core.input.mouseWorld() : Tmp.v1.trns(unit.rotation, Core.input.mouseWorld().dst(unit)).add(unit.x, unit.y));
        }

        if (target != null) {
            targetPos = new Vec2(target.getX(), target.getY());
            if (player.within(targetPos, unit.type.range)) {
                unit.aim(intercept);
                player.shooting = true;
            }
        }

        unit.controlWeapons(true, player.shooting && !boosted);

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

        //auto select target
        if (target == null && MindVars.settings.getBool("ai.AutoTarget.enable", false)) {
            target = Units.closestTarget(unit.team, unit.x, unit.y, range, u -> u.checkTarget(unit.type.targetAir, unit.type.targetGround), u -> unit.type.targetGround);
        }

        //select target
        if (Core.input.keyDown(KeyCode.altLeft)) {
            target = null;
            player.shooting = Core.input.keyDown(Binding.select);
            unit.lookAt(Angles.mouseAngle(unit.getX(), unit.getY()));
            if (Core.input.keyTap(Binding.select)) {
                Tile tile = Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
                if (tile == null) return;
                selectTarget = tile.build;
            }
        } else if (selectTarget != null) {
            target = selectTarget;
            selectTarget = null;
        }
    }

    @Override
    public void drawBottom() {
        super.drawBottom();
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
