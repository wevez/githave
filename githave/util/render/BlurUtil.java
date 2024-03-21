package githave.util.render;

import net.minecraft.client.shader.Framebuffer;
import githave.GitHave;
import githave.MCHook;
import githave.event.Events;
import githave.gui.click.ClickGui;
import githave.module.setting.impl.DoubleSetting;

public class BlurUtil implements MCHook {

    private static Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    private static final Events.BlurEvent blurEvent = new Events.BlurEvent();

    private static final DoubleSetting iterations = new DoubleSetting.Builder("Iterations", 3, 1, 8, 1).build();
    private static final DoubleSetting offset = new DoubleSetting.Builder("Offset", 1, 1, 10, 1).build();

    public static void blur() {
        stencilFramebuffer = Render2DUtil.createFrameBuffer(stencilFramebuffer);

        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);
        GitHave.INSTANCE.eventManager.call(blurEvent);
        if (mc.currentScreen instanceof ClickGui) {
            GitHave.INSTANCE.clickGui.onBlur();
        }
        stuffToBlur(false);
        stencilFramebuffer.unbindFramebuffer();


        KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, (int) iterations.getValue(), (int) offset.getValue());
    }

    public static void stuffToBlur(boolean bloom) {
//        ScaledResolution sr = new ScaledResolution(mc);
//        if (mc.currentScreen instanceof GuiChat) {
//            Gui.drawRect2(2, sr.getScaledHeight() - (14 * GuiChat.openingAnimation.getOutput().floatValue()), sr.getScaledWidth() - 4, 12, Color.BLACK.getRGB());
//        }

//        if (mc.currentScreen == ClickGUIMod.dropdownClickGui) {
//            ClickGUIMod.dropdownClickGui.renderEffects();
//        }
//        if (mc.currentScreen == ClickGUIMod.dropdownClickGui || mc.currentScreen == ClickGUIMod.modernClickGui || mc.currentScreen == ClickGUIMod.compactClickgui) {
//            Tenacity.INSTANCE.getSideGui().drawForEffects(bloom);
//            Tenacity.INSTANCE.getSearchBar().drawEffects();
//        }
//
//
//        RenderUtil.resetColor();
//        mc.ingameGUI.getChatGUI().renderChatBox();
//        RenderUtil.resetColor();
//        mc.ingameGUI.renderScoreboardBlur(sr);
//        RenderUtil.resetColor();
//        NotificationsMod notificationsMod = Tenacity.INSTANCE.getModuleCollection().getModule(NotificationsMod.class);
//        if (notificationsMod.isEnabled()) {
//            notificationsMod.renderEffects(glowOptions.getSetting("Notifications").isEnabled());
//        }
//
//        if (bloom) {
//            if (mc.currentScreen instanceof ModernClickGui) {
//                ClickGUIMod.modernClickGui.drawBigRect();
//            }
//        }

    }
}
