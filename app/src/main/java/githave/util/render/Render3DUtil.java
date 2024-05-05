package githave.util.render;

import githave.MCHook;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class Render3DUtil implements MCHook {

    public static void drawFilledBox(AxisAlignedBB bb, int color) {
        drawFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color);
    }

    public static void drawFilledBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color) {
        minX -= RenderManager.renderPosX;
        minY -= RenderManager.renderPosY;
        minZ -= RenderManager.renderPosZ;
        maxX -= RenderManager.renderPosX;
        maxY -= RenderManager.renderPosY;
        maxZ -= RenderManager.renderPosZ;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        ColorUtil.glColor(color);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(minX, minY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(maxX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, minZ).endVertex();
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).endVertex();
        worldRenderer.pos(minX, minY, maxZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();
        worldRenderer.pos(minX, minY, maxZ).endVertex();
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(minX, minY, maxZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();
        worldRenderer.pos(maxX, minY, minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        worldRenderer.pos(minX, minY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(maxX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(minX, maxY, maxZ).endVertex();
        worldRenderer.pos(minX, minY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawOutlinedBox(AxisAlignedBB bb, int color) {
        drawOutlinedBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color);
    }

    public static void drawOutlinedBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        ColorUtil.glColor(color);
        GL11.glLineWidth(0.5f);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();
        worldRenderer.pos(minX, minY, maxZ).endVertex();
        worldRenderer.pos(minX, minY, minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(minX, minY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
