package githave.util.render;

import githave.MCHook;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class Render3DUtil implements MCHook {

    public static void drawFilledBox(AxisAlignedBB bb, int color) {
        drawFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color);
    }

    public static void drawFilledBox(double x, double y, double z, double x1, double y1, double z1, int color) {
        GL11.glBegin(GL11.GL_QUADS);
        ColorUtil.glColor(color);

        // Bottom face
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y, z1);
        GL11.glVertex3d(x1, y, z1);
        GL11.glVertex3d(x1, y, z);

        // Top face
        GL11.glVertex3d(x, y1, z);
        GL11.glVertex3d(x, y1, z1);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x1, y1, z);

        // Front face
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y1, z);
        GL11.glVertex3d(x1, y1, z);
        GL11.glVertex3d(x1, y, z);

        // Back face
        GL11.glVertex3d(x, y, z1);
        GL11.glVertex3d(x, y1, z1);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x1, y, z1);

        // Left face
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y, z1);
        GL11.glVertex3d(x, y1, z1);
        GL11.glVertex3d(x, y1, z);

        // Right face
        GL11.glVertex3d(x1, y, z);
        GL11.glVertex3d(x1, y, z1);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x1, y1, z);

        GL11.glEnd();
    }

    public static void drawOutlinedBox(AxisAlignedBB bb, int color) {
        drawOutlinedBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color);
    }

    public static void drawOutlinedBox(double x, double y, double z, double x1, double y1, double z1, int color) {
        GL11.glBegin(GL11.GL_LINES);
        ColorUtil.glColor(color);

        // Bottom edges
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y, z);

        GL11.glVertex3d(x1, y, z);
        GL11.glVertex3d(x1, y, z1);

        GL11.glVertex3d(x1, y, z1);
        GL11.glVertex3d(x, y, z1);

        GL11.glVertex3d(x, y, z1);
        GL11.glVertex3d(x, y, z);

        // Top edges
        GL11.glVertex3d(x, y1, z);
        GL11.glVertex3d(x1, y1, z);

        GL11.glVertex3d(x1, y1, z);
        GL11.glVertex3d(x1, y1, z1);

        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x, y1, z1);

        GL11.glVertex3d(x, y1, z1);
        GL11.glVertex3d(x, y1, z);

        // Vertical edges
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y1, z);

        GL11.glVertex3d(x1, y, z);
        GL11.glVertex3d(x1, y1, z);

        GL11.glVertex3d(x1, y, z1);
        GL11.glVertex3d(x1, y1, z1);

        GL11.glVertex3d(x, y, z1);
        GL11.glVertex3d(x, y1, z1);

        GL11.glEnd();
    }
}
