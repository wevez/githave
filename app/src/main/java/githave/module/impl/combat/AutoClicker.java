package githave.module.impl.combat;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.DoubleSetting;
import githave.module.setting.impl.ModeSetting;
import githave.util.bypass.IndependentCPS;
import net.minecraft.util.MovingObjectPosition;

import java.util.Arrays;

public class AutoClicker extends Module {

    private final ModeSetting clickMode = new ModeSetting.Builder("Click Mode", "Normal", "Timing", "1.9+")
            .build();

    private final DoubleSetting minCPS = new DoubleSetting.Builder("Min CPS", 12, 0, 20, 0.1)
            .visibility(() -> !clickMode.getValue().equals("1.9+"))
            .build();

    private final DoubleSetting maxCPS = new DoubleSetting.Builder("Max CPS", 15, 0, 20, 0.1)
            .visibility(() -> !clickMode.getValue().equals("1.9+"))
            .build();

    private IndependentCPS cpsTimer;

    public AutoClicker() {
        super("AutoClicker", "Clicks as fast as you want", ModuleCategory.Combat);
        getSettingList().addAll(Arrays.asList(
                clickMode,
                minCPS,
                maxCPS
        ));
    }

    public static boolean shouldClick;

    @Override
    public void init() {
        cpsTimer = new IndependentCPS(this.minCPS, this.maxCPS);
        super.init();
    }

    @Override
    public void onTick(Events.Tick event) {
        if (mc.thePlayer == null || mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK || !mc.gameSettings.keyBindAttack.isKeyDown()) {
            return;
        }
        if (shouldClick || cpsTimer.onTick()) {
            shouldClick = false;
            mc.clickMouse();
        }
        super.onTick(event);
    }
}
