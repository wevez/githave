package githave.gui.click;

import githave.util.animation.AnimationUtil;
import githave.util.animation.BackAnimation;
import githave.util.render.Render2DUtil;
import net.minecraft.client.gui.GuiScreen;
import githave.module.ModuleCategory;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGui extends GuiScreen {

    private final List<ClickGuiWindow> windows = new ArrayList<>();

    private final AnimationUtil animationUtil = new BackAnimation();

    {
        float currentX = 50;
        for (ModuleCategory c : ModuleCategory.values()) {
            windows.add(new ClickGuiWindow(currentX, 30, c));
            currentX += 155;
        }
    }

    @Override
    public void initGui() {
        animationUtil.setTick(0.25);
        super.initGui();
    }

    public void onBlur() {
        windows.forEach(c -> c.onBlur(mouseX, mouseY, partialTicks));
    }

    public void onPost() {
        windows.forEach(c -> c.onPost(mouseX, mouseY, partialTicks));
    }

    private int mouseX, mouseY;
    private float partialTicks;

    @Override
    public void onGuiClosed() {
        animationUtil.setTick(0);
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        super.drawScreen(mouseX, mouseY, partialTicks);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
    }

    public void a() {
        super.drawScreen(mouseX, mouseY, partialTicks);
        double per = animationUtil.uodate(0.05).calcPercent();
        GlStateManager.pushMatrix();
        Render2DUtil.setAlphaLimit((float) per);
        GlStateManager.translate(mc.displayWidth / 4, mc.displayHeight / 4, 0);
        GlStateManager.scale(per, per, 0);
        GlStateManager.translate(-mc.displayWidth / 4, -mc.displayHeight / 4, 0);
        windows.forEach(c -> c.drawScreen(mouseX, mouseY, partialTicks));
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        windows.forEach(c -> c.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        windows.forEach(c -> c.mouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }
}
