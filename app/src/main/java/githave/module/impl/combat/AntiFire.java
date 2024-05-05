package githave.module.impl.combat;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import net.minecraft.init.Items;

public class AntiFire extends Module {

    private boolean canWork;

    public AntiFire() {
        super("AntiFire", "Prevents you from catching on fire", ModuleCategory.Combat);
    }

    @Override
    public void onTick(Events.Tick event) {
        if (canWork) {
            mc.rightClickMouse();
            if (!mc.thePlayer.isBurning()) {
                mc.rightClickMouse();
                canWork = false;
                return;
            }
        }
        super.onTick(event);
    }

    @Override
    public void onRotation(Events.Rotation event) {
        if (canWork) {
            event.pitch = 90f;
        }
        int slot = getBucketSlot();
        if (slot == -1 || !mc.thePlayer.isBurning()) return;
        mc.thePlayer.inventory.currentItem = slot;
        canWork = true;
        super.onRotation(event);
    }

    public static int getBucketSlot()  {
        int item = -1;
        int stacksize = 0;
        for (int i = 36; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null && (mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() == Items.water_bucket))  {
                item = i - 36;
                stacksize = mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize;
            }
        }
        return item;
    }
}
