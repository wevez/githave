package githave.module.impl.render;

import githave.GitHave;
import githave.event.Events;
import githave.gui.font.TTFFontRenderer;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.util.animation.AnimationUtil;
import githave.util.animation.EaseOutAnimation;
import githave.util.animation.LinearAnimation;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static githave.util.render.Render2DUtil.*;

public class ModuleList extends Module {

    private static TTFFontRenderer font;

    private AnimationUtil[] animationsX;

    private List<Module> sortedList;

    public ModuleList() {
        super("ModuleList", "Display enabled modules", ModuleCategory.Render);
    }

    @Override
    public void init() {
        animationsX = new AnimationUtil[GitHave.INSTANCE.moduleManager.getModules().size()];
        for (int i = 0; i < animationsX.length; i++) {
            animationsX[i] = new LinearAnimation();
        }
        sortedList = new ArrayList<>(GitHave.INSTANCE.moduleManager.getModules());
        font = TTFFontRenderer.of("Roboto-Regular", 22);
        sortedList.sort(Comparator.comparing(m -> -font.width(m.getName())));
        super.init();
    }

    @Override
    public void onRenderGui(Events.PreRenderGui event) {
        float offset = 0f;
        ScaledResolution sr = new ScaledResolution(mc);
        for (int i = 0; i < animationsX.length; i++) {
            AnimationUtil x = animationsX[i];
            Module m = sortedList.get(i);
            x.uodate(m.isToggled() ? 0.1 : -0.1);
            double percent = x.calcPercent();
            if (percent == 0) continue;
            double fontWidth = font.width(m.getName());
            double height = percent * 16;
            rect(sr.getScaledWidth() - (6 + fontWidth) * percent, offset, fontWidth + 4, height, 0xff190640);
            rect(sr.getScaledWidth() - 2, offset, 2, height, 0xff937CE1);
            font.drawString(m.getName(), sr.getScaledWidth() - (4 + fontWidth) * percent, offset + 4, 0xff937CE1);
            offset += height;
        }
        super.onRenderGui(event);
    }
}
