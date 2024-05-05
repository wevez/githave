package githave.module.impl.movement;

import githave.event.Events;
import githave.manager.RotationManager;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.DoubleSetting;
import githave.module.setting.impl.ModeSetting;
import githave.util.*;
import githave.util.bypass.BypassRotation;
import githave.util.bypass.IndependentCPS;
import githave.util.data.BlockData;
import githave.util.render.Render3DUtil;
import net.minecraft.block.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.function.ToDoubleFunction;
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
    private final TimerUtil sneakTimer = new TimerUtil();
    private final TimerUtil dragClickTimer = new TimerUtil();
    private float pitchStack;
    private int placeCounter;

    private int lastSlot;

    private BlockData data;
    public static boolean shouldScaffold;

    private int sameYPos;

    private final ModeSetting mode = new ModeSetting.Builder("Mode", "Polar", "Intave")
            .build();

    private final ModeSetting itemSpoof = new ModeSetting.Builder("Item Spoof", "Switch", "Spoof")
            .build();

    private final DoubleSetting affordBlock = new DoubleSetting.Builder("Afford block", 0, 1, 5, 1)
            .build();

    private final ModeSetting blockESP = new ModeSetting.Builder("Block ESP", "Outline", "Fill", "Both", "None")
            .build();

    private boolean positionAdjustFinished = false;

    private BlockPos lastGroundPos = null;

    public Scaffold() {
        super("Scaffold", "Place blocks at your feet", ModuleCategory.Movement);
        this.getSettingList().addAll(Arrays.asList(
                mode,
                itemSpoof,
                affordBlock,
                blockESP
        ));
        this.setKeyCode(Keyboard.KEY_C);
    }

    @Override
    protected void onEnable() {
        lastSlot = mc.thePlayer.inventory.currentItem;
        placeCounter = 0;
        positionAdjustFinished = false;
        lastGroundPos = null;
        data = null;
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        shouldScaffold = false;
        mc.thePlayer.inventory.currentItem = lastSlot;
        EntityPlayerSP.resetTimer.reset();
        positionAdjustFinished = false;
        data = null;
        super.onDisable();
    }

    private boolean isGoodBlock(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemBlock)) return false;
        if (stack.stackSize <= affordBlock.getValue()) return false;
        return !ItemUtil.invalidBlocks.contains(((ItemBlock) stack.getItem()).getBlock());
    }

    private boolean adjustResetForJump;

    @Override
    public void onMovementInput(Events.MovementInput event) {
        if (MoveUtil.isMoving(event)) {
            int currentSlot = mc.thePlayer.inventory.currentItem;
            if (mc.thePlayer.inventory.getStackInSlot(currentSlot) == null || !(mc.thePlayer.inventory.getStackInSlot(currentSlot).getItem() instanceof ItemBlock)) {
                switch (itemSpoof.getValue()) {
                    case "Switch":
                        for (int i = 0; i < 9; i++) {
                            if (isGoodBlock(mc.thePlayer.inventory.getStackInSlot(i))) {
                                mc.thePlayer.inventory.currentItem = i;
                                break;
                            }
                        }
                        break;
                }
            }
        }
        event.moveFix = true;
        // TODO position adjustment for diagonal
        if (mode.getValue().equals("Polar")) {
            if (!positionAdjustFinished) {
                if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, -1, 0)).getBlock() != Blocks.air) {
                    sneakTimer.reset();
                }
                event.input.sneak = true;
                if (!isDiagonal()) {
                    if (sneakTimer.hasTimeElapsed(250)) {
                        if (sneakTimer.hasTimeElapsed(500)) {
                            positionAdjustFinished = true;
                            event.input.sneak = false;
                        }
                        event.input.moveForward = 0f;
                        event.input.moveStrafe = 0f;
                    } else {
                        event.input.moveForward = 1f;
                    }
                } else {
                    positionAdjustFinished = true;
                }
            }
        }
        if (!sneakTimer.hasTimeElapsed(150)) {
//            event.input.sneak = true;
//            event.input.moveForward = 0f;
//            event.input.moveStrafe = 0f;
        }
