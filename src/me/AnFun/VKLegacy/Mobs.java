package me.AnFun.VKLegacy;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Mobs implements Listener {
  public static HashMap<LivingEntity, Integer> crit = new HashMap<>();
  
  static ConcurrentHashMap<Creature, Player> target = new ConcurrentHashMap<>();
  
  public void onEnable() {
    Main.log.info("[Mobs] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    (new BukkitRunnable() {
        public void run() {
          for (Entity ent : ((World)Bukkit.getWorlds().get(0)).getEntities()) {
            if (ent instanceof LivingEntity && !(ent instanceof Player)) {
              LivingEntity l = (LivingEntity)ent;
              if (Mobs.crit.containsKey(l) && 
                Mobs.isElite(l)) {
                int step = ((Integer)Mobs.crit.get(l)).intValue();
                if (step > 0) {
                  step--;
                  Mobs.crit.put(l, Integer.valueOf(step));
                  l.getWorld().playSound(l.getLocation(), Sound.CREEPER_HISS, 1.0F, 4.0F);
                  ParticleEffect.EXPLOSION_LARGE.display(0.0F, 0.0F, 0.0F, 0.3F, 40, 
                      l.getLocation().add(0.0D, 1.0D, 0.0D), 20.0D);
                } 
                if (step == 0) {
                  for (Entity e : l.getNearbyEntities(8.0D, 8.0D, 8.0D)) {
                    if (e instanceof Player) {
                      if (Listeners.mobd.containsKey(l.getUniqueId()))
                        Listeners.mobd.remove(l.getUniqueId()); 
                      Player p = (Player)e;
                      p.damage(1.0D, (Entity)l);
                      Vector v = p.getLocation().toVector().subtract(l.getLocation().toVector());
                      if (v.getX() != 0.0D || v.getY() != 0.0D || v.getZ() != 0.0D)
                        v.normalize(); 
                      p.setVelocity(v.multiply(3));
                    } 
                  } 
                  l.getWorld().playSound(l.getLocation(), Sound.EXPLODE, 1.0F, 0.5F);
                  ParticleEffect.EXPLOSION_HUGE.display(0.0F, 0.0F, 0.0F, 1.0F, 40, l.getLocation().add(0.0D, 1.0D, 0.0D), 
                      20.0D);
                  Mobs.crit.remove(l);
                  l.setCustomName(Mobs.generateOverheadBar((Entity)l, l.getHealth(), l.getMaxHealth(), 
                        Mobs.getMobTier(l), true));
                  l.setCustomNameVisible(true);
                  if (l.hasPotionEffect(PotionEffectType.SLOW)) {
                    l.removePotionEffect(PotionEffectType.SLOW);
                    if (l.getEquipment().getItemInHand() != null && 
                      l.getEquipment().getItemInHand().getType().name().contains("_HOE"))
                      l.addPotionEffect(
                          new PotionEffect(PotionEffectType.SLOW, 2147483647, 3), 
                          true); 
                  } 
                  if (l.hasPotionEffect(PotionEffectType.JUMP))
                    l.removePotionEffect(PotionEffectType.JUMP); 
                } 
              } 
              if (Listeners.named.containsKey(l.getUniqueId()) && 
                System.currentTimeMillis() - ((Long)Listeners.named.get(l.getUniqueId())).longValue() >= 5000L) {
                Listeners.named.remove(l.getUniqueId());
                String name = "";
                if (l.hasMetadata("name"))
                  name = ((MetadataValue)l.getMetadata("name").get(0)).asString(); 
                l.setCustomName(name);
              } 
            } 
          } 
        }
      }).runTaskTimer(Main.plugin, 20L, 20L);
    (new BukkitRunnable() {
        public void run() {
          for (Entity ent : ((World)Bukkit.getWorlds().get(0)).getEntities()) {
            if (ent instanceof LivingEntity && !(ent instanceof Player)) {
              LivingEntity l = (LivingEntity)ent;
              if (Mobs.crit.containsKey(l) && 
                !Mobs.isElite(l)) {
                int step = ((Integer)Mobs.crit.get(l)).intValue();
                if (step > 0) {
                  step--;
                  Mobs.crit.put(l, Integer.valueOf(step));
                  l.getWorld().playSound(l.getLocation(), Sound.PISTON_EXTEND, 1.0F, 2.0F);
                } 
                if (step == 0)
                  ParticleEffect.SPELL_WITCH.display(0.0F, 0.0F, 0.0F, 0.5F, 35, l.getLocation().add(0.0D, 1.0D, 0.0D), 
                      20.0D); 
              } 
            } 
          } 
        }
      }).runTaskTimer(Main.plugin, 20L, 10L);
    (new BukkitRunnable() {
        public void run() {
          for (Entity ent : ((World)Bukkit.getWorlds().get(0)).getEntities()) {
            if (ent instanceof Creature) {
              Creature c = (Creature)ent;
              if (c.getEquipment().getItemInHand() != null && 
                c.getEquipment().getItemInHand().getType().name().contains("_HOE")) {
                if (Mobs.isElite((LivingEntity)c) && Mobs.crit.containsKey(c))
                  return; 
                if (Mobs.isPlayerNearby(c) && 
                  c.getTarget() != null) {
                  LivingEntity livingEntity = c.getTarget();
                  if (c.getLocation().distanceSquared(livingEntity.getLocation()) > 9.0D) {
                    Projectile pj = null;
                    if (Mobs.getMobTier((LivingEntity)c) == 1)
                      pj = c.launchProjectile(Snowball.class); 
                    if (Mobs.getMobTier((LivingEntity)c) == 2)
                      pj = c.launchProjectile(SmallFireball.class); 
                    if (Mobs.getMobTier((LivingEntity)c) == 3) {
                      pj = c.launchProjectile(EnderPearl.class);
                      pj.setVelocity(pj.getVelocity().multiply(1.25D));
                    } 
                    if (Mobs.getMobTier((LivingEntity)c) == 4)
                      pj = c.launchProjectile(WitherSkull.class); 
                    if (Mobs.getMobTier((LivingEntity)c) == 5) {
                      pj = c.launchProjectile(LargeFireball.class);
                      pj.setVelocity(pj.getVelocity().multiply(2));
                    } 
                  } 
                } 
              } 
            } 
          } 
        }
      }).runTaskTimer(Main.plugin, 20L, 25L);
  }
  
  public void onDisable() {
    Main.log.info("[Mobs] has been disabled.");
  }
  
  static boolean isPlayerNearby(Creature c) {
    for (Entity ent : c.getNearbyEntities(12.0D, 12.0D, 12.0D)) {
      if (ent != null && ent instanceof Player && 
        ent == c.getTarget())
        return true; 
    } 
    return false;
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onHit(ProjectileHitEvent e) {
    Projectile pj = e.getEntity();
    if (pj.getShooter() != null && pj.getShooter() instanceof LivingEntity && 
      !(pj.getShooter() instanceof Player)) {
      LivingEntity d = (LivingEntity)pj.getShooter();
      Player target = null;
      for (Entity ent : pj.getNearbyEntities(2.0D, 1.5D, 2.0D)) {
        if (ent instanceof Player)
          target = (Player)ent; 
      } 
      if (target != null) {
        if (pj instanceof SmallFireball)
          e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.EXPLODE, 1.0F, 1.0F); 
        if (pj instanceof EnderPearl)
          e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENDERMAN_TELEPORT, 2.0F, 1.5F); 
        target.damage(1.0D, (Entity)d);
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntitySpawn(CreatureSpawnEvent e) {
    e.getEntity().getEquipment().clear();
  }
  
  @EventHandler
  public void onCubeSplit(SlimeSplitEvent e) {
    e.setCancelled(true);
  }
  
  @EventHandler
  public void onCombust(EntityCombustEvent e) {
    if (!(e.getEntity() instanceof Player))
      e.setCancelled(true); 
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onEntityDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player) && 
      !e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
      e.setCancelled(true);
      e.setDamage(0.0D);
    } 
  }
  
  public static int getMobTier(LivingEntity e) {
    if (e.getEquipment().getItemInHand() != null) {
      if (e.getEquipment().getItemInHand().getType().name().contains("WOOD_"))
        return 1; 
      if (e.getEquipment().getItemInHand().getType().name().contains("STONE_"))
        return 2; 
      if (e.getEquipment().getItemInHand().getType().name().contains("IRON_"))
        return 3; 
      if (e.getEquipment().getItemInHand().getType().name().contains("DIAMOND_"))
        return 4; 
      if (e.getEquipment().getItemInHand().getType().name().contains("GOLD_"))
        return 5; 
    } 
    return 0;
  }
  
  public static int getPlayerTier(Player e) {
    int tier = 0;
    byte b;
    int i;
    ItemStack[] arrayOfItemStack;
    for (i = (arrayOfItemStack = e.getInventory().getArmorContents()).length, b = 0; b < i; ) {
      ItemStack is = arrayOfItemStack[b];
      if (is != null && is.getType() != Material.AIR) {
        if (is.getType().name().contains("LEATHER_") && 
          1 > tier)
          tier = 1; 
        if (is.getType().name().contains("CHAINMAIL_") && 
          2 > tier)
          tier = 2; 
        if (is.getType().name().contains("IRON_") && 
          3 > tier)
          tier = 3; 
        if (is.getType().name().contains("DIAMOND_") && 
          4 > tier)
          tier = 4; 
        if (is.getType().name().contains("GOLD_") && 
          5 > tier)
          tier = 5; 
      } 
      b++;
    } 
    return tier;
  }
  
  public static HashMap<UUID, Long> sound = new HashMap<>();
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onKnockback(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity) {
      LivingEntity l = (LivingEntity)e.getEntity();
      if (e.getDamage() <= 0.0D)
        return; 
      if (!sound.containsKey(l.getUniqueId()) || (sound.containsKey(l.getUniqueId()) && 
        System.currentTimeMillis() - ((Long)sound.get(l.getUniqueId())).longValue() > 500L)) {
        sound.put(l.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
        if (e.getEntity() instanceof org.bukkit.entity.Skeleton) {
          if (e.getDamage() >= l.getHealth())
            l.getWorld().playSound(l.getLocation(), Sound.SKELETON_DEATH, 1.0F, 1.0F); 
          l.getWorld().playSound(l.getLocation(), Sound.SKELETON_HURT, 1.0F, 1.0F);
        } 
        if (e.getEntity() instanceof org.bukkit.entity.Zombie) {
          if (e.getDamage() >= l.getHealth())
            l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_DEATH, 1.0F, 1.0F); 
          l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_HURT, 1.0F, 1.0F);
        } 
        if ((e.getEntity() instanceof org.bukkit.entity.Spider || e.getEntity() instanceof org.bukkit.entity.CaveSpider) && 
          e.getDamage() >= l.getHealth())
          l.getWorld().playSound(l.getLocation(), Sound.SPIDER_DEATH, 1.0F, 1.0F); 
        if (e.getEntity() instanceof org.bukkit.entity.PigZombie) {
          if (e.getDamage() >= l.getHealth())
            l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_PIG_DEATH, 1.0F, 1.0F); 
          l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_PIG_HURT, 1.0F, 1.0F);
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onEntityTarget(EntityTargetEvent e) {
    if (e.getReason() == EntityTargetEvent.TargetReason.CLOSEST_PLAYER && 
      e.getTarget() instanceof Player && e.getEntity() instanceof Creature) {
      Creature l = (Creature)e.getEntity();
      Player p = (Player)e.getTarget();
      if (l.getLocation().distance(p.getLocation()) > 15.0D) {
        e.setCancelled(true);
        e.setTarget(null);
        return;
      } 
      if (p.hasMetadata("NPC") || p.getPlayerListName() == " ") {
        e.setCancelled(true);
        e.setTarget(null);
        return;
      } 
      if (getPlayerTier(p) - getMobTier((LivingEntity)l) > 2) {
        e.setCancelled(true);
        e.setTarget(null);
        return;
      } 
      if (l.hasPotionEffect(PotionEffectType.SLOW)) {
        l.removePotionEffect(PotionEffectType.SLOW);
        if (l.getEquipment().getItemInHand() != null && 
          l.getEquipment().getItemInHand().getType().name().contains("_HOE"))
          l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147483647, 2), true); 
      } 
      if (l.hasPotionEffect(PotionEffectType.JUMP))
        l.removePotionEffect(PotionEffectType.JUMP); 
      target.put(l, p);
      return;
    } 
    e.setTarget(null);
    e.setCancelled(true);
  }
  
  @EventHandler
  public void onEntityTargetLastHit(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Creature && e.getDamager() instanceof Player) {
      Creature c = (Creature)e.getEntity();
      Player p = (Player)e.getDamager();
      if (target.containsKey(c) && target.get(c) != null) {
        if (p.getLocation().distanceSquared(c.getLocation()) < ((Player)target.get(c)).getLocation()
          .distanceSquared(c.getLocation())) {
          c.setTarget((LivingEntity)p);
          target.put(c, p);
        } 
      } else {
        c.setTarget((LivingEntity)p);
        target.put(c, p);
      } 
    } 
  }
  
  public static boolean isElite(LivingEntity e) {
    if (e.getEquipment().getItemInHand() != null && e.getEquipment().getItemInHand().getType() != Material.AIR && 
      e.getEquipment().getItemInHand().getItemMeta().hasEnchants())
      return true; 
    return false;
  }
  
  public static int getBarLength(int tier) {
    if (tier == 1)
      return 25; 
    if (tier == 2)
      return 30; 
    if (tier == 3)
      return 35; 
    if (tier == 4)
      return 40; 
    if (tier == 5)
      return 50; 
    return 25;
  }
  
  public static String generateOverheadBar(Entity ent, double cur_hp, double max_hp, int tier, boolean elite) {
    int max_bar = getBarLength(tier);
    ChatColor cc = null;
    DecimalFormatSymbols HpDot = new DecimalFormatSymbols(Locale.GERMAN);
    HpDot.setDecimalSeparator('.');
    HpDot.setGroupingSeparator(',');
    DecimalFormat df = new DecimalFormat("##.#", HpDot);
    double percent_hp = Math.round(100.0D * Double.parseDouble(df.format(cur_hp / max_hp)));
    if (percent_hp <= 0.0D && cur_hp > 0.0D)
      percent_hp = 1.0D; 
    double percent_interval = 100.0D / max_bar;
    int bar_count = 0;
    cc = ChatColor.GREEN;
    if (percent_hp <= 45.0D)
      cc = ChatColor.YELLOW; 
    if (percent_hp <= 20.0D)
      cc = ChatColor.RED; 
    if (crit.containsKey(ent) && cur_hp > 0.0D)
      cc = ChatColor.LIGHT_PURPLE; 
    String return_string = cc + ChatColor.BOLD.toString() + "+ ChatColor.RESET.toString() + cc.toString();
    if (elite)
      return_string = String.valueOf(return_string) + ChatColor.BOLD.toString(); 
    while (percent_hp > 0.0D && bar_count < max_bar) {
      percent_hp -= percent_interval;
      bar_count++;
      return_string = String.valueOf(return_string) + "|";
    } 
    return_string = String.valueOf(return_string) + ChatColor.BLACK.toString();
    if (elite)
      return_string = String.valueOf(return_string) + ChatColor.BOLD.toString(); 
    while (bar_count < max_bar) {
      return_string = String.valueOf(return_string) + "|";
      bar_count++;
    } 
    return_string = String.valueOf(return_string) + cc + ChatColor.BOLD.toString() + ";
    return return_string;
  }
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onMobDeath(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player)) {
      LivingEntity s = (LivingEntity)e.getEntity();
      if (e.getDamage() >= s.getHealth() && 
        crit.containsKey(s)) {
        crit.remove(s);
        String mname = "";
        if (s.getEquipment().getItemInHand() != null && 
          s.getEquipment().getItemInHand().getType() != Material.AIR) {
          mname = generateOverheadBar((Entity)s, 0.0D, s.getMaxHealth(), getMobTier(s), isElite(s));
          s.setCustomName(mname);
        } 
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onCrit(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player) && 
      e.getDamager() instanceof Player) {
      if (e.getDamage() <= 0.0D)
        return; 
      LivingEntity s = (LivingEntity)e.getEntity();
      Random random = new Random();
      int rcrt = random.nextInt(100) + 1;
      if (!crit.containsKey(s) && ((
        getMobTier(s) == 1 && rcrt <= 5) || (getMobTier(s) == 2 && rcrt <= 7) || (
        getMobTier(s) == 3 && rcrt <= 10) || (getMobTier(s) == 4 && rcrt <= 13) || (
        getMobTier(s) == 5 && rcrt <= 20))) {
        crit.put(s, Integer.valueOf(4));
        if (isElite(s)) {
          s.getWorld().playSound(s.getLocation(), Sound.CREEPER_HISS, 1.0F, 4.0F);
          double max = s.getMaxHealth();
          double hp = s.getHealth() - e.getDamage();
          s.setCustomName(generateOverheadBar((Entity)s, hp, max, getMobTier(s), isElite(s)));
          s.setCustomNameVisible(true);
          Listeners.named.put(s.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
          s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147483647, 10), true);
          s.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2147483647, 127), true);
        } else {
          s.getWorld().playSound(s.getLocation(), Sound.PISTON_EXTEND, 1.0F, 2.0F);
          double max = s.getMaxHealth();
          double hp = s.getHealth() - e.getDamage();
          s.setCustomName(generateOverheadBar((Entity)s, hp, max, getMobTier(s), isElite(s)));
          s.setCustomNameVisible(true);
          Listeners.named.put(s.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onSafeSpot(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player) && 
      e.getDamager() instanceof Player) {
      if (e.getDamage() <= 0.0D)
        return; 
      LivingEntity s = (LivingEntity)e.getEntity();
      Player p = (Player)e.getDamager();
      Random random = new Random();
      int rcrt = random.nextInt(10) + 1;
      if (rcrt == 1 && 
        p.getLocation().getY() - s.getLocation().getY() > 1.0D && 
        p.getLocation().distance(s.getLocation()) <= 6.0D)
        s.teleport(p.getLocation().add(0.0D, 0.25D, 0.0D)); 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onMobHitMob(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof LivingEntity && !(e.getDamager() instanceof Player) && 
      !(e.getEntity() instanceof Player)) {
      e.setCancelled(true);
      e.setDamage(0.0D);
    } 
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onMobHit(EntityDamageByEntityEvent e) {
    if (e.getDamage() <= 0.0D)
      return; 
    if (e.getDamager() instanceof LivingEntity && !(e.getDamager() instanceof Player) && 
      e.getEntity() instanceof Player) {
      LivingEntity s = (LivingEntity)e.getDamager();
      Player p = (Player)e.getEntity();
      Random random = new Random();
      int dmg = 1;
      if (s.getEquipment().getItemInHand() != null && 
        s.getEquipment().getItemInHand().getType() != Material.AIR) {
        int min = ((Integer)Damage.getDamageRange(s.getEquipment().getItemInHand()).get(0)).intValue();
        int max = ((Integer)Damage.getDamageRange(s.getEquipment().getItemInHand()).get(1)).intValue();
        dmg = random.nextInt(max - min + 1) + min;
      } 
      if (crit.containsKey(s) && ((Integer)crit.get(s)).intValue() == 0) {
        if (isElite(s)) {
          dmg *= 4;
        } else {
          dmg *= 3;
        } 
        if (!isElite(s))
          crit.remove(s); 
        p.playSound(p.getLocation(), Sound.EXPLODE, 1.0F, 0.3F);
        double max = s.getMaxHealth();
        double hp = s.getHealth() - e.getDamage();
        s.setCustomName(generateOverheadBar((Entity)s, hp, max, getMobTier(s), isElite(s)));
        s.setCustomNameVisible(true);
        Listeners.named.put(s.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
      } 
      if (e.getDamage() <= 0.0D)
        return; 
      if (s.getEquipment().getItemInHand().getType().name().contains("WOOD_"))
        if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
          dmg = (int)(dmg * 2.5D);
        } else {
          dmg = (int)(dmg * 0.8D);
        }  
      if (s.getEquipment().getItemInHand().getType().name().contains("STONE_"))
        if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
          dmg = (int)(dmg * 2.5D);
        } else {
          dmg = (int)(dmg * 0.9D);
        }  
      if (s.getEquipment().getItemInHand().getType().name().contains("IRON_"))
        if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
          dmg *= 3;
        } else {
          dmg = (int)(dmg * 1.2D);
        }  
      if (s.getEquipment().getItemInHand().getType().name().contains("DIAMOND_"))
        if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
          dmg *= 5;
        } else {
          dmg = (int)(dmg * 1.4D);
        }  
      if (s.getEquipment().getItemInHand().getType().name().contains("GOLD_"))
        if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
          dmg *= 7;
        } else {
          dmg *= 2;
        }  
      if (s instanceof org.bukkit.entity.MagmaCube)
        dmg = (int)(dmg * 0.5D); 
      if (dmg < 1)
        dmg = 1; 
      e.setDamage(dmg);
    } 
  }
}
