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

    private final BooleanSetting checkNameCheck = new BooleanSetting.Builder("Chest Name Check")
            .value(true)
            .build();

    private final ModeSetting stealOrder = new ModeSetting.Builder("Steal Mode", "Normal", "Random", "Reverse", "Distance")
            .build();

    private final ModeSetting stealMethod = new ModeSetting.Builder("Steal Method", "Normal", "ViaFix")
            .build();

    private final BooleanSetting checkName = new BooleanSetting.Builder("Check Name")
            .value(true)
            .build();

    private long currentDelay;

    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil startTimer = new TimerUtil();

    private int decidedTimer = 0;

    public static boolean closeAfterContainer;

    private boolean gotItems;
    private int ticksInChest;

    private boolean lastInChest;

    public ChestStealer() {
        super("ChestStealer", "", ModuleCategory.Player);
        this.getSettingList().addAll(Arrays.asList(
                startDelay,
                minDelay,
                maxDelay,
                ignoreJunk,
                autoClose,
                checkNameCheck,
                stealOrder,
                stealMethod,
                checkName
        ));
    }

    @Override
    public void onRenderGui(Events.PreRenderGui event) {
        if (!lastInChest)
        {
            startTimer.reset();
        }

        lastInChest = mc.currentScreen instanceof GuiChest;

        if (mc.currentScreen instanceof GuiChest)
        {
            if (checkName.getValue())
            {
                final String name = ((GuiChest) mc.currentScreen).lowerChestInventory.getDisplayName().getUnformattedText();

                if (!name.toLowerCase().contains("chest"))
                {
                    return;
                }
            }

            if(!startTimer.hasTimeElapsed((long) startDelay.getValue()))return;

            if (decidedTimer == 0)
            {
                final int delayFirst = (int) Math.floor(Math.min(minDelay.getValue(), maxDelay.getValue()));
                final int delaySecond = (int) Math.ceil(Math.max(minDelay.getValue(), maxDelay.getValue()));
                decidedTimer = RandomUtil.nextInt(delayFirst, delaySecond);
            }

            if (timer.hasTimeElapsed(decidedTimer))
            {
                final ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;

                boolean randomize = false;

                if(randomize) {
                    boolean found = false;
                    for (int i = 0; i < chest.inventorySlots.size(); i++)
                    {
                        final ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);

                        if (stack != null && (itemWhitelisted(stack) && ignoreJunk.getValue()))
                        {
                            found = true;
                        }
                    }

                    int i = 0;
                    while(chest.getLowerChestInventory().getStackInSlot(i) == null) {
                        i = RandomUtil.nextInt(1, chest.inventorySlots.size());
                        break;
                    }

                    final ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);

                    if (stack != null && (itemWhitelisted(stack) && ignoreJunk.getValue()))
                    {
                        mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                        timer.reset();
                        final int delayFirst = (int) Math.floor(Math.min(minDelay.getValue(), maxDelay.getValue()));
                        final int delaySecond = (int) Math.ceil(Math.max(minDelay.getValue(), maxDelay.getValue()));
                        decidedTimer = RandomUtil.nextInt(delayFirst, delaySecond);
                        gotItems = true;
                        return;
                    }

                    if (gotItems && !found && autoClose.getValue() && ticksInChest > 3)
                    {
                        mc.thePlayer.closeScreen();
                        return;
                    }
                }else {
                    for (int i = 0; i < chest.inventorySlots.size(); i++)
                    {
                        final ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);

                        if (stack != null && (itemWhitelisted(stack) && ignoreJunk.getValue()))
                        {
                            mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                            timer.reset();
                            final int delayFirst = (int) Math.floor(Math.min(minDelay.getValue(), maxDelay.getValue()));
                            final int delaySecond = (int) Math.ceil(Math.max(minDelay.getValue(), maxDelay.getValue()));
                            decidedTimer = RandomUtil.nextInt(delayFirst, delaySecond);
                            gotItems = true;
                            return;
                        }
                    }

                    if (gotItems && autoClose.getValue() && ticksInChest > 3)
                    {
                        mc.thePlayer.closeScreen();
                    }
                }
            }
        }
        super.onRenderGui(event);
    }

    private boolean itemWhitelisted(final ItemStack itemStack)
    {
        final ArrayList<Item> whitelistedItems = new ArrayList<Item>()
        {
            {
                add(Items.ender_pearl);
                add(Items.iron_ingot);
                add(Items.snowball);
                add(Items.gold_ingot);
                add(Items.redstone);
                add(Items.diamond);
                add(Items.emerald);
                add(Items.quartz);
                add(Items.bow);
                add(Items.arrow);
                add(Items.fishing_rod);
            }
        };
        final Item item = itemStack.getItem();
        final String itemName = itemStack.getDisplayName();

        if (itemName.contains("Right Click") || itemName.contains("Click to Use") || itemName.contains("Players Finder"))
        {
            return true;
        }

        final ArrayList<Integer> whitelistedPotions = new ArrayList<Integer>()
        {
            {
                add(6);
                add(1);
                add(5);
                add(8);
                add(14);
                add(12);
                add(10);
                add(16);
            }
        };

        if (item instanceof ItemPotion)
        {
            final int potionID = getPotionId(itemStack);
            return whitelistedPotions.contains(potionID);
        }

        return (item instanceof ItemBlock
                && !(((ItemBlock) item).getBlock() instanceof BlockTNT)
                && !(((ItemBlock) item).getBlock() instanceof BlockSlime)
                && !(((ItemBlock) item).getBlock() instanceof BlockFalling))
                || item instanceof ItemAnvilBlock
                || item instanceof ItemSword
                || item instanceof ItemArmor
                || item instanceof ItemTool
                || item instanceof ItemFood
                || item instanceof ItemSkull
                || itemName.contains("\247")
                || whitelistedItems.contains(item)
                && !item.equals(Items.spider_eye);
    }
    private int getPotionId(final ItemStack potion)
    {
        final Item item = potion.getItem();

        try
        {
            if (item instanceof ItemPotion)
            {
                final ItemPotion p = (ItemPotion) item;
                return p.getEffects(potion.getMetadata()).get(0).getPotionID();
            }
        }
        catch (final NullPointerException ignored)
        {
        }

        return 0;
    }

    @Override
    public void onUpdate(Events.Update event) {
        if (!event.pre) return;
        if (mc.currentScreen instanceof GuiChest && Display.isVisible() && (!checkName.getValue() || (((GuiChest) mc.currentScreen).lowerChestInventory.getDisplayName().getUnformattedText().contains("chest"))))
        {
            mc.mouseHelper.mouseXYChange();
            mc.mouseHelper.ungrabMouseCursor();
            mc.mouseHelper.grabMouseCursor();
        }

        if (mc.currentScreen instanceof GuiChest)
        {
            ticksInChest++;

            if (ticksInChest * 50 > 255)
            {
                ticksInChest = 10;
            }
        }
        else
        {
            ticksInChest--;
            gotItems = false;

            if (ticksInChest < 0)
            {
                ticksInChest = 0;
            }
        }
        super.onUpdate(event);
    }

    private void updateCurrentDelay() {
        currentDelay = RandomUtil.nextInt((int) minDelay.getValue(), (int) maxDelay.getValue());
    }
}
