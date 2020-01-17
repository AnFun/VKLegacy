package me.AnFun.VKLegacy;

import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.EnchantmentWrapper;

public class GlowEnchant extends EnchantmentWrapper
{
    public GlowEnchant(final int id) {
        super(id);
    }
    
    public boolean canEnchantItem(final ItemStack item) {
        return true;
    }
    
    public boolean conflictsWith(final Enchantment other) {
        return false;
    }
    
    public EnchantmentTarget getItemTarget() {
        return null;
    }
    
    public int getMaxLevel() {
        return 10;
    }
    
    public String getName() {
        return "Glow";
    }
    
    public int getStartLevel() {
        return 1;
    }
}
