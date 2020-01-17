package me.AnFun.VKLegacy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class LootChests implements Listener, CommandExecutor {
  static HashMap<Location, Integer> loot = new HashMap<>();
  
  static HashMap<Location, Integer> respawn = new HashMap<>();
  
  static HashMap<String, Location> creatingloot = new HashMap<>();
  
  public void onEnable() {
    Main.log.info("[LootChests] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    (new BukkitRunnable() {
        public void run() {
          for (Location loc : LootChests.loot.keySet()) {
            if (LootChests.respawn.containsKey(loc)) {
              if (((Integer)LootChests.respawn.get(loc)).intValue() >= 1) {
                LootChests.respawn.put(loc, Integer.valueOf(((Integer)LootChests.respawn.get(loc)).intValue() - 1));
                continue;
              } 
              LootChests.respawn.remove(loc);
              continue;
            } 
            if (loc.getWorld().getChunkAt(loc).isLoaded() && 
              !loc.getWorld().getBlockAt(loc).getType().equals(Material.GLOWSTONE))
              loc.getWorld().getBlockAt(loc).setType(Material.CHEST); 
          } 
        }
      }).runTaskTimer(Main.plugin, 20L, 20L);
    File file = new File(Main.plugin.getDataFolder(), "loot.yml");
    YamlConfiguration config = new YamlConfiguration();
    if (!file.exists())
      try {
        file.createNewFile();
      } catch (IOException e1) {
        e1.printStackTrace();
      }  
    try {
      config.load(file);
    } catch (Exception e) {
      e.printStackTrace();
    } 
    for (String key : config.getKeys(false)) {
      int val = config.getInt(key);
      String[] str = key.split(",");
      World world = Bukkit.getWorld(str[0]);
      double x = Double.valueOf(str[1]).doubleValue();
      double y = Double.valueOf(str[2]).doubleValue();
      double z = Double.valueOf(str[3]).doubleValue();
      Location loc = new Location(world, x, y, z);
      loot.put(loc, Integer.valueOf(val));
    } 
  }
  
  public void onDisable() {
    Main.log.info("[LootChests] has been disabled.");
    File file = new File(Main.plugin.getDataFolder(), "loot.yml");
    if (file.exists())
      file.delete(); 
    YamlConfiguration config = new YamlConfiguration();
    for (Location loc : loot.keySet()) {
      String s = String.valueOf(loc.getWorld().getName()) + "," + (int)loc.getX() + "," + (int)loc.getY() + "," + 
        (int)loc.getZ();
      config.set(s, loot.get(loc));
      try {
        config.save(file);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player)sender;
      if (p.isOp()) {
        if (cmd.getName().equalsIgnoreCase("showloot")) {
          if (args.length != 1) {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED + 
                "/showloot <radius>");
            return true;
          } 
          int radius = 0;
          try {
            radius = Integer.parseInt(args[0]);
          } catch (Exception e) {
            radius = 0;
          } 
          Location loc = p.getLocation();
          World w = loc.getWorld();
          int x = (int)loc.getX();
          int y = (int)loc.getY();
          int z = (int)loc.getZ();
          int count = 0;
          for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
              for (int k = -radius; k <= radius; k++) {
                loc = w.getBlockAt(x + i, y + j, z + k).getLocation();
                if (loot.containsKey(loc)) {
                  count++;
                  loc.getBlock().setType(Material.GLOWSTONE);
                } 
              } 
            } 
          } 
          p.sendMessage(ChatColor.YELLOW + "Displaying " + count + " lootchests in a " + radius + 
              " block radius...");
          p.sendMessage(ChatColor.GRAY + "Break them to unregister the spawn point.");
        } 
        if (cmd.getName().equalsIgnoreCase("hideloot")) {
          if (args.length != 1) {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED + 
                "/hideloot <radius>");
            return true;
          } 
          int radius = 0;
          try {
            radius = Integer.parseInt(args[0]);
          } catch (Exception e) {
            radius = 0;
          } 
          Location loc = p.getLocation();
          World w = loc.getWorld();
          int x = (int)loc.getX();
          int y = (int)loc.getY();
          int z = (int)loc.getZ();
          int count = 0;
          for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
              for (int k = -radius; k <= radius; k++) {
                loc = w.getBlockAt(x + i, y + j, z + k).getLocation();
                if (loot.containsKey(loc) && 
                  loc.getBlock().getType() == Material.GLOWSTONE) {
                  loc.getBlock().setType(Material.AIR);
                  count++;
                } 
              } 
            } 
          } 
          p.sendMessage(
              ChatColor.YELLOW + "Hiding " + count + " loot chests in a " + radius + " block radius...");
        } 
      } 
    } 
    return false;
  }
  
  public boolean isMobNear(Player p) {
    for (Entity ent : p.getNearbyEntities(6.0D, 6.0D, 6.0D)) {
      if (ent instanceof org.bukkit.entity.LivingEntity && !(ent instanceof Player) && !(ent instanceof org.bukkit.entity.Horse))
        return true; 
    } 
    return false;
  }
  
  HashMap<Location, Inventory> opened = new HashMap<>();
  
  HashMap<Player, Location> viewers = new HashMap<>();
  
  @EventHandler
  public void onChestClick(PlayerInteractEvent e) {
    if (e.getPlayer() instanceof Player) {
      Player p = e.getPlayer();
      if (e.hasBlock())
        if (e.getClickedBlock().getType() == Material.CHEST) {
          Location loc = e.getClickedBlock().getLocation();
          if (loot.containsKey(loc)) {
            e.setCancelled(true);
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
              if (isMobNear(p)) {
                p.sendMessage(ChatColor.RED + "It is " + ChatColor.BOLD + "NOT" + ChatColor.RED + 
                    " safe to open that right now.");
                p.sendMessage(ChatColor.GRAY + "Eliminate the monsters in the area first.");
              } else if (!this.opened.containsKey(loc)) {
                Inventory inv = Bukkit.createInventory(null, 27, "Loot Chest");
                inv.addItem(new ItemStack[] { LootDrops.createLootDrop(((Integer)loot.get(loc)).intValue()) });
                p.openInventory(inv);
                p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
                this.viewers.put(e.getPlayer(), loc);
                this.opened.put(loc, inv);
              } else {
                p.openInventory(this.opened.get(loc));
                p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
                this.viewers.put(e.getPlayer(), loc);
              } 
            } else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
              if (isMobNear(p)) {
                p.sendMessage(ChatColor.RED + "It is " + ChatColor.BOLD + "NOT" + ChatColor.RED + 
                    " safe to open that right now.");
                p.sendMessage(ChatColor.GRAY + "Eliminate the monsters in the area first.");
              } else if (this.opened.containsKey(loc)) {
                loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
                byte b;
                int i;
                ItemStack[] arrayOfItemStack;
                for (i = (arrayOfItemStack = ((Inventory)this.opened.get(loc)).getContents()).length, b = 0; b < i; ) {
                  ItemStack is = arrayOfItemStack[b];
                  if (is != null)
                    loc.getWorld().dropItemNaturally(loc, is); 
                  b++;
                } 
                this.opened.remove(loc);
                int tier = ((Integer)loot.get(loc)).intValue();
                respawn.put(loc, Integer.valueOf(60 * tier));
                for (Player v : this.viewers.keySet()) {
                  if (((Location)this.viewers.get(v)).equals(loc)) {
                    this.viewers.remove(v);
                    v.closeInventory();
                    v.playSound(v.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
                    v.playSound(v.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F);
                  } 
                } 
              } else {
                loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.WOOD);
                p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
                loc.getWorld().dropItemNaturally(loc, LootDrops.createLootDrop(((Integer)loot.get(loc)).intValue()));
                int tier = ((Integer)loot.get(loc)).intValue();
                respawn.put(loc, Integer.valueOf(60 * tier));
                for (Player v : this.viewers.keySet()) {
                  if (((Location)this.viewers.get(v)).equals(loc)) {
                    this.viewers.remove(v);
                    v.closeInventory();
                    v.playSound(v.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
                    v.playSound(v.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F);
                  } 
                } 
              } 
            } 
          } else if (!p.isOp()) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.GRAY + "The chest is locked.");
          } 
        } else if (e.getClickedBlock().getType() == Material.GLOWSTONE && 
          p.isOp()) {
          Location loc = e.getClickedBlock().getLocation();
          if (e.getAction() == Action.RIGHT_CLICK_BLOCK && 
            getPlayerTier(p) > 0) {
            e.setCancelled(true);
            loot.put(loc, Integer.valueOf(getPlayerTier(p)));
            p.sendMessage(
                ChatColor.GREEN + ChatColor.BOLD + "     *** LOOT CHEST CREATED ***");
            loc.getWorld().getBlockAt(loc).setType(Material.CHEST);
            loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.CHEST);
          } 
        }  
    } 
  }
  
  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    Player p = e.getPlayer();
    if (p.isOp() && 
      e.getBlock().getType().equals(Material.GLOWSTONE)) {
      Location loc = e.getBlock().getLocation();
      if (loot.containsKey(loc)) {
        loot.remove(loc);
        p.sendMessage(ChatColor.RED + ChatColor.BOLD + "     *** LOOT CHEST REMOVED ***");
        loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.CHEST);
      } 
    } 
  }
  
  public static int getPlayerTier(Player e) {
    ItemStack is = e.getItemInHand();
    if (is != null && is.getType() != Material.AIR) {
      if (is.getType().name().contains("WOOD_"))
        return 1; 
      if (is.getType().name().contains("STONE_"))
        return 2; 
      if (is.getType().name().contains("IRON_"))
        return 3; 
      if (is.getType().name().contains("DIAMOND_"))
        return 4; 
      if (is.getType().name().contains("GOLD_"))
        return 5; 
    } 
    return 0;
  }
  
  @EventHandler
  public void onCloseChest(InventoryCloseEvent e) {
    if (e.getPlayer() instanceof Player) {
      Player p = (Player)e.getPlayer();
      if (e.getInventory().getName().contains("Loot Chest") && 
        this.viewers.containsKey(p)) {
        Location loc = this.viewers.get(p);
        this.viewers.remove(p);
        p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F);
        boolean isempty = true;
        byte b;
        int i;
        ItemStack[] arrayOfItemStack;
        for (i = (arrayOfItemStack = e.getInventory().getContents()).length, b = 0; b < i; ) {
          ItemStack itms = arrayOfItemStack[b];
          if (itms != null && itms.getType() != Material.AIR)
            isempty = false; 
          b++;
        } 
        if (isempty) {
          loc.getWorld().getBlockAt(loc).setType(Material.AIR);
          loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.WOOD);
          p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
          this.opened.remove(loc);
          int tier = ((Integer)loot.get(loc)).intValue();
          respawn.put(loc, Integer.valueOf(60 * tier));
          for (Player v : this.viewers.keySet()) {
            if (((Location)this.viewers.get(v)).equals(loc)) {
              this.viewers.remove(v);
              v.closeInventory();
              v.playSound(v.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
              v.playSound(v.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F);
            } 
          } 
        } 
      } 
    } 
  }
}
