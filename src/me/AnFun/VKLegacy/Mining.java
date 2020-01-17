package me.AnFun.VKLegacy;

import org.bukkit.Effect;
import java.util.Random;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import java.util.HashMap;
import org.bukkit.Location;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.event.Listener;

public class Mining implements Listener
{
    ConcurrentHashMap<Location, Integer> regenores;
    HashMap<Location, Material> oretypes;
    
    public Mining() {
        this.regenores = new ConcurrentHashMap<Location, Integer>();
        this.oretypes = new HashMap<Location, Material>();
    }
    
    public void onEnable() {
        Main.log.info("[Mining] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Location loc : Mining.this.regenores.keySet()) {
                    int time = Mining.this.regenores.get(loc);
                    if (time < 1) {
                        Mining.this.regenores.remove(loc);
                        loc.getBlock().setType((Material)Mining.this.oretypes.get(loc));
                    }
                    else {
                        --time;
                        Mining.this.regenores.put(loc, time);
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 20L, 20L);
    }
    
    public void onDisable() {
        Main.log.info("[Mining] has been disabled.");
    }
    
    public static ItemStack ore(final int tier) {
        Material m = null;
        ChatColor cc = ChatColor.WHITE;
        String name = "";
        String lore = "";
        if (tier == 1) {
            m = Material.COAL_ORE;
            name = "Coal";
            lore = "A chunk of coal ore.";
        }
        if (tier == 2) {
            m = Material.EMERALD_ORE;
            name = "Emerald";
            lore = "An unrefined piece of emerald ore.";
            cc = ChatColor.GREEN;
        }
        if (tier == 3) {
            m = Material.IRON_ORE;
            name = "Iron";
            lore = "A piece of raw iron.";
            cc = ChatColor.AQUA;
        }
        if (tier == 4) {
            m = Material.DIAMOND_ORE;
            name = "Diamond";
            lore = "A sharp chunk of diamond ore.";
            cc = ChatColor.LIGHT_PURPLE;
        }
        if (tier == 5) {
            m = Material.GOLD_ORE;
            name = "Gold";
            lore = "A sparkling piece of gold ore.";
            cc = ChatColor.YELLOW;
        }
        final ItemStack is = new ItemStack(m);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(cc + name + " Ore");
        im.setLore((List)Arrays.asList(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append(lore).toString()));
        is.setItemMeta(im);
        return is;
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        int seconds = 0;
        int level = 0;
        if (e.getAction() == Action.LEFT_CLICK_BLOCK && p.getItemInHand().getType().name().contains("_PICKAXE") && e.getClickedBlock().getType().name().contains("_ORE")) {
            final int picktier = ProfessionMechanics.getPickaxeTier(p.getItemInHand());
            final int oretier = ProfessionMechanics.getOreTier(e.getClickedBlock().getType());
            if (oretier == 1) {
                if (picktier == 1) {
                    seconds = 5;
                    level = 1;
                }
                if (picktier == 2) {
                    seconds = 2;
                    level = 1;
                }
                if (picktier == 3) {
                    seconds = 2;
                    level = 1;
                }
                if (picktier == 4) {
                    seconds = 2;
                    level = 1;
                }
                if (picktier == 5) {
                    seconds = 2;
                    level = 0;
                }
            }
            if (oretier == 2) {
                if (picktier == 2) {
                    seconds = 0;
                    level = 0;
                }
                if (picktier == 3) {
                    seconds = 2;
                    level = 2;
                }
                if (picktier == 4) {
                    seconds = 2;
                    level = 2;
                }
                if (picktier == 5) {
                    seconds = 2;
                    level = 0;
                }
            }
            if (oretier == 3) {
                if (picktier == 3) {
                    seconds = 5;
                    level = 2;
                }
                if (picktier == 4) {
                    seconds = 2;
                    level = 2;
                }
                if (picktier == 5) {
                    seconds = 0;
                    level = 0;
                }
            }
            if (oretier == 4) {
                if (picktier == 4) {
                    seconds = 5;
                    level = 3;
                }
                if (picktier == 5) {
                    seconds = 2;
                    level = 0;
                }
            }
            if (oretier == 5 && picktier == 5) {
                seconds = 5;
                level = 2;
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, seconds * 20, level), true);
        }
    }
    
