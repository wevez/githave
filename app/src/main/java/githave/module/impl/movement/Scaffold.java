package githave.module.impl.movement;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.DoubleSetting;
import githave.util.bypass.IndependentCPS;
import githave.util.data.BlockData;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

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

    private List<BlockData> data = new ArrayList<>();
    private int lastDataUpdateTicks;

    private int sameYPos;

    private final DoubleSetting minCPS = new DoubleSetting.Builder("Min CPS", 12, 0, 20, 0.1)
            .build();

    private final DoubleSetting maxCPS = new DoubleSetting.Builder("Max CPS", 15, 0, 20, 0.1)
            .build();

    private final IndependentCPS independentCPS = new IndependentCPS(minCPS, maxCPS);

    public Scaffold() {
        super("Scaffold", "Place blocks at your feet", ModuleCategory.Movement);
    }

    @Override
    protected void onEnable() {
        data.clear();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        data.clear();
        super.onDisable();
    }

    @Override
    public void onRotation(Events.Rotation event) {
        if (lastDataUpdateTicks != mc.thePlayer.ticksExisted) {
            lastDataUpdateTicks = mc.thePlayer.ticksExisted;
            data = getBlockData();
        }
        super.onRotation(event);
    }

    @Override
    public void onTick(Events.Tick event) {
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
