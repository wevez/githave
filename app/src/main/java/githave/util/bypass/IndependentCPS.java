package githave.util.bypass;

import githave.module.setting.impl.DoubleSetting;
import githave.util.RandomUtil;
import githave.util.TimerUtil;

public class IndependentCPS {

    private final TimerUtil clickCheckTimer = new TimerUtil();
    private int clicksIn;

    private final DoubleSetting minCPS, maxCPS;

    public IndependentCPS(DoubleSetting minCPS, DoubleSetting maxCPS) {
        this.minCPS = minCPS;
        this.maxCPS = maxCPS;
    }

    public boolean onTick() {
        if (clickCheckTimer.hasTimeElapsed(1000)) {
            clickCheckTimer.reset();
            clicksIn = 0;
        }
        if (clicksIn >= maxCPS.getValue()) return false;
        double centerCPS = this.minCPS.getValue() + (this.maxCPS.getValue() - this.minCPS.getValue()) / 2;
        if (RandomUtil.percent((int) (100 * centerCPS / 20))) {
            clicksIn++;
            return true;
        }
        return false;
    }
}
