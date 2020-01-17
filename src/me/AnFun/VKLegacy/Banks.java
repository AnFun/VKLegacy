package me.AnFun.VKLegacy;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import org.bukkit.OfflinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryOpenEvent;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.Sound;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import java.io.File;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class Banks implements Listener
{
    public static HashMap<Player, String> banksee;
    List<String> withdraw;
    
    static {
        Banks.banksee = new HashMap<Player, String>();
    }
    
    public Banks() {
        this.withdraw = new ArrayList<String>();
    }
    
    public void onEnable() {
        Main.log.info("[Banks] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        final File file = new File(Main.plugin.getDataFolder(), "banks");
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    
    public void onDisable() {
        Main.log.info("[Banks] has been disabled.");
        final File file = new File(Main.plugin.getDataFolder(), "banks");
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    
    @EventHandler
    public void onClick(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ENDER_CHEST) {
            e.setCancelled(true);
            Inventory inv = getBank(p);
            if (inv == null) {
                inv = Bukkit.createInventory((InventoryHolder)null, 63, "Bank Chest (1/1)");
            }
            p.openInventory(inv);
            p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1.0f, 1.0f);
        }
    }
    
    @EventHandler
    public void onClose(final InventoryCloseEvent e) {
        final Player p = (Player)e.getPlayer();
        if (e.getInventory().getName().equals("Bank Chest (1/1)")) {
            this.saveBank(e.getInventory(), p);
            new BukkitRunnable() {
                public void run() {
                    Banks.this.saveBank(e.getInventory(), p);
                    if (Banks.banksee.containsKey(p)) {
                        Banks.banksee.remove(p);
                    }
                }
            }.runTaskLater(Main.plugin, 1L);
        }
    }
    
    @EventHandler
    public void onClickSave(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        if (e.getInventory().getName().equals("Bank Chest (1/1)")) {
            this.saveBank(e.getInventory(), p);
            new BukkitRunnable() {
                public void run() {
                    Banks.this.saveBank(e.getInventory(), p);
                }
            }.runTaskLater(Main.plugin, 1L);
        }
    }
    
    public void saveBank(final Inventory inv, final Player p) {
        String name = p.getName();
        if (Banks.banksee.containsKey(p)) {
            name = Banks.banksee.get(p);
        }
        final File file = new File(Main.plugin.getDataFolder() + "/banks", String.valueOf(name) + ".yml");
        final YamlConfiguration config = new YamlConfiguration();
        for (int i = 0; i < inv.getSize(); ++i) {
            if (inv.getItem(i) != null) {
                config.set(new StringBuilder().append(i).toString(), (Object)inv.getItem(i));
            }
        }
        try {
            config.save(file);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static Inventory getBank(final Player p) {
        String name = p.getName();
        if (Banks.banksee.containsKey(p)) {
            name = Banks.banksee.get(p);
        }
        final File file = new File(Main.plugin.getDataFolder() + "/banks", String.valueOf(name) + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        final YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 63, "Bank Chest (1/1)");
        for (int i = 0; i < inv.getSize(); ++i) {
            if (config.contains(new StringBuilder().append(i).toString())) {
                inv.setItem(i, config.getItemStack(new StringBuilder().append(i).toString()));
            }
        }
        return inv;
    }
    
    @EventHandler
    public void onInvOpen(final InventoryOpenEvent e) {
        if (e.getInventory().getName().equals("Bank Chest (1/1)")) {
            final Inventory inv = e.getInventory();
            final ItemStack glass = new ItemStack(Material.THIN_GLASS);
            final ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
            for (int i = 54; i < 63; ++i) {
                inv.setItem(i, glass);
            }
            final ItemStack gem = new ItemStack(Material.EMERALD);
            final ItemMeta im = gem.getItemMeta();
            im.setDisplayName(new StringBuilder().append(ChatColor.GREEN).append((int)Main.econ.getBalance((OfflinePlayer)e.getPlayer())).append(ChatColor.GREEN).append(ChatColor.BOLD).append(" GEM(s)").toString());
            im.setLore((List)Arrays.asList(ChatColor.GRAY + "Right Click to create " + ChatColor.GREEN + "A GEM NOTE"));
            gem.setItemMeta(im);
            inv.setItem(58, gem);
        }
    }
    
    int getGems(final ItemStack is) {
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
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPromptAmount(final AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (this.withdraw.contains(p.getName())) {
            e.setCancelled(true);
            final String message = e.getMessage();
            if (e.getMessage().equalsIgnoreCase("cancel") && this.withdraw.contains(p.getName())) {
                for (int i = 0; i < this.withdraw.size(); ++i) {
                    this.withdraw.remove(p.getName());
                }
                p.sendMessage(ChatColor.RED + "Withdrawl operation - " + ChatColor.BOLD + "CANCELLED");
                return;
            }
            int amt = 0;
            try {
                amt = Integer.parseInt(message);
                if (amt > Main.econ.getBalance((OfflinePlayer)p)) {
                    p.sendMessage(ChatColor.GRAY + "You cannot withdraw more GEMS than you have stored.");
                }
                else if (amt <= 0) {
                    p.sendMessage(ChatColor.RED + "You must enter a POSIVIVE amount.");
                }
                else {
                    for (int j = 0; j < this.withdraw.size(); ++j) {
                        this.withdraw.remove(p.getName());
                    }
                    Main.econ.withdrawPlayer((OfflinePlayer)p, (double)amt);
                    if (p.getInventory().firstEmpty() != -1) {
                        p.getInventory().setItem(p.getInventory().firstEmpty(), Money.createBankNote(amt));
                    }
                    p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("New Balance: ").append(ChatColor.GREEN).append((int)Main.econ.getBalance((OfflinePlayer)p)).append(" GEM(s)").toString());
                    p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1.0f, 1.2f);
                }
            }
            catch (NumberFormatException ex) {
                p.sendMessage(ChatColor.RED + "Please enter a NUMBER, the amount you'd like to WITHDRAW from your bank account. Or type 'cancel' to void the withdrawl.");
            }
        }
    }
    
    @EventHandler
    public void onInvClick(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.PAPER && e.getCurrentItem().getItemMeta().hasLore() && e.getCursor().getType() == Material.PAPER && e.getCursor().getItemMeta().hasLore()) {
            e.setCancelled(true);
            final int first = this.getGems(e.getCurrentItem());
            final int second = this.getGems(e.getCursor());
            final ItemStack gem = new ItemStack(Material.PAPER);
            final ItemMeta im = gem.getItemMeta();
            im.setDisplayName(ChatColor.GREEN + "Bank Note");
            im.setLore((List)Arrays.asList(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Value: ").append(ChatColor.WHITE).append(first + second).append(" Gems").toString(), ChatColor.GRAY + "Exchange at any bank for GEM(s)"));
            gem.setItemMeta(im);
            e.setCurrentItem(gem);
            e.setCursor((ItemStack)null);
            p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1.0f, 1.2f);
        }
        if (e.getInventory().getName().equals("Bank Chest (1/1)")) {
            if (e.getClick() == ClickType.RIGHT && e.getSlot() == 58) {
                e.setCancelled(true);
                p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("Current Balance: ").append(ChatColor.GREEN).append((int)Main.econ.getBalance(p.getName())).append(" GEM(s)").toString());
                for (int i = 0; i < this.withdraw.size(); ++i) {
                    this.withdraw.remove(p.getName());
                }
                this.withdraw.add(p.getName());
                p.sendMessage(ChatColor.GRAY + "Please enter the amount you'd like To CONVERT into a gem note. Alternatively, type " + ChatColor.RED + "'cancel'" + ChatColor.GRAY + " to void this operation.");
                new BukkitRunnable() {
                    public void run() {
                        p.closeInventory();
                    }
                }.runTaskLater(Main.plugin, 1L);
            }
            for (int i = 54; i < 63; ++i) {
                if (e.getSlot() == i) {
                    e.setCancelled(true);
                }
            }
            if (e.getSlotType() == InventoryType.SlotType.OUTSIDE) {
                return;
            }
            if (e.isShiftClick() && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.EMERALD) {
                if (e.getRawSlot() < 63) {
                    return;
                }
                final int amt = e.getCurrentItem().getAmount();
                e.setCancelled(true);
                Main.econ.depositPlayer(p.getName(), (double)amt);
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                e.getInventory().setItem(58, this.gemBalance((int)Main.econ.getBalance(p.getName())));
                p.updateInventory();
                p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("+").append(ChatColor.GREEN).append(amt).append(ChatColor.GREEN).append(ChatColor.BOLD).append("G").append(ChatColor.GREEN).append(", ").append(ChatColor.BOLD).append("New Balance: ").append(ChatColor.GREEN).append((int)Main.econ.getBalance(p.getName())).append(" GEM(s)").toString());
                e.setCurrentItem((ItemStack)null);
            }
            if (e.isShiftClick() && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.PAPER) {
                if (e.getRawSlot() < 63) {
                    return;
                }
                final int amt = this.getGems(e.getCurrentItem());
                e.setCancelled(true);
                Main.econ.depositPlayer(p.getName(), (double)amt);
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                e.getInventory().setItem(58, this.gemBalance((int)Main.econ.getBalance(p.getName())));
                p.updateInventory();
                p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("+").append(ChatColor.GREEN).append(amt).append(ChatColor.GREEN).append(ChatColor.BOLD).append("G").append(ChatColor.GREEN).append(", ").append(ChatColor.BOLD).append("New Balance: ").append(ChatColor.GREEN).append((int)Main.econ.getBalance(p.getName())).append(" GEM(s)").toString());
                e.setCurrentItem((ItemStack)null);
            }
            if (e.isShiftClick() && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.INK_SACK && e.getCurrentItem().getDurability() == 0) {
                if (e.getRawSlot() < 63) {
                    return;
                }
                final int amt = GemPouches.getCurrentValue(e.getCurrentItem());
                if (amt < 1) {
                    if (e.getInventory().firstEmpty() != -1) {
                        e.getInventory().setItem(e.getInventory().firstEmpty(), e.getCurrentItem());
                        e.setCurrentItem((ItemStack)null);
                    }
                    return;
                }
                e.setCancelled(true);
                Main.econ.depositPlayer(p.getName(), (double)amt);
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                e.getInventory().setItem(58, this.gemBalance((int)Main.econ.getBalance(p.getName())));
                p.updateInventory();
                p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("+").append(ChatColor.GREEN).append(amt).append(ChatColor.GREEN).append(ChatColor.BOLD).append("G").append(ChatColor.GREEN).append(", ").append(ChatColor.BOLD).append("New Balance: ").append(ChatColor.GREEN).append((int)Main.econ.getBalance(p.getName())).append(" GEM(s)").toString());
                GemPouches.setPouchBal(e.getCurrentItem(), 0);
            }
            if (!e.isShiftClick() && e.getCursor() != null && e.getCursor().getType() == Material.EMERALD) {
                if (e.getRawSlot() > 53) {
                    return;
                }
                final int amt = e.getCursor().getAmount();
                e.setCancelled(true);
                Main.econ.depositPlayer(p.getName(), (double)amt);
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                e.getInventory().setItem(58, this.gemBalance((int)Main.econ.getBalance(p.getName())));
                p.updateInventory();
                p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("+").append(ChatColor.GREEN).append(amt).append(ChatColor.GREEN).append(ChatColor.BOLD).append("G").append(ChatColor.GREEN).append(", ").append(ChatColor.BOLD).append("New Balance: ").append(ChatColor.GREEN).append((int)Main.econ.getBalance(p.getName())).append(" GEM(s)").toString());
                e.setCursor((ItemStack)null);
            }
            if (!e.isShiftClick() && e.getCursor() != null && e.getCursor().getType() == Material.PAPER) {
                if (e.getRawSlot() > 53) {
                    return;
                }
                final int amt = this.getGems(e.getCursor());
                e.setCancelled(true);
                Main.econ.depositPlayer(p.getName(), (double)amt);
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                e.getInventory().setItem(58, this.gemBalance((int)Main.econ.getBalance(p.getName())));
                p.updateInventory();
                p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("+").append(ChatColor.GREEN).append(amt).append(ChatColor.GREEN).append(ChatColor.BOLD).append("G").append(ChatColor.GREEN).append(", ").append(ChatColor.BOLD).append("New Balance: ").append(ChatColor.GREEN).append((int)Main.econ.getBalance(p.getName())).append(" GEM(s)").toString());
                e.setCursor((ItemStack)null);
            }
            if (!e.isShiftClick() && e.getCursor() != null && e.getCursor().getType() == Material.INK_SACK && e.getCursor().getDurability() == 0) {
                if (e.getRawSlot() > 53) {
                    return;
                }
                final int amt = GemPouches.getCurrentValue(e.getCursor());
                if (amt < 1) {
                    return;
                }
                e.setCancelled(true);
                Main.econ.depositPlayer(p.getName(), (double)amt);
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                e.getInventory().setItem(58, this.gemBalance((int)Main.econ.getBalance(p.getName())));
                p.updateInventory();
                p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("+").append(ChatColor.GREEN).append(amt).append(ChatColor.GREEN).append(ChatColor.BOLD).append("G").append(ChatColor.GREEN).append(", ").append(ChatColor.BOLD).append("New Balance: ").append(ChatColor.GREEN).append((int)Main.econ.getBalance(p.getName())).append(" GEM(s)").toString());
                GemPouches.setPouchBal(e.getCursor(), 0);
            }
        }
    }
    
    ItemStack gemBalance(final int amt) {
        final ItemStack is = new ItemStack(Material.EMERALD);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.GREEN).append(amt).append(ChatColor.GREEN).append(ChatColor.BOLD).append(" GEM(s)").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Right Click to create " + ChatColor.GREEN + "A GEM NOTE"));
        is.setItemMeta(im);
        return is;
    }
}
