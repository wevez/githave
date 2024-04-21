package githave.module;

import githave.module.impl.combat.*;
import githave.module.impl.movement.Scaffold;
import githave.module.impl.player.AutoSprint;
import githave.module.impl.player.Blink;
import githave.module.impl.player.ChestStealer;
import githave.module.impl.render.Animations;
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
            new TickBase(),
            new Velocity(),
            new WTap(),

            new TargetHUD(),
            new ModuleList(),
            new Animations(),
            new HUD(),
            new Blink(),
            new AutoSprint(),
            new ChestStealer(),
            new Scaffold()
    );

    public void init() {
        modules.forEach(Module::init);
    }

    public List<Module> getModules() {
        return modules;
    }
}
