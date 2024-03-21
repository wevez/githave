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
        font = TTFFontRenderer.of("Roboto-Regular", 20);
        super.init();
    }

    public HUD() {
        super("HUD", "Display HUD", ModuleCategory.Render);
    }

    @Override
    public void onRenderGui(Events.PreRenderGui event) {
        String formatted = String.format("%s %s | %s | %dfps | %s", GitHave.INSTANCE.name,
                GitHave.INSTANCE.version, GitHave.INSTANCE.account, Minecraft.getDebugFPS(), getServerIp());
        float wid = font.width(formatted);
        rect(5, 5, wid + 14, 20, 0xb0505050);
        rect(5, 5, wid + 14, 2, 0xff727888);
        font.drawString(formatted, 7, 12, 0xffE0DFE2);
        super.onRenderGui(event);
    }

    private String getServerIp() {
        if (mc.isSingleplayer()) {
            return "Single";
        }
        return mc.getCurrentServerData().serverIP;
    }
}
