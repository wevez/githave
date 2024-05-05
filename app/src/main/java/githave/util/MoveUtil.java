package githave.util;

import githave.MCHook;
import githave.event.Events;

public class MoveUtil implements MCHook {

    public static double getSpeed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static boolean isMoving(Events.MovementInput input) {
        return input.input.jump || input.input.moveForward != 0 || input.input.moveStrafe != 0;
    }
}
