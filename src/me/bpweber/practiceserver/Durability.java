package me.bpweber.practiceserver;

import org.bukkit.Effect;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.Sound;
import java.util.Random;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Durability implements Listener
{
    public void onEnable() {
        Main.log.info("[Durability] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
    }
    
    public void onDisable() {
        Main.log.info("[Durability] has been disabled.");
    }
    
    public static ItemStack scrap(final int tier) {
        Material m = null;
        ChatColor cc = ChatColor.WHITE;
        String name = "";
        String lore = "";
        short dura = 0;
        if (tier == 1) {
            m = Material.LEATHER;
            name = "Leather";
            lore = "Leather";
        }
        if (tier == 2) {
            m = Material.IRON_FENCE;
            name = "Chainmail";
            lore = "Chainmail";
            cc = ChatColor.GREEN;
        }
        if (tier > 2) {
            m = Material.INK_SACK;
        }
        if (tier == 3) {
            name = "Iron";
            lore = "Iron";
            dura = 7;
            cc = ChatColor.AQUA;
        }
        if (tier == 4) {
            name = "Diamond";
            lore = "Diamond";
            dura = 12;
            cc = ChatColor.LIGHT_PURPLE;
        }
        if (tier == 5) {
            name = "Golden";
            lore = "Gold";
            dura = 11;
            cc = ChatColor.YELLOW;
        }
        final ItemStack is = new ItemStack(m);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(cc + name + " Armor Scrap");
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Recovers 3% Durability of " + cc + lore + " Equipment"));
        is.setItemMeta(im);
        is.setDurability(dura);
        return is;
    }
    
    public static float getDuraPercent(final ItemStack is) {
        final float max = is.getType().getMaxDurability();
        final float curr = (float)(is.getType().getMaxDurability() - is.getDurability());
        final float dura = curr / max * 100.0f;
        return (float)Math.round(dura);
    }
    
    public static ItemStack addDura(final ItemStack is, final float amt) {
        final float add = amt / 100.0f;
        final float max = is.getType().getMaxDurability();
        float adding = max * add;
        if (adding < 1.0f) {
            adding = 1.0f;
        }
        adding = (float)Math.round(adding);
        short dura = (short)(is.getDurability() - adding);
        if (dura < 0) {
            dura = 0;
        }
        is.setDurability(dura);
        return is;
    }
    
    public static ItemStack takeDura(final ItemStack is, final float amt) {
        final float add = amt / 100.0f;
        final float max = is.getType().getMaxDurability();
        float adding = max * add;
        if (adding < 1.0f) {
            adding = 1.0f;
        }
        adding = (float)Math.round(adding);
        short dura = (short)(is.getDurability() + adding);
        if (dura < 0) {
            dura = 0;
        }
        is.setDurability(dura);
        return is;
    }
    
    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent e) {
        if (e.getDamage() <= 0.0) {
            return;
        }
        if (e.getDamager() instanceof Player) {
            final Player d = (Player)e.getDamager();
            if (d.getItemInHand() == null || d.getItemInHand().getType() == Material.AIR || e.getDamage() == 1.0) {
                return;
            }
        }
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            final Random r = new Random();
            final int dodura = r.nextInt(1500);
            if (p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR) {
                final ItemStack is = p.getInventory().getHelmet();
                if (dodura <= is.getType().getMaxDurability()) {
                    if (is.getDurability() >= is.getType().getMaxDurability()) {
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        p.getInventory().setHelmet((ItemStack)null);
                        Listeners.hpCheck(p);
                    }
                    else {
                        is.setDurability((short)(is.getDurability() + 1));
                    }
                }
            }
            if (p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() != Material.AIR) {
                final ItemStack is = p.getInventory().getChestplate();
                if (dodura <= is.getType().getMaxDurability()) {
                    if (is.getDurability() >= is.getType().getMaxDurability()) {
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        p.getInventory().setChestplate((ItemStack)null);
                        Listeners.hpCheck(p);
                    }
                    else {
                        is.setDurability((short)(is.getDurability() + 1));
                    }
                }
            }
            if (p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getType() != Material.AIR) {
                final ItemStack is = p.getInventory().getLeggings();
                if (dodura <= is.getType().getMaxDurability()) {
                    if (is.getDurability() >= is.getType().getMaxDurability()) {
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        p.getInventory().setLeggings((ItemStack)null);
                        Listeners.hpCheck(p);
                    }
                    else {
                        is.setDurability((short)(is.getDurability() + 1));
                    }
                }
            }
            if (p.getInventory().getBoots() != null && p.getInventory().getBoots().getType() != Material.AIR) {
                final ItemStack is = p.getInventory().getBoots();
                if (dodura <= is.getType().getMaxDurability()) {
                    if (is.getDurability() >= is.getType().getMaxDurability()) {
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        p.getInventory().setBoots((ItemStack)null);
                        Listeners.hpCheck(p);
                    }
                    else {
                        is.setDurability((short)(is.getDurability() + 1));
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onDamager(final EntityDamageByEntityEvent e) {
        if (e.getDamage() <= 0.0) {
            return;
        }
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            final Player p = (Player)e.getDamager();
            if (p.getItemInHand() != null && (p.getItemInHand().getType().name().contains("_AXE") || p.getItemInHand().getType().name().contains("_SWORD") || p.getItemInHand().getType().name().contains("_SPADE"))) {
                final Random r = new Random();
                final int dodura = r.nextInt(1500);
                if (dodura <= p.getItemInHand().getType().getMaxDurability()) {
                    if (p.getItemInHand().getDurability() >= p.getItemInHand().getType().getMaxDurability()) {
                        p.setItemInHand((ItemStack)null);
                        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                    }
                    else {
                        p.getItemInHand().setDurability((short)(p.getItemInHand().getDurability() + 1));
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onDuraUpdate(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            final Player p = (Player)e.getDamager();
            if (p.getItemInHand() != null && (p.getItemInHand().getType().name().contains("_AXE") || p.getItemInHand().getType().name().contains("_SWORD") || p.getItemInHand().getType().name().contains("_SPADE"))) {
                p.updateInventory();
            }
        }
    }
    
    @EventHandler
    public void onScrapUse(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player p = (Player)e.getWhoClicked();
            if (e.getInventory().getHolder() == p && isArmor(e.getCurrentItem())) {
                final ItemStack is = e.getCurrentItem();
                final ItemStack scrap = e.getCursor();
                if (this.isCorrectScrap(scrap, is) && is.getDurability() > 0) {
                    e.setCancelled(true);
                    if (e.getCursor().getAmount() > 1) {
                        e.getCursor().setAmount(e.getCursor().getAmount() - 1);
                    }
                    else if (e.getCursor().getAmount() == 1) {
                        e.setCursor((ItemStack)null);
                    }
                    float dura = getDuraPercent(is) + 3.0f;
                    if (dura > 100.0f) {
                        dura = 100.0f;
                    }
                    p.sendMessage("               " + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + "3.0% DURABILITY" + ChatColor.GREEN + ChatColor.BOLD + " -> " + ChatColor.GREEN + dura + "% TOTAL");
                    e.setCurrentItem(addDura(is, 3.0f));
                    if (Orbs.getItemTier(is) == 1) {
                        p.getWorld().playEffect(p.getLocation().add(0.0, 2.0, 0.0), Effect.STEP_SOUND, (Object)Material.JUKEBOX);
                    }
                    if (Orbs.getItemTier(is) == 2) {
                        p.getWorld().playEffect(p.getLocation().add(0.0, 2.0, 0.0), Effect.STEP_SOUND, (Object)Material.WEB);
                    }
                    if (Orbs.getItemTier(is) == 3) {
                        p.getWorld().playEffect(p.getLocation().add(0.0, 2.0, 0.0), Effect.STEP_SOUND, (Object)Material.IRON_BLOCK);
                    }
                    if (Orbs.getItemTier(is) == 4) {
                        p.getWorld().playEffect(p.getLocation().add(0.0, 2.0, 0.0), Effect.STEP_SOUND, (Object)Material.DIAMOND_BLOCK);
                    }
                    if (Orbs.getItemTier(is) == 5) {
                        p.getWorld().playEffect(p.getLocation().add(0.0, 2.0, 0.0), Effect.STEP_SOUND, (Object)Material.GOLD_BLOCK);
                    }
                }
            }
        }
    }
    
    boolean isCorrectScrap(final ItemStack scrap, final ItemStack armor) {
        if (scrap == null || armor == null) {
            return false;
        }
        if (scrap.getType() == Material.AIR || armor.getType() == Material.AIR) {
            return false;
        }
        if (!isArmor(armor)) {
            return false;
        }
        if (!scrap.getItemMeta().hasDisplayName()) {
            return false;
        }
        if (!scrap.getItemMeta().getDisplayName().contains("Armor Scrap")) {
            return false;
        }
        final int tier = Orbs.getItemTier(armor);
        if (scrap.getType() == Material.LEATHER && tier == 1) {
            return true;
        }
        if (scrap.getType() == Material.IRON_FENCE && tier == 2) {
            return true;
        }
        if (scrap.getType() == Material.INK_SACK) {
            final short dura = scrap.getDurability();
            if (tier == 3 && dura == 7) {
                return true;
            }
            if (tier == 4 && dura == 12) {
                return true;
            }
            if (tier == 5 && dura == 11) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isArmor(final ItemStack is) {
        return is != null && is.getType() != Material.AIR && (is.getType().name().contains("_HELMET") || is.getType().name().contains("_CHESTPLATE") || is.getType().name().contains("_LEGGINGS") || is.getType().name().contains("_BOOTS") || is.getType().name().contains("_SWORD") || is.getType().name().contains("_AXE") || is.getType().name().contains("_HOE") || is.getType().name().contains("_SPADE") || is.getType().name().contains("_PICKAXE"));
    }
    
    public static boolean isScrap(final ItemStack is) {
        if (is == null) {
            return false;
        }
        if (is.getType() == Material.AIR) {
            return false;
        }
        if (!is.getItemMeta().hasDisplayName()) {
            return false;
        }
        if (!is.getItemMeta().getDisplayName().contains("Armor Scrap")) {
            return false;
        }
        if (is.getType() == Material.LEATHER) {
            return true;
        }
        if (is.getType() == Material.IRON_FENCE) {
            return true;
        }
        if (is.getType() == Material.INK_SACK) {
            final short dura = is.getDurability();
            if (dura == 7) {
                return true;
            }
            if (dura == 12) {
                return true;
            }
            if (dura == 11) {
                return true;
            }
        }
        return false;
    }
}
