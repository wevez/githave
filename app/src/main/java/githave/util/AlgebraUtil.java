package githave.util;

import githave.MCHook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class AlgebraUtil implements MCHook {

    public static Vec3 nearest(AxisAlignedBB bb) {
        Vec3 p = mc.thePlayer.getPositionEyes(1f);
        return new Vec3(
                MathHelper.clamp_double(p.xCoord, bb.minX, bb.maxX),
                MathHelper.clamp_double(p.yCoord, bb.minY, bb.maxY),
                MathHelper.clamp_double(p.zCoord, bb.minZ, bb.maxZ)
        );
    }

    public static Vec3 nearest(AxisAlignedBB bb, Vec3 p) {
        return new Vec3(
                MathHelper.clamp_double(p.xCoord, bb.minX, bb.maxX),
                MathHelper.clamp_double(p.yCoord, bb.minY, bb.maxY),
                MathHelper.clamp_double(p.zCoord, bb.minZ, bb.maxZ)
        );
    }
}
