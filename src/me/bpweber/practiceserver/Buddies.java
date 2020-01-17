package me.bpweber.practiceserver;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Iterator;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public class Buddies implements Listener, CommandExecutor
{
    public static HashMap<String, ArrayList<String>> buddies;
    
    static {
        Buddies.buddies = new HashMap<String, ArrayList<String>>();
    }
    
    public void onEnable() {
        Main.log.info("[Buddies] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        final File file = new File(Main.plugin.getDataFolder(), "buddies.yml");
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
            final ArrayList<String> buddy = new ArrayList<String>();
            for (final String t : config.getStringList(p)) {
                buddy.add(t);
            }
            Buddies.buddies.put(p, buddy);
        }
    }
    
    public void onDisable() {
        Main.log.info("[Buddies] has been disabled.");
        final File file = new File(Main.plugin.getDataFolder(), "buddies.yml");
        final YamlConfiguration config = new YamlConfiguration();
        for (final String s : Buddies.buddies.keySet()) {
            config.set(s, (Object)Buddies.buddies.get(s));
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
            if (cmd.getName().equalsIgnoreCase("add")) {
                if (args.length > 0) {
                    final ArrayList<String> buddy = getBuddies(p.getName());
                    if (!buddy.contains(args[0].toLowerCase())) {
                        if (args[0].equalsIgnoreCase(p.getName())) {
                            p.sendMessage(ChatColor.YELLOW + "You can't add yourself to your buddy list!");
                        }
                        else {
                            buddy.add(args[0].toLowerCase());
                            p.sendMessage(ChatColor.GREEN + "You've added " + ChatColor.BOLD + args[0] + ChatColor.GREEN + " to your BUDDY list.");
                            Buddies.buddies.put(p.getName(), buddy);
                        }
                    }
                    else {
                        p.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append(ChatColor.BOLD).append(args[0]).append(ChatColor.YELLOW).append(" is already on your BUDDY LIST.").toString());
                    }
                }
                else {
                    p.sendMessage(ChatColor.RED + "Incorrect Syntax - " + ChatColor.BOLD + "/add <PLAYER>");
                }
            }
            if (cmd.getName().equalsIgnoreCase("del") || cmd.getName().equalsIgnoreCase("delete")) {
                if (args.length > 0) {
                    final ArrayList<String> buddy = getBuddies(p.getName());
                    if (buddy.contains(args[0].toLowerCase())) {
                        buddy.remove(args[0].toLowerCase());
                        p.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.YELLOW + " has been removed from your BUDDY list.");
                        Buddies.buddies.put(p.getName(), buddy);
                    }
                    else {
                        p.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.YELLOW + " is not on any of your social lists.");
                    }
                }
                else {
                    p.sendMessage(ChatColor.RED + "Incorrect Syntax - " + ChatColor.BOLD + "/delete <PLAYER>");
                }
            }
        }
        return true;
    }
    
    public static ArrayList<String> getBuddies(final String s) {
        if (Buddies.buddies.containsKey(s)) {
            return Buddies.buddies.get(s);
        }
        return new ArrayList<String>();
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        if (p.isOp()) {
            return;
        }
        for (final Player pl : Bukkit.getServer().getOnlinePlayers()) {
            if (Buddies.buddies.containsKey(pl.getName())) {
                final ArrayList<String> buddy = getBuddies(pl.getName());
                if (!buddy.contains(p.getName().toLowerCase())) {
                    continue;
                }
                pl.sendMessage(ChatColor.YELLOW + p.getName() + " has joined this server.");
                pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 2.0f, 1.2f);
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        if (p.isOp()) {
            return;
        }
        for (final Player pl : Bukkit.getServer().getOnlinePlayers()) {
            if (Buddies.buddies.containsKey(pl.getName())) {
                final ArrayList<String> buddy = getBuddies(pl.getName());
                if (!buddy.contains(p.getName().toLowerCase())) {
                    continue;
                }
                pl.sendMessage(ChatColor.YELLOW + p.getName() + " has logged out.");
                pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 2.0f, 0.5f);
            }
        }
    }
}
