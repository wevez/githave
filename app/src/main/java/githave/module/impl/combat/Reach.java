package githave.module.impl.combat;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.DoubleSetting;

public class Reach extends Module {

    private final DoubleSetting reach = new DoubleSetting.Builder("Reach", 3, 1, 8, 0.1)
            .build();

    public Reach() {
        super("Reach", "", ModuleCategory.Combat);
    }

    @Override
    public void onReach(Events.Reach event) {
        event.reach = reach.getValue();
        super.onReach(event);
    }
}
