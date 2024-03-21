package githave.module.impl.combat;

import net.minecraft.entity.EntityLivingBase;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.BooleanSetting;
import githave.module.setting.impl.DoubleSetting;
import githave.module.setting.impl.ModeSetting;
import githave.module.setting.impl.MultiBooleanSetting;

import java.util.Arrays;

public class KillAura extends Module {

    public static EntityLivingBase target;

    private final MultiBooleanSetting targets = new MultiBooleanSetting.Builder("Targets", "Animals", "Monsters", "Villagers", "Players")
            .value("Players", true)
            .build();

    private final ModeSetting teams = new ModeSetting.Builder("Teams", "None", "Team Color")
            .build();

    private final ModeSetting blockMode = new ModeSetting.Builder("Block Mode", "None", "NCP", "Legit")
            .build();

    private final DoubleSetting aimRange = new DoubleSetting.Builder("Aim Range", 4, 0, 8, 0.1)
            .build();

    private final DoubleSetting attackRange = new DoubleSetting.Builder("Attack Range", 3, 0, 8, 0.1)
            .build();

    private final DoubleSetting blockRange = new DoubleSetting.Builder("Block Range", 4, 0, 8, 0.1)
            .visibility(() -> !blockMode.getValue().equals("None"))
            .build();

    private final BooleanSetting throughWalls = new BooleanSetting.Builder("Through Walls")
            .build();

    private final DoubleSetting minCPS = new DoubleSetting.Builder("Min CPS", 8, 0, 20, 0.1)
            .build();

    private final DoubleSetting maxCPS = new DoubleSetting.Builder("Max CPS", 12, 0, 20, 0.1)
            .build();

    public KillAura() {
        super("KillAura", "Attacks entities around you", ModuleCategory.Combat);
        this.getSettingList().addAll(Arrays.asList(
           targets, teams, blockMode, aimRange, attackRange, blockRange, throughWalls, minCPS, maxCPS
        ));
    }
}