//        if (mode.getValue().equals("Intave")) {
//            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
//                            mc.thePlayer.getEntityBoundingBox()
//                                    .addCoord(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ)
//                                    .expand(-0.175, 0, -0.175))
//                    .isEmpty()) {
//                event.input.sneak = true;
//            }
//        }
        if (mode.getValue().equals("Polar")) {
            if (!isDiagonal() && mc.thePlayer.onGround && (placeCounter > 7)) {
                event.input.jump = true;
                placeCounter = 0;
            }
        }

        if (event.input.sneak) {
            placeCounter = 0;
        }
        super.onMovementInput(event);
    }

    private boolean isDiagonal() {
        return Math.abs(Math.round(RotationManager.virtualYaw / 90) * 90 - RotationManager.virtualYaw) > 22.5;
    }

    private boolean isDiagonal(float v) {
        return Math.abs(Math.round(RotationManager.virtualYaw / 90) * 90 - RotationManager.virtualYaw) > v;
    }

    private int offGroundTick;

    private float[] calcRotationStd() {
        float yaw = MathHelper.wrapAngleTo180_float(RotationManager.virtualYaw + 180f);
        yaw = Math.round(yaw / 45) * 45;
        final boolean diagonal = isDiagonal();
        if (!diagonal) {
            final double modX = lastGroundPos == null ? mc.thePlayer.posX - Math.floor(mc.thePlayer.posX) : lastGroundPos.getX() + 0.5 - mc.thePlayer.posX;
            final double modZ = lastGroundPos == null ? mc.thePlayer.posZ - Math.floor(mc.thePlayer.posZ) : lastGroundPos.getZ() + 0.5- mc.thePlayer.posZ;
//            final double modX =  mc.thePlayer.posX - Math.floor(mc.thePlayer.posX);
//            final double modZ = mc.thePlayer.posZ - Math.floor(mc.thePlayer.posZ);
            double va = 0;
            double ma = 0;
            switch (EnumFacing.fromAngle(yaw).toString()) {
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
        float pitch = diagonal ? 76.2f : 75.8f;
//        System.out.println(RotationManager.virtualPitch);

        if (mode.getValue().equals("Intave")) {
            pitch = mc.thePlayer.rotationPitch;
            yaw = mc.thePlayer.rotationYaw;
            if (data != null && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, -1, 0)).getBlock() == Blocks.air) {
                if (!isGood(mc.thePlayer.rayTrace(3, 1f))) {
                    final float gcd = RotationUtil.getGcd();
                    final float[] lastRotation = new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch };
                    float minPitchDiff = Float.MAX_VALUE, bestPitch = 0f;
                    float[] tempRotation = new float[] { mc.thePlayer.rotationYaw, 0f };
                    for (float searchPitch = 10f; searchPitch < 90f; searchPitch += gcd) {
                        final float currentPitchDiff = Math.abs(searchPitch - mc.thePlayer.rotationPitch);
                        if (minPitchDiff < currentPitchDiff) continue;
                        tempRotation[1] = searchPitch;
                        float[] fixed = RotationUtil.getFixedRotation(
                                tempRotation,
                                lastRotation
                        );
                        if (!isGood(mc.thePlayer.rayTraceCustom(3, 1f, fixed[0], fixed[1]))) continue;
                        minPitchDiff = currentPitchDiff;
                        bestPitch = searchPitch;
                    }
                    if (minPitchDiff != Float.MAX_VALUE) {
                        pitch = bestPitch;
                    }
                    else {
                        tempRotation = RotationUtil.rotation(data.toBox().center());
                        for (float searchPitch = 10f; searchPitch < 90f; searchPitch += gcd) {
                            final float currentPitchDiff = Math.abs(searchPitch - mc.thePlayer.rotationPitch);
                            if (minPitchDiff < currentPitchDiff) continue;
                            tempRotation[1] = searchPitch;
                            float[] fixed = RotationUtil.getFixedRotation(
                                    tempRotation,
                                    lastRotation
                            );
                            if (!isGood(mc.thePlayer.rayTraceCustom(3, 1f, fixed[0], fixed[1]))) continue;
                            minPitchDiff = currentPitchDiff;
                            bestPitch = searchPitch;
                        }
                        if (minPitchDiff != Float.MAX_VALUE) {
                            yaw = tempRotation[0];
                            pitch = bestPitch;
                        }
                        else {
                            tempRotation = RotationUtil.rotation(AlgebraUtil.nearest(data.toBox()));
                            for (float searchPitch = 10f; searchPitch < 90f; searchPitch += gcd) {
                                final float currentPitchDiff = Math.abs(searchPitch - mc.thePlayer.rotationPitch);
                                if (minPitchDiff < currentPitchDiff) continue;
                                tempRotation[1] = searchPitch;
                                float[] fixed = RotationUtil.getFixedRotation(
                                        tempRotation,
                                        lastRotation
                                );
                                if (!isGood(mc.thePlayer.rayTraceCustom(3, 1f, fixed[0], fixed[1]))) continue;
                                minPitchDiff = currentPitchDiff;
                                bestPitch = searchPitch;
                            }
                            if (minPitchDiff != Float.MAX_VALUE) {
                                yaw = tempRotation[0];
                                pitch = bestPitch;
                            } else {
                                System.out.println("NOT FOUND");
                            }
                        }
                    }
                }
            }
            pitchStack += Math.abs(mc.thePlayer.rotationPitch - pitch);
            if (Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - yaw)) > 22.5f || pitchStack > 30f) {
                sneakTimer.reset();
                pitchStack = 0;
            }
        }

        return new float[] {
                yaw,
                pitch
        };
    }

    private boolean isGood(MovingObjectPosition objectPosition) {
        if (data == null) return false;
        if (objectPosition == null) return false;
        if (objectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return false;
        if (mc.theWorld.getBlockState(objectPosition.getBlockPos()).getBlock() == Blocks.air) return false;
        return data.getFacing() == objectPosition.sideHit && data.getPos().equalsBlockPos(objectPosition.getBlockPos());
    }

    @Override
    public void onRotation(Events.Rotation event) {
        Vec3 predicted = null;
        boolean lastShouldScaffold = shouldScaffold;
        shouldScaffold = true;
//        {
//            List<Vec3> positions = PlayerUtil.predict(5);
//            predicted = positions.get(positions.size() - 1);
//            shouldScaffold = true;
//            for (int i = 0; i < 4; i++) {
//                if (mc.theWorld.getBlockState(new BlockPos(predicted).add(0, -i, 0)).getBlock() != Blocks.air) {
//                    shouldScaffold = false;
//                    break;
//                }
//            }
//        }
        if (!shouldScaffold) {
            if (lastShouldScaffold) mc.thePlayer.inventory.currentItem = lastSlot;
            return;
        }
        data = getBlockData();
        float[] rotation = calcRotationStd();
        if (Math.abs(MathHelper.wrapAngleTo180_float(rotation[0] - mc.thePlayer.rotationYaw)) > 1) {
            positionAdjustFinished = false;
            sneakTimer.reset();
        }
        if (mode.getValue().equals("Polar")) {
            rotation = BypassRotation.getInstance().limitAngle(
                    new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch},
                    rotation,
                    5f,
                    1f
            );
        }
        if (rotation != null) {
//            System.out.println(rotation[0]);
            event.yaw = rotation[0];
            event.pitch = rotation[1];
        }
        super.onRotation(event);
    }

    @Override
    public void onUpdate(Events.Update event) {
        if (event.pre) {
            if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, -1, 0)).getBlock() != Blocks.air) {
                lastGroundPos = new BlockPos(mc.thePlayer).add(0, -1, 0);
                airTimer.reset();
            }
            if (!mc.thePlayer.onGround) offGroundTick++;
            else offGroundTick = 0;
        }
        super.onUpdate(event);
    }

    @Override
    public void onRender3D(Events.Render3D event) {
        if (data != null) {
            switch (blockESP.getValue()) {
                case "Fill":
                    Render3DUtil.drawFilledBox(data.getPos().getX(), data.getPos().getY(), data.getPos().getZ(), data.getPos().getX() + 1, data.getPos().getY() + 1, data.getPos().getZ() + 1, -1);
                    break;
                case "Outline":
                    Render3DUtil.drawOutlinedBox(data.toBox(), 0x80FF0000);
                    break;
                case "Both":
                    Render3DUtil.drawFilledBox(data.toBox(), 0x80FF0000);
                    Render3DUtil.drawOutlinedBox(data.toBox(), 0xFFFF0000);
                    break;
            }
        }
        super.onRender3D(event);
    }

    private void rightClick(MovingObjectPosition objectPosition) {
//        mc.rightClickDelayTimer = 0;
        if (!positionAdjustFinished) return;
        if (mc.thePlayer.isSneaking()) return;
        if (mc.playerController.getIsHittingBlock()) return;
        if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, -1, 0)).getBlock() != Blocks.air) return;
        if (objectPosition == null || objectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
        if (mc.theWorld.getBlockState(objectPosition.getBlockPos()).getBlock() == Blocks.air) return;
        if (mc.thePlayer.getHeldItem() == null) return;
        if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) return;
        if (!mc.thePlayer.onGround && objectPosition.hitVec.yCoord >= mc.thePlayer.posY) return;
        if (mode.getValue().equals("Intave") && !isGood(objectPosition)) return;
        float x = (float) (objectPosition.hitVec.xCoord - objectPosition.getBlockPos().getX());
        float y = (float) (objectPosition.hitVec.yCoord - objectPosition.getBlockPos().getY());
        float z = (float) (objectPosition.hitVec.zCoord - objectPosition.getBlockPos().getZ());