    @EventHandler
    public void onBreak(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        if (!p.getItemInHand().getType().name().contains("_PICKAXE")) {
            return;
        }
        final Material m = e.getBlock().getType();
        if (m == Material.COAL_ORE || m == Material.EMERALD_ORE || m == Material.IRON_ORE || m == Material.DIAMOND_ORE || m == Material.GOLD_ORE) {
            final Random random = new Random();
            final int dura = random.nextInt(2000);
            final int fail = random.nextInt(100);
            if (dura < p.getItemInHand().getType().getMaxDurability()) {
                p.getItemInHand().setDurability((short)(p.getItemInHand().getDurability() + 1));
            }
            if (p.getItemInHand().getDurability() >= p.getItemInHand().getType().getMaxDurability()) {
                p.setItemInHand((ItemStack)null);
            }
            if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
                return;
            }
            p.updateInventory();
            this.oretypes.put(e.getBlock().getLocation(), m);
            final int oretier = ProfessionMechanics.getOreTier(m);
            final int level = ProfessionMechanics.getPickaxeLevel(p.getItemInHand());
            if (oretier > 0 && oretier <= ProfessionMechanics.getPickaxeTier(p.getItemInHand())) {
                e.setCancelled(true);
                e.getBlock().setType(Material.STONE);
                if (p.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
                    p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                }
                this.regenores.put(e.getBlock().getLocation(), oretier * 30);
                if (fail < ProfessionMechanics.getFailPercent(oretier, level)) {
                    this.addToInv(p, ore(oretier));
                    final int gemfind = random.nextInt(100);
                    final int dore = random.nextInt(100);
                    final int tore = random.nextInt(100);
                    if (gemfind < ProfessionMechanics.getPickEnchants(p.getItemInHand(), "GEM FIND")) {
                        int gemamt = 0;
                        if (oretier == 1) {
                            gemamt = random.nextInt(32) + 1;
                        }
                        if (oretier == 2) {
                            gemamt = random.nextInt(33) + 32;
                        }
                        if (oretier == 3) {
                            gemamt = random.nextInt(65) + 64;
                        }
                        if (oretier == 4) {
                            gemamt = random.nextInt(257) + 256;
                        }
                        if (oretier == 5) {
                            gemamt = random.nextInt(513) + 512;
                        }
                        p.sendMessage("          " + ChatColor.YELLOW + ChatColor.BOLD + "FOUND " + gemamt + " GEM(s)");
                        while (gemamt > 0) {
                            final ItemStack gem = new ItemStack(Material.EMERALD, 64);
                            final ItemMeta gm = gem.getItemMeta();
                            gm.setDisplayName(ChatColor.WHITE + "Gem");
                            gm.setLore((List)Arrays.asList(ChatColor.GRAY + "The currency of Andalucia"));
                            gem.setItemMeta(gm);
                            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), gem);
                            gemamt -= 64;
                        }
                        final ItemStack gem = new ItemStack(Material.EMERALD, gemamt);
                        final ItemMeta gm = gem.getItemMeta();
                        gm.setDisplayName(ChatColor.WHITE + "Gem");
                        gm.setLore((List)Arrays.asList(ChatColor.GRAY + "The currency of Andalucia"));
                        gem.setItemMeta(gm);
                        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), gem);
                    }
                    if (dore < ProfessionMechanics.getPickEnchants(p.getItemInHand(), "DOUBLE ORE")) {
                        p.sendMessage("          " + ChatColor.YELLOW + ChatColor.BOLD + "DOUBLE ORE DROP" + ChatColor.YELLOW + " (2x)");
                        this.addToInv(p, ore(oretier));
                    }
                    if (tore < ProfessionMechanics.getPickEnchants(p.getItemInHand(), "TRIPLE ORE")) {
                        p.sendMessage("          " + ChatColor.YELLOW + ChatColor.BOLD + "TRIPLE ORE DROP" + ChatColor.YELLOW + " (3x)");
                        this.addToInv(p, ore(oretier));
                        this.addToInv(p, ore(oretier));
                    }
                    final int xp = ProfessionMechanics.getExpFromOre(oretier);
                    ProfessionMechanics.addExp(p, p.getItemInHand(), xp);
                }
                else {
                    e.setCancelled(true);
                    p.sendMessage(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("You fail to gather any ore.").toString());
                    e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, (Object)Material.STONE);
                }
            }
            else {
                e.setCancelled(true);
                p.sendMessage(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("You cannot mine this ore.").toString());
            }
        }
    }
    
    public void addToInv(final Player p, final ItemStack is) {
        ItemStack[] contents;
        for (int length = (contents = p.getInventory().getContents()).length, j = 0; j < length; ++j) {
            final ItemStack i = contents[j];
            if (i != null && i.getType() != Material.AIR) {
                final int amt = i.getAmount();
                if (amt < 64 && i.getType() == is.getType() && i.getItemMeta().equals(is.getItemMeta())) {
                    p.getInventory().addItem(new ItemStack[] { is });
                    return;
                }
            }
        }
        final int slot = p.getInventory().firstEmpty();
        if (slot != -1) {
            p.getInventory().setItem(slot, is);
        }
        else {
            p.getWorld().dropItemNaturally(p.getLocation(), is);
        }
    }
}
