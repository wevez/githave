package githave.manager.rotation;

import githave.MCHook;
import net.minecraft.util.Vec3;

public class PositionManager implements MCHook {

    private static final Vec3[] positions = new Vec3[20];

    {
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new Vec3(0, 0, 0);
        }
    }

    public static Vec3[] getPositions() {
        return positions;
    }

    public static void updatePositions() {
//        Vec3 position = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
//        if (positions[0].is(position)) return;
//        for (int i = positions.length - 1; i > 0; i--) {
//            positions[i] = positions[i - 1];
//        }
//        positions[0] = position;
    }
}
