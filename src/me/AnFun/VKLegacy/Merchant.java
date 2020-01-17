package me.AnFun.VKLegacy;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Merchant {
  public static int getTier(ItemStack is) {
    int tier = 0;
    if (is.getType().name().contains("WOOD_") || is.getType().name().contains("LEATHER_") || 
      is.getType().name().contains("COAL_") || (
      is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Leather")))
      tier = 1; 
    if (is.getType().name().contains("STONE_") || is.getType().name().contains("CHAINMAIL_") || 
      is.getType().name().contains("EMERALD_") || (
      is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Chainmail")))
      tier = 2; 
    if ((is.getType().name().contains("IRON_") && is.getType() != Material.IRON_FENCE) || (
      is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Iron")))
      tier = 3; 
    if (is.getType().name().contains("DIAMOND_") || (
      is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Diamond")))
      tier = 4; 
    if (is.getType().name().contains("GOLD_") || (
      is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Golden")))
      tier = 5; 
    return tier;
  }
}
