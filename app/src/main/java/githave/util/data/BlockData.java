package githave.util.data;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockData {

    private final BlockPos pos;
    private final EnumFacing facing;

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public BlockData(BlockPos pos, EnumFacing facing) {
        this.pos = pos;
        this.facing = facing;
    }
}
