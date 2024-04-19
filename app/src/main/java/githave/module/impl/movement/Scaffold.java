package githave.module.impl.movement;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;

public class Scaffold extends Module {

    public Scaffold() {
        super("Scaffold", "Place blocks at your feet", ModuleCategory.Movement);
    }

    @Override
    public void onTick(Events.Tick event) {
        super.onTick(event);
    }
}
