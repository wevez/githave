package githave.module;

import githave.module.impl.combat.*;
import githave.module.impl.player.AutoSprint;
import githave.module.impl.player.ChestStealer;
import githave.module.impl.render.HUD;
import githave.module.impl.render.ModuleList;
import githave.module.impl.render.TargetHUD;

import java.util.Arrays;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = Arrays.asList(
            new AntiBot(),
            new AutoClicker(),
            new Backtrack(),
            new KillAura(),
            new Reach(),
            new TickBase(),
            new Velocity(),
            new WTap(),

            new TargetHUD(),
            new ModuleList(),
            new HUD(),
            new AutoSprint(),
            new ChestStealer()
    );

    public void init() {
        modules.forEach(Module::init);
    }

    public List<Module> getModules() {
        return modules;
    }
}