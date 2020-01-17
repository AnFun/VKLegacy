package me.bpweber.practiceserver;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.HashMap;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public class Logout implements Listener, CommandExecutor
{
    static HashMap<String, Integer> logging;
    static HashMap<String, Location> loggingloc;
    static HashMap<String, Long> syncing;
    
    static {
        Logout.logging = new HashMap<String, Integer>();
        Logout.loggingloc = new HashMap<String, Location>();
        Logout.syncing = new HashMap<String, Long>();
    }
    
    public void onEnable() {
        Main.log.info("[Logout] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (Logout.logging.containsKey(p.getName())) {
                        if (Logout.logging.get(p.getName()) == 0) {
                            Logout.logging.remove(p.getName());
                            Logout.loggingloc.remove(p.getName());
                            if (Alignments.tagged.containsKey(p.getName())) {
                                Alignments.tagged.remove(p.getName());
                            }
                            p.saveData();
                            p.kickPlayer(String.valueOf(ChatColor.GREEN.toString()) + "You have safely logged out." + "\n\n" + ChatColor.GRAY.toString() + "Your player data has been synced.");
                        }
                        else {
                            p.sendMessage(ChatColor.RED + "Logging out in ... " + ChatColor.BOLD + Logout.logging.get(p.getName()) + "s");
                            Logout.logging.put(p.getName(), Logout.logging.get(p.getName()) - 1);
                        }
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 20L, 20L);
    }
    
    public void onDisable() {
        Main.log.info("[Logout] has been disabled.");
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("logout") && !Logout.logging.containsKey(p.getName())) {
                if (Alignments.isSafeZone(p.getLocation())) {
                    if (Alignments.tagged.containsKey(p.getName())) {
                        Alignments.tagged.remove(p.getName());
                    }
                    p.saveData();
                    p.kickPlayer(String.valueOf(ChatColor.GREEN.toString()) + "You have safely logged out." + "\n\n" + ChatColor.GRAY.toString() + "Your player data has been synced.");
                }
                else if (Alignments.tagged.containsKey(p.getName()) && System.currentTimeMillis() - Alignments.tagged.get(p.getName()) < 10000L) {
                    p.sendMessage(ChatColor.RED + "You will be " + ChatColor.BOLD + "LOGGED OUT" + ChatColor.RED + " of the game world shortly.");
                    Logout.logging.put(p.getName(), 10);
                    Logout.loggingloc.put(p.getName(), p.getLocation());
                }
                else {
                    p.sendMessage(ChatColor.RED + "You will be " + ChatColor.BOLD + "LOGGED OUT" + ChatColor.RED + " of the game world shortly.");
                    Logout.logging.put(p.getName(), 3);
                    Logout.loggingloc.put(p.getName(), p.getLocation());
                }
            }
            if (cmd.getName().equalsIgnoreCase("sync")) {
                if (Logout.syncing.containsKey(p.getName()) && System.currentTimeMillis() - Logout.syncing.get(p.getName()) < 10000L) {
                    p.sendMessage(ChatColor.RED + "You already have a recent sync request -- please wait a few seconds.");
                }
                else {
                    p.updateInventory();
                    p.teleport((Entity)p);
                    p.saveData();
                    p.sendMessage(ChatColor.GREEN + "Synced player data to " + ChatColor.UNDERLINE + "HIVE" + ChatColor.GREEN + " server.");
                    Logout.syncing.put(p.getName(), System.currentTimeMillis());
                }
            }
        }
        return false;
    }
    
    @EventHandler
    public void onCancelDamager(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            final Player p = (Player)e.getDamager();
            if (Logout.logging.containsKey(p.getName())) {
                Logout.logging.remove(p.getName());
                Logout.loggingloc.remove(p.getName());
                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Logout - CANCELLED").toString());
            }
        }
    }
    
    @EventHandler
    public void onCancelDamage(final EntityDamageEvent e) {
        if (e.getDamage() <= 0.0) {
            return;
        }
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            if (Logout.logging.containsKey(p.getName())) {
                Logout.logging.remove(p.getName());
                Logout.loggingloc.remove(p.getName());
                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Logout - CANCELLED").toString());
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        p.saveData();
        if (Logout.logging.containsKey(p.getName())) {
            Logout.logging.remove(p.getName());
            Logout.loggingloc.remove(p.getName());
        }
    }
    
    @EventHandler
    public void onCancelMove(final PlayerMoveEvent e) {
        final Player p = e.getPlayer();
        if (Logout.logging.containsKey(p.getName())) {
            final Location loc = Logout.loggingloc.get(p.getName());
            if (loc.distanceSquared(e.getTo()) >= 2.0) {
                Logout.logging.remove(p.getName());
                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Logout - CANCELLED").toString());
            }
        }
    }
}
