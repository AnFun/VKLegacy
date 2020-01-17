package me.bpweber.practiceserver;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class Hearthstone implements Listener
{
    static HashMap<String, Integer> casting;
    static HashMap<String, Location> castingloc;
    
    static {
        Hearthstone.casting = new HashMap<String, Integer>();
        Hearthstone.castingloc = new HashMap<String, Location>();
    }
    
    public void onEnable() {
        Main.log.info("[Hearthstone] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (p.isOnline() && Hearthstone.casting.containsKey(p.getName())) {
                        if (Hearthstone.casting.get(p.getName()) == 0) {
                            p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1.0f, 1.0f);
                            Hearthstone.casting.remove(p.getName());
                            Hearthstone.castingloc.remove(p.getName());
                            p.eject();
                            p.teleport(TeleportBooks.Cyrennica);
                        }
                        else {
                            ParticleEffect.SPELL.display(0.0f, 0.0f, 0.0f, 0.5f, 80, p.getLocation().add(0.0, 0.15, 0.0), 20.0);
                            p.sendMessage(ChatColor.BOLD + "TELEPORTING" + ChatColor.WHITE + " ... " + Hearthstone.casting.get(p.getName()) + "s");
                            Hearthstone.casting.put(p.getName(), Hearthstone.casting.get(p.getName()) - 1);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
    }
    
    public void onDisable() {
        Main.log.info("[Hearthstone] has been disabled.");
    }
    
    public static ItemStack hearthstone() {
        final ItemStack is = new ItemStack(Material.QUARTZ);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.YELLOW).append(ChatColor.BOLD).append("Hearthstone").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Teleports you to your home town.", ChatColor.GRAY + "Talk to an Innkeeper to change your home town.", ChatColor.GREEN + "Location: Cyrennica"));
        is.setItemMeta(im);
        return is;
    }
    
    @EventHandler
    public void onRightClick(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && p.getItemInHand() != null && p.getItemInHand().equals((Object)hearthstone()) && !Hearthstone.casting.containsKey(p.getName()) && !Horses.mounting.containsKey(p.getName())) {
            if (Alignments.chaotic.containsKey(p.getName())) {
                p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " do this while chaotic!");
            }
            else {
                p.sendMessage(ChatColor.BOLD + "TELEPORTING" + ChatColor.WHITE + " - " + ChatColor.AQUA + "Cyrennica" + ChatColor.WHITE + " ... " + 10 + "s");
                Hearthstone.casting.put(p.getName(), 10);
                Hearthstone.castingloc.put(p.getName(), p.getLocation());
            }
        }
    }
    
    @EventHandler
    public void onCancelDamager(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            final Player p = (Player)e.getDamager();
            if (Hearthstone.casting.containsKey(p.getName())) {
                Hearthstone.casting.remove(p.getName());
                Hearthstone.castingloc.remove(p.getName());
                p.sendMessage(ChatColor.RED + "Hearthstone - " + ChatColor.BOLD + "CANCELLED");
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
            if (Hearthstone.casting.containsKey(p.getName())) {
                Hearthstone.casting.remove(p.getName());
                Hearthstone.castingloc.remove(p.getName());
                p.sendMessage(ChatColor.RED + "Hearthstone - " + ChatColor.BOLD + "CANCELLED");
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        if (Hearthstone.casting.containsKey(p.getName())) {
            Hearthstone.casting.remove(p.getName());
            Hearthstone.castingloc.remove(p.getName());
        }
    }
    
    @EventHandler
    public void onCancelMove(final PlayerMoveEvent e) {
        final Player p = e.getPlayer();
        if (Hearthstone.casting.containsKey(p.getName())) {
            final Location loc = Hearthstone.castingloc.get(p.getName());
            if (loc.distanceSquared(e.getTo()) >= 2.0) {
                Hearthstone.casting.remove(p.getName());
                p.sendMessage(ChatColor.RED + "Hearthstone - " + ChatColor.BOLD + "CANCELLED");
            }
        }
    }
}
