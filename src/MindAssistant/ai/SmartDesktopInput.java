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
import mindustry.input.DesktopInput;
import mindustry.type.UnitType;
import mindustry.world.Tile;

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
    public boolean autoTarget = false;
    public Teamc lastTarget;
    private Teamc selectTarget;

    public void init() {
        Vars.control.input = this;
    }

    @Override
    protected void updateMovement(Unit unit) {
        if (target != null) {
            var faceTargetRec = unit.type.faceTarget;
            unit.type.faceTarget = false;
            super.updateMovement(unit);
            unit.type.faceTarget = faceTargetRec;
        } else {
            super.updateMovement(unit);
        }

        UnitType type = unit.type;
        if (type == null) return;

        boolean omni = unit.type.omniMovement;
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

        if (target == null && autoTarget) {
            target = Units.closestTarget(unit.team, unit.x, unit.y, range, u -> u.checkTarget(type.targetAir, type.targetGround), u -> type.targetGround);
        }

        if (Core.input.keyDown(KeyCode.altLeft)) {
            target = null;
            player.shooting = Core.input.keyDown(Binding.select);
            float mouseAngle = Angles.mouseAngle(unit.getX(), unit.getY());
            unit.lookAt(mouseAngle);
            unit.rotation = mouseAngle;
            if (Core.input.keyTap(Binding.select)) {
                Tile tile = Vars.world.tileWorld(Core.input.mouseWorldX(), Core.input.mouseWorldY());
                if (tile == null) return;
                selectTarget = tile.build;
            }
        } else if (selectTarget != null) {
            target = selectTarget;
            selectTarget = null;

        }

        if (target != null) {
            targetPos = new Vec2(target.getX(), target.getY());
            if (player.within(targetPos, type.range)) {
                player.shooting = true;

                float bulletSpeed = unit.hasWeapons() ? type.weapons.first().bullet.speed : 0f;
                Vec2 intercept = Predict.intercept(unit, target, bulletSpeed);

                float mouseAngle = unit.angleTo(intercept.x, intercept.y);
                boolean aimCursor = omni && player.shooting && unit.type.hasWeapons() && unit.type.faceTarget && !boosted && unit.type.rotateShooting;
                if (aimCursor) {
                    unit.lookAt(mouseAngle);
                    unit.rotation = mouseAngle;
                } else {
                    unit.lookAt(unit.prefRotation());
                }

                unit.aim(intercept.x, intercept.y);

                unit.controlWeapons(player.shooting && !boosted);
            }
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
