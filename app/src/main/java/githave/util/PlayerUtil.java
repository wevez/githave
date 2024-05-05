package githave.util;

import githave.MCHook;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtil implements MCHook {

    public static boolean predicting = false;

    public static List<Vec3> predict(int tick) {
        predicting = true;
        List<Vec3> positions = new ArrayList<>();
        EntityPlayerSP sp = new EntityPlayerSP(
                mc,
                mc.theWorld,
                mc.getNetHandler(),
                new StatFileWriter()
        );
        sp.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        sp.onGround = mc.thePlayer.onGround;
        sp.setSprinting(mc.thePlayer.isSprinting());
        sp.setSneaking(mc.thePlayer.isSneaking());
        sp.motionX = mc.thePlayer.motionX;
        sp.motionY = mc.thePlayer.motionY;
        sp.motionZ = mc.thePlayer.motionZ;
        sp.movementInput = new MovementInputFromOptions(mc.gameSettings);
        for (int i = 0; i < tick; i++) {
            sp.movementInput.moveStrafe = mc.thePlayer.movementInput.moveStrafe;
            sp.movementInput.moveForward = mc.thePlayer.movementInput.moveForward;
            sp.movementInput.jump = mc.thePlayer.movementInput.jump;
            sp.movementInput.sneak = mc.thePlayer.movementInput.sneak;
            sp.moveForward = mc.thePlayer.moveForward;
            sp.moveStrafing = mc.thePlayer.moveStrafing;
            sp.setJumping(mc.thePlayer.movementInput.jump);
            sp.onUpdate();
            positions.add(new Vec3(sp.posX, sp.posY, sp.posZ));
        }
        predicting = false;
        return positions;
    }
}