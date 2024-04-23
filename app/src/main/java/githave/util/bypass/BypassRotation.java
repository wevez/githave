package githave.util.bypass;

import githave.MCHook;
import githave.util.RayUtil;
import githave.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class BypassRotation implements MCHook {

    private static final BypassRotation SINGLETON = new BypassRotation();

    public static BypassRotation getInstance() {
        return SINGLETON;
    }

    private float coefDistance = -0.75f;
    private float coefDiffH = 0.37f;
    private float coefDiffV = 0.2f;
    private float coefCrosshairH = 1f;
    private float coefCrosshairV = -5.7f;
    private float interceptH = 11f;
    private float interceptV = 4.71f;
    private float minumumTurnSpeedH = 0.05f;
    private float minumumTurnSpeedV = 0.05f;

    public float[] limitAngle(float[] current, float[] to) {
        float yawDifference = MathHelper.wrapAngleTo180_float(to[0] - current[0]);
        float pitchDifference = to[1] - current[1];

        float rotationDifference = (float) Math.hypot(yawDifference, pitchDifference);

        float[] factor = computeTurnSpeed((float) 0, Math.abs(yawDifference), Math.abs(pitchDifference), false);

        float straightLineYaw = Math.max(Math.abs(yawDifference / rotationDifference) * factor[0], minumumTurnSpeedH) / 8;
        float straightLinePitch = Math.max(Math.abs(pitchDifference / rotationDifference) * factor[1], minumumTurnSpeedV) / 8;

        return new float[] {
                current[0] + MathHelper.clamp_float(yawDifference, -straightLineYaw, straightLineYaw),
                current[1] + MathHelper.clamp_float(pitchDifference, -straightLinePitch, straightLinePitch)
        };
    }

    public float[] limitAngle(float[] current, float[] to, Vec3 vec3, Entity entity) {
        double distance = vec3.distanceTo(mc.thePlayer.getPositionVector());
        boolean crosshair = RayUtil.rayEntity(Math.max(3, distance), current) == entity;

        float yawDifference = MathHelper.wrapAngleTo180_float(to[0] - current[0]);
        float pitchDifference = to[1] - current[1];

        float rotationDifference = (float) Math.hypot(yawDifference, pitchDifference);

        float[] factor = computeTurnSpeed((float) distance, Math.abs(yawDifference), Math.abs(pitchDifference), crosshair);

        float straightLineYaw = Math.max(Math.abs(yawDifference / rotationDifference) * factor[0], minumumTurnSpeedH) / 3;
        float straightLinePitch = Math.max(Math.abs(pitchDifference / rotationDifference) * factor[1], minumumTurnSpeedV) / 3;

        return new float[] {
                current[0] + MathHelper.clamp_float(yawDifference, -straightLineYaw, straightLineYaw),
                current[1] + MathHelper.clamp_float(pitchDifference, -straightLinePitch, straightLinePitch)
        };
    }

    private float[] computeTurnSpeed(float distance, float diffH, float diffV, boolean crosshair) {
        final float turnSpeedH = coefDistance * distance + coefDiffH + diffH + (crosshair ? coefCrosshairH : 0) + interceptH;
        float turnSpeedV = coefDistance * distance + coefDiffV * Math.max(0f, diffV - diffH) + (crosshair ? coefCrosshairV : 0) + interceptV;
        return new float[] {
                Math.max(Math.abs(turnSpeedH), minumumTurnSpeedH),
                Math.max(Math.abs(turnSpeedV), minumumTurnSpeedV)
        };
    }
}
