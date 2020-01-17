package me.AnFun.VKLegacy;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Speedfish implements Listener
{
    public void onEnable() {
        Main.log.info("[Speedfish] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
    }
    
    public void onDisable() {
        Main.log.info("[Speedfish] has been disabled.");
    }
    
    public static ItemStack fish(final int tier, final boolean inshop) {
        final ItemStack is = new ItemStack(Material.RAW_FISH);
        final ItemMeta im = is.getItemMeta();
        final List<String> lore = new ArrayList<String>();
        String name = "";
        int price = 0;
        if (tier == 1) {
            price = 100;
            name = ChatColor.WHITE + "Raw Shrimp of Lesser Agility";
            lore.add(ChatColor.RED + "SPEED (I) BUFF " + ChatColor.GRAY + "(15s)");
            lore.add(ChatColor.RED + "-10% HUNGER " + ChatColor.GRAY + "(instant)");
            lore.add(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("A pink scaled crustacean.").toString());
        }
        if (tier == 2) {
            price = 200;
            name = ChatColor.GREEN + "Raw Herring of Agility";
            lore.add(ChatColor.RED + "SPEED (I) BUFF " + ChatColor.GRAY + "(20s)");
            lore.add(ChatColor.RED + "-20% HUNGER " + ChatColor.GRAY + "(instant)");
            lore.add(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("A colourful and medium sized fish.").toString());
        }
        if (tier == 3) {
            price = 400;
            name = ChatColor.AQUA + "Raw Salmon of Lasting Agility";
            lore.add(ChatColor.RED + "SPEED (I) BUFF " + ChatColor.GRAY + "(30s)");
            lore.add(ChatColor.RED + "-30% HUNGER " + ChatColor.GRAY + "(instant)");
            lore.add(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("A beautiful jumping fish.").toString());
        }
        if (tier == 4) {
            price = 800;
            name = ChatColor.LIGHT_PURPLE + "Raw Lobster of Bursting Agility";
            lore.add(ChatColor.RED + "SPEED (II) BUFF " + ChatColor.GRAY + "(15s)");
            lore.add(ChatColor.RED + "-40% HUNGER " + ChatColor.GRAY + "(instant)");
            lore.add(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("A large red crustacean.").toString());
        }
        if (tier == 5) {
            price = 1600;
            name = ChatColor.YELLOW + "Raw Swordfish of Godlike Speed";
            lore.add(ChatColor.RED + "SPEED (II) BUFF " + ChatColor.GRAY + "(30s)");
            lore.add(ChatColor.RED + "-50% HUNGER " + ChatColor.GRAY + "(instant)");
            lore.add(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("An elongated fish with a long bill.").toString());
        }
        if (inshop) {
            lore.add(ChatColor.GREEN + "Price: " + ChatColor.WHITE + price + "g");
        }
        im.setDisplayName(name);
        im.setLore((List)lore);
        is.setItemMeta(im);
        return is;
    }
    
    int getSpeed(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            for (final String line : is.getItemMeta().getLore()) {
                if (line.contains("SPEED")) {
                    if (line.contains("(I)")) {
                        return 1;
                    }
                    if (line.contains("(II)")) {
                        return 2;
                    }
                    continue;
                }
            }
        }
        return 0;
    }
    
    int getDuration(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            for (final String line : is.getItemMeta().getLore()) {
                if (line.contains("SPEED")) {
                    try {
                        return Integer.parseInt(line.split("\\(")[2].split("s\\)")[0]);
                    }
                    catch (Exception e) {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }
    
    int getHunger(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            for (final String line : is.getItemMeta().getLore()) {
                if (line.contains("HUNGER")) {
                    try {
                        final int amt = Integer.parseInt(line.split("-")[1].split("%")[0]);
                        return amt / 5;
                    }
                    catch (Exception e) {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }
    
    @EventHandler
    public void onSpeedFish(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (p.getItemInHand().getType() == Material.COOKED_FISH && p.getItemInHand().getItemMeta().hasDisplayName() && p.getItemInHand().getItemMeta().getDisplayName().contains("Cooked") && p.getItemInHand().getItemMeta().hasLore()) {
                if (e.hasBlock() && (e.getClickedBlock().getType() == Material.FURNACE || e.getClickedBlock().getType() == Material.BURNING_FURNACE || e.getClickedBlock().getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0.0, 1.0, 0.0)).getType() == Material.FIRE || e.getClickedBlock().getRelative(e.getBlockFace()).getType() == Material.STATIONARY_LAVA)) {
                    return;
                }
                if (p.hasPotionEffect(PotionEffectType.SPEED)) {
                    e.setCancelled(true);
                }
                else {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, this.getDuration(p.getItemInHand()) * 20, this.getSpeed(p.getItemInHand())));
                    p.playSound(p.getLocation(), Sound.EAT, 1.0f, 1.0f);
                    if (p.getFoodLevel() + this.getHunger(p.getItemInHand()) > 20) {
                        p.setFoodLevel(20);
                    }
                    else {
                        p.setFoodLevel(p.getFoodLevel() + this.getHunger(p.getItemInHand()));
                    }
                    if (p.getItemInHand().getAmount() > 1) {
                        p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                    }
                    else {
                        e.setCancelled(true);
                        p.setItemInHand((ItemStack)null);
                    }
                }
            }
            else if (p.getItemInHand().getType() == Material.RAW_FISH && p.getItemInHand().getItemMeta().hasDisplayName() && p.getItemInHand().getItemMeta().getDisplayName().contains("Raw")) {
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.FURNACE) {
                    return;
                }
                p.sendMessage(ChatColor.YELLOW + "To cook, " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.YELLOW + " any heat source.");
                p.sendMessage(ChatColor.GRAY + "Ex. Fire, Lava, Furnace");
            }
        }
    }
    
    @EventHandler
    public void onFishCook(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && p.getItemInHand().getType() == Material.RAW_FISH && p.getItemInHand().getItemMeta().hasDisplayName() && (e.getClickedBlock().getType() == Material.FURNACE || e.getClickedBlock().getType() == Material.BURNING_FURNACE || e.getClickedBlock().getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0.0, 1.0, 0.0)).getType() == Material.FIRE || e.getClickedBlock().getRelative(e.getBlockFace()).getType() == Material.STATIONARY_LAVA)) {
            e.setCancelled(true);
            p.playSound(p.getLocation(), Sound.FIZZ, 1.0f, 0.0f);
            final ItemStack is = p.getItemInHand();
            final ItemMeta im = is.getItemMeta();
            String name = im.getDisplayName();
            is.setType(Material.COOKED_FISH);
            name = name.replaceAll("Raw", "Cooked");
            im.setDisplayName(name);
            is.setItemMeta(im);
            p.setItemInHand(is);
        }
    }
}
