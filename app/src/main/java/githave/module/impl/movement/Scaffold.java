package githave.module.impl.movement;

import githave.event.Events;
import githave.manager.RotationManager;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.DoubleSetting;
import githave.util.RayUtil;
import githave.util.TimerUtil;
import githave.util.bypass.BypassRotation;
import githave.util.bypass.IndependentCPS;
import githave.util.data.BlockData;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.stream.Collectors;

public class Scaffold extends Module {

    private static final EnumFacing[] invert = {
            EnumFacing.UP,
            EnumFacing.DOWN,
            EnumFacing.SOUTH,
            EnumFacing.NORTH,
            EnumFacing.EAST,
            EnumFacing.WEST
    };

    private static final BlockPos[][][] addons;

    static {
        addons = new BlockPos[7][2][7];
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 0; y++) {
                for (int z = -3; z <= 3; z++) {
                    addons[x + 3][y + 1][z + 3] = new BlockPos(x, y, z);
                }
            }
        }
    }

    private final TimerUtil airTimer = new TimerUtil();
    private final TimerUtil clickTimer = new TimerUtil();

    private List<BlockData> data = new ArrayList<>();
    private int lastDataUpdateTicks;

    private int sameYPos;

    private final DoubleSetting minCPS = new DoubleSetting.Builder("Min CPS", 10, 0, 20, 0.1)
            .build();

    private final DoubleSetting maxCPS = new DoubleSetting.Builder("Max CPS", 12, 0, 20, 0.1)
            .build();

    private boolean positionAdjustFinished = false;

    private BlockPos lastGroundPos = null;

    private final IndependentCPS cps = new IndependentCPS(minCPS, maxCPS);

    public Scaffold() {
        super("Scaffold", "Place blocks at your feet", ModuleCategory.Movement);
        this.getSettingList().addAll(Arrays.asList(
                minCPS,
                maxCPS
        ));
        this.setKeyCode(Keyboard.KEY_C);
    }

    @Override
    protected void onEnable() {
        lastGroundPos = null;
        data.clear();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        positionAdjustFinished = false;
        data.clear();
        super.onDisable();
    }

    @Override
    public void onMovementInput(Events.MovementInput event) {
        event.moveFix = true;
        if (!positionAdjustFinished) {
//            event.input.sneak = true;
            if (lastGroundPos == null) {
                System.out.println("lastGroundPos is null! Please stand on the ground once.");
                return;
            }
            positionAdjustFinished = true;
        }
//        event.input.moveStrafe = mc.thePlayer.ticksExisted % 2 == 0 ? -1 : 1;
        super.onMovementInput(event);
    }

    @Override
    public void onGameLoop(Events.GameLoop event) {
        MovingObjectPosition a = mc.objectMouseOver;
//        if (airTimer.hasTimeElapsed(60) && mc.thePlayer.onGround && (a.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || a.sideHit == EnumFacing.UP)) {
////            event.input.jump = true;
//            mc.thePlayer.jump();
//        }
        super.onGameLoop(event);
    }

    private boolean isDiagonal() {
        return Math.abs(Math.round(RotationManager.virtualYaw / 90) * 90 - RotationManager.virtualYaw) > 22.5;
    }

    private float[] calcRotationStd() {
        float yaw = RotationManager.virtualYaw + 180f;
        yaw = Math.round(yaw / 45) * 45;
        final boolean diagonal = isDiagonal();
        if (!diagonal) {
            final double modX = lastGroundPos == null ? mc.thePlayer.posX - Math.floor(mc.thePlayer.posX) : lastGroundPos.getX() + 0.5 - mc.thePlayer.posX;
            final double modZ = lastGroundPos == null ? mc.thePlayer.posZ - Math.floor(mc.thePlayer.posZ) : lastGroundPos.getZ() + 0.5- mc.thePlayer.posZ;
//            System.out.println(modX + " " + modZ);
//            double modX = mc.thePlayer.posX - Math.floor(mc.thePlayer.posX);
//            double modZ = mc.thePlayer.posZ - Math.floor(mc.thePlayer.posZ);
            double va = 0;
            double ma = 0;
            switch (EnumFacing.fromAngle(mc.thePlayer.rotationYaw).toString()) {
                case "south":
                    if (modX > ma) yaw -= 45;
                    if (modX < va) yaw += 45;
                    break;
                case "north":
                    if (modX > ma) yaw += 45;
                    if (modX < va) yaw -= 45;
                    break;
                case "east":
                    if (modZ > ma) yaw += 45;
                    if (modZ < va) yaw -= 45;
                    break;
                case "west":
                    if (modZ > ma) yaw -= 45;
                    if (modZ < va) yaw += 45;
                    break;
            }
        }
        // 75.9003f: diagonal
        return new float[] {
                yaw,
                75.9003f//76.003f
        };
    }

    @Override
    public void onRotation(Events.Rotation event) {
//        System.out.println(mc.thePlayer.rotationPitch);
//        mc.objectMouseOver = mc.thePlayer.rayTrace(3, 1f);
        if (lastDataUpdateTicks != mc.thePlayer.ticksExisted) {
            lastDataUpdateTicks = mc.thePlayer.ticksExisted;
            data = getBlockData();
        }
        float[] rotation = calcRotationStd();
        rotation = BypassRotation.getInstance().limitAngle(
                new float[] {mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch},
                rotation
        );
        if (Math.abs(MathHelper.wrapAngleTo180_float(rotation[0] - mc.thePlayer.rotationYaw)) > 0.1) {
            positionAdjustFinished = false;
        }
        mc.timer.timerSpeed = 1f;
        if (rotation != null) {
            event.yaw = rotation[0];
            event.pitch = rotation[1];
//            RotationManager.virtualPrevYaw = RotationManager.virtualYaw = event.yaw = 45;
//            RotationManager.virtualPitch = RotationManager.virtualPrevPitch = event.pitch = rotation[1];
        }
        super.onRotation(event);
    }

    @Override
    public void onUpdate(Events.Update event) {
        if (event.pre) {
            BlockPos under = new BlockPos(mc.thePlayer).add(0, -1, 0);
            if (mc.theWorld.getBlockState(under).getBlock() != Blocks.air) {
                lastGroundPos = under;
                airTimer.reset();
            }
        }else {
        }
        super.onUpdate(event);
    }

    private void rightClick(MovingObjectPosition objectPosition) {
//        if (!clickTimer.hasTimeElapsed(50)) return;
        if (mc.playerController.getIsHittingBlock()) return;
        if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, -1, 0)).getBlock() != Blocks.air) return;
