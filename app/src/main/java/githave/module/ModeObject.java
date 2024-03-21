package githave.module;

import githave.MCHook;
import githave.event.EventListener;

public abstract class ModeObject implements MCHook, EventListener {

    public void onEnable() {}

    public void onDisable() {}
}
