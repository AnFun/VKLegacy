package me.AnFun.VKLegacy;

import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public class Toggles implements Listener, CommandExecutor
{
    public static HashMap<String, ArrayList<String>> toggles;
    
    static {
        Toggles.toggles = new HashMap<String, ArrayList<String>>();
    }
    
    public void onEnable() {
        Main.log.info("[Toggles] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        final File file = new File(Main.plugin.getDataFolder(), "toggles.yml");
        final YamlConfiguration config = new YamlConfiguration();
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            config.load(file);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        for (final String p : config.getKeys(false)) {
            final ArrayList<String> toggle = new ArrayList<String>();
            for (final String t : config.getStringList(p)) {
                toggle.add(t);
            }
            Toggles.toggles.put(p, toggle);
        }
    }
    
    public void onDisable() {
        Main.log.info("[Toggles] has been disabled.");
        final File file = new File(Main.plugin.getDataFolder(), "toggles.yml");
        final YamlConfiguration config = new YamlConfiguration();
        for (final String s : Toggles.toggles.keySet()) {
            config.set(s, (Object)Toggles.toggles.get(s));
        }
        try {
            config.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("toggle")) {
                p.openInventory(getToggleMenu(p));
            }
            if (cmd.getName().equalsIgnoreCase("toggledebug")) {
                final ArrayList<String> toggle = getToggles(p.getName());
                if (toggle.contains("debug")) {
                    toggle.remove("debug");
                    Toggles.toggles.put(p.getName(), toggle);
                    p.sendMessage(ChatColor.RED + "Debug Messages - " + ChatColor.BOLD + "DISABLED");
                }
                else {
                    toggle.add("debug");
                    Toggles.toggles.put(p.getName(), toggle);
                    p.sendMessage(ChatColor.GREEN + "Debug Messages - " + ChatColor.BOLD + "ENABLED");
                }
            }
            if (cmd.getName().equalsIgnoreCase("togglepvp")) {
                final ArrayList<String> toggle = getToggles(p.getName());
                if (toggle.contains("pvp")) {
                    toggle.remove("pvp");
                    Toggles.toggles.put(p.getName(), toggle);
                    p.sendMessage(ChatColor.GREEN + "Outgoing PVP Damage - " + ChatColor.BOLD + "ENABLED");
                }
                else {
                    toggle.add("pvp");
                    Toggles.toggles.put(p.getName(), toggle);
                    p.sendMessage(ChatColor.RED + "Outgoing PVP Damage - " + ChatColor.BOLD + "DISABLED");
                }
            }
            if (cmd.getName().equalsIgnoreCase("togglechaos")) {
                final ArrayList<String> toggle = getToggles(p.getName());
                if (toggle.contains("chaos")) {
                    toggle.remove("chaos");
                    Toggles.toggles.put(p.getName(), toggle);
                    p.sendMessage(ChatColor.RED + "Anti-Chaotic - " + ChatColor.BOLD + "DISABLED");
                }
                else {
                    toggle.add("chaos");
                    Toggles.toggles.put(p.getName(), toggle);
                    p.sendMessage(ChatColor.GREEN + "Anti-Chaotic - " + ChatColor.BOLD + "ENABLED");
                }
            }
            if (cmd.getName().equalsIgnoreCase("toggleff")) {
                final ArrayList<String> toggle = getToggles(p.getName());
                if (toggle.contains("ff")) {
                    toggle.remove("ff");
                    Toggles.toggles.put(p.getName(), toggle);
                    p.sendMessage(ChatColor.RED + "Friendly Fire - " + ChatColor.BOLD + "DISABLED");
                }
                else {
                    toggle.add("ff");
                    Toggles.toggles.put(p.getName(), toggle);
                    p.sendMessage(ChatColor.GREEN + "Friendly Fire - " + ChatColor.BOLD + "ENABLED");
                }
            }
        }
        return true;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNoDamageToggle(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            final Player p = (Player)e.getDamager();
            final Player pp = (Player)e.getEntity();
            if (e.getDamage() <= 0.0) {
                return;
            }
            final ArrayList<String> gettoggles = getToggles(p.getName());
            final ArrayList<String> buddies = Buddies.getBuddies(p.getName());
            if (buddies.contains(pp.getName().toLowerCase()) && !gettoggles.contains("ff")) {
                e.setDamage(0.0);
                e.setCancelled(true);
                return;
            }
            if (Parties.arePartyMembers(p, pp)) {
                e.setDamage(0.0);
                e.setCancelled(true);
                return;
            }
            if (gettoggles.contains("pvp")) {
                e.setDamage(0.0);
                e.setCancelled(true);
                return;
            }
            if (!Alignments.neutral.containsKey(pp.getName()) && !Alignments.chaotic.containsKey(pp.getName()) && gettoggles.contains("chaos")) {
                e.setDamage(0.0);
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onClickToggle(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        if (p.getOpenInventory().getTopInventory().getName().equals("Toggle Menu")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getCurrentItem().getItemMeta().hasDisplayName() && e.getCurrentItem().getItemMeta().getDisplayName().contains("/toggle")) {
                String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                name = name.substring(1, name.length());
                p.performCommand(name);
                final boolean on = e.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.RED.toString());
                e.setCurrentItem(getToggleButton(name, on));
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 0.5f);
            }
        }
    }
    
    public static ArrayList<String> getToggles(final String s) {
        if (Toggles.toggles.containsKey(s)) {
            return Toggles.toggles.get(s);
        }
        return new ArrayList<String>();
    }
    
    public static Inventory getToggleMenu(final Player p) {
        final ArrayList<String> toggles = getToggles(p.getName());
        final Inventory inv = Bukkit.getServer().createInventory((InventoryHolder)null, 9, "Toggle Menu");
        if (toggles.contains("pvp")) {
            inv.setItem(0, getToggleButton("togglepvp", true));
        }
        else {
            inv.setItem(0, getToggleButton("togglepvp", false));
        }
        if (toggles.contains("chaos")) {
            inv.setItem(1, getToggleButton("togglechaos", true));
        }
        else {
            inv.setItem(1, getToggleButton("togglechaos", false));
        }
        if (toggles.contains("ff")) {
            inv.setItem(2, getToggleButton("toggleff", true));
        }
        else {
            inv.setItem(2, getToggleButton("toggleff", false));
        }
        if (toggles.contains("debug")) {
            inv.setItem(3, getToggleButton("toggledebug", true));
        }
        else {
            inv.setItem(3, getToggleButton("toggledebug", false));
        }
        return inv;
    }
    
    public static ItemStack getToggleButton(final String s, final boolean on) {
        final ItemStack is = new ItemStack(Material.INK_SACK);
        final ItemMeta im = is.getItemMeta();
        ChatColor cc = null;
        if (on) {
            is.setDurability((short)10);
            cc = ChatColor.GREEN;
        }
        else {
            is.setDurability((short)8);
            cc = ChatColor.RED;
        }
        im.setDisplayName(cc + "/" + s);
        im.setLore((List)Arrays.asList(getToggleDescription(s)));
        is.setItemMeta(im);
        return is;
    }
    
    public static String getToggleDescription(final String toggle) {
        String desc = ChatColor.GRAY.toString();
        if (toggle.equalsIgnoreCase("toggledebug")) {
            desc = String.valueOf(desc) + "Toggles displaying combat debug messages.";
        }
        if (toggle.equalsIgnoreCase("toggleff")) {
            desc = String.valueOf(desc) + "Toggles friendly-fire between buddies.";
        }
        if (toggle.equalsIgnoreCase("togglechaos")) {
            desc = String.valueOf(desc) + "Toggles killing blows on lawful players (anti-chaotic).";
        }
        if (toggle.equalsIgnoreCase("togglepvp")) {
            desc = String.valueOf(desc) + "Toggles all outgoing PvP damage (anti-neutral).";
        }
        return desc;
    }
}
