package githave.module.impl.render;

import githave.GitHave;
import githave.event.Events;
import githave.gui.font.TTFFontRenderer;
import githave.module.Module;
import githave.module.ModuleCategory;
import net.minecraft.client.Minecraft;

import static githave.util.render.Render2DUtil.*;

public class HUD extends Module {

    private TTFFontRenderer font;

    @Override
    public void init() {
        font = TTFFontRenderer.of("Roboto-Light", 20);
        super.init();
    }

    public HUD() {
        super("HUD", "Display HUD", ModuleCategory.Render);
    }

    @Override
    public void onRenderGui(Events.PreRenderGui event) {
        rect(5, 5, 200, 20, 0xb0000000);
        rect(5, 5, 200, 2, 0xffEC1BF6);
        font.drawString(String.format("%s %s | %s | %dfps | %s", GitHave.INSTANCE.name, GitHave.INSTANCE.version, "wevez", Minecraft.getDebugFPS(), ""), 7, 12, -1);
        super.onRenderGui(event);
    }
}
