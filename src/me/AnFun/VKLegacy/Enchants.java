package me.AnFun.VKLegacy;

import org.bukkit.event.EventHandler;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import java.util.Random;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;

public class Enchants implements Listener
{
    public static Enchantment glow;
    
    static {
        Enchants.glow = (Enchantment)new GlowEnchant(69);
    }
    
    public void onEnable() {
        Main.log.info("[Enchants] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        registerNewEnchantment();
    }
    
    public void onDisable() {
        Main.log.info("[Enchants] has been disabled.");
    }
    
    public static boolean registerNewEnchantment() {
        try {
            final Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            try {
                Enchantment.registerEnchantment(Enchants.glow);
                return true;
            }
            catch (IllegalArgumentException ex) {}
        }
        catch (Exception ex2) {}
        return false;
    }
    
    public static int getPlus(final ItemStack is) {
        if (is.getItemMeta().hasDisplayName()) {
            String name = ChatColor.stripColor(is.getItemMeta().getDisplayName());
            if (name.startsWith("[+")) {
                name = name.split("\\[+")[1].split("\\]")[0];
                try {
                    return Integer.parseInt(name);
                }
                catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    @EventHandler
    public void onInvClick(final InventoryClickEvent e) throws Exception {
        final Player p = (Player)e.getWhoClicked();
        if (!e.getInventory().getName().equalsIgnoreCase("container.crafting")) {
            return;
        }
        if (e.getSlotType() == InventoryType.SlotType.ARMOR) {
            return;
        }
        if (e.getCursor() != null && e.getCursor().getType() == Material.EMPTY_MAP && e.getCursor().getItemMeta().getDisplayName() != null && e.getCursor().getItemMeta().getDisplayName().contains("Armor") && e.getCurrentItem() != null && (e.getCurrentItem().getType().name().contains("_HELMET") || e.getCurrentItem().getType().name().contains("_CHESTPLATE") || e.getCurrentItem().getType().name().contains("_LEGGINGS") || e.getCurrentItem().getType().name().contains("_BOOTS")) && e.getCurrentItem().getItemMeta().getLore() != null && e.getCurrentItem().getItemMeta().hasDisplayName() && ((e.getCurrentItem().getType().name().contains("GOLD_") && e.getCursor().getItemMeta().getDisplayName().contains("Gold")) || (e.getCurrentItem().getType().name().contains("DIAMOND_") && e.getCursor().getItemMeta().getDisplayName().contains("Diamond")) || (e.getCurrentItem().getType().name().contains("IRON_") && e.getCursor().getItemMeta().getDisplayName().contains("Iron")) || (e.getCurrentItem().getType().name().contains("CHAINMAIL_") && e.getCursor().getItemMeta().getDisplayName().contains("Chainmail")) || (e.getCurrentItem().getType().name().contains("LEATHER_") && e.getCursor().getItemMeta().getDisplayName().contains("Leather")))) {
            final List<String> curlore = (List<String>)e.getCurrentItem().getItemMeta().getLore();
            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            if (name.startsWith(ChatColor.RED + "[+")) {
                name = name.split("] ")[1];
            }
            final double beforehp = Damage.getHp(e.getCurrentItem());
            final double beforehpgen = Damage.getHps(e.getCurrentItem());
            final int beforenrg = Damage.getEnergy(e.getCurrentItem());
            final int plus = getPlus(e.getCurrentItem());
            if (plus < 3) {
                if (e.getCursor().getAmount() > 1) {
                    e.getCursor().setAmount(e.getCursor().getAmount() - 1);
                }
                else if (e.getCursor().getAmount() == 1) {
                    e.setCursor((ItemStack)null);
                }
                p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.25f);
                final Firework fw = (Firework)p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                final FireworkMeta fwm = fw.getFireworkMeta();
                final FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).trail(true).build();
                fwm.addEffect(effect);
                fwm.setPower(0);
                fw.setFireworkMeta(fwm);
                e.setCancelled(true);
                double added = beforehp * 0.05;
                if (added < 1.0) {
                    added = 1.0;
                }
                final int newhp = (int)(beforehp + added);
                final ItemStack is = e.getCurrentItem();
                final ItemMeta im = is.getItemMeta();
                im.setDisplayName(ChatColor.RED + "[+" + (plus + 1) + "] " + name);
                final List<String> lore = (List<String>)im.getLore();
                lore.set(1, ChatColor.RED + "HP: +" + newhp);
                if (curlore.get(2).contains("ENERGY REGEN")) {
                    lore.set(2, ChatColor.RED + "ENERGY REGEN: +" + (beforenrg + 1) + "%");
                }
                else if (curlore.get(2).contains("HP REGEN")) {
                    double addedhps = beforehpgen * 0.05;
                    if (addedhps < 1.0) {
                        addedhps = 1.0;
                    }
                    final int newhps = (int)(beforehpgen + addedhps);
                    lore.set(2, ChatColor.RED + "HP REGEN: +" + newhps + " HP/s");
                }
                im.setLore((List)lore);
                is.setItemMeta(im);
                e.setCurrentItem(is);
            }
            if (plus >= 3 && plus < 12) {
                if (e.getCursor().getAmount() > 1) {
                    e.getCursor().setAmount(e.getCursor().getAmount() - 1);
                }
                else if (e.getCursor().getAmount() == 1) {
                    e.setCursor((ItemStack)null);
                }
                final Random random = new Random();
                final int drop = random.nextInt(100) + 1;
                int doifail = 0;
                if (plus == 3) {
                    doifail = 30;
                }
                if (plus == 4) {
                    doifail = 40;
                }
                if (plus == 5) {
                    doifail = 50;
                }
                if (plus == 6) {
                    doifail = 65;
                }
                if (plus == 7) {
                    doifail = 75;
                }
                if (plus == 8) {
                    doifail = 80;
                }
                if (plus == 9) {
                    doifail = 85;
                }
                if (plus == 10) {
                    doifail = 90;
                }
                if (plus == 11) {
                    doifail = 95;
                }
                e.setCancelled(true);
                if (drop <= doifail) {
                    p.getWorld().playSound(p.getLocation(), Sound.FIZZ, 2.0f, 1.25f);
                    ParticleEffect.LAVA.display(0.0f, 0.0f, 0.0f, 5.0f, 10, p.getEyeLocation(), 20.0);
                    e.setCurrentItem((ItemStack)null);
                }
                else {
                    p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.25f);
                    final Firework fw2 = (Firework)p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                    final FireworkMeta fwm2 = fw2.getFireworkMeta();
                    final FireworkEffect effect2 = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).trail(true).build();
                    fwm2.addEffect(effect2);
                    fwm2.setPower(0);
                    fw2.setFireworkMeta(fwm2);
                    e.setCancelled(true);
                    double added2 = beforehp * 0.05;
                    if (added2 < 1.0) {
                        added2 = 1.0;
                    }
                    final int newhp2 = (int)(beforehp + added2);
                    final ItemStack is2 = e.getCurrentItem();
                    final ItemMeta im2 = is2.getItemMeta();
                    im2.setDisplayName(ChatColor.RED + "[+" + (plus + 1) + "] " + name);
                    final List<String> lore2 = (List<String>)im2.getLore();
                    lore2.set(1, ChatColor.RED + "HP: +" + newhp2);
                    if (curlore.get(2).contains("ENERGY REGEN")) {
                        lore2.set(2, ChatColor.RED + "ENERGY REGEN: +" + (beforenrg + 1) + "%");
                    }
                    else if (curlore.get(2).contains("HP REGEN")) {
                        double addedhps2 = beforehpgen * 0.05;
                        if (addedhps2 < 1.0) {
                            addedhps2 = 1.0;
                        }
                        final int newhps2 = (int)(beforehpgen + addedhps2);
                        lore2.set(2, ChatColor.RED + "HP REGEN: +" + newhps2 + " HP/s");
                    }
                    im2.setLore((List)lore2);
                    is2.setItemMeta(im2);
                    is2.addUnsafeEnchantment(Enchants.glow, 1);
                    e.setCurrentItem(is2);
                }
            }
        }
        if (e.getCursor() != null && e.getCursor().getType() == Material.EMPTY_MAP && e.getCursor().getItemMeta().getDisplayName() != null && e.getCursor().getItemMeta().getDisplayName().contains("Weapon") && e.getCurrentItem() != null && (e.getCurrentItem().getType().name().contains("_SWORD") || e.getCurrentItem().getType().name().contains("_HOE") || e.getCurrentItem().getType().name().contains("_SPADE") || e.getCurrentItem().getType().name().contains("_AXE")) && e.getCurrentItem().getItemMeta().getLore() != null && e.getCurrentItem().getItemMeta().hasDisplayName() && ((e.getCurrentItem().getType().name().contains("GOLD_") && e.getCursor().getItemMeta().getDisplayName().contains("Gold")) || (e.getCurrentItem().getType().name().contains("DIAMOND_") && e.getCursor().getItemMeta().getDisplayName().contains("Diamond")) || (e.getCurrentItem().getType().name().contains("IRON_") && e.getCursor().getItemMeta().getDisplayName().contains("Iron")) || (e.getCurrentItem().getType().name().contains("STONE_") && e.getCursor().getItemMeta().getDisplayName().contains("Stone")) || (e.getCurrentItem().getType().name().contains("WOOD_") && e.getCursor().getItemMeta().getDisplayName().contains("Wooden")))) {
            String name2 = e.getCurrentItem().getItemMeta().getDisplayName();
            if (name2.startsWith(ChatColor.RED + "[+")) {
                name2 = name2.split("] ")[1];
            }
            final double beforemin = Damage.getDamageRange(e.getCurrentItem()).get(0);
            final double beforemax = Damage.getDamageRange(e.getCurrentItem()).get(1);
            final int plus2 = getPlus(e.getCurrentItem());
            if (plus2 < 3) {
                if (e.getCursor().getAmount() > 1) {
                    e.getCursor().setAmount(e.getCursor().getAmount() - 1);
                }
                else if (e.getCursor().getAmount() == 1) {
                    e.setCursor((ItemStack)null);
                }
                p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.25f);
                final Firework fw3 = (Firework)p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                final FireworkMeta fwm3 = fw3.getFireworkMeta();
                final FireworkEffect effect3 = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).trail(true).build();
                fwm3.addEffect(effect3);
                fwm3.setPower(0);
                fw3.setFireworkMeta(fwm3);
                e.setCancelled(true);
                double addedmin = beforemin * 0.05;
                if (addedmin < 1.0) {
                    addedmin = 1.0;
                }
                final int min = (int)(beforemin + addedmin);
                double addedmax = beforemax * 0.05;
                if (addedmax < 1.0) {
                    addedmax = 1.0;
                }
                final int max = (int)(beforemax + addedmax);
                final ItemStack is3 = e.getCurrentItem();
                final ItemMeta im3 = is3.getItemMeta();
                im3.setDisplayName(ChatColor.RED + "[+" + (plus2 + 1) + "] " + name2);
                final List<String> lore3 = (List<String>)im3.getLore();
                lore3.set(0, ChatColor.RED + "DMG: " + min + " - " + max);
                im3.setLore((List)lore3);
                is3.setItemMeta(im3);
                e.setCurrentItem(is3);
            }
            if (plus2 >= 3 && plus2 < 12) {
                if (e.getCursor().getAmount() > 1) {
                    e.getCursor().setAmount(e.getCursor().getAmount() - 1);
                }
                else if (e.getCursor().getAmount() == 1) {
                    e.setCursor((ItemStack)null);
                }
                final Random random2 = new Random();
                final int drop2 = random2.nextInt(100) + 1;
                int doifail2 = 0;
                if (plus2 == 3) {
                    doifail2 = 30;
                }
                if (plus2 == 4) {
                    doifail2 = 40;
                }
                if (plus2 == 5) {
                    doifail2 = 50;
                }
                if (plus2 == 6) {
                    doifail2 = 65;
                }
                if (plus2 == 7) {
                    doifail2 = 75;
                }
                if (plus2 == 8) {
                    doifail2 = 80;
                }
                if (plus2 == 9) {
                    doifail2 = 85;
                }
                if (plus2 == 10) {
                    doifail2 = 90;
                }
                if (plus2 == 11) {
                    doifail2 = 95;
                }
                e.setCancelled(true);
                if (drop2 <= doifail2) {
                    p.getWorld().playSound(p.getLocation(), Sound.FIZZ, 2.0f, 1.25f);
                    ParticleEffect.LAVA.display(0.0f, 0.0f, 0.0f, 5.0f, 10, p.getEyeLocation(), 20.0);
                    e.setCurrentItem((ItemStack)null);
                }
                else {
                    p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.25f);
                    final Firework fw4 = (Firework)p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                    final FireworkMeta fwm4 = fw4.getFireworkMeta();
                    final FireworkEffect effect4 = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).trail(true).build();
                    fwm4.addEffect(effect4);
                    fwm4.setPower(0);
                    fw4.setFireworkMeta(fwm4);
                    e.setCancelled(true);
                    double addedmin2 = beforemin * 0.05;
                    if (addedmin2 < 1.0) {
                        addedmin2 = 1.0;
                    }
                    final int min2 = (int)(beforemin + addedmin2);
                    double addedmax2 = beforemax * 0.05;
                    if (addedmax2 < 1.0) {
                        addedmax2 = 1.0;
                    }
                    final int max2 = (int)(beforemax + addedmax2);
                    final ItemStack is4 = e.getCurrentItem();
                    final ItemMeta im4 = is4.getItemMeta();
                    im4.setDisplayName(ChatColor.RED + "[+" + (plus2 + 1) + "] " + name2);
                    final List<String> lore4 = (List<String>)im4.getLore();
                    lore4.set(0, ChatColor.RED + "DMG: " + min2 + " - " + max2);
                    im4.setLore((List)lore4);
                    is4.setItemMeta(im4);
                    is4.addUnsafeEnchantment(Enchants.glow, 1);
                    e.setCurrentItem(is4);
                }
            }
        }
    }
}