//        final boolean tick = (!dragClickTimer.hasTimeElapsed(50) || dragClickTimer.hasTimeElapsed(100)) && !dragClickTimer.hasTimeElapsed(500);
        if (mc.thePlayer.getHeldItem().getItem().onItemUse(mc.thePlayer.getHeldItem(), mc.thePlayer, mc.theWorld, objectPosition.getBlockPos(), objectPosition.sideHit,
                x, y,z)) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(objectPosition.getBlockPos(), objectPosition.sideHit.getIndex(), mc.thePlayer.inventory.getCurrentItem(), x, y, z));
            mc.thePlayer.swingItem();
            if (objectPosition.sideHit != EnumFacing.UP) placeCounter++;
            clickTimer.reset();
            mc.rightClickDelayTimer = 4;
            dragClickTimer.reset();
        } else {
            if (mode.getValue().equals("Polar")) {
                mc.rightClickMouse();
            }
        }
    }

    @Override
    public void onTick(Events.Tick event) {
        if (!shouldScaffold) {
            return;
        }
        mc.objectMouseOver = mc.thePlayer.rayTrace(3, 1f);
        this.rightClick(mc.objectMouseOver);
        super.onTick(event);
    }

    private BlockData getBlockData() {
        BlockPos playerPos = new BlockPos(mc.thePlayer).add(0, -1, 0);
        ArrayList<Vec3> positions = new ArrayList<>();
        HashMap<Vec3, BlockPos> hashMap = new HashMap<>();

        for (int y = playerPos.getY() - 1; y <= playerPos.getY(); ++y) {
            for (int x = playerPos.getX() - 5; x <= playerPos.getX() + 5; ++x) {
                for (int z = playerPos.getZ() - 5; z <= playerPos.getZ() + 5; ++z) {
                    if (isValidBock(new BlockPos(x, y, z))) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                        double ex = MathHelper.clamp_double(mc.thePlayer.posX, blockPos.getX(),
                                blockPos.getX() + block.getBlockBoundsMaxX());
                        double ey = MathHelper.clamp_double(mc.thePlayer.posY - 1 + 1, blockPos.getY(),
                                blockPos.getY() + block.getBlockBoundsMaxY());
                        double ez = MathHelper.clamp_double(mc.thePlayer.posZ, blockPos.getZ(),
                                blockPos.getZ() + block.getBlockBoundsMaxZ());
                        Vec3 vec3 = new Vec3(ex, ey, ez);
                        positions.add(vec3);
                        hashMap.put(vec3, blockPos);
                    }
                }
            }
        }
        if (positions.isEmpty()) {
            return null;
        }
        positions.sort(Comparator.comparingDouble((ToDoubleFunction<? super Vec3>) this::getBestBlock));
        BlockPos pos = hashMap.get(positions.get(0));
        EnumFacing facing = getPlaceSide(pos);
        if (facing == null) return null;
        return new BlockData(pos, facing);
    }

    private EnumFacing getPlaceSide(BlockPos blockPos) {
        ArrayList<Vec3> positions = new ArrayList<>();
        HashMap<Vec3, EnumFacing> hashMap = new HashMap<>();
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        if (!isPosSolid(blockPos.add(0, 1, 0)) && !blockPos.add(0, 1, 0).equalsBlockPos(playerPos)
                && !mc.thePlayer.onGround) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
//            if (jump.isEnabled()) {
//                    BlockPos bp = blockPos.add(0, 1, 0);
//                    Vec3 vec4 = this.getBestHitFeet(bp);
//                    positions.add(vec4);
//                    hashMap.put(vec4, EnumFacing.UP);
//            } else {
//                BlockPos bp = blockPos.add(0, 1, 0);
//                Vec3 vec4 = this.getBestHitFeet(bp);
//                positions.add(vec4);
//                hashMap.put(vec4, EnumFacing.UP);
//            }
        }
        if (!isPosSolid(blockPos.add(1, 0, 0)) && !blockPos.add(1, 0, 0).equalsBlockPos(playerPos)) {
            BlockPos bp = blockPos.add(1, 0, 0);
            Vec3 vec4 = this.getBestHitFeet(bp);
            positions.add(vec4);
            hashMap.put(vec4, EnumFacing.EAST);
        }
        if (!isPosSolid(blockPos.add(-1, 0, 0)) && !blockPos.add(-1, 0, 0).equalsBlockPos(playerPos)) {
            BlockPos bp = blockPos.add(-1, 0, 0);
            Vec3 vec4 = this.getBestHitFeet(bp);
            positions.add(vec4);
            hashMap.put(vec4, EnumFacing.WEST);
        }
        if (!isPosSolid(blockPos.add(0, 0, 1)) && !blockPos.add(0, 0, 1).equalsBlockPos(playerPos)) {
            BlockPos bp = blockPos.add(0, 0, 1);
            Vec3 vec4 = this.getBestHitFeet(bp);
            positions.add(vec4);
            hashMap.put(vec4, EnumFacing.SOUTH);
        }
        if (!isPosSolid(blockPos.add(0, 0, -1)) && !blockPos.add(0, 0, -1).equalsBlockPos(playerPos)) {
            BlockPos bp = blockPos.add(0, 0, -1);
            Vec3 vec4 = this.getBestHitFeet(bp);
            positions.add(vec4);
            hashMap.put(vec4, EnumFacing.NORTH);
        }
        positions.sort(
                Comparator.comparingDouble(vec3 -> mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord)));
        if (!positions.isEmpty()) {
            Vec3 vec5 = this.getBestHitFeet(blockPos);
            if (mc.thePlayer.getDistance(vec5.xCoord, vec5.yCoord, vec5.zCoord) >= mc.thePlayer
                    .getDistance(positions.get(0).xCoord, positions.get(0).yCoord, positions.get(0).zCoord)) {
                return hashMap.get(positions.get(0));
            }
        }
        return null;
    }

    private Vec3 getBestHitFeet(final BlockPos blockPos) {
        final Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        final double ex = MathHelper.clamp_double(mc.thePlayer.posX, blockPos.getX(),
                blockPos.getX() + block.getBlockBoundsMaxX());
        final double ey = MathHelper.clamp_double(mc.thePlayer.posY, blockPos.getY(),
                blockPos.getY() + block.getBlockBoundsMaxY());
        final double ez = MathHelper.clamp_double(mc.thePlayer.posZ, blockPos.getZ(),
                blockPos.getZ() + block.getBlockBoundsMaxZ());
        return new Vec3(ex, ey, ez);
    }

    public static boolean isPosSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        if ((block.getMaterial().isSolid() || !block.isTranslucent() || block instanceof BlockLadder
                || block instanceof BlockCarpet || block instanceof BlockSnow || block instanceof BlockSkull)
                && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer)) {
            return true;
        }
        return false;
    }

    private double getBestBlock(Vec3 vec3) {
        return mc.thePlayer.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    }

    public static boolean isValidBock(BlockPos blockPos) {
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        return !(block instanceof BlockLiquid) && !(block instanceof BlockAir) && !(block instanceof BlockChest)
                && !(block instanceof BlockFurnace);
    }
}
