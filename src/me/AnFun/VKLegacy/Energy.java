package me.AnFun.VKLegacy;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.Sound;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class Energy implements Listener
{
    public static HashMap<String, Long> nodamage;
    public static HashMap<String, Long> cd;
    
    static {
        Energy.nodamage = new HashMap<String, Long>();
        Energy.cd = new HashMap<String, Long>();
    }
    
    public void onEnable() {
        Main.log.info("[Energy] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    final PlayerInventory i = p.getInventory();
                    float amt = 100.0f;
                    ItemStack[] armorContents;
                    for (int length = (armorContents = i.getArmorContents()).length, j = 0; j < length; ++j) {
                        final ItemStack is = armorContents[j];
                        if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                            int added = Damage.getEnergy(is);
                            int intel = 0;
                            final int addedint = Damage.getElem(is, "INT");
                            intel += addedint;
                            if (intel > 0) {
                                added += (int)Math.round(intel * 0.016);
                            }
                            amt += added * 5;
                        }
                    }
                    if (Energy.getEnergy(p) < 100.0f && (!Energy.cd.containsKey(p.getName()) || (Energy.cd.containsKey(p.getName()) && System.currentTimeMillis() - Energy.cd.get(p.getName()) > 2000L))) {
                        Energy.setEnergy(p, Energy.getEnergy(p) + amt / 100.0f);
                    }
                    if (Energy.getEnergy(p) <= 0.0f) {
                        p.setSprinting(false);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 1L, 1L);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (p.isSprinting() && !Alignments.isSafeZone(p.getLocation())) {
                        float amt = 100.0f;
                        amt += 85.0f;
                        amt *= 4.0f;
                        if (Energy.getEnergy(p) > 0.0f) {
                            Energy.setEnergy(p, Energy.getEnergy(p) - amt / 100.0f);
                        }
                        if (Energy.getEnergy(p) > 0.0f) {
                            continue;
                        }
                        Energy.setEnergy(p, 0.0f);
                        Energy.cd.put(p.getName(), System.currentTimeMillis());
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
                        p.setSprinting(false);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 4L, 4L);
    }
    
    public void onDisable() {
        Main.log.info("[Energy] has been disabled.");
    }
    
    public static float getEnergy(final Player p) {
        float energy = 0.0f;
        energy = p.getExp() * 100.0f;
        return energy;
    }
    
    public static void setEnergy(final Player p, float energy) {
        if (energy > 100.0f) {
            energy = 100.0f;
        }
        p.setExp(energy / 100.0f);
        p.setLevel((int)energy);
    }
    
    public static void removeEnergy(final Player p, final int amt) {
        if (Alignments.isSafeZone(p.getLocation())) {
            return;
        }
        if (p.hasMetadata("lastenergy") && System.currentTimeMillis() - p.getMetadata("lastenergy").get(0).asLong() < 100L) {
            return;
        }
        p.setMetadata("lastenergy", (MetadataValue)new FixedMetadataValue(Main.plugin, (Object)System.currentTimeMillis()));
        setEnergy(p, getEnergy(p) - amt);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEnergyUse(final PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            final Player p = e.getPlayer();
            if (Energy.nodamage.containsKey(p.getName()) && System.currentTimeMillis() - Energy.nodamage.get(p.getName()) < 100L) {
                e.setUseItemInHand(Event.Result.DENY);
                e.setCancelled(true);
                return;
            }
            if (getEnergy(p) > 0.0f) {
                int amt = 6;
                if (p.getItemInHand().getType() == Material.WOOD_SWORD) {
                    amt = 7;
                }
                else if (p.getItemInHand().getType() == Material.WOOD_AXE || p.getItemInHand().getType() == Material.WOOD_SPADE || p.getItemInHand().getType() == Material.STONE_SWORD) {
                    amt = 8;
                }
                else if (p.getItemInHand().getType() == Material.STONE_AXE || p.getItemInHand().getType() == Material.STONE_SPADE || p.getItemInHand().getType() == Material.IRON_SWORD) {
                    amt = 9;
                }
                else if (p.getItemInHand().getType() == Material.IRON_AXE || p.getItemInHand().getType() == Material.IRON_SPADE || p.getItemInHand().getType() == Material.DIAMOND_SWORD) {
                    amt = 10;
                }
                else if (p.getItemInHand().getType() == Material.DIAMOND_AXE || p.getItemInHand().getType() == Material.DIAMOND_SPADE || p.getItemInHand().getType() == Material.GOLD_SWORD) {
                    amt = 11;
                }
                else if (p.getItemInHand().getType() == Material.GOLD_AXE || p.getItemInHand().getType() == Material.GOLD_SPADE) {
                    amt = 12;
                }
                removeEnergy(p, amt);
            }
            if (getEnergy(p) <= 0.0f) {
                setEnergy(p, 0.0f);
                Energy.cd.put(p.getName(), System.currentTimeMillis());
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
                p.playSound(p.getLocation(), Sound.WOLF_PANT, 10.0f, 1.5f);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEnergyUseDamage(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            final Player p = (Player)e.getDamager();
            if (Energy.nodamage.containsKey(p.getName()) && System.currentTimeMillis() - Energy.nodamage.get(p.getName()) < 100L) {
                e.setCancelled(true);
                e.setDamage(0.0);
                return;
            }
            Energy.nodamage.put(p.getName(), System.currentTimeMillis());
            if (getEnergy(p) > 0.0f) {
                if (p.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
                    p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                }
                int amt = 6;
                if (p.getItemInHand().getType() == Material.WOOD_SWORD) {
                    amt = 7;
                }
                else if (p.getItemInHand().getType() == Material.WOOD_AXE || p.getItemInHand().getType() == Material.STONE_SWORD) {
                    amt = 8;
                }
                else if (p.getItemInHand().getType() == Material.STONE_AXE || p.getItemInHand().getType() == Material.IRON_SWORD) {
                    amt = 9;
                }
                else if (p.getItemInHand().getType() == Material.IRON_AXE || p.getItemInHand().getType() == Material.DIAMOND_SWORD) {
                    amt = 10;
                }
                else if (p.getItemInHand().getType() == Material.DIAMOND_AXE || p.getItemInHand().getType() == Material.GOLD_SWORD) {
                    amt = 11;
                }
                else if (p.getItemInHand().getType() == Material.GOLD_AXE) {
                    amt = 12;
                }
                removeEnergy(p, amt);
            }
            if (getEnergy(p) <= 0.0f) {
                setEnergy(p, 0.0f);
                Energy.cd.put(p.getName(), System.currentTimeMillis());
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
                p.playSound(p.getLocation(), Sound.WOLF_PANT, 10.0f, 1.5f);
            }
        }
    }
}
