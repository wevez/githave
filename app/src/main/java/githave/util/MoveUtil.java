package githave.util;

import githave.MCHook;

public class MoveUtil implements MCHook {
    public static double getSpeed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }
}
