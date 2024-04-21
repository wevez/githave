package githave.util.bypass;

import githave.module.setting.impl.DoubleSetting;
import githave.util.RandomUtil;
import githave.util.TimerUtil;
import net.minecraft.util.MathHelper;

import java.util.concurrent.ThreadLocalRandom;

public class IndependentCPS {

    private final DoubleSetting minCPS, maxCPS;

    private int lastTotalClicks = -1;
    private int[] lastClickArray;

    private int index;

    public IndependentCPS(DoubleSetting minCPS, DoubleSetting maxCPS) {
        this.minCPS = minCPS;
        this.maxCPS = maxCPS;
        updateClickArray();
    }

    public boolean onTick() {
        if (index >= lastClickArray.length) {
            index = 0;
            updateClickArray();
        }
        return lastClickArray[index++] > 0;
    }

    private void updateClickArray() {
        int clicksBefore = lastTotalClicks == -1 ? (int) RandomUtil.nextDouble(minCPS.getValue(), maxCPS.getValue()) : lastTotalClicks;
        int limit = RandomUtil.nextInt(0, 2);
        int clicks = MathHelper.clamp_int((int) RandomUtil.nextDouble(minCPS.getValue(), maxCPS.getValue()), clicksBefore- limit, clicksBefore + limit);
        int[] clickArray = new int[20];
        double distance = Math.max(1, clickArray.length / (double) clicks);
        int remainingClicks = clicks;
        double currentIndex = 0;
        while (remainingClicks > 0) {
            clickArray[(int) currentIndex % clickArray.length]++;
            currentIndex += distance;
            remainingClicks--;
        }
        lastTotalClicks = clicks;
        lastClickArray = clickArray;
    }
}
