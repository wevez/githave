package githave.module.impl.player;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.BooleanSetting;
import githave.module.setting.impl.DoubleSetting;
import githave.module.setting.impl.ModeSetting;
import githave.util.RandomUtil;
import githave.util.TimerUtil;
import net.minecraft.network.play.server.S30PacketWindowItems;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChestStealer extends Module {

    private final DoubleSetting startDelay = new DoubleSetting.Builder("Start Delay", 250, 0, 1000, 10)
            .build();
    private final DoubleSetting minDelay = new DoubleSetting.Builder("Min Delay", 100, 0, 1000, 10)
            .build();
    private final DoubleSetting maxDelay = new DoubleSetting.Builder("Max Delay", 500, 0, 1000, 10)
            .build();
    private final BooleanSetting ignoreJunk = new BooleanSetting.Builder("Ignore Junk")
            .value(true)
            .build();
    private final BooleanSetting autoClose = new BooleanSetting.Builder("Auto Close")
            .value(true)
            .build();
    private final BooleanSetting chestNameCheck = new BooleanSetting.Builder("Chest Name Check")
            .value(true)
            .build();
    private final ModeSetting stealMode = new ModeSetting.Builder("Steal Mode", "Normal", "Random", "Reverse", "Distance")
            .build();

    public ChestStealer() {
        super("ChestStealer", "", ModuleCategory.Player);
        this.getSettingList().addAll(Arrays.asList(
                startDelay,
                minDelay,
                maxDelay,
                ignoreJunk,
                autoClose,
                chestNameCheck,
                stealMode
        ));
    }

    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil startTimer = new TimerUtil();
}
