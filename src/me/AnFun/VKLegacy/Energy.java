package me.AnFun.VKLegacy;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Energy implements Listener {
  public void onEnable() {
    Main.log.info("[Energy] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    (new BukkitRunnable() {
        public void run() {
          for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            PlayerInventory i = p.getInventory();
            float amt = 100.0F;
            byte b;
            int j;
            ItemStack[] arrayOfItemStack;
            for (j = (arrayOfItemStack = i.getArmorContents()).length, b = 0; b < j; ) {
              ItemStack is = arrayOfItemStack[b];
              if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && 
                is.getItemMeta().hasLore()) {
                int added = Damage.getEnergy(is);
                int intel = 0;
                int addedint = Damage.getElem(is, "INT");
                intel += addedint;
                if (intel > 0)
                  added = (int)(added + Math.round(intel * 0.016D)); 
                amt += (added * 5);
              } 
              b++;
            } 
            if (Energy.getEnergy(p) < 100.0F && (
              !Energy.cd.containsKey(p.getName()) || (Energy.cd.containsKey(p.getName()) && 
              System.currentTimeMillis() - ((Long)Energy.cd.get(p.getName())).longValue() > 2000L)))
              Energy.setEnergy(p, Energy.getEnergy(p) + amt / 100.0F); 
            if (Energy.getEnergy(p) <= 0.0F)
              p.setSprinting(false); 
          } 
        }
      }).runTaskTimerAsynchronously(Main.plugin, 1L, 1L);
    (new BukkitRunnable() {
        public void run() {
          for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.isSprinting() && 
              !Alignments.isSafeZone(p.getLocation())) {
              float amt = 100.0F;
              amt += 85.0F;
              amt *= 4.0F;
              if (Energy.getEnergy(p) > 0.0F)
                Energy.setEnergy(p, Energy.getEnergy(p) - amt / 100.0F); 
              if (Energy.getEnergy(p) <= 0.0F) {
                Energy.setEnergy(p, 0.0F);
                Energy.cd.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
                p.setSprinting(false);
              } 
            } 
          } 
        }
      }).runTaskTimerAsynchronously(Main.plugin, 4L, 4L);
  }
  
  public void onDisable() {
    Main.log.info("[Energy] has been disabled.");
  }
  
  public static float getEnergy(Player p) {
    float energy = 0.0F;
    energy = p.getExp() * 100.0F;
    return energy;
  }
  
  public static void setEnergy(Player p, float energy) {
    if (energy > 100.0F)
      energy = 100.0F; 
    p.setExp(energy / 100.0F);
    p.setLevel((int)energy);
  }
  
  public static void removeEnergy(Player p, int amt) {
    if (Alignments.isSafeZone(p.getLocation()))
      return; 
    if (p.hasMetadata("lastenergy") && 
      System.currentTimeMillis() - ((MetadataValue)p.getMetadata("lastenergy").get(0)).asLong() < 100L)
      return; 
    p.setMetadata("lastenergy", (MetadataValue)new FixedMetadataValue(Main.plugin, Long.valueOf(System.currentTimeMillis())));
    setEnergy(p, getEnergy(p) - amt);
  }
  
  public static HashMap<String, Long> nodamage = new HashMap<>();
  
  public static HashMap<String, Long> cd = new HashMap<>();
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onEnergyUse(PlayerInteractEvent e) {
    if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
      Player p = e.getPlayer();
      if (nodamage.containsKey(p.getName()) && System.currentTimeMillis() - ((Long)nodamage.get(p.getName())).longValue() < 100L) {
        e.setUseItemInHand(Event.Result.DENY);
        e.setCancelled(true);
        return;
      } 
      if (getEnergy(p) > 0.0F) {
        int amt = 6;
        if (p.getItemInHand().getType() == Material.WOOD_SWORD) {
          amt = 7;
        } else if (p.getItemInHand().getType() == Material.WOOD_AXE || 
          p.getItemInHand().getType() == Material.WOOD_SPADE || 
          p.getItemInHand().getType() == Material.STONE_SWORD) {
          amt = 8;
        } else if (p.getItemInHand().getType() == Material.STONE_AXE || 
          p.getItemInHand().getType() == Material.STONE_SPADE || 
          p.getItemInHand().getType() == Material.IRON_SWORD) {
          amt = 9;
        } else if (p.getItemInHand().getType() == Material.IRON_AXE || 
          p.getItemInHand().getType() == Material.IRON_SPADE || 
          p.getItemInHand().getType() == Material.DIAMOND_SWORD) {
          amt = 10;
        } else if (p.getItemInHand().getType() == Material.DIAMOND_AXE || 
          p.getItemInHand().getType() == Material.DIAMOND_SPADE || 
          p.getItemInHand().getType() == Material.GOLD_SWORD) {
          amt = 11;
        } else if (p.getItemInHand().getType() == Material.GOLD_AXE || 
          p.getItemInHand().getType() == Material.GOLD_SPADE) {
          amt = 12;
        } 
        removeEnergy(p, amt);
      } 
      if (getEnergy(p) <= 0.0F) {
        setEnergy(p, 0.0F);
        cd.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
        p.playSound(p.getLocation(), Sound.WOLF_PANT, 10.0F, 1.5F);
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onEnergyUseDamage(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player && e.getEntity() instanceof org.bukkit.entity.LivingEntity) {
      Player p = (Player)e.getDamager();
      if (nodamage.containsKey(p.getName()) && System.currentTimeMillis() - ((Long)nodamage.get(p.getName())).longValue() < 100L) {
        e.setCancelled(true);
        e.setDamage(0.0D);
        return;
      } 
      nodamage.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
      if (getEnergy(p) > 0.0F) {
        if (p.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
          p.removePotionEffect(PotionEffectType.SLOW_DIGGING); 
        int amt = 6;
        if (p.getItemInHand().getType() == Material.WOOD_SWORD) {
          amt = 7;
        } else if (p.getItemInHand().getType() == Material.WOOD_AXE || 
          p.getItemInHand().getType() == Material.STONE_SWORD) {
          amt = 8;
        } else if (p.getItemInHand().getType() == Material.STONE_AXE || 
          p.getItemInHand().getType() == Material.IRON_SWORD) {
          amt = 9;
        } else if (p.getItemInHand().getType() == Material.IRON_AXE || 
          p.getItemInHand().getType() == Material.DIAMOND_SWORD) {
          amt = 10;
        } else if (p.getItemInHand().getType() == Material.DIAMOND_AXE || 
          p.getItemInHand().getType() == Material.GOLD_SWORD) {
          amt = 11;
        } else if (p.getItemInHand().getType() == Material.GOLD_AXE) {
          amt = 12;
        } 
        removeEnergy(p, amt);
      } 
      if (getEnergy(p) <= 0.0F) {
        setEnergy(p, 0.0F);
        cd.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
        p.playSound(p.getLocation(), Sound.WOLF_PANT, 10.0F, 1.5F);
      } 
    } 
  }
}
