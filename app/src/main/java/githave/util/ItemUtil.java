package githave.util;

import githave.MCHook;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemUtil implements MCHook {

    public static float getProtection(ItemStack stack) {
        float prot = 0.0F;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)stack.getItem();
            prot = (float)(prot + armor.damageReduceAmount + ((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075D);
            prot = (float)(prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0D);
            prot = (float)(prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0D);
            prot = (float)(prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0D);
            prot = (float)(prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0D);
            prot = (float)(prot + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) / 100.0D);
        }
        return prot;
    }
}
