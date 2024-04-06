package githave.module.impl.player;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.BooleanSetting;
import githave.module.setting.impl.DoubleSetting;
import githave.util.ItemUtil;
import githave.util.RandomUtil;
import githave.util.TimerUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class InvManager extends Module {

    private final BooleanSetting autoArmor = new BooleanSetting.Builder("Auto Armor")
            .value(true)
            .build();
    private final BooleanSetting openInv = new BooleanSetting.Builder("Open Inv")
            .value(true)
            .build();
    private final DoubleSetting startDelay = new DoubleSetting.Builder("Start Delay", 250, 0, 1000, 10)
            .build();
    private final DoubleSetting minDelay = new DoubleSetting.Builder("Min Delay", 100, 0, 1000, 10)
            .build();
    private final DoubleSetting maxDelay = new DoubleSetting.Builder("Max Delay", 500, 0, 1000, 10)
            .build();

    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil startTimer = new TimerUtil();
    private long currentDelay;

    public InvManager() {
        super("InvManager", "", ModuleCategory.Player);
        this.getSettingList().addAll(Arrays.asList(
                autoArmor,
                openInv,
                startDelay,
                minDelay,
                maxDelay
        ));
    }

    @Override
    protected void onEnable() {
        currentDelay = RandomUtil.nextInt((int) minDelay.getValue(), (int) maxDelay.getValue());
        super.onEnable();
    }

    private static boolean isBestArmor(ItemStack stack, int type)
    {
        float prot = ItemUtil.getProtection(stack);
        String strType = "";

        if (type == 1)
        {
            strType = "helmet";
        }
        else if (type == 2)
        {
            strType = "chestplate";
        }
        else if (type == 3)
        {
            strType = "leggings";
        }
        else if (type == 4)
        {
            strType = "boots";
        }

        if (!stack.getUnlocalizedName().contains(strType))
        {
            return false;
        }

        for (int i = 5; i < 45; i++)
        {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
            {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (ItemUtil.getProtection(is) > prot && is.getUnlocalizedName().contains(strType))
                {
                    return false;
                }
            }
        }
        return true;
    }
}
