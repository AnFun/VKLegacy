package me.AnFun.VKLegacy;

import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Items
{
    public static ItemStack orb(final boolean inshop) {
        final ItemStack orb = new ItemStack(Material.MAGMA_CREAM);
        final ItemMeta orbmeta = orb.getItemMeta();
        final List<String> lore = new ArrayList<String>();
        orbmeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Orb of Alteration");
        lore.add(ChatColor.GRAY + "Randomizes stats of selected equipment.");
        if (inshop) {
            lore.add(ChatColor.GREEN + "Price: " + ChatColor.WHITE + "2000g");
        }
        orbmeta.setLore((List)lore);
        orb.setItemMeta(orbmeta);
        return orb;
    }
    
    public static ItemStack enchant(final int tier, final int type, final boolean inshop) {
        final ItemStack is = new ItemStack(Material.EMPTY_MAP);
        final ItemMeta im = is.getItemMeta();
        final List<String> lore = new ArrayList<String>();
        String name = "";
        int price = 0;
        if (tier == 1) {
            price = 100;
            name = ChatColor.WHITE + " Enchant ";
            if (type == 0) {
                name = String.valueOf(name) + "Wooden";
            }
            if (type == 1) {
                name = String.valueOf(name) + "Leather";
            }
        }
        if (tier == 2) {
            price = 200;
            name = ChatColor.GREEN + " Enchant ";
            if (type == 0) {
                name = String.valueOf(name) + "Stone";
            }
            if (type == 1) {
                name = String.valueOf(name) + "Chainmail";
            }
        }
        if (tier == 3) {
            price = 400;
            name = ChatColor.AQUA + " Enchant Iron";
        }
        if (tier == 4) {
            price = 800;
            name = ChatColor.LIGHT_PURPLE + " Enchant Diamond";
        }
        if (tier == 5) {
            price = 1600;
            name = ChatColor.YELLOW + " Enchant Gold";
        }
        if (type == 0) {
            price *= (int)1.5;
            name = String.valueOf(name) + " Weapon";
            lore.add(ChatColor.RED + "+5% DMG");
            lore.add(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("Weapon will VANISH if enchant above +3 FAILS.").toString());
        }
        if (type == 1) {
            name = String.valueOf(name) + " Armor";
            lore.add(ChatColor.RED + "+5% HP");
            lore.add(ChatColor.RED + "+5% HP REGEN");
            lore.add(ChatColor.GRAY + "   - OR -");
            lore.add(ChatColor.RED + "+1% ENERGY REGEN");
            lore.add(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("Armor will VANISH if enchant above +3 FAILS.").toString());
        }
        if (inshop) {
            lore.add(ChatColor.GREEN + "Price: " + ChatColor.WHITE + price + "g");
        }
        im.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Scroll:").append(name).toString());
        im.setLore((List)lore);
        is.setItemMeta(im);
        return is;
    }
}
