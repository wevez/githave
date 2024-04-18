package githave.module.impl.render;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.util.render.BlurUtil;
import githave.util.render.Render2DUtil;

public class TargetHUD extends Module {

    public TargetHUD() {
        super("TargetHUD", "Display information about target", ModuleCategory.Render);
    }

    @Override
    public void onRenderGui(Events.PreRenderGui event) {
        super.onRenderGui(event);
    }
}
