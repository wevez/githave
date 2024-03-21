package githave.gui.click;

import net.minecraft.client.gui.GuiScreen;
import githave.module.ModuleCategory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGui extends GuiScreen {

    private final List<ClickGuiWindow> windows = new ArrayList<>();

    {
        float currentX = 50;
        for (ModuleCategory c : ModuleCategory.values()) {
            windows.add(new ClickGuiWindow(currentX, 30, c));
            currentX += 155;
        }
    }

    @Override
    public void initGui() {
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        super.drawScreen(mouseX, mouseY, partialTicks);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
    }

    public void a() {
        super.drawScreen(mouseX, mouseY, partialTicks);
        windows.forEach(c -> c.drawScreen(mouseX, mouseY, partialTicks));
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
