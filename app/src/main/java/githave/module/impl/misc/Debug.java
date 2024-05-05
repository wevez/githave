package githave.module.impl.misc;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.util.render.Render3DUtil;
import net.minecraft.util.Vec3;

import java.util.List;

public class Debug extends Module {

    public Debug() {
        super("Debug", "Debug", ModuleCategory.Misc);
    }

    @Override
    public void onRender3D(Events.Render3D event) {
        List<Vec3> positions = githave.util.PlayerUtil.predict(50);
        for (Vec3 pos : positions) {
            Render3DUtil.drawFilledBox(pos.xCoord, pos.yCoord, pos.zCoord, pos.xCoord + 1, pos.yCoord + 1, pos.zCoord + 1, 0xa0ffff00);
        }
        super.onRender3D(event);
    }
}
