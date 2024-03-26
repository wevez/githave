package githave.module.impl.combat;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;

public class WTap extends Module {

    public WTap() {
        super("WTap", "Auto WTap", ModuleCategory.Combat);
    }

    @Override
    public void onTick(Events.Tick event) {
        if (KillAura.target == null) return;
        if (KillAura.target.hurtTime == 10) {
            mc.thePlayer.reSprint = 1;
        }
        super.onTick(event);
    }
}
