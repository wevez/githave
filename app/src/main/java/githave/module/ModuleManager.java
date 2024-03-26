package githave.module;

import githave.module.impl.combat.Backtrack;
import githave.module.impl.combat.KillAura;
import githave.module.impl.combat.TickBase;
import githave.module.impl.combat.WTap;
import githave.module.impl.player.AutoSprint;
import githave.module.impl.render.HUD;
import githave.module.impl.render.ModuleList;
import githave.module.impl.render.TargetHUD;

import java.util.Arrays;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = Arrays.asList(
            new Backtrack(),
            new KillAura(),
            new TickBase(),
            new WTap(),
            new TargetHUD(),
            new ModuleList(),
            new HUD(),
            new AutoSprint()
    );

    public void init() {
        modules.forEach(Module::init);
    }

    public List<Module> getModules() {
        return modules;
    }
}
