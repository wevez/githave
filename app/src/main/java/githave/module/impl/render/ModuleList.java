package githave.module.impl.render;

import githave.GitHave;
import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.util.animation.AnimationUtil;
import githave.util.animation.LinearAnimation;

public class ModuleList extends Module {

    private AnimationUtil[] animations;

    public ModuleList() {
        super("ModuleList", "Display enabled modules", ModuleCategory.Render);
    }

    @Override
    public void init() {
        animations = new AnimationUtil[GitHave.INSTANCE.moduleManager.getModules().size()];
        for (int i = 0; i < animations.length; i++) {
            animations[i] = new LinearAnimation();
        }
        super.init();
    }

    @Override
    public void onRenderGui(Events.PreRenderGui event) {
        super.onRenderGui(event);
    }
}
