package githave.util;

import githave.MCHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtil implements MCHook {

    public static float[] getFixedRotation(final float[] rotations, final float[] lastRotations) {
        final float yaw = rotations[0];
        final float pitch = rotations[1];

        final float lastYaw = lastRotations[0];
        final float lastPitch = lastRotations[1];

        final float f = 0.25f * 0.6F + 0.5F;
        final float gcd = f * f * f * 1.5F;

        final float deltaYaw = yaw - lastYaw;
        final float deltaPitch = pitch - lastPitch;

        final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);
        final float fixedDeltaPitch = deltaPitch - (deltaPitch % gcd);

        final float fixedYaw = lastYaw + fixedDeltaYaw;
        final float fixedPitch = lastPitch + fixedDeltaPitch;

        return new float[]{fixedYaw, fixedPitch};
    }

    public static float[] rotation(double x, double y, double z, double ax, double ay, double az) {
        final double diffX = x - ax, diffY = y - ay, diffZ = z - az;
        final float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F),
                pitch = (float) (-Math.toDegrees(Math.atan2(diffY, Math.hypot(diffX, diffZ))));
        return new float[] { yaw, pitch };
    }

    public static float[] rotation(Vec3 to, Vec3 from) {
        return rotation(to.xCoord, to.yCoord, to.zCoord, from.xCoord, from.yCoord, from.zCoord);
    }

    public static float[] rotation(Vec3 to) {
        return rotation(to, mc.thePlayer.getPositionEyes(1f));
    }

    public static float[] rotation(double x, double y, double z) {
        return rotation(x, y, z, mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static float smoothRot(float current, float intended, float factor) {
        return current + MathHelper.clamp_float(
                MathHelper.wrapAngleTo180_float(intended - current),
                -factor,
                factor
        );
    }

    public static float distSq(float[] a, float[] b) {
        float y = MathHelper.wrapAngleTo180_float(a[0] - b[0]);
        float p = MathHelper.wrapAngleTo180_float(a[1] - b[1]);
        return y * y + p * p;
    }

    public static float distSq(float[] a) {
        return distSq(a, new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch });
    }

    public static float dist(float a, float b) {
        return Math.abs(MathHelper.wrapAngleTo180_float(a - b));
    }

    public static float normalize(float a) {
        a %= 360;
        if (a < 0) a+= 360;
        return a;
    }

    public static boolean isIn(float a, float b, float c) {
        a = normalize(a);
        b = normalize(b);
        c = normalize(c);
        boolean r = false;
        boolean revert =  (Math.abs(a - b) > 180);
        if (a > b) {
            r = a > c && b < c;
        } else {
            r = a < c && b > c;
        }
        if (revert) r = !r;
        return r;
    }
}
