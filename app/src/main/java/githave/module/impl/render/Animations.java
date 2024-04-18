package githave.module.impl.render;

import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.ModeSetting;

import java.util.Arrays;

public class Animations extends Module {
    public static boolean toggledStatic;

    public static final ModeSetting mode = new ModeSetting.Builder("Animation Mode", "Funi")
            .build();

    public Animations() {
        super("Animations", "Change the swordblock animation", ModuleCategory.Render);
        this.getSettingList().addAll(Arrays.asList(
            mode
        ));
    }

    @Override
    protected void onEnable() {
        toggledStatic = true;

        super.onEnable();
    }

    @Override
    protected void onDisable() {
        toggledStatic = false;

        super.onEnable();
    }
}
