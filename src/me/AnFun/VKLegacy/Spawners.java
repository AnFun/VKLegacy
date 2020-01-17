package me.AnFun.VKLegacy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Spawners implements Listener, CommandExecutor {
  static ConcurrentHashMap<Location, String> spawners = new ConcurrentHashMap<>();
  
  static ConcurrentHashMap<LivingEntity, Location> mobs = new ConcurrentHashMap<>();
  
  static ConcurrentHashMap<Location, Long> respawntimer = new ConcurrentHashMap<>();
  
  public void onEnable() {
    Main.log.info("[Spawners] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    File file = new File(Main.plugin.getDataFolder(), "spawners.yml");
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
      String val = config.getString(key);
      String[] str = key.split(",");
      World world = Bukkit.getWorld(str[0]);
      double x = Double.valueOf(str[1]).doubleValue();
      double y = Double.valueOf(str[2]).doubleValue();
      double z = Double.valueOf(str[3]).doubleValue();
      Location loc = new Location(world, x, y, z);
      spawners.put(loc, val);
    } 
    for (Entity e : ((World)Bukkit.getServer().getWorlds().get(0)).getEntities()) {
      if ((e instanceof LivingEntity && !(e instanceof Player)) || e instanceof org.bukkit.entity.Item || e instanceof org.bukkit.entity.EnderCrystal)
        e.remove(); 
    } 
    (new BukkitRunnable() {
        public void run() {
          for (Entity e : ((World)Bukkit.getWorlds().get(0)).getEntities()) {
            if (e instanceof LivingEntity) {
              LivingEntity s = (LivingEntity)e;
              if (Spawners.mobs.containsKey(s)) {
                Location loc = Spawners.mobs.get(s);
                Location newloc = s.getLocation();
                if (loc.distance(newloc) > 30.0D) {
                  Random r = new Random();
                  int randX = r.nextInt(7) - 3;
                  int randZ = r.nextInt(7) - 3;
                  Location sloc = new Location(loc.getWorld(), loc.getX() + randX + 0.5D, loc.getY() + 2.0D, 
                      loc.getZ() + randZ + 0.5D);
                  if (sloc.getWorld().getBlockAt(sloc).getType() != Material.AIR || 
                    sloc.getWorld().getBlockAt(sloc.add(0.0D, 1.0D, 0.0D)).getType() != Material.AIR) {
                    sloc = loc.clone().add(0.0D, 1.0D, 0.0D);
                  } else {
                    sloc.subtract(0.0D, 1.0D, 0.0D);
                  } 
                  s.teleport(sloc);
                  s.setFallDistance(0.0F);
                  s.setHealth(s.getMaxHealth());
                  s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147483647, 10));
                  s.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2147483647, 127));
                  if (s.hasMetadata("name")) {
                    s.setCustomName(((MetadataValue)s.getMetadata("name").get(0)).asString());
                    s.setCustomNameVisible(true);
                  } 
                } 
              } 
            } 
          } 
          for (LivingEntity l : Spawners.mobs.keySet()) {
            if (l == null || l.isDead())
              Spawners.mobs.remove(l); 
          } 
        }
      }).runTaskTimer(Main.plugin, 1L, 1L);
    (new BukkitRunnable() {
        public void run() {
          for (Location loc : Spawners.spawners.keySet()) {
            if (Spawners.isPlayerNearby(loc) && 
              loc.getChunk().isLoaded() && 
              !Spawners.mobs.containsValue(loc) && (
              !Spawners.respawntimer.containsKey(loc) || 
              System.currentTimeMillis() > ((Long)Spawners.respawntimer.get(loc)).longValue())) {
              String data = Spawners.spawners.get(loc);
              if (Spawners.isCorrectFormat(data)) {
                if (data.contains(",")) {
                  byte b;
                  int j;
                  String[] arrayOfString;
                  for (j = (arrayOfString = data.split(",")).length, b = 0; b < j; ) {
                    String s = arrayOfString[b];
                    String str1 = s.split(":")[0];
                    int k = Integer.parseInt(s.split(":")[1].split("@")[0]);
                    boolean bool = Boolean.parseBoolean(s.split("@")[1].split("#")[0]);
                    int m = Integer.parseInt(s.split("#")[1]);
                    for (int n = 0; n < m; n++)
                      Spawners.this.spawnMob(loc, str1, k, bool); 
                    b++;
                  } 
                  continue;
                } 
                String type = data.split(":")[0];
                int tier = Integer.parseInt(data.split(":")[1].split("@")[0]);
                boolean elite = Boolean.parseBoolean(data.split("@")[1].split("#")[0]);
                int amt = Integer.parseInt(data.split("#")[1]);
                for (int i = 0; i < amt; i++)
                  Spawners.this.spawnMob(loc, type, tier, elite); 
              } 
            } 
          } 
        }
      }).runTaskTimer(Main.plugin, 100L, 20L);
    (new BukkitRunnable() {
        public void run() {
          for (Entity e : ((World)Bukkit.getWorlds().get(0)).getEntities()) {
            if (e instanceof LivingEntity && !(e instanceof Player) && 
              !Spawners.isPlayerNearby(e.getLocation())) {
              e.remove();
              Spawners.mobs.remove(e);
            } 
          } 
        }
      }).runTaskTimer(Main.plugin, 200L, 200L);
    (new BukkitRunnable() {
        public void run() {
          for (Entity e : ((World)Bukkit.getWorlds().get(0)).getEntities()) {
            if (e instanceof LivingEntity && !(e instanceof Player))
              e.remove(); 
          } 
          Spawners.mobs.clear();
          Spawners.respawntimer.clear();
        }
      }).runTaskLater(Main.plugin, 72000L);
  }
  
  static boolean isPlayerNearby(Location loc) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.getLocation().distanceSquared(loc) < 6400.0D)
        return true; 
    } 
    return false;
  }
  
  public void onDisable() {
    Main.log.info("[Spawners] has been disabled.");
    File file = new File(Main.plugin.getDataFolder(), "spawners.yml");
    if (file.exists())
      file.delete(); 
    YamlConfiguration config = new YamlConfiguration();
    for (Location loc : spawners.keySet()) {
      String s = String.valueOf(loc.getWorld().getName()) + "," + (int)loc.getX() + "," + (int)loc.getY() + "," + 
        (int)loc.getZ();
      config.set(s, spawners.get(loc));
      try {
        config.save(file);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
    for (Entity e : ((World)Bukkit.getServer().getWorlds().get(0)).getEntities()) {
      if ((e instanceof LivingEntity && !(e instanceof Player)) || e instanceof org.bukkit.entity.Item || 
        e instanceof org.bukkit.entity.EnderCrystal) {
        if (e instanceof org.bukkit.entity.EnderCrystal)
          e.getLocation().getWorld().getBlockAt(e.getLocation().subtract(0.0D, 1.0D, 0.0D)).setType(Material.CHEST); 
        e.remove();
      } 
    } 
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player)sender;
      if (p.isOp()) {
        if (cmd.getName().equalsIgnoreCase("showms")) {
          if (args.length != 1) {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED + 
                "/showms <radius>");
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
                if (spawners.containsKey(loc)) {
                  count++;
                  loc.getBlock().setType(Material.MOB_SPAWNER);
                } 
              } 
            } 
          } 
          p.sendMessage(ChatColor.YELLOW + "Displaying " + count + " mob spawners in a " + radius + 
              " block radius...");
          p.sendMessage(ChatColor.GRAY + "Break them to unregister the spawn point.");
        } 
        if (cmd.getName().equalsIgnoreCase("hidems")) {
          if (args.length != 1) {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED + 
                "/hidems <radius>");
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
                if (spawners.containsKey(loc) && 
                  loc.getBlock().getType() == Material.MOB_SPAWNER) {
                  loc.getBlock().setType(Material.AIR);
                  count++;
                } 
              } 
            } 
          } 
          p.sendMessage(
              ChatColor.YELLOW + "Hiding " + count + " mob spawners in a " + radius + " block radius...");
        } 
        if (cmd.getName().equalsIgnoreCase("killall")) {
          for (Entity e : ((World)Bukkit.getWorlds().get(0)).getEntities()) {
            if (e instanceof LivingEntity && !(e instanceof Player))
              e.remove(); 
          } 
          mobs.clear();
          respawntimer.clear();
        } 
        if (cmd.getName().equals("monspawn")) {
          if (args.length != 1) {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED + 
                "/monspawn <mobtype>");
            return true;
          } 
          String data = args[0];
          Location loc = p.getTargetBlock(null, 100).getLocation();
          if (isCorrectFormat(data)) {
            if (data.contains(",")) {
              byte b;
              int i;
              String[] arrayOfString;
              for (i = (arrayOfString = data.split(",")).length, b = 0; b < i; ) {
                String s = arrayOfString[b];
                String type = s.split(":")[0];
                int tier = Integer.parseInt(s.split(":")[1].split("@")[0]);
                boolean elite = Boolean.parseBoolean(s.split("@")[1].split("#")[0]);
                int amt = Integer.parseInt(s.split("#")[1]);
                for (int j = 0; j < amt; j++)
                  spawnMob(loc, type, tier, elite); 
                b++;
              } 
            } else {
              String type = data.split(":")[0];
              int tier = Integer.parseInt(data.split(":")[1].split("@")[0]);
              boolean elite = Boolean.parseBoolean(data.split("@")[1].split("#")[0]);
              int amt = Integer.parseInt(data.split("#")[1]);
              for (int i = 0; i < amt; i++)
                spawnMob(loc, type, tier, elite); 
            } 
            p.sendMessage(ChatColor.GRAY + "Spawned " + ChatColor.YELLOW + data + ChatColor.GRAY + " at " + 
                ChatColor.YELLOW + loc.toVector().toString());
          } else {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED + 
                "/monspawn <mobtype>");
          } 
        } 
      } 
    } 
    return false;
  }
  
  public static int getHp(ItemStack is) {
    if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
      List<String> lore = is.getItemMeta().getLore();
      if (lore.size() > 1 && (
        (String)lore.get(1)).contains("HP"))
        try {
          return Integer.parseInt(((String)lore.get(1)).split(": +")[1]);
        } catch (Exception e) {
          return 0;
        }  
    } 
    return 0;
  }
  
  public int hpCheck(LivingEntity s) {
    int a = 0;
    byte b;
    int i;
    ItemStack[] arrayOfItemStack;
    for (i = (arrayOfItemStack = s.getEquipment().getArmorContents()).length, b = 0; b < i; ) {
      ItemStack is = arrayOfItemStack[b];
      if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
        int health = getHp(is);
        a += health;
      } 
      b++;
    } 
    return a;
  }
  
  public LivingEntity mob(Location loc, String type) {
    if (type.equalsIgnoreCase("skeleton") || type.equalsIgnoreCase("impa") || type.equalsIgnoreCase("skeletonking") || 
      type.equalsIgnoreCase("kingofgreed")) {
      Skeleton skeleton = (Skeleton)loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
      return (LivingEntity)skeleton;
    } 
    if (type.equalsIgnoreCase("witherskeleton") || type.equalsIgnoreCase("kilatan")) {
      Skeleton skeleton = (Skeleton)loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
      skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
      return (LivingEntity)skeleton;
    } 
    if (type.equalsIgnoreCase("zombie") || type.equalsIgnoreCase("mitsuki") || type.equalsIgnoreCase("blayshan") || 
      type.equalsIgnoreCase("bloodbutcher") || type.equalsIgnoreCase("copjak")) {
      Zombie zombie = (Zombie)loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
      zombie.setBaby(false);
      zombie.setVillager(false);
      return (LivingEntity)zombie;
    } 
    if (type.equalsIgnoreCase("magmacube")) {
      MagmaCube cube = (MagmaCube)loc.getWorld().spawnEntity(loc, EntityType.MAGMA_CUBE);
      cube.setSize(3);
      return (LivingEntity)cube;
    } 
    if (type.equalsIgnoreCase("spider")) {
      Spider spider = (Spider)loc.getWorld().spawnEntity(loc, EntityType.SPIDER);
      return (LivingEntity)spider;
    } 
    if (type.equalsIgnoreCase("cavespider")) {
      CaveSpider cspider = (CaveSpider)loc.getWorld().spawnEntity(loc, EntityType.CAVE_SPIDER);
      return (LivingEntity)cspider;
    } 
    if (type.equalsIgnoreCase("daemon")) {
      PigZombie daemon = (PigZombie)loc.getWorld().spawnEntity(loc, EntityType.PIG_ZOMBIE);
      daemon.setAngry(true);
      daemon.setBaby(false);
      return (LivingEntity)daemon;
    } 
    if (type.equalsIgnoreCase("imp")) {
      PigZombie imp = (PigZombie)loc.getWorld().spawnEntity(loc, EntityType.PIG_ZOMBIE);
      imp.setAngry(true);
      imp.setBaby(true);
      return (LivingEntity)imp;
    } 
    return null;
  }
  
  public void spawnMob(final Location loc, String type, int tier, boolean elite) {
    Random r = new Random();
    int randX = r.nextInt(7) + -3;
    int randZ = r.nextInt(7) + -3;
    Location sloc = new Location(loc.getWorld(), loc.getX() + randX + 0.5D, loc.getY() + 2.0D, loc.getZ() + randZ + 0.5D);
    if (sloc.getWorld().getBlockAt(sloc).getType() != Material.AIR || 
      sloc.getWorld().getBlockAt(sloc.add(0.0D, 1.0D, 0.0D)).getType() != Material.AIR) {
      sloc = loc.clone().add(0.0D, 1.0D, 0.0D);
    } else {
      sloc.subtract(0.0D, 1.0D, 0.0D);
    } 
    final LivingEntity s = mob(sloc, type);
    String name = "";
    int gearcheck = r.nextInt(3) + 1;
    if (tier == 3) {
      int m_type = r.nextInt(2);
      if (m_type == 0)
        gearcheck = 3; 
      if (m_type == 1)
        gearcheck = 4; 
    } 
    if (tier >= 4 || elite)
      gearcheck = 4; 
    int held = r.nextInt(2) + 2;
    if (s.getType() == EntityType.SKELETON || s.getType() == EntityType.ZOMBIE)
      held = r.nextInt(4); 
    ItemStack hand = Drops.createDrop(tier, held);
    if (elite)
      hand.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1); 
    ItemStack head = null;
    ItemStack chest = null;
    ItemStack legs = null;
    ItemStack boots = null;
    int a_type = 0;
    while (gearcheck > 0) {
      a_type = r.nextInt(4) + 1;
      if (a_type == 1 && head == null) {
        head = Drops.createDrop(tier, 4);
        if (elite)
          head.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1); 
        gearcheck--;
      } 
      if (a_type == 2 && chest == null) {
        chest = Drops.createDrop(tier, 5);
        if (elite)
          chest.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1); 
        gearcheck--;
      } 
      if (a_type == 3 && legs == null) {
        legs = Drops.createDrop(tier, 6);
        if (elite)
          legs.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1); 
        gearcheck--;
      } 
      if (a_type == 4 && boots == null) {
        boots = Drops.createDrop(tier, 7);
        if (elite)
          boots.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1); 
        gearcheck--;
      } 
    } 
    s.setCanPickupItems(false);
    s.setRemoveWhenFarAway(false);
    if (type.equalsIgnoreCase("skeleton")) {
      if (tier == 1)
        name = "Broken Skeleton"; 
      if (tier == 2)
        name = "Wandering Cracking Skeleton"; 
      if (tier == 3)
        name = "Demonic Skeleton"; 
      if (tier == 4)
        name = "Skeleton Guardian"; 
      if (tier == 5)
        name = "Infernal Skeleton"; 
    } 
    if (type.equalsIgnoreCase("witherskeleton")) {
      if (tier == 1)
        name = "Broken Chaos Skeleton"; 
      if (tier == 2)
        name = "Wandering Cracking Chaos Skeleton"; 
      if (tier == 3)
        name = "Demonic Chaos Skeleton"; 
      if (tier == 4)
        name = "Skeleton Chaos Guardian"; 
      if (tier == 5)
        name = "Infernal Chaos Skeleton"; 
    } 
    if (type.equalsIgnoreCase("imp")) {
      if (tier == 1)
        name = "Ugly Imp"; 
      if (tier == 2)
        name = "Angry Imp"; 
      if (tier == 3)
        name = "Warrior Imp"; 
      if (tier == 4)
        name = "Armoured Imp"; 
      if (tier == 5)
        name = "Infernal Imp"; 
    } 
    if (type.equalsIgnoreCase("daemon")) {
      if (tier == 1)
        name = "Broken Daemon"; 
      if (tier == 2)
        name = "Wandering Cracking Daemon"; 
      if (tier == 3)
        name = "Demonic Daemon"; 
      if (tier == 4)
        name = "Daemon Guardian"; 
      if (tier == 5)
        name = "Infernal Daemon"; 
    } 
    if (type.equalsIgnoreCase("zombie")) {
      if (tier == 1)
        name = "Rotting Zombie"; 
      if (tier == 2)
        name = "Savaged Zombie"; 
      if (tier == 3)
        name = "Greater Zombie"; 
      if (tier == 4)
        name = "Demonic Zombie"; 
      if (tier == 5)
        name = "Infernal Zombie"; 
    } 
    if (type.equalsIgnoreCase("magmacube")) {
      if (tier == 1)
        name = "Weak Magma Cube"; 
      if (tier == 2)
        name = "Bubbling Magma Cube"; 
      if (tier == 3)
        name = "Unstable Magma Cube"; 
      if (tier == 4)
        name = "Boiling Magma Cube"; 
      if (tier == 5)
        name = "Unstoppable Magma Cube"; 
    } 
    if (type.equalsIgnoreCase("spider") || type.equalsIgnoreCase("cavespider")) {
      if (tier == 1)
        name = "Harmless "; 
      if (tier == 2)
        name = "Wild "; 
      if (tier == 3)
        name = "Fierce "; 
      if (tier == 4)
        name = "Dangerous "; 
      if (tier == 5)
        name = "Lethal "; 
      if (type.equalsIgnoreCase("cavespider"))
        name = String.valueOf(name) + "Cave "; 
      name = String.valueOf(name) + "Spider";
    } 
    if (type.equalsIgnoreCase("mitsuki"))
      name = "Mitsuki The Dominator"; 
    if (type.equalsIgnoreCase("copjak"))
      name = "Cop'jak"; 
    if (type.equalsIgnoreCase("kingofgreed"))
      name = "The King Of Greed"; 
    if (type.equalsIgnoreCase("skeletonking"))
      name = "The Skeleton King"; 
    if (type.equalsIgnoreCase("impa"))
      name = "Impa The Impaler"; 
    if (type.equalsIgnoreCase("bloodbutcher"))
      name = "The Blood Butcher"; 
    if (type.equalsIgnoreCase("blayshan"))
      name = "Blayshan The Naga"; 
    if (type.equalsIgnoreCase("kilatan"))
      name = "Daemon Lord Kilatan"; 
    String color = ChatColor.WHITE.toString();
    switch (tier) {
      case 1:
        color = ChatColor.WHITE.toString();
        break;
      case 2:
        color = ChatColor.GREEN.toString();
        break;
      case 3:
        color = ChatColor.AQUA.toString();
        break;
      case 4:
        color = ChatColor.LIGHT_PURPLE.toString();
        break;
      case 5:
        color = ChatColor.YELLOW.toString();
        break;
    } 
    if (elite)
      color = String.valueOf(ChatColor.LIGHT_PURPLE.toString()) + ChatColor.BOLD.toString(); 
    s.setCustomName(String.valueOf(color) + name);
    s.setCustomNameVisible(true);
    s.setMetadata("name", (MetadataValue)new FixedMetadataValue(Main.plugin, String.valueOf(color) + name));
    s.setMetadata("type", (MetadataValue)new FixedMetadataValue(Main.plugin, type));
    if (elite)
      s.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2147483647, 1)); 
    if (tier == 4 || tier == 5) {
      int speed_chance = tier * 15;
      int do_i_speed = (new Random()).nextInt(100);
      if (speed_chance > do_i_speed)
        s.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2147483647, 0)); 
    } 
    s.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2147483647, 1));
    s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147483647, 10));
    s.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2147483647, 127));
    s.getEquipment().clear();
    s.getEquipment().setItemInHand(hand);
    s.getEquipment().setHelmet(head);
    s.getEquipment().setChestplate(chest);
    s.getEquipment().setLeggings(legs);
    s.getEquipment().setBoots(boots);
    int hp = hpCheck(s);
    if (elite) {
      if (tier == 1)
        hp = (int)(hp * 1.8D); 
      if (tier == 2)
        hp = (int)(hp * 2.5D); 
      if (tier == 3)
        hp *= 3; 
      if (tier == 4)
        hp *= 5; 
      if (tier == 5)
        hp *= 7; 
    } else {
      if (tier == 1)
        hp = (int)(hp * 0.4D); 
      if (tier == 2)
        hp = (int)(hp * 0.9D); 
      if (tier == 3)
        hp = (int)(hp * 1.2D); 
      if (tier == 4)
        hp = (int)(hp * 1.4D); 
      if (tier == 5)
        hp *= 2; 
    } 
    if (hp < 1)
      hp = 1; 
    s.setMaxHealth(hp);
    s.setHealth(hp);
    (new BukkitRunnable() {
        public void run() {
          Spawners.mobs.put(s, loc);
        }
      }).runTaskLater(Main.plugin, 1L);
  }
  
  public static boolean isCorrectFormat(String data) {
    if (data.contains(":") && data.contains("@") && data.contains("#")) {
      if (data.contains(",")) {
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = data.split(",")).length, b = 0; b < i; ) {
          String s = arrayOfString[b];
          if (!s.contains(":") || !s.contains("@") || !s.contains("#"))
            return false; 
          String str1 = s.split(":")[0];
          if (!str1.equalsIgnoreCase("skeleton") && !str1.equalsIgnoreCase("zombie") && 
            !str1.equalsIgnoreCase("magmacube") && !str1.equalsIgnoreCase("spider") && 
            !str1.equalsIgnoreCase("cavespider") && !str1.equalsIgnoreCase("imp") && 
            !str1.equalsIgnoreCase("witherskeleton") && !str1.equalsIgnoreCase("daemon"))
            if (!str1.equalsIgnoreCase("mitsuki") && !str1.equalsIgnoreCase("copjak") && 
              !str1.equalsIgnoreCase("kingofgreed") && !str1.equalsIgnoreCase("skeletonking") && 
              !str1.equalsIgnoreCase("impa") && !str1.equalsIgnoreCase("bloodbutcher") && 
              !str1.equalsIgnoreCase("blayshan") && !str1.equalsIgnoreCase("kilatan"))
              return false;  
          try {
            int k = Integer.parseInt(s.split(":")[1].split("@")[0]);
            if (k < 1 || k > 5)
              return false; 
          } catch (Exception e) {
            return false;
          } 
          String str2 = s.split("@")[1].split("#")[0];
          if (!str2.equalsIgnoreCase("true") && !str2.equalsIgnoreCase("false"))
            return false; 
          try {
            int amt = Integer.parseInt(s.split("#")[1]);
            if (amt < 1 || amt > 10)
              return false; 
          } catch (Exception e) {
            return false;
          } 
          int j = Integer.parseInt(data.split(":")[1].split("@")[0]);
          boolean bool = Boolean.parseBoolean(str2);
          if ((str1.equalsIgnoreCase("mitsuki") && (!bool || j != 1)) || (
            str1.equalsIgnoreCase("copjak") && (!bool || j != 2)) || (
            str1.equalsIgnoreCase("kingofgreed") && (!bool || j != 3)) || (
            str1.equalsIgnoreCase("skeletonking") && (!bool || j != 3)) || (
            str1.equalsIgnoreCase("impa") && (!bool || j != 3)) || (
            str1.equalsIgnoreCase("bloodbutcher") && (!bool || j != 4)) || (
            str1.equalsIgnoreCase("blayshan") && (!bool || j != 4)) || (
            str1.equalsIgnoreCase("kilatan") && (!bool || j != 5)))
            return false; 
          b++;
        } 
        return true;
      } 
      String type = data.split(":")[0];
      if (!type.equalsIgnoreCase("skeleton") && !type.equalsIgnoreCase("zombie") && 
        !type.equalsIgnoreCase("magmacube") && !type.equalsIgnoreCase("spider") && 
        !type.equalsIgnoreCase("cavespider") && !type.equalsIgnoreCase("imp") && 
        !type.equalsIgnoreCase("witherskeleton") && !type.equalsIgnoreCase("daemon"))
        if (!type.equalsIgnoreCase("mitsuki") && !type.equalsIgnoreCase("copjak") && 
          !type.equalsIgnoreCase("kingofgreed") && !type.equalsIgnoreCase("skeletonking") && 
          !type.equalsIgnoreCase("impa") && !type.equalsIgnoreCase("bloodbutcher") && 
          !type.equalsIgnoreCase("blayshan") && !type.equalsIgnoreCase("kilatan"))
          return false;  
      try {
        int i = Integer.parseInt(data.split(":")[1].split("@")[0]);
        if (i < 1 || i > 5)
          return false; 
      } catch (Exception e) {
        return false;
      } 
      String elite = data.split("@")[1].split("#")[0];
      if (!elite.equalsIgnoreCase("true") && !elite.equalsIgnoreCase("false"))
        return false; 
      try {
        int amt = Integer.parseInt(data.split("#")[1]);
        if (amt < 1 || amt > 10)
          return false; 
      } catch (Exception e) {
        return false;
      } 
      int tier = Integer.parseInt(data.split(":")[1].split("@")[0]);
      boolean iselite = Boolean.parseBoolean(elite);
      if ((type.equalsIgnoreCase("mitsuki") && (!iselite || tier != 1)) || (
        type.equalsIgnoreCase("copjak") && (!iselite || tier != 2)) || (
        type.equalsIgnoreCase("kingofgreed") && (!iselite || tier != 3)) || (
        type.equalsIgnoreCase("skeletonking") && (!iselite || tier != 3)) || (
        type.equalsIgnoreCase("impa") && (!iselite || tier != 3)) || (
        type.equalsIgnoreCase("bloodbutcher") && (!iselite || tier != 4)) || (
        type.equalsIgnoreCase("blayshan") && (!iselite || tier != 4)) || (
        type.equalsIgnoreCase("kilatan") && (!iselite || tier != 5)))
        return false; 
      return true;
    } 
    return false;
  }
  
  HashMap<String, Location> creatingspawner = new HashMap<>();
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onSpawnerCreate(AsyncPlayerChatEvent e) {
    Player p = e.getPlayer();
    if (p.isOp() && 
      this.creatingspawner.containsKey(p.getName())) {
      e.setCancelled(true);
      if (e.getMessage().equalsIgnoreCase("cancel")) {
        p.sendMessage(ChatColor.RED + ChatColor.BOLD + "     *** SPAWNER CREATION CANCELLED ***");
        this.creatingspawner.remove(p.getName());
      } else if (isCorrectFormat(e.getMessage())) {
        p.sendMessage(
            ChatColor.GRAY + "Spawner with data '" + ChatColor.YELLOW + e.getMessage() + ChatColor.GRAY + 
            "' created at " + ChatColor.YELLOW + ((Location)this.creatingspawner.get(p.getName())).toVector());
        spawners.put(this.creatingspawner.get(p.getName()), e.getMessage());
        this.creatingspawner.remove(p.getName());
      } else {
        p.sendMessage(ChatColor.RED + ChatColor.BOLD + "     *** INCORRECT FORMAT ***");
        p.sendMessage(" ");
        p.sendMessage(ChatColor.YELLOW + "FORMAT: " + ChatColor.GRAY + "mobtype:tier@elite#amount");
        p.sendMessage(ChatColor.YELLOW + "EX: " + ChatColor.GRAY + 
            "skeleton:5@true#1,zombie:4@true#1,magmacube:4@false#5");
        p.sendMessage(" ");
        p.sendMessage(ChatColor.RED + ChatColor.BOLD + "     *** INCORRECT FORMAT ***");
      } 
    } 
  }
  
  @EventHandler
  public void onBlockPlace(BlockPlaceEvent e) {
    Player p = e.getPlayer();
    if (p.isOp() && 
      e.getBlock().getType().equals(Material.MOB_SPAWNER)) {
      p.sendMessage(ChatColor.GREEN + ChatColor.BOLD + "     *** SPAWNER CREATION STARTED ***");
      p.sendMessage(" ");
      p.sendMessage(ChatColor.YELLOW + "FORMAT: " + ChatColor.GRAY + "mobtype:tier@elite#amount");
      p.sendMessage(ChatColor.YELLOW + "EX: " + ChatColor.GRAY + 
          "skeleton:5@true#1,zombie:4@true#1,magmacube:4@false#5");
      p.sendMessage(" ");
      p.sendMessage(ChatColor.GREEN + ChatColor.BOLD + "     *** SPAWNER CREATION STARTED ***");
      this.creatingspawner.put(p.getName(), e.getBlock().getLocation());
    } 
  }
  
  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    Player p = e.getPlayer();
    if (p.isOp() && 
      e.getBlock().getType().equals(Material.MOB_SPAWNER)) {
      if (spawners.containsKey(e.getBlock().getLocation())) {
        p.sendMessage(ChatColor.GRAY + "Spawner with data '" + ChatColor.YELLOW + 
            (String)spawners.get(e.getBlock().getLocation()) + ChatColor.GRAY + "' removed at " + 
            ChatColor.YELLOW + e.getBlock().getLocation().toVector());
        spawners.remove(e.getBlock().getLocation());
      } 
      if (this.creatingspawner.containsValue(e.getBlock().getLocation()))
        for (String s : this.creatingspawner.keySet()) {
          if (((Location)this.creatingspawner.get(s)).equals(e.getBlock().getLocation())) {
            p.sendMessage(
                ChatColor.RED + ChatColor.BOLD + "     *** SPAWNER CREATION CANCELLED ***");
            this.creatingspawner.remove(s);
          } 
        }  
    } 
  }
  
  @EventHandler
  public void onBlockClick(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    if (p.isOp() && 
      e.getAction() == Action.RIGHT_CLICK_BLOCK && 
      e.getClickedBlock().getType().equals(Material.MOB_SPAWNER) && 
      spawners.containsKey(e.getClickedBlock().getLocation()))
      p.sendMessage(ChatColor.GRAY + "Spawner with data '" + ChatColor.YELLOW + 
          (String)spawners.get(e.getClickedBlock().getLocation()) + ChatColor.GRAY + "' at " + 
          ChatColor.YELLOW + e.getClickedBlock().getLocation().toVector()); 
  }
  
  @EventHandler
  public void onChunkUnload(ChunkUnloadEvent e) {
    byte b;
    int i;
    Entity[] arrayOfEntity;
    for (i = (arrayOfEntity = e.getChunk().getEntities()).length, b = 0; b < i; ) {
      Entity ent = arrayOfEntity[b];
      if (ent instanceof LivingEntity && !(ent instanceof Player) && !(ent instanceof org.bukkit.entity.EnderCrystal)) {
        if (mobs.containsKey(ent))
          mobs.remove(ent); 
        if (ent instanceof org.bukkit.entity.EnderCrystal)
          ent.getLocation().getWorld().getBlockAt(ent.getLocation().subtract(0.0D, 1.0D, 0.0D))
            .setType(Material.CHEST); 
        ent.remove();
      } 
      b++;
    } 
  }
  
  @EventHandler
  public void onChunkLoad(ChunkLoadEvent e) {
    if (e.isNewChunk()) {
      e.getChunk().unload(false, false);
      return;
    } 
    byte b;
    int i;
    Entity[] arrayOfEntity;
    for (i = (arrayOfEntity = e.getChunk().getEntities()).length, b = 0; b < i; ) {
      Entity ent = arrayOfEntity[b];
      if ((ent instanceof LivingEntity && !(ent instanceof Player)) || ent instanceof org.bukkit.entity.EnderCrystal) {
        if (mobs.containsKey(ent))
          mobs.remove(ent); 
        if (ent instanceof org.bukkit.entity.EnderCrystal)
          ent.getLocation().getWorld().getBlockAt(ent.getLocation().subtract(0.0D, 1.0D, 0.0D))
            .setType(Material.CHEST); 
        ent.remove();
      } 
      b++;
    } 
  }
  
  public static int getMobTier(LivingEntity e) {
    if (e.getEquipment().getItemInHand() != null) {
      if (e.getEquipment().getItemInHand().getType().name().contains("WOOD_"))
        return 1; 
      if (e.getEquipment().getItemInHand().getType().name().contains("STONE_"))
        return 2; 
      if (e.getEquipment().getItemInHand().getType().name().contains("IRON_"))
        return 3; 
      if (e.getEquipment().getItemInHand().getType().name().contains("DIAMOND_"))
        return 4; 
      if (e.getEquipment().getItemInHand().getType().name().contains("GOLD_"))
        return 5; 
    } 
    return 0;
  }
  
  public static boolean isElite(LivingEntity e) {
    if (e.getEquipment().getItemInHand() != null && e.getEquipment().getItemInHand().getType() != Material.AIR) {
      if (e.getEquipment().getItemInHand().getItemMeta().hasEnchants())
        return true; 
      return false;
    } 
    return false;
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityDeathd(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity) {
      LivingEntity s = (LivingEntity)e.getEntity();
      if (e.getDamage() >= s.getHealth() && 
        mobs.containsKey(s)) {
        long time = 30L;
        time *= getMobTier(s);
        if (isElite(s))
          time *= 2L; 
        time *= 1000L;
        time += System.currentTimeMillis();
        if (!respawntimer.containsKey(mobs.get(s)) || ((Long)respawntimer.get(mobs.get(s))).longValue() < time)
          respawntimer.put(mobs.get(s), Long.valueOf(time)); 
        mobs.remove(s);
      } 
    } 
  }
}
