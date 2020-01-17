package me.bpweber.practiceserver;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class Money
{
    public static boolean hasEnoughGems(final Player p, final int amt) {
        int gems = 0;
        ItemStack[] contents;
        for (int length = (contents = p.getInventory().getContents()).length, i = 0; i < length; ++i) {
            final ItemStack is = contents[i];
            if (isGem(is)) {
                gems += is.getAmount();
            }
            if (isBankNote(is)) {
                gems += getGems(is);
            }
            if (GemPouches.isGemPouch(is)) {
                gems += GemPouches.getCurrentValue(is);
            }
        }
        return gems >= amt;
    }
    
    public static int getGems(final Player p) {
        int gems = 0;
        ItemStack[] contents;
        for (int length = (contents = p.getInventory().getContents()).length, i = 0; i < length; ++i) {
            final ItemStack is = contents[i];
            if (isGem(is)) {
                gems += is.getAmount();
            }
            if (isBankNote(is)) {
                gems += getGems(is);
            }
            if (GemPouches.isGemPouch(is)) {
                gems += GemPouches.getCurrentValue(is);
            }
        }
        return gems;
    }
    
    public static void takeGems(final Player p, int amt) {
        if (hasEnoughGems(p, amt)) {
            for (int i = 0; i < p.getInventory().getSize(); ++i) {
                final ItemStack is = p.getInventory().getItem(i);
                if (amt > 0) {
                    if (isGem(is)) {
                        if (amt >= is.getAmount()) {
                            amt -= is.getAmount();
                            p.getInventory().setItem(i, (ItemStack)null);
                        }
                        else {
                            is.setAmount(is.getAmount() - amt);
                            amt = 0;
                        }
                    }
                    if (isBankNote(is)) {
                        final int val = getGems(is);
                        if (amt >= val) {
                            amt -= val;
                            p.getInventory().setItem(i, (ItemStack)null);
                        }
                        else {
                            final ItemMeta im = is.getItemMeta();
                            im.setLore((List)Arrays.asList(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Value: ").append(ChatColor.WHITE).append(val - amt).append(" Gems").toString(), ChatColor.GRAY + "Exchange at any bank for GEM(s)"));
                            is.setItemMeta(im);
                            amt = 0;
                        }
                    }
                    if (GemPouches.isGemPouch(is)) {
                        final int val = GemPouches.getCurrentValue(is);
                        if (amt >= val) {
                            amt -= val;
                            GemPouches.setPouchBal(is, 0);
                        }
                        else {
                            GemPouches.setPouchBal(is, val - amt);
                            amt = 0;
                        }
                    }
                }
            }
        }
    }
    
    public static boolean isGem(final ItemStack is) {
        return is != null && is.getType() != Material.AIR && is.getType() == Material.EMERALD && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().toLowerCase().contains("gem");
    }
    
    public static boolean isBankNote(final ItemStack is) {
        return is != null && is.getType() != Material.AIR && is.getType() == Material.PAPER && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().toLowerCase().contains("bank note");
    }
    
    public static ItemStack createBankNote(final int amt) {
        final ItemStack is = new ItemStack(Material.PAPER);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + "Bank Note");
        im.setLore((List)Arrays.asList(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Value: ").append(ChatColor.WHITE).append(amt).append(" Gems").toString(), ChatColor.GRAY + "Exchange at any bank for GEM(s)"));
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack makeGems(final int amt) {
        final ItemStack is = new ItemStack(Material.EMERALD, amt);
        final ItemMeta im = is.getItemMeta();
        im.setLore((List)Arrays.asList(String.valueOf(ChatColor.GRAY.toString()) + "The currency of Andalucia"));
        im.setDisplayName(String.valueOf(ChatColor.WHITE.toString()) + "Gem");
        is.setItemMeta(im);
        is.setAmount(amt);
        return is;
    }
    
    static int getGems(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getType() == Material.PAPER && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            if (lore.size() > 0 && lore.get(0).contains("Value")) {
                try {
                    final String line = ChatColor.stripColor((String)lore.get(0));
                    return Integer.parseInt(line.split(": ")[1].split(" Gems")[0]);
                }
                catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
}
