package githave.module.impl.combat;

import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.ModeSetting;
import net.minecraft.entity.EntityLivingBase;

import java.util.Arrays;

public class AntiBot extends Module {

    private static final ModeSetting mode = new ModeSetting.Builder("Mode", "Hypixel", "Shotbow")
            .build();

    public AntiBot() {
        super("AntiBot", "Removes bots from game", ModuleCategory.Combat);
        getSettingList().addAll(Arrays.asList(
                mode
        ));
    }

    public static boolean isBot(EntityLivingBase entity) {
        switch (mode.getValue()) {
            case "Hypixel": return entity.isInvisible();
            // TODO: add shotbow
        }
        return false;
    }
}
