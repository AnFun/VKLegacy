package me.AnFun.VKLegacy;

import org.bukkit.entity.MagmaCube;
import java.util.Random;
import org.bukkit.ChatColor;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.entity.Creature;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.LivingEntity;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class Mobs implements Listener
{
    public static HashMap<LivingEntity, Integer> crit;
    static ConcurrentHashMap<Creature, Player> target;
    public static HashMap<UUID, Long> sound;
    
    static {
        Mobs.crit = new HashMap<LivingEntity, Integer>();
        Mobs.target = new ConcurrentHashMap<Creature, Player>();
        Mobs.sound = new HashMap<UUID, Long>();
    }
    
    public void onEnable() {
        Main.log.info("[Mobs] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Entity ent : Bukkit.getWorlds().get(0).getEntities()) {
                    if (ent instanceof LivingEntity && !(ent instanceof Player)) {
                        final LivingEntity l = (LivingEntity)ent;
                        if (Mobs.crit.containsKey(l) && Mobs.isElite(l)) {
                            int step = Mobs.crit.get(l);
                            if (step > 0) {
                                --step;
                                Mobs.crit.put(l, step);
                                l.getWorld().playSound(l.getLocation(), Sound.CREEPER_HISS, 1.0f, 4.0f);
                                ParticleEffect.EXPLOSION_LARGE.display(0.0f, 0.0f, 0.0f, 0.3f, 40, l.getLocation().add(0.0, 1.0, 0.0), 20.0);
                            }
                            if (step == 0) {
                                for (final Entity e : l.getNearbyEntities(8.0, 8.0, 8.0)) {
                                    if (e instanceof Player) {
                                        if (Listeners.mobd.containsKey(l.getUniqueId())) {
                                            Listeners.mobd.remove(l.getUniqueId());
                                        }
                                        final Player p = (Player)e;
                                        p.damage(1.0, (Entity)l);
                                        final Vector v = p.getLocation().toVector().subtract(l.getLocation().toVector());
                                        if (v.getX() != 0.0 || v.getY() != 0.0 || v.getZ() != 0.0) {
                                            v.normalize();
                                        }
                                        p.setVelocity(v.multiply(3));
                                    }
                                }
                                l.getWorld().playSound(l.getLocation(), Sound.EXPLODE, 1.0f, 0.5f);
                                ParticleEffect.EXPLOSION_HUGE.display(0.0f, 0.0f, 0.0f, 1.0f, 40, l.getLocation().add(0.0, 1.0, 0.0), 20.0);
                                Mobs.crit.remove(l);
                                l.setCustomName(Mobs.generateOverheadBar((Entity)l, l.getHealth(), l.getMaxHealth(), Mobs.getMobTier(l), true));
                                l.setCustomNameVisible(true);
                                if (l.hasPotionEffect(PotionEffectType.SLOW)) {
                                    l.removePotionEffect(PotionEffectType.SLOW);
                                    if (l.getEquipment().getItemInHand() != null && l.getEquipment().getItemInHand().getType().name().contains("_HOE")) {
                                        l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3), true);
                                    }
                                }
                                if (l.hasPotionEffect(PotionEffectType.JUMP)) {
                                    l.removePotionEffect(PotionEffectType.JUMP);
                                }
                            }
                        }
                        if (!Listeners.named.containsKey(l.getUniqueId()) || System.currentTimeMillis() - Listeners.named.get(l.getUniqueId()) < 5000L) {
                            continue;
                        }
                        Listeners.named.remove(l.getUniqueId());
                        String name = "";
                        if (l.hasMetadata("name")) {
                            name = l.getMetadata("name").get(0).asString();
                        }
                        l.setCustomName(name);
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 20L, 20L);
        new BukkitRunnable() {
            public void run() {
                for (final Entity ent : Bukkit.getWorlds().get(0).getEntities()) {
                    if (ent instanceof LivingEntity && !(ent instanceof Player)) {
                        final LivingEntity l = (LivingEntity)ent;
                        if (!Mobs.crit.containsKey(l) || Mobs.isElite(l)) {
                            continue;
                        }
                        int step = Mobs.crit.get(l);
                        if (step > 0) {
                            --step;
                            Mobs.crit.put(l, step);
                            l.getWorld().playSound(l.getLocation(), Sound.PISTON_EXTEND, 1.0f, 2.0f);
                        }
                        if (step != 0) {
                            continue;
                        }
                        ParticleEffect.SPELL_WITCH.display(0.0f, 0.0f, 0.0f, 0.5f, 35, l.getLocation().add(0.0, 1.0, 0.0), 20.0);
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 20L, 10L);
        new BukkitRunnable() {
            public void run() {
                for (final Entity ent : Bukkit.getWorlds().get(0).getEntities()) {
                    if (ent instanceof Creature) {
                        final Creature c = (Creature)ent;
                        if (c.getEquipment().getItemInHand() == null || !c.getEquipment().getItemInHand().getType().name().contains("_HOE")) {
                            continue;
                        }
                        if (Mobs.isElite((LivingEntity)c) && Mobs.crit.containsKey(c)) {
                            return;
                        }
                        if (!Mobs.isPlayerNearby(c) || c.getTarget() == null) {
                            continue;
                        }
                        final Entity trgt = (Entity)c.getTarget();
                        if (c.getLocation().distanceSquared(trgt.getLocation()) <= 9.0) {
                            continue;
                        }
                        Projectile pj = null;
                        if (Mobs.getMobTier((LivingEntity)c) == 1) {
                            pj = c.launchProjectile((Class)Snowball.class);
                        }
                        if (Mobs.getMobTier((LivingEntity)c) == 2) {
                            pj = c.launchProjectile((Class)SmallFireball.class);
                        }
                        if (Mobs.getMobTier((LivingEntity)c) == 3) {
                            pj = c.launchProjectile((Class)EnderPearl.class);
                            pj.setVelocity(pj.getVelocity().multiply(1.25));
                        }
                        if (Mobs.getMobTier((LivingEntity)c) == 4) {
                            pj = c.launchProjectile((Class)WitherSkull.class);
                        }
                        if (Mobs.getMobTier((LivingEntity)c) != 5) {
                            continue;
                        }
                        pj = c.launchProjectile((Class)LargeFireball.class);
                        pj.setVelocity(pj.getVelocity().multiply(2));
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 20L, 25L);
    }
    
    public void onDisable() {
        Main.log.info("[Mobs] has been disabled.");
    }
    
    static boolean isPlayerNearby(final Creature c) {
        for (final Entity ent : c.getNearbyEntities(12.0, 12.0, 12.0)) {
            if (ent != null && ent instanceof Player && ent == c.getTarget()) {
                return true;
            }
        }
        return false;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onHit(final ProjectileHitEvent e) {
        final Projectile pj = e.getEntity();
        if (pj.getShooter() != null && pj.getShooter() instanceof LivingEntity && !(pj.getShooter() instanceof Player)) {
            final LivingEntity d = (LivingEntity)pj.getShooter();
            Player target = null;
            for (final Entity ent : pj.getNearbyEntities(2.0, 1.5, 2.0)) {
                if (ent instanceof Player) {
                    target = (Player)ent;
                }
            }
            if (target != null) {
                if (pj instanceof SmallFireball) {
                    e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.EXPLODE, 1.0f, 1.0f);
                }
                if (pj instanceof EnderPearl) {
                    e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENDERMAN_TELEPORT, 2.0f, 1.5f);
                }
                target.damage(1.0, (Entity)d);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(final CreatureSpawnEvent e) {
        e.getEntity().getEquipment().clear();
    }
    
    @EventHandler
    public void onCubeSplit(final SlimeSplitEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onCombust(final EntityCombustEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player) && !e.getCause().equals((Object)EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            e.setCancelled(true);
            e.setDamage(0.0);
        }
    }
    
    public static int getMobTier(final LivingEntity e) {
        if (e.getEquipment().getItemInHand() != null) {
            if (e.getEquipment().getItemInHand().getType().name().contains("WOOD_")) {
                return 1;
            }
            if (e.getEquipment().getItemInHand().getType().name().contains("STONE_")) {
                return 2;
            }
            if (e.getEquipment().getItemInHand().getType().name().contains("IRON_")) {
                return 3;
            }
            if (e.getEquipment().getItemInHand().getType().name().contains("DIAMOND_")) {
                return 4;
            }
            if (e.getEquipment().getItemInHand().getType().name().contains("GOLD_")) {
                return 5;
            }
        }
        return 0;
    }
    
    public static int getPlayerTier(final Player e) {
        int tier = 0;
        ItemStack[] armorContents;
        for (int length = (armorContents = e.getInventory().getArmorContents()).length, i = 0; i < length; ++i) {
            final ItemStack is = armorContents[i];
            if (is != null && is.getType() != Material.AIR) {
                if (is.getType().name().contains("LEATHER_") && 1 > tier) {
                    tier = 1;
                }
                if (is.getType().name().contains("CHAINMAIL_") && 2 > tier) {
                    tier = 2;
                }
                if (is.getType().name().contains("IRON_") && 3 > tier) {
                    tier = 3;
                }
                if (is.getType().name().contains("DIAMOND_") && 4 > tier) {
                    tier = 4;
                }
                if (is.getType().name().contains("GOLD_") && 5 > tier) {
                    tier = 5;
                }
            }
        }
        return tier;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onKnockback(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            final LivingEntity l = (LivingEntity)e.getEntity();
            if (e.getDamage() <= 0.0) {
                return;
            }
            if (!Mobs.sound.containsKey(l.getUniqueId()) || (Mobs.sound.containsKey(l.getUniqueId()) && System.currentTimeMillis() - Mobs.sound.get(l.getUniqueId()) > 500L)) {
                Mobs.sound.put(l.getUniqueId(), System.currentTimeMillis());
                if (e.getEntity() instanceof Skeleton) {
                    if (e.getDamage() >= l.getHealth()) {
                        l.getWorld().playSound(l.getLocation(), Sound.SKELETON_DEATH, 1.0f, 1.0f);
                    }
                    l.getWorld().playSound(l.getLocation(), Sound.SKELETON_HURT, 1.0f, 1.0f);
                }
                if (e.getEntity() instanceof Zombie) {
                    if (e.getDamage() >= l.getHealth()) {
                        l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_DEATH, 1.0f, 1.0f);
                    }
                    l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_HURT, 1.0f, 1.0f);
                }
                if ((e.getEntity() instanceof Spider || e.getEntity() instanceof CaveSpider) && e.getDamage() >= l.getHealth()) {
                    l.getWorld().playSound(l.getLocation(), Sound.SPIDER_DEATH, 1.0f, 1.0f);
                }
                if (e.getEntity() instanceof PigZombie) {
                    if (e.getDamage() >= l.getHealth()) {
                        l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_PIG_DEATH, 1.0f, 1.0f);
                    }
                    l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_PIG_HURT, 1.0f, 1.0f);
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityTarget(final EntityTargetEvent e) {
        if (e.getReason() != EntityTargetEvent.TargetReason.CLOSEST_PLAYER || !(e.getTarget() instanceof Player) || !(e.getEntity() instanceof Creature)) {
            e.setTarget((Entity)null);
            e.setCancelled(true);
            return;
        }
        final Creature l = (Creature)e.getEntity();
        final Player p = (Player)e.getTarget();
        if (l.getLocation().distance(p.getLocation()) > 15.0) {
            e.setCancelled(true);
            e.setTarget((Entity)null);
            return;
        }
        if (p.hasMetadata("NPC") || p.getPlayerListName() == " ") {
            e.setCancelled(true);
            e.setTarget((Entity)null);
            return;
        }
        if (getPlayerTier(p) - getMobTier((LivingEntity)l) > 2) {
            e.setCancelled(true);
            e.setTarget((Entity)null);
            return;
        }
        if (l.hasPotionEffect(PotionEffectType.SLOW)) {
            l.removePotionEffect(PotionEffectType.SLOW);
            if (l.getEquipment().getItemInHand() != null && l.getEquipment().getItemInHand().getType().name().contains("_HOE")) {
                l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2), true);
            }
        }
        if (l.hasPotionEffect(PotionEffectType.JUMP)) {
            l.removePotionEffect(PotionEffectType.JUMP);
        }
        Mobs.target.put(l, p);
    }
    
    @EventHandler
    public void onEntityTargetLastHit(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Creature && e.getDamager() instanceof Player) {
            final Creature c = (Creature)e.getEntity();
            final Player p = (Player)e.getDamager();
            if (Mobs.target.containsKey(c) && Mobs.target.get(c) != null) {
                if (p.getLocation().distanceSquared(c.getLocation()) < Mobs.target.get(c).getLocation().distanceSquared(c.getLocation())) {
                    c.setTarget((LivingEntity)p);
                    Mobs.target.put(c, p);
                }
            }
            else {
                c.setTarget((LivingEntity)p);
                Mobs.target.put(c, p);
            }
        }
    }
    
    public static boolean isElite(final LivingEntity e) {
        return e.getEquipment().getItemInHand() != null && e.getEquipment().getItemInHand().getType() != Material.AIR && e.getEquipment().getItemInHand().getItemMeta().hasEnchants();
    }
    
    public static int getBarLength(final int tier) {
        if (tier == 1) {
            return 25;
        }
        if (tier == 2) {
            return 30;
        }
        if (tier == 3) {
            return 35;
        }
        if (tier == 4) {
            return 40;
        }
        if (tier == 5) {
            return 50;
        }
        return 25;
    }
    
    public static String generateOverheadBar(final Entity ent, final double cur_hp, final double max_hp, final int tier, final boolean elite) {
        final int max_bar = getBarLength(tier);
        ChatColor cc = null;
        final DecimalFormatSymbols HpDot = new DecimalFormatSymbols(Locale.GERMAN);
        HpDot.setDecimalSeparator('.');
        HpDot.setGroupingSeparator(',');
        final DecimalFormat df = new DecimalFormat("##.#", HpDot);
        double percent_hp = (double)Math.round(100.0 * Double.parseDouble(df.format(cur_hp / max_hp)));
        if (percent_hp <= 0.0 && cur_hp > 0.0) {
            percent_hp = 1.0;
        }
        final double percent_interval = 100.0 / max_bar;
        int bar_count = 0;
        cc = ChatColor.GREEN;
        if (percent_hp <= 45.0) {
            cc = ChatColor.YELLOW;
        }
        if (percent_hp <= 20.0) {
            cc = ChatColor.RED;
        }
        if (Mobs.crit.containsKey(ent) && cur_hp > 0.0) {
            cc = ChatColor.LIGHT_PURPLE;
        }
        String return_string = cc + ChatColor.BOLD.toString() + "\u2551" + ChatColor.RESET.toString() + cc.toString();
        if (elite) {
            return_string = String.valueOf(return_string) + ChatColor.BOLD.toString();
        }
        while (percent_hp > 0.0 && bar_count < max_bar) {
            percent_hp -= percent_interval;
            ++bar_count;
            return_string = String.valueOf(return_string) + "|";
        }
        return_string = String.valueOf(return_string) + ChatColor.BLACK.toString();
        if (elite) {
            return_string = String.valueOf(return_string) + ChatColor.BOLD.toString();
        }
        while (bar_count < max_bar) {
            return_string = String.valueOf(return_string) + "|";
            ++bar_count;
        }
        return_string = String.valueOf(return_string) + cc + ChatColor.BOLD.toString() + "\u2551";
        return return_string;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onMobDeath(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player)) {
            final LivingEntity s = (LivingEntity)e.getEntity();
            if (e.getDamage() >= s.getHealth() && Mobs.crit.containsKey(s)) {
                Mobs.crit.remove(s);
                String mname = "";
                if (s.getEquipment().getItemInHand() != null && s.getEquipment().getItemInHand().getType() != Material.AIR) {
                    mname = generateOverheadBar((Entity)s, 0.0, s.getMaxHealth(), getMobTier(s), isElite(s));
                    s.setCustomName(mname);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onCrit(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player) && e.getDamager() instanceof Player) {
            if (e.getDamage() <= 0.0) {
                return;
            }
            final LivingEntity s = (LivingEntity)e.getEntity();
            final Random random = new Random();
            final int rcrt = random.nextInt(100) + 1;
            if (!Mobs.crit.containsKey(s) && ((getMobTier(s) == 1 && rcrt <= 5) || (getMobTier(s) == 2 && rcrt <= 7) || (getMobTier(s) == 3 && rcrt <= 10) || (getMobTier(s) == 4 && rcrt <= 13) || (getMobTier(s) == 5 && rcrt <= 20))) {
                Mobs.crit.put(s, 4);
                if (isElite(s)) {
                    s.getWorld().playSound(s.getLocation(), Sound.CREEPER_HISS, 1.0f, 4.0f);
                    final double max = s.getMaxHealth();
                    final double hp = s.getHealth() - e.getDamage();
                    s.setCustomName(generateOverheadBar((Entity)s, hp, max, getMobTier(s), isElite(s)));
                    s.setCustomNameVisible(true);
                    Listeners.named.put(s.getUniqueId(), System.currentTimeMillis());
                    s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10), true);
                    s.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 127), true);
                }
                else {
                    s.getWorld().playSound(s.getLocation(), Sound.PISTON_EXTEND, 1.0f, 2.0f);
                    final double max = s.getMaxHealth();
                    final double hp = s.getHealth() - e.getDamage();
                    s.setCustomName(generateOverheadBar((Entity)s, hp, max, getMobTier(s), isElite(s)));
                    s.setCustomNameVisible(true);
                    Listeners.named.put(s.getUniqueId(), System.currentTimeMillis());
                }
            }
        }
    }
    
    @EventHandler
    public void onSafeSpot(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player) && e.getDamager() instanceof Player) {
            if (e.getDamage() <= 0.0) {
                return;
            }
            final LivingEntity s = (LivingEntity)e.getEntity();
            final Player p = (Player)e.getDamager();
            final Random random = new Random();
            final int rcrt = random.nextInt(10) + 1;
            if (rcrt == 1 && p.getLocation().getY() - s.getLocation().getY() > 1.0 && p.getLocation().distance(s.getLocation()) <= 6.0) {
                s.teleport(p.getLocation().add(0.0, 0.25, 0.0));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobHitMob(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof LivingEntity && !(e.getDamager() instanceof Player) && !(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
            e.setDamage(0.0);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onMobHit(final EntityDamageByEntityEvent e) {
        if (e.getDamage() <= 0.0) {
            return;
        }
        if (e.getDamager() instanceof LivingEntity && !(e.getDamager() instanceof Player) && e.getEntity() instanceof Player) {
            final LivingEntity s = (LivingEntity)e.getDamager();
            final Player p = (Player)e.getEntity();
            final Random random = new Random();
            int dmg = 1;
            if (s.getEquipment().getItemInHand() != null && s.getEquipment().getItemInHand().getType() != Material.AIR) {
                final int min = Damage.getDamageRange(s.getEquipment().getItemInHand()).get(0);
                final int max = Damage.getDamageRange(s.getEquipment().getItemInHand()).get(1);
                dmg = random.nextInt(max - min + 1) + min;
            }
            if (Mobs.crit.containsKey(s) && Mobs.crit.get(s) == 0) {
                if (isElite(s)) {
                    dmg *= 4;
                }
                else {
                    dmg *= 3;
                }
                if (!isElite(s)) {
                    Mobs.crit.remove(s);
                }
                p.playSound(p.getLocation(), Sound.EXPLODE, 1.0f, 0.3f);
                final double max2 = s.getMaxHealth();
                final double hp = s.getHealth() - e.getDamage();
                s.setCustomName(generateOverheadBar((Entity)s, hp, max2, getMobTier(s), isElite(s)));
                s.setCustomNameVisible(true);
                Listeners.named.put(s.getUniqueId(), System.currentTimeMillis());
            }
            if (e.getDamage() <= 0.0) {
                return;
            }
            if (s.getEquipment().getItemInHand().getType().name().contains("WOOD_")) {
                if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
                    dmg *= (int)2.5;
                }
                else {
                    dmg *= (int)0.8;
                }
            }
            if (s.getEquipment().getItemInHand().getType().name().contains("STONE_")) {
                if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
                    dmg *= (int)2.5;
                }
                else {
                    dmg *= (int)0.9;
                }
            }
            if (s.getEquipment().getItemInHand().getType().name().contains("IRON_")) {
                if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
                    dmg *= 3;
                }
                else {
                    dmg *= (int)1.2;
                }
            }
            if (s.getEquipment().getItemInHand().getType().name().contains("DIAMOND_")) {
                if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
                    dmg *= 5;
                }
                else {
                    dmg *= (int)1.4;
                }
            }
            if (s.getEquipment().getItemInHand().getType().name().contains("GOLD_")) {
                if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
                    dmg *= 7;
                }
                else {
                    dmg *= 2;
                }
            }
            if (s instanceof MagmaCube) {
                dmg *= (int)0.5;
            }
            if (dmg < 1) {
                dmg = 1;
            }
            e.setDamage((double)dmg);
        }
    }
}
