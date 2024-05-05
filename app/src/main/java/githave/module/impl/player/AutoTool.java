package githave.module.impl.player;

import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.BooleanSetting;
import githave.util.TimerUtil;

import java.util.Arrays;

public class AutoTool extends Module {

    private int serverSideSlot = -1, lastServerSideSlot = -1;
    private int tool = -1;
    private boolean reset;

    private final TimerUtil timer = new TimerUtil();

    private final BooleanSetting silent = new BooleanSetting.Builder("Silent")
            .value(true)
            .build();

    public AutoTool() {
        super("AutoTool", "Automatically selects the best tool for the job", ModuleCategory.Player);
        this.getSettingList().addAll(Arrays.asList(
                silent
        ));
    }

    // TODO
}
