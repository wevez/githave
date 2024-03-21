package githave;

import de.florianmichael.viamcp.ViaMCP;
import githave.event.EventManager;
import githave.gui.click.ClickGui;
import githave.module.ModuleManager;
import org.lwjgl.opengl.Display;

public enum GitHave {

    INSTANCE;

    public String name = "GitHave", version = "1.0", account = "";

    public void init() {
        try {
            ViaMCP.create();
            // In case you want a version slider like in the Minecraft options, you can use this code here, please choose one of those:

            ViaMCP.INSTANCE.initAsyncSlider(); // For top left aligned slider
//            ViaMCP.INSTANCE.initAsyncSlider(x, y, width (min. 110), height (recommended 20)); // For custom position and size slider
        } catch (Exception e) {
            e.printStackTrace();
        }
        Display.setTitle(name + " " + version);

        // TODO:
        account = "dev";
        // Avoid no OpenGL context error
        clickGui = new ClickGui();
        moduleManager.init();
    }

    public void shutdown() {}

    public ClickGui clickGui;

    public final ModuleManager moduleManager = new ModuleManager();

    public final EventManager eventManager = new EventManager();
}
