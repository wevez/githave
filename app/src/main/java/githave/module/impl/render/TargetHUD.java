package githave.module.impl.render;

import githave.event.Events;
import githave.gui.font.TTFFontRenderer;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.impl.combat.KillAura;
import githave.util.animation.AnimationUtil;
import githave.util.animation.BackAnimation;
import githave.util.animation.LinearAnimation;
import githave.util.render.ColorUtil;
import githave.util.render.Render2DUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class TargetHUD extends Module {
    private final AnimationUtil bodyAnim = new BackAnimation();
    private TTFFontRenderer font;

    public TargetHUD() {
        super("TargetHUD", "Display information about target", ModuleCategory.Render);
    }

    @Override
    protected void onEnable() {
        font = TTFFontRenderer.of("Roboto-Regular", 20);
        bodyAnim.setTick(0);
        super.onEnable();
    }

    @Override
    public void onRenderGui(Events.PreRenderGui event) {
        if (KillAura.target != null) {
            GlStateManager.pushMatrix();
            double x = mc.displayWidth/12d;
            double y = mc.displayHeight/3d;
            double w = 150;
            double h = 40;
            GlStateManager.translate(x, y, 0);
            double bodyPer = bodyAnim.update(0.05).calcPercent();
            GlStateManager.scale(bodyPer, bodyPer, 0);
            GlStateManager.translate(-x, -y, 0);
            Render2DUtil.rect(x, y, w, h, 0xff202226);
            Render2DUtil.rect(x, y+h, w, 2, 0xff151519);
            Render2DUtil.rect(x, y+h, w*getPercent(KillAura.target), 2, 0xffEC1BF6);
            font.drawString(KillAura.target.getName(), x, y+h/2-font.height()/4, ColorUtil.interpolateColor(0xffE0DFE2, 0xffEC1BF6, 0));
            GlStateManager.popMatrix();
        } else {
            bodyAnim.setTick(0);
        }

        super.onRenderGui(event);
    }

    private float getPercent(EntityLivingBase target) {
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();

        return health / maxHealth;
    }
}
