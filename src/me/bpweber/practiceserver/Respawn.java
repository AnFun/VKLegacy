package me.bpweber.practiceserver;

import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Iterator;
import java.util.Arrays;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.Random;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.io.File;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import java.util.List;
import org.bukkit.event.Listener;

public class Respawn implements Listener
{
    List<Player> dead;
    
    public Respawn() {
        this.dead = new ArrayList<Player>();
    }
    
    public void onEnable() {
        Main.log.info("[Respawn] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        final File file = new File(Main.plugin.getDataFolder(), "respawndata");
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    
    public void onDisable() {
        Main.log.info("[Respawn] has been disabled.");
        final File file = new File(Main.plugin.getDataFolder(), "respawndata");
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(final PlayerDeathEvent e) {
        final Player p = e.getEntity();
        if (!this.dead.contains(p)) {
            this.dead.add(p);
            final Random random = new Random();
            final int wepdrop = random.nextInt(2) + 1;
            final int armor = random.nextInt(4) + 1;
            final List<ItemStack> newInventory = new ArrayList<ItemStack>();
            if (!Alignments.neutral.containsKey(p.getName()) && !Alignments.chaotic.containsKey(p.getName())) {
                ItemStack[] armorContents;
                for (int length = (armorContents = p.getInventory().getArmorContents()).length, j = 0; j < length; ++j) {
                    final ItemStack is = armorContents[j];
                    if (is != null && is.getType() != Material.AIR) {
                        if (Durability.getDuraPercent(is) > 30.0f) {
                            newInventory.add(Durability.takeDura(is, 30.0f));
                        }
                        else {
                            e.getDrops().remove(is);
                            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        }
                    }
                }
                if (p.getInventory().getItem(0) != null && !p.getInventory().getItem(0).getType().name().contains("_PICKAXE")) {
                    final ItemStack is = p.getInventory().getItem(0);
                    if (is.getType().name().contains("_SWORD") || is.getType().name().contains("_AXE") || is.getType().name().contains("_SPADE") || is.getType().name().contains("_HOE") || is.getType().name().contains("_HELMET") || is.getType().name().contains("_CHESTPLATE") || is.getType().name().contains("_BOOTS")) {
                        if (Durability.getDuraPercent(is) > 30.0f) {
                            newInventory.add(Durability.takeDura(is, 30.0f));
                        }
                        else {
                            e.getDrops().remove(is);
                            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        }
                    }
                    else {
                        newInventory.add(is);
                    }
                }
                ItemStack[] contents;
                for (int length2 = (contents = p.getInventory().getContents()).length, k = 0; k < length2; ++k) {
                    final ItemStack is = contents[k];
                    if (is != null && is.getType() != Material.AIR && is.getType().name().contains("_PICKAXE")) {
                        if (Durability.getDuraPercent(is) > 30.0f) {
                            newInventory.add(Durability.takeDura(is, 30.0f));
                        }
                        else {
                            e.getDrops().remove(is);
                            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        }
                    }
                }
            }
            else if (Alignments.neutral.containsKey(p.getName()) && !Alignments.chaotic.containsKey(p.getName())) {
                ItemStack[] armorContents2;
                for (int length3 = (armorContents2 = p.getInventory().getArmorContents()).length, l = 0; l < length3; ++l) {
                    final ItemStack is = armorContents2[l];
                    if (is != null && is.getType() != Material.AIR) {
                        if (Durability.getDuraPercent(is) > 30.0f) {
                            newInventory.add(Durability.takeDura(is, 30.0f));
                        }
                        else {
                            e.getDrops().remove(is);
                            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        }
                    }
                }
                ItemStack[] contents2;
                for (int length4 = (contents2 = p.getInventory().getContents()).length, n = 0; n < length4; ++n) {
                    final ItemStack is = contents2[n];
                    if (is != null && is.getType() != Material.AIR && is.getType().name().contains("_PICKAXE")) {
                        if (Durability.getDuraPercent(is) > 30.0f) {
                            newInventory.add(Durability.takeDura(is, 30.0f));
                        }
                        else {
                            e.getDrops().remove(is);
                            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        }
                    }
                }
            }
            else if (Alignments.neutral.containsKey(p.getName()) && !Alignments.chaotic.containsKey(p.getName())) {
                final List<ItemStack> arm = new ArrayList<ItemStack>();
                ItemStack[] armorContents3;
                for (int length5 = (armorContents3 = p.getInventory().getArmorContents()).length, n2 = 0; n2 < length5; ++n2) {
                    final ItemStack is2 = armorContents3[n2];
                    if (is2 != null && is2.getType() != Material.AIR) {
                        arm.add(is2);
                    }
                }
                if (armor == 1 && arm.size() > 0) {
                    arm.remove(arm.get(random.nextInt(arm.size())));
                }
                if (arm.size() > 0) {
                    for (final ItemStack is2 : arm) {
                        if (Durability.getDuraPercent(is2) > 30.0f) {
                            newInventory.add(Durability.takeDura(is2, 30.0f));
                        }
                        else {
                            e.getDrops().remove(is2);
                            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        }
                    }
                }
                if (wepdrop == 1 && p.getInventory().getItem(0) != null && !p.getInventory().getItem(0).getType().name().contains("_PICKAXE")) {
                    final ItemStack is2 = p.getInventory().getItem(0);
                    if (is2.getType().name().contains("_SWORD") || is2.getType().name().contains("_AXE") || is2.getType().name().contains("_SPADE") || is2.getType().name().contains("_HOE") || is2.getType().name().contains("_HELMET") || is2.getType().name().contains("_CHESTPLATE") || is2.getType().name().contains("_BOOTS")) {
                        if (Durability.getDuraPercent(is2) > 30.0f) {
                            newInventory.add(Durability.takeDura(is2, 30.0f));
                        }
                        else {
                            e.getDrops().remove(is2);
                            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        }
                    }
                    else {
                        newInventory.add(is2);
                    }
                }
                ItemStack[] contents3;
                for (int length6 = (contents3 = p.getInventory().getContents()).length, n3 = 0; n3 < length6; ++n3) {
                    final ItemStack is2 = contents3[n3];
                    if (is2 != null && is2.getType() != Material.AIR && is2.getType().name().contains("_PICKAXE")) {
                        if (Durability.getDuraPercent(is2) > 30.0f) {
                            newInventory.add(Durability.takeDura(is2, 30.0f));
                        }
                        else {
                            e.getDrops().remove(is2);
                            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        }
                    }
                }
            }
            ItemStack[] contents4;
            for (int length7 = (contents4 = p.getInventory().getContents()).length, n4 = 0; n4 < length7; ++n4) {
                final ItemStack is = contents4[n4];
                if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore() && is.getItemMeta().getLore().contains(ChatColor.GRAY + "Permenant Untradeable") && !newInventory.contains(is)) {
                    newInventory.add(is);
                }
            }
            final File file = new File(Main.plugin.getDataFolder() + "/respawndata", String.valueOf(p.getName()) + ".yml");
            final YamlConfiguration config = new YamlConfiguration();
            for (int i = 0; i < newInventory.size(); ++i) {
                config.set(new StringBuilder().append(i).toString(), (Object)newInventory.get(i));
            }
            try {
                config.save(file);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            for (final ItemStack is3 : newInventory) {
                if (is3 != null) {
                    final ItemMeta meta = is3.getItemMeta();
                    meta.setLore((List)Arrays.asList("notarealitem"));
                    is3.setItemMeta(meta);
                }
            }
        }
    }
    
    @EventHandler
    public void onRespawn(final PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        if (this.dead.contains(p)) {
            this.dead.remove(p);
        }
        final File file = new File(Main.plugin.getDataFolder() + "/respawndata", String.valueOf(p.getName()) + ".yml");
        final YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        for (int i = 0; i < p.getInventory().getSize(); ++i) {
            if (config.contains(new StringBuilder().append(i).toString())) {
                e.getPlayer().getInventory().addItem(new ItemStack[] { config.getItemStack(new StringBuilder().append(i).toString()) });
            }
        }
        final Random random = new Random();
        final int wep = random.nextInt(2) + 1;
        if (wep == 1) {
            final ItemStack S = new ItemStack(Material.WOOD_SWORD);
            final ItemMeta smeta = S.getItemMeta();
            smeta.setDisplayName(ChatColor.WHITE + "Training Sword");
            final List<String> slore = new ArrayList<String>();
            slore.add(ChatColor.RED + "DMG: 3 - 4");
            slore.add(ChatColor.GRAY + "Untradeable");
            smeta.setLore((List)slore);
            S.setItemMeta(smeta);
            p.getInventory().addItem(new ItemStack[] { S });
        }
        if (wep == 2) {
            final ItemStack S = new ItemStack(Material.WOOD_AXE);
            final ItemMeta smeta = S.getItemMeta();
            smeta.setDisplayName(ChatColor.WHITE + "Training Hatchet");
            final List<String> slore = new ArrayList<String>();
            slore.add(ChatColor.RED + "DMG: 2 - 5");
            slore.add(ChatColor.GRAY + "Untradeable");
            smeta.setLore((List)slore);
            S.setItemMeta(smeta);
            p.getInventory().addItem(new ItemStack[] { S });
        }
        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)1);
        final ItemMeta potmeta = pot.getItemMeta();
        potmeta.setDisplayName(ChatColor.WHITE + "Minor Health Potion");
        potmeta.setLore((List)Arrays.asList(ChatColor.GRAY + "A potion that restores " + ChatColor.WHITE + "15HP", ChatColor.GRAY + "Untradeable"));
        final PotionMeta pm = (PotionMeta)potmeta;
        pm.clearCustomEffects();
        pm.addCustomEffect(PotionEffectType.HEAL.createEffect(0, 0), true);
        pot.setItemMeta((ItemMeta)pm);
        for (int t = 0; t < 3; ++t) {
            p.getInventory().addItem(new ItemStack[] { pot });
        }
        final ItemStack bread = new ItemStack(Material.BREAD);
        final ItemMeta breadmeta = bread.getItemMeta();
        breadmeta.setLore((List)Arrays.asList(ChatColor.GRAY + "Untradeable"));
        bread.setItemMeta(breadmeta);
        for (int t2 = 0; t2 < 2; ++t2) {
            p.getInventory().setItem(p.getInventory().firstEmpty(), bread);
        }
        if (!p.getInventory().contains(Material.QUARTZ)) {
            p.getInventory().addItem(new ItemStack[] { Hearthstone.hearthstone() });
        }
        if (!p.getInventory().contains(Material.WRITTEN_BOOK)) {
            p.getInventory().addItem(new ItemStack[] { Journal.journal() });
        }
        e.getPlayer().setMaxHealth(50.0);
        e.getPlayer().setHealth(50.0);
        p.getInventory().setHeldItemSlot(0);
        new BukkitRunnable() {
            public void run() {
                p.setLevel(100);
                p.setExp(1.0f);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                p.playSound(p.getLocation(), Sound.ZOMBIE_UNFECT, 1.0f, 1.5f);
            }
        }.runTaskLater(Main.plugin, 1L);
    }
}
