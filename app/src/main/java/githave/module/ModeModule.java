package githave.module;

import githave.GitHave;
import githave.module.setting.impl.ModeSetting;

public abstract class ModeModule extends Module {

    private final ModeSetting mode;

    private ModeObject object;

    public ModeModule(String name, String description, ModuleCategory category, String... option) {
        super(name, description, category);
        this.mode = new ModeSetting.Builder("Mode", option)
                .onUpdate(v -> this.object = this.getObject(v))
                .build();

        this.object = this.getObject(this.mode.getValue());
    }

    @Override
    protected void onEnable() {
        this.objectIsNonnull();
        this.object.onEnable();
        GitHave.INSTANCE.eventManager.register(this.object);
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        this.objectIsNonnull();
        this.object.onDisable();
        GitHave.INSTANCE.eventManager.unregister(this.object);
        super.onDisable();
    }

    private final void objectIsNonnull() {
        if (this.object == null) {
            this.object = this.getObject(mode.getValue());
        }
    }

    protected abstract ModeObject getObject(String value);
}
