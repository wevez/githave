package githave.util;

import githave.MCHook;
import net.minecraft.util.Vec3;

public class RotationUtil implements MCHook {

    public static float[] getFixedRotation(final float[] rotations, final float[] lastRotations) {
        final float yaw = rotations[0];
        final float pitch = rotations[1];

        final float lastYaw = lastRotations[0];
        final float lastPitch = lastRotations[1];

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.5F;
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
}
