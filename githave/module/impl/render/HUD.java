package githave.module.impl.render;

import githave.event.Events;
import githave.gui.font.TTFFontRenderer;
import githave.module.Module;
import githave.module.ModuleCategory;

import static githave.util.render.Render2DUtil.*;

public class HUD extends Module {

    private TTFFontRenderer font;

    @Override
    public void init() {
        font = TTFFontRenderer.of("Roboto-Light", 22);
        super.init();
    }

    public HUD() {
        super("HUD", "Display HUD", ModuleCategory.Render);
    }

    @Override
    public void onRenderGui(Events.PreRenderGui event) {
        rect(5, 5, 150, 20, 0xa0000000);

        super.onRenderGui(event);
    }
}
