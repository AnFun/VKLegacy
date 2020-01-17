package me.AnFun.VKLegacy;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class GemPouches implements Listener
{
    public void onEnable() {
        Main.log.info("[GemPouches] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
    }
    
    public void onDisable() {
        Main.log.info("[GemPouches] has been disabled.");
    }
    
    static ItemStack gemPouch(final int tier) {
        String name = "";
        String lore = "";
        if (tier == 1) {
            name = ChatColor.WHITE + "Small Gem Pouch" + ChatColor.GREEN + ChatColor.BOLD + " 0g";
            lore = ChatColor.GRAY + "A small linen pouch that holds " + ChatColor.BOLD + "100g";
        }
        if (tier == 2) {
            name = ChatColor.GREEN + "Medium Gem Sack" + ChatColor.GREEN + ChatColor.BOLD + " 0g";
            lore = ChatColor.GRAY + "A medium wool sack that holds " + ChatColor.BOLD + "150g";
        }
        if (tier == 3) {
            name = ChatColor.AQUA + "Large Gem Satchel" + ChatColor.GREEN + ChatColor.BOLD + " 0g";
            lore = ChatColor.GRAY + "A large leather satchel that holds " + ChatColor.BOLD + "200g";
        }
        if (tier == 4) {
            name = ChatColor.LIGHT_PURPLE + "Gigantic Gem Container" + ChatColor.GREEN + ChatColor.BOLD + " 0g";
            lore = ChatColor.GRAY + "A giant container that holds " + ChatColor.BOLD + "300g";
        }
        final ItemStack is = new ItemStack(Material.INK_SACK);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        im.setLore((List)Arrays.asList(lore));
        is.setItemMeta(im);
        return is;
    }
    
    @EventHandler
    public void onInvClick(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        if (e.getInventory().getName().equals("container.crafting")) {
            if (e.isLeftClick() && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.INK_SACK && isGemPouch(e.getCurrentItem()) && e.getCursor() != null && e.getCursor().getType() == Material.EMERALD) {
                if (e.getCurrentItem().getAmount() != 1) {
                    return;
                }
                e.setCancelled(true);
                final int amt = getCurrentValue(e.getCurrentItem());
                final int max = getMaxValue(e.getCurrentItem());
                final int add = e.getCursor().getAmount();
                if (amt < max) {
                    if (amt + add > max) {
                        e.getCursor().setAmount(add - (max - amt));
                        setPouchBal(e.getCurrentItem(), max);
                        p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                    }
                    else {
                        e.setCursor((ItemStack)null);
                        setPouchBal(e.getCurrentItem(), amt + add);
                        p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                    }
                }
            }
            if (e.isRightClick() && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.INK_SACK && isGemPouch(e.getCurrentItem()) && (e.getCursor() == null || e.getCursor().getType() == Material.AIR)) {
                if (e.getCurrentItem().getAmount() != 1) {
                    return;
                }
                e.setCancelled(true);
                int amt = getCurrentValue(e.getCurrentItem());
                if (amt <= 0) {
                    return;
                }
                if (amt > 64) {
                    e.setCursor(Money.makeGems(64));
                    amt -= 64;
                    setPouchBal(e.getCurrentItem(), amt);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                }
                else {
                    e.setCursor(Money.makeGems(amt));
                    setPouchBal(e.getCurrentItem(), 0);
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onItemPickup(final PlayerPickupItemEvent e) {
        final Player p = e.getPlayer();
        if (e.isCancelled()) {
            return;
        }
        if (e.getItem().getItemStack().getType() != Material.EMERALD) {
            return;
        }
        int add = e.getItem().getItemStack().getAmount();
        ItemStack[] contents;
        for (int length = (contents = p.getInventory().getContents()).length, i = 0; i < length; ++i) {
            final ItemStack is = contents[i];
            if (is != null) {
                if (isGemPouch(is)) {
                    if (is.getAmount() != 1) {
                        return;
                    }
                    final int amt = getCurrentValue(is);
                    final int max = getMaxValue(is);
                    if (add > 0 && amt < max) {
                        if (amt + add > max) {
                            add -= max - amt;
                            final ItemStack newis = e.getItem().getItemStack();
                            newis.setAmount(add);
                            e.getItem().setItemStack(newis);
                            setPouchBal(is, max);
                            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                            e.setCancelled(true);
                            final int adding = max - amt;
                            p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("                    +").append(ChatColor.GREEN).append(adding).append(ChatColor.GREEN).append(ChatColor.BOLD).append("G").toString());
                        }
                        else {
                            e.getItem().remove();
                            setPouchBal(is, amt + add);
                            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                            e.setCancelled(true);
                            p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("                    +").append(ChatColor.GREEN).append(add).append(ChatColor.GREEN).append(ChatColor.BOLD).append("G").toString());
                            add = 0;
                        }
                    }
                }
            }
        }
    }
    
    static int getMaxValue(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getType() == Material.INK_SACK && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            if (lore.size() > 0 && lore.get(0).contains("g")) {
                try {
                    final String line = ChatColor.stripColor((String)lore.get(0));
                    return Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1, line.lastIndexOf("g")));
                }
                catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    static int getCurrentValue(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getType() == Material.INK_SACK && is.getItemMeta().hasDisplayName()) {
            try {
                final String line = ChatColor.stripColor(is.getItemMeta().getDisplayName());
                return Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1, line.lastIndexOf("g")));
            }
            catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    static void setPouchBal(final ItemStack is, final int bal) {
        if (is.getItemMeta().hasDisplayName()) {
            String name = is.getItemMeta().getDisplayName();
            name = name.substring(0, name.lastIndexOf(" "));
            name = String.valueOf(name) + " " + bal + "g";
            final ItemMeta im = is.getItemMeta();
            im.setDisplayName(name);
            is.setItemMeta(im);
        }
    }
    
    static boolean isGemPouch(final ItemStack is) {
        return is != null && is.getType() == Material.INK_SACK && is.getDurability() == 0 && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("g") && getMaxValue(is) != 0;
    }
}
