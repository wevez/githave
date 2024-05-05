package githave.module;

import githave.module.impl.combat.*;
import githave.module.impl.movement.*;
import githave.module.impl.player.*;
import githave.module.impl.render.*;

import java.util.Arrays;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = Arrays.asList(
            // Combat
            new AntiBot(),
            new AntiFire(),
            new AutoClicker(),
            new Backtrack(),
            new KillAura(),
            new TickBase(),
            new Velocity(),
            new WTap(),
            // Movement
            new Scaffold(),
            // Player
            new AutoSprint(),
            new AutoTool(),
            new Blink(),
            new ChestStealer(),
            new InvManager(),
            // Render
            new Animations(),
            new ESP(),
            new HUD(),
            new ModuleList(),
            new TargetHUD()
    );

    public void init() {
        modules.forEach(Module::init);
    }

    public List<Module> getModules() {
        return modules;
    }
}
