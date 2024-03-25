package githave.util;

import githave.MCHook;
import githave.util.data.BlockData;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RayCastUtil implements MCHook {

    public static boolean lookingAtBlock(BlockData data, float yaw, float pitch, boolean strict) {
        MovingObjectPosition movingObjectPosition = mc.thePlayer.rayTraceCustom(mc.playerController.getBlockReachDistance(), mc.timer.renderPartialTicks, yaw, pitch);

        if (movingObjectPosition == null) return false;

        Vec3 hitVec = movingObjectPosition.hitVec;
        if (hitVec == null) return false;

        return movingObjectPosition.getBlockPos().equals(data.getPos()) && (!strict || (movingObjectPosition.sideHit == data.getFacing() && movingObjectPosition.sideHit != null));
    }
}
