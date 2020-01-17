package me.AnFun.VKLegacy;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.World;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Location;
import java.util.HashMap;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public class LootChests implements Listener, CommandExecutor
{
    static HashMap<Location, Integer> loot;
    static HashMap<Location, Integer> respawn;
    static HashMap<String, Location> creatingloot;
    HashMap<Location, Inventory> opened;
    HashMap<Player, Location> viewers;
    
    static {
        LootChests.loot = new HashMap<Location, Integer>();
        LootChests.respawn = new HashMap<Location, Integer>();
        LootChests.creatingloot = new HashMap<String, Location>();
    }
    
    public LootChests() {
        this.opened = new HashMap<Location, Inventory>();
        this.viewers = new HashMap<Player, Location>();
    }
    
    public void onEnable() {
        Main.log.info("[LootChests] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Location loc : LootChests.loot.keySet()) {
                    if (LootChests.respawn.containsKey(loc)) {
                        if (LootChests.respawn.get(loc) >= 1) {
                            LootChests.respawn.put(loc, LootChests.respawn.get(loc) - 1);
                        }
                        else {
                            LootChests.respawn.remove(loc);
                        }
                    }
                    else {
                        if (!loc.getWorld().getChunkAt(loc).isLoaded() || loc.getWorld().getBlockAt(loc).getType().equals((Object)Material.GLOWSTONE)) {
                            continue;
                        }
                        loc.getWorld().getBlockAt(loc).setType(Material.CHEST);
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 20L, 20L);
        final File file = new File(Main.plugin.getDataFolder(), "loot.yml");
        final YamlConfiguration config = new YamlConfiguration();
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            config.load(file);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        for (final String key : config.getKeys(false)) {
            final int val = config.getInt(key);
            final String[] str = key.split(",");
            final World world = Bukkit.getWorld(str[0]);
            final double x = Double.valueOf(str[1]);
            final double y = Double.valueOf(str[2]);
            final double z = Double.valueOf(str[3]);
            final Location loc = new Location(world, x, y, z);
            LootChests.loot.put(loc, val);
        }
    }
    
    public void onDisable() {
        Main.log.info("[LootChests] has been disabled.");
        final File file = new File(Main.plugin.getDataFolder(), "loot.yml");
        if (file.exists()) {
            file.delete();
        }
        final YamlConfiguration config = new YamlConfiguration();
        for (final Location loc : LootChests.loot.keySet()) {
            final String s = String.valueOf(loc.getWorld().getName()) + "," + (int)loc.getX() + "," + (int)loc.getY() + "," + (int)loc.getZ();
            config.set(s, (Object)LootChests.loot.get(loc));
            try {
                config.save(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player)sender;
            if (p.isOp()) {
                if (cmd.getName().equalsIgnoreCase("showloot")) {
                    if (args.length != 1) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax. ").append(ChatColor.RED).append("/showloot <radius>").toString());
                        return true;
                    }
                    int radius = 0;
                    try {
                        radius = Integer.parseInt(args[0]);
                    }
                    catch (Exception e) {
                        radius = 0;
                    }
                    Location loc = p.getLocation();
                    final World w = loc.getWorld();
                    final int x = (int)loc.getX();
                    final int y = (int)loc.getY();
                    final int z = (int)loc.getZ();
                    int count = 0;
                    for (int i = -radius; i <= radius; ++i) {
                        for (int j = -radius; j <= radius; ++j) {
                            for (int k = -radius; k <= radius; ++k) {
                                loc = w.getBlockAt(x + i, y + j, z + k).getLocation();
                                if (LootChests.loot.containsKey(loc)) {
                                    ++count;
                                    loc.getBlock().setType(Material.GLOWSTONE);
                                }
                            }
                        }
                    }
                    p.sendMessage(ChatColor.YELLOW + "Displaying " + count + " lootchests in a " + radius + " block radius...");
                    p.sendMessage(ChatColor.GRAY + "Break them to unregister the spawn point.");
                }
                if (cmd.getName().equalsIgnoreCase("hideloot")) {
                    if (args.length != 1) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax. ").append(ChatColor.RED).append("/hideloot <radius>").toString());
                        return true;
                    }
                    int radius = 0;
                    try {
                        radius = Integer.parseInt(args[0]);
                    }
                    catch (Exception e) {
                        radius = 0;
                    }
                    Location loc = p.getLocation();
                    final World w = loc.getWorld();
                    final int x = (int)loc.getX();
                    final int y = (int)loc.getY();
                    final int z = (int)loc.getZ();
                    int count = 0;
                    for (int i = -radius; i <= radius; ++i) {
                        for (int j = -radius; j <= radius; ++j) {
                            for (int k = -radius; k <= radius; ++k) {
                                loc = w.getBlockAt(x + i, y + j, z + k).getLocation();
                                if (LootChests.loot.containsKey(loc) && loc.getBlock().getType() == Material.GLOWSTONE) {
                                    loc.getBlock().setType(Material.AIR);
                                    ++count;
                                }
                            }
                        }
                    }
                    p.sendMessage(ChatColor.YELLOW + "Hiding " + count + " loot chests in a " + radius + " block radius...");
                }
            }
        }
        return false;
    }
    
    public boolean isMobNear(final Player p) {
        for (final Entity ent : p.getNearbyEntities(6.0, 6.0, 6.0)) {
            if (ent instanceof LivingEntity && !(ent instanceof Player) && !(ent instanceof Horse)) {
                return true;
            }
        }
        return false;
    }
    
    @EventHandler
    public void onChestClick(final PlayerInteractEvent e) {
        if (e.getPlayer() instanceof Player) {
            final Player p = e.getPlayer();
            if (e.hasBlock()) {
                if (e.getClickedBlock().getType() == Material.CHEST) {
                    final Location loc = e.getClickedBlock().getLocation();
                    if (LootChests.loot.containsKey(loc)) {
                        e.setCancelled(true);
                        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            if (this.isMobNear(p)) {
                                p.sendMessage(ChatColor.RED + "It is " + ChatColor.BOLD + "NOT" + ChatColor.RED + " safe to open that right now.");
                                p.sendMessage(ChatColor.GRAY + "Eliminate the monsters in the area first.");
                            }
                            else if (!this.opened.containsKey(loc)) {
                                final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, "Loot Chest");
                                inv.addItem(new ItemStack[] { LootDrops.createLootDrop(LootChests.loot.get(loc)) });
                                p.openInventory(inv);
                                p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1.0f, 1.0f);
                                this.viewers.put(e.getPlayer(), loc);
                                this.opened.put(loc, inv);
                            }
                            else {
                                p.openInventory((Inventory)this.opened.get(loc));
                                p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1.0f, 1.0f);
                                this.viewers.put(e.getPlayer(), loc);
                            }
                        }
                        else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                            if (this.isMobNear(p)) {
                                p.sendMessage(ChatColor.RED + "It is " + ChatColor.BOLD + "NOT" + ChatColor.RED + " safe to open that right now.");
                                p.sendMessage(ChatColor.GRAY + "Eliminate the monsters in the area first.");
                            }
                            else if (this.opened.containsKey(loc)) {
                                loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                                p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 1.2f);
                                ItemStack[] contents;
                                for (int length = (contents = this.opened.get(loc).getContents()).length, i = 0; i < length; ++i) {
                                    final ItemStack is = contents[i];
                                    if (is != null) {
                                        loc.getWorld().dropItemNaturally(loc, is);
                                    }
                                }
                                this.opened.remove(loc);
                                final int tier = LootChests.loot.get(loc);
                                LootChests.respawn.put(loc, 60 * tier);
                                for (final Player v : this.viewers.keySet()) {
                                    if (this.viewers.get(v).equals((Object)loc)) {
                                        this.viewers.remove(v);
                                        v.closeInventory();
                                        v.playSound(v.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 1.2f);
                                        v.playSound(v.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
                                    }
                                }
                            }
                            else {
                                loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                                loc.getWorld().playEffect(loc, Effect.STEP_SOUND, (Object)Material.WOOD);
                                p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 1.2f);
                                loc.getWorld().dropItemNaturally(loc, LootDrops.createLootDrop(LootChests.loot.get(loc)));
                                final int tier = LootChests.loot.get(loc);
                                LootChests.respawn.put(loc, 60 * tier);
                                for (final Player v : this.viewers.keySet()) {
                                    if (this.viewers.get(v).equals((Object)loc)) {
                                        this.viewers.remove(v);
                                        v.closeInventory();
                                        v.playSound(v.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 1.2f);
                                        v.playSound(v.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
                                    }
                                }
                            }
                        }
                    }
                    else if (!p.isOp()) {
                        e.setCancelled(true);
                        p.sendMessage(ChatColor.GRAY + "The chest is locked.");
                    }
                }
                else if (e.getClickedBlock().getType() == Material.GLOWSTONE && p.isOp()) {
                    final Location loc = e.getClickedBlock().getLocation();
                    if (e.getAction() == Action.RIGHT_CLICK_BLOCK && getPlayerTier(p) > 0) {
                        e.setCancelled(true);
                        LootChests.loot.put(loc, getPlayerTier(p));
                        p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("     *** LOOT CHEST CREATED ***").toString());
                        loc.getWorld().getBlockAt(loc).setType(Material.CHEST);
                        loc.getWorld().playEffect(loc, Effect.STEP_SOUND, (Object)Material.CHEST);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        if (p.isOp() && e.getBlock().getType().equals((Object)Material.GLOWSTONE)) {
            final Location loc = e.getBlock().getLocation();
            if (LootChests.loot.containsKey(loc)) {
                LootChests.loot.remove(loc);
                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("     *** LOOT CHEST REMOVED ***").toString());
                loc.getWorld().playEffect(loc, Effect.STEP_SOUND, (Object)Material.CHEST);
            }
        }
    }
    
    public static int getPlayerTier(final Player e) {
        final ItemStack is = e.getItemInHand();
        if (is != null && is.getType() != Material.AIR) {
            if (is.getType().name().contains("WOOD_")) {
                return 1;
            }
            if (is.getType().name().contains("STONE_")) {
                return 2;
            }
            if (is.getType().name().contains("IRON_")) {
                return 3;
            }
            if (is.getType().name().contains("DIAMOND_")) {
                return 4;
            }
            if (is.getType().name().contains("GOLD_")) {
                return 5;
            }
        }
        return 0;
    }
    
    @EventHandler
    public void onCloseChest(final InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            final Player p = (Player)e.getPlayer();
            if (e.getInventory().getName().contains("Loot Chest") && this.viewers.containsKey(p)) {
                final Location loc = this.viewers.get(p);
                this.viewers.remove(p);
                p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
                boolean isempty = true;
                ItemStack[] contents;
                for (int length = (contents = e.getInventory().getContents()).length, i = 0; i < length; ++i) {
                    final ItemStack itms = contents[i];
                    if (itms != null && itms.getType() != Material.AIR) {
                        isempty = false;
                    }
                }
                if (isempty) {
                    loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                    loc.getWorld().playEffect(loc, Effect.STEP_SOUND, (Object)Material.WOOD);
                    p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 1.2f);
                    this.opened.remove(loc);
                    final int tier = LootChests.loot.get(loc);
                    LootChests.respawn.put(loc, 60 * tier);
                    for (final Player v : this.viewers.keySet()) {
                        if (this.viewers.get(v).equals((Object)loc)) {
                            this.viewers.remove(v);
                            v.closeInventory();
                            v.playSound(v.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5f, 1.2f);
                            v.playSound(v.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
                        }
                    }
                }
            }
        }
    }
}
