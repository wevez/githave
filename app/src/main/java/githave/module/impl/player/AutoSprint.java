package githave.module.impl.player;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;

public class AutoSprint extends Module {

    public AutoSprint() {
        super("AutoSprint", "Keeps you sprinting", ModuleCategory.Player);
    }

    @Override
    public void onUpdate(Events.Update event) {

        mc.gameSettings.keyBindSprint.pressed = true;
        super.onUpdate(event);
    }
}