//        if (cps.onTick()) {
//            mc.rightClickMouse();
//            return;
//        }
        if (objectPosition == null || objectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
        if (mc.theWorld.getBlockState(objectPosition.getBlockPos()).getBlock() == Blocks.air) return;
        if (mc.thePlayer.getHeldItem() == null) return;
        float x = (float) (objectPosition.hitVec.xCoord - objectPosition.getBlockPos().getX());
        float y = (float) (objectPosition.hitVec.yCoord - objectPosition.getBlockPos().getY());
        float z = (float) (objectPosition.hitVec.zCoord - objectPosition.getBlockPos().getZ());
        if (mc.thePlayer.getHeldItem().getItem().onItemUse(mc.thePlayer.getHeldItem(), mc.thePlayer, mc.theWorld, objectPosition.getBlockPos(), objectPosition.sideHit,
                x, y,z)) {
            mc.rightClickDelayTimer = 4;
            mc.thePlayer.swingItem();
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(objectPosition.getBlockPos(), objectPosition.sideHit.getIndex(), mc.thePlayer.inventory.getCurrentItem(), x, y, z));
            clickTimer.reset();
        } else {
            MovingObjectPosition a = mc.thePlayer.rayTrace(3, 1f);
            if (!isDiagonal() && airTimer.hasTimeElapsed(50) && mc.thePlayer.onGround && (a.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || a.sideHit == EnumFacing.UP)) {
//            event.input.jump = true;
                mc.thePlayer.jump();
            }
        }
    }

    @Override
    public void onTick(Events.Tick event) {
//        if (data.isEmpty()) return;
//        if (!clickTimer.hasTimeElapsed(100)) return;
//        mc.objectMouseOver = mc.thePlayer.rayTrace(3, mc.timer.renderPartialTicks);
//        MovingObjectPosition objectPosition = mc.objectMouseOver;
//        if (objectPosition == null) return;
//        if (objectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
//        BlockPos pushed = objectPosition.getBlockPos().offset(objectPosition.sideHit);
//        if (!mc.thePlayer.onGround && !mc.thePlayer.getEntityBoundingBox().intersectsWith(
//                new AxisAlignedBB(
//                        pushed.getX(),
//                        pushed.getY(),
//                        pushed.getZ(),
//                        pushed.getX() + 1,
//                        pushed.getY() + 1,
//                        pushed.getZ() + 1
//                )
//        )) {
//            System.out.println(mc.thePlayer.ticksExisted);
//            mc.rightClickMouse();
//            clickTimer.reset();
//            return;
//        }
//        if (objectPosition.sideHit == EnumFacing.UP) return;
//        this.rightClick(objectPosition);
//        System.out.println(mc.thePlayer.ticksExisted);
//        clickTimer.reset();
        this.rightClick(mc.objectMouseOver);
        super.onTick(event);
    }

    private List<BlockData> getBlockData() {
        BlockPos blockPos = new BlockPos(
                (int) mc.thePlayer.posX,
                sameYPos,
                (int) mc.thePlayer.posZ
        ).add(0, -1, 0);
        if (mc.theWorld.getBlockState(blockPos).getBlock() != Blocks.air) {
            return null;
        }
        {
            List<BlockData> data = Arrays.stream(EnumFacing.values()).filter(facing -> mc.theWorld.getBlockState(blockPos.offset(facing)).getBlock() == Blocks.air)
                    .map(facing -> new BlockData(blockPos.offset(facing), invert[facing.ordinal()]))
                    .collect(Collectors.toList());
            if (!data.isEmpty()) return data;
        }
        Vec3 eye = mc.thePlayer.getPositionEyes(1f);
        List<BlockData> dataEntry = new ArrayList<>();
        for (BlockPos[][] xBP : addons) {
            for (BlockPos[] yBP : xBP) {
                for (BlockPos zBP : yBP) {
                    BlockPos offsetPos = blockPos.add(zBP);
                    if (mc.theWorld.getBlockState(offsetPos).getBlock() != Blocks.air) continue;;
                    for (EnumFacing facing : EnumFacing.values()) {
                        if (mc.theWorld.getBlockState(offsetPos.offset(facing)).getBlock() == Blocks.air) continue;
                        dataEntry.add(new BlockData(offsetPos.offset(facing), invert[facing.ordinal()]));
                    }
                }
            }
        }
        final double placeRangeSq = 9;
        return dataEntry.stream()
                .filter(d -> mc.thePlayer.getDistanceSq(d.getPos().offset(d.getFacing())) < placeRangeSq)
                .sorted(Comparator.comparingDouble(d -> eye.distanceTo(d.getPos().offset(d.getFacing()).vec3())))
                .collect(Collectors.toList());
    }
}
