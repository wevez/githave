package githave.util.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import githave.MCHook;

import static org.lwjgl.opengl.GL11.*;

public class Render2DUtil implements MCHook {

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight;
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void rect(double x, double y, double width, double height, int color) {
        ColorUtil.resetColor();
        Render2DUtil.setAlphaLimit(0);
        GLUtil.setup2DRendering(true);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        int r = ColorUtil.red(color), g = ColorUtil.green(color), b = ColorUtil.blue(color), a = ColorUtil.alpha(color);

        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x, y, 0.0D).color(r, g, b, a).endVertex();
        worldrenderer.pos(x, y + height, 0.0D).color(r, g, b, a).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).color(r, g, b, a).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();

        GLUtil.end2DRendering();
    }
}
