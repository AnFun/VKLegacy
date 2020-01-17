package me.AnFun.VKLegacy;

import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Hearthstone implements Listener {
  static HashMap<String, Integer> casting = new HashMap<>();
  
  static HashMap<String, Location> castingloc = new HashMap<>();
  
  public void onEnable() {
    Main.log.info("[Hearthstone] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    (new BukkitRunnable() {
        public void run() {
          for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.isOnline() && 
              Hearthstone.casting.containsKey(p.getName())) {
              if (((Integer)Hearthstone.casting.get(p.getName())).intValue() == 0) {
                p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1.0F, 1.0F);
                Hearthstone.casting.remove(p.getName());
                Hearthstone.castingloc.remove(p.getName());
                p.eject();
                p.teleport(TeleportBooks.Cyrennica);
                continue;
              } 
              ParticleEffect.SPELL.display(0.0F, 0.0F, 0.0F, 0.5F, 80, p.getLocation().add(0.0D, 0.15D, 0.0D), 20.0D);
              p.sendMessage(ChatColor.BOLD + "TELEPORTING" + ChatColor.WHITE + " ... " + 
                  Hearthstone.casting.get(p.getName()) + "s");
              Hearthstone.casting.put(p.getName(), Integer.valueOf(((Integer)Hearthstone.casting.get(p.getName())).intValue() - 1));
            } 
          } 
        }
      }).runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
  }
  
  public void onDisable() {
    Main.log.info("[Hearthstone] has been disabled.");
  }
  
  public static ItemStack hearthstone() {
    ItemStack is = new ItemStack(Material.QUARTZ);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD + "Hearthstone");
    im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Teleports you to your home town.", 
            ChatColor.GRAY + "Talk to an Innkeeper to change your home town.", 
            ChatColor.GREEN + "Location: Cyrennica" }));
    is.setItemMeta(im);
    return is;
  }
  
  @EventHandler
  public void onRightClick(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && 
      p.getItemInHand() != null && p.getItemInHand().equals(hearthstone()) && 
      !casting.containsKey(p.getName()) && 
      !Horses.mounting.containsKey(p.getName()))
      if (Alignments.chaotic.containsKey(p.getName())) {
        p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + 
            " do this while chaotic!");
      } else {
        p.sendMessage(ChatColor.BOLD + "TELEPORTING" + ChatColor.WHITE + " - " + ChatColor.AQUA + 
            "Cyrennica" + ChatColor.WHITE + " ... " + '\n' + "s");
        casting.put(p.getName(), Integer.valueOf(10));
        castingloc.put(p.getName(), p.getLocation());
      }  
  }
  
  @EventHandler
  public void onCancelDamager(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player && e.getEntity() instanceof org.bukkit.entity.LivingEntity) {
      Player p = (Player)e.getDamager();
      if (casting.containsKey(p.getName())) {
        casting.remove(p.getName());
        castingloc.remove(p.getName());
        p.sendMessage(ChatColor.RED + "Hearthstone - " + ChatColor.BOLD + "CANCELLED");
      } 
    } 
  }
  
  @EventHandler
  public void onCancelDamage(EntityDamageEvent e) {
    if (e.getDamage() <= 0.0D)
      return; 
    if (e.getEntity() instanceof Player) {
      Player p = (Player)e.getEntity();
      if (casting.containsKey(p.getName())) {
        casting.remove(p.getName());
        castingloc.remove(p.getName());
        p.sendMessage(ChatColor.RED + "Hearthstone - " + ChatColor.BOLD + "CANCELLED");
      } 
    } 
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    if (casting.containsKey(p.getName())) {
      casting.remove(p.getName());
      castingloc.remove(p.getName());
    } 
  }
  
  @EventHandler
  public void onCancelMove(PlayerMoveEvent e) {
    Player p = e.getPlayer();
    if (casting.containsKey(p.getName())) {
      Location loc = castingloc.get(p.getName());
      if (loc.distanceSquared(e.getTo()) >= 2.0D) {
        casting.remove(p.getName());
        p.sendMessage(ChatColor.RED + "Hearthstone - " + ChatColor.BOLD + "CANCELLED");
      } 
    } 
  }
}
