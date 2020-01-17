package me.AnFun.VKLegacy;

import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Random;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Projectile;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class Staffs implements Listener
{
    HashMap<Projectile, ItemStack> shots;
    public static HashMap<Player, ItemStack> staff;
    
    static {
        Staffs.staff = new HashMap<Player, ItemStack>();
    }
    
    public Staffs() {
        this.shots = new HashMap<Projectile, ItemStack>();
    }
    
    public void onEnable() {
        Main.log.info("[Staffs] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
    }
    
    public void onDisable() {
        Main.log.info("[Staffs] has been disabled.");
    }
    
    @EventHandler
    public void onStaffShot(final PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player p = e.getPlayer();
            if (p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR && p.getItemInHand().getType().name().contains("_HOE") && p.getItemInHand().getItemMeta().hasLore()) {
                if (Alignments.isSafeZone(p.getLocation())) {
                    p.playSound(p.getLocation(), Sound.FIZZ, 1.0f, 1.25f);
                    ParticleEffect.CRIT_MAGIC.display(0.0f, 0.0f, 0.0f, 0.5f, 20, p.getLocation().add(0.0, 1.0, 0.0), 20.0);
                }
                else {
                    if (Energy.nodamage.containsKey(p.getName()) && System.currentTimeMillis() - Energy.nodamage.get(p.getName()) < 100L) {
                        e.setCancelled(true);
                        return;
                    }
                    if (Energy.getEnergy(p) > 0.0f) {
                        int amt = 0;
                        Projectile ep = null;
                        if (p.getItemInHand().getType() == Material.WOOD_HOE) {
                            ep = p.launchProjectile((Class)Snowball.class);
                            this.shots.put(ep, p.getItemInHand());
                            amt = 7;
                        }
                        if (p.getItemInHand().getType() == Material.STONE_HOE) {
                            ep = p.launchProjectile((Class)SmallFireball.class);
                            ep.setVelocity(ep.getVelocity().multiply(1.25));
                            ep.setBounce(false);
                            this.shots.put(ep, p.getItemInHand());
                            amt = 8;
                        }
                        if (p.getItemInHand().getType() == Material.IRON_HOE) {
                            ep = p.launchProjectile((Class)EnderPearl.class);
                            ep.setVelocity(ep.getVelocity().multiply(1.25));
                            this.shots.put(ep, p.getItemInHand());
                            amt = 9;
                        }
                        if (p.getItemInHand().getType() == Material.DIAMOND_HOE) {
                            ep = p.launchProjectile((Class)WitherSkull.class);
                            ep.setVelocity(ep.getVelocity().multiply(1.5));
                            this.shots.put(ep, p.getItemInHand());
                            amt = 10;
                        }
                        if (p.getItemInHand().getType() == Material.GOLD_HOE) {
                            ep = p.launchProjectile((Class)LargeFireball.class);
                            ep.setVelocity(ep.getVelocity().multiply(3));
                            this.shots.put(ep, p.getItemInHand());
                            amt = 11;
                        }
                        final Random r = new Random();
                        final int dodura = r.nextInt(2000);
                        if (dodura <= p.getItemInHand().getType().getMaxDurability()) {
                            if (p.getItemInHand().getDurability() >= p.getItemInHand().getType().getMaxDurability()) {
                                p.setItemInHand((ItemStack)null);
                                p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                            }
                            else {
                                p.getItemInHand().setDurability((short)(p.getItemInHand().getDurability() + 1));
                            }
                        }
                        Energy.removeEnergy(p, amt);
                        p.playSound(p.getLocation(), Sound.SHOOT_ARROW, 1.0f, 0.25f);
                        this.shots.put(ep, p.getItemInHand());
                    }
                    else {
                        Energy.setEnergy(p, 0.0f);
                        Energy.cd.put(p.getName(), System.currentTimeMillis());
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
                        p.playSound(p.getLocation(), Sound.WOLF_PANT, 10.0f, 1.5f);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHit(final ProjectileHitEvent e) {
        final Projectile pj = e.getEntity();
        if (pj.getShooter() != null && pj.getShooter() instanceof Player) {
            final Player d = (Player)pj.getShooter();
            if (this.shots.containsKey(pj)) {
                LivingEntity target = null;
                final ItemStack wep = this.shots.get(pj);
                this.shots.remove(pj);
                for (final Entity ent : pj.getNearbyEntities(2.0, 2.0, 2.0)) {
                    if (ent instanceof LivingEntity && ent != d) {
                        target = (LivingEntity)ent;
                    }
                }
                if (target != null) {
                    if (pj instanceof SmallFireball) {
                        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.EXPLODE, 1.0f, 1.0f);
                    }
                    if (pj instanceof EnderPearl) {
                        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENDERMAN_TELEPORT, 2.0f, 1.5f);
                    }
                    Staffs.staff.put(d, wep);
                    target.damage(1.0, (Entity)d);
                    Staffs.staff.remove(d);
                }
                this.shots.remove(pj);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockIgnite(final BlockIgniteEvent e) {
        if (e.getCause() == BlockIgniteEvent.IgniteCause.FIREBALL) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplodePrimeEvent(final ExplosionPrimeEvent e) {
        e.setFire(false);
        e.setRadius(0.0f);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplodeEvent(final EntityExplodeEvent e) {
        e.setCancelled(true);
        e.setYield(0.0f);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile) {
            e.setCancelled(true);
            e.setDamage(0.0);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTp(final PlayerTeleportEvent e) {
        if (e.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            e.setCancelled(true);
        }
    }
}
