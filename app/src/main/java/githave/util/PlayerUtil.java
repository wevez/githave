package githave.util;

import githave.MCHook;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtil implements MCHook {

    public static boolean predicting = false;

    // TODO: predict movement input
    public static List<Vec3> predict(Entity entity, int tick) {
        predicting = true;
        List<Vec3> positions = new ArrayList<>();
        EntityPlayerSP sp = new EntityPlayerSP(
                mc,
                mc.theWorld,
                mc.getNetHandler(),
                new StatFileWriter()
        );
        sp.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
        sp.onGround = entity.onGround;
        sp.setSprinting(entity.isSprinting());
        sp.setSneaking(entity.isSneaking());
        sp.motionX = entity.motionX;
        sp.motionY = entity.motionY;
        sp.motionZ = entity.motionZ;
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

    public static List<MovingObjectPosition> rayTracePredict(int tick) {
        predicting = true;
        List<MovingObjectPosition> positions = new ArrayList<>();
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
            positions.add(sp.rayTrace(3, 1f));
        }
        predicting = false;
        return positions;
    }
}
