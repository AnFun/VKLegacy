package me.bpweber.practiceserver;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Spider;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Iterator;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Material;
import java.util.Random;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.World;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.Bukkit;
import java.util.HashMap;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public class Spawners implements Listener, CommandExecutor
{
    static ConcurrentHashMap<Location, String> spawners;
    static ConcurrentHashMap<LivingEntity, Location> mobs;
    static ConcurrentHashMap<Location, Long> respawntimer;
    HashMap<String, Location> creatingspawner;
    
    static {
        Spawners.spawners = new ConcurrentHashMap<Location, String>();
        Spawners.mobs = new ConcurrentHashMap<LivingEntity, Location>();
        Spawners.respawntimer = new ConcurrentHashMap<Location, Long>();
    }
    
    public Spawners() {
        this.creatingspawner = new HashMap<String, Location>();
    }
    
    public void onEnable() {
        Main.log.info("[Spawners] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        final File file = new File(Main.plugin.getDataFolder(), "spawners.yml");
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
            final String val = config.getString(key);
            final String[] str = key.split(",");
            final World world = Bukkit.getWorld(str[0]);
            final double x = Double.valueOf(str[1]);
            final double y = Double.valueOf(str[2]);
            final double z = Double.valueOf(str[3]);
            final Location loc = new Location(world, x, y, z);
            Spawners.spawners.put(loc, val);
        }
        for (final Entity e3 : Bukkit.getServer().getWorlds().get(0).getEntities()) {
            if ((e3 instanceof LivingEntity && !(e3 instanceof Player)) || e3 instanceof Item || e3 instanceof EnderCrystal) {
                e3.remove();
            }
        }
        new BukkitRunnable() {
            public void run() {
                for (final Entity e : Bukkit.getWorlds().get(0).getEntities()) {
                    if (e instanceof LivingEntity) {
                        final LivingEntity s = (LivingEntity)e;
                        if (!Spawners.mobs.containsKey(s)) {
                            continue;
                        }
                        final Location loc = Spawners.mobs.get(s);
                        final Location newloc = s.getLocation();
                        if (loc.distance(newloc) <= 30.0) {
                            continue;
                        }
                        final Random r = new Random();
                        final int randX = r.nextInt(7) - 3;
                        final int randZ = r.nextInt(7) - 3;
                        Location sloc = new Location(loc.getWorld(), loc.getX() + randX + 0.5, loc.getY() + 2.0, loc.getZ() + randZ + 0.5);
                        if (sloc.getWorld().getBlockAt(sloc).getType() != Material.AIR || sloc.getWorld().getBlockAt(sloc.add(0.0, 1.0, 0.0)).getType() != Material.AIR) {
                            sloc = loc.clone().add(0.0, 1.0, 0.0);
                        }
                        else {
                            sloc.subtract(0.0, 1.0, 0.0);
                        }
                        s.teleport(sloc);
                        s.setFallDistance(0.0f);
                        s.setHealth(s.getMaxHealth());
                        s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10));
                        s.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 127));
                        if (!s.hasMetadata("name")) {
                            continue;
                        }
                        s.setCustomName(s.getMetadata("name").get(0).asString());
                        s.setCustomNameVisible(true);
                    }
                }
                for (final LivingEntity l : Spawners.mobs.keySet()) {
                    if (l == null || l.isDead()) {
                        Spawners.mobs.remove(l);
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 1L, 1L);
        new BukkitRunnable() {
            public void run() {
                for (final Location loc : Spawners.spawners.keySet()) {
                    if (Spawners.isPlayerNearby(loc) && loc.getChunk().isLoaded() && !Spawners.mobs.containsValue(loc) && (!Spawners.respawntimer.containsKey(loc) || System.currentTimeMillis() > Spawners.respawntimer.get(loc))) {
                        final String data = Spawners.spawners.get(loc);
                        if (!Spawners.isCorrectFormat(data)) {
                            continue;
                        }
                        if (data.contains(",")) {
                            String[] split;
                            for (int length = (split = data.split(",")).length, k = 0; k < length; ++k) {
                                final String s = split[k];
                                final String type = s.split(":")[0];
                                final int tier = Integer.parseInt(s.split(":")[1].split("@")[0]);
                                final boolean elite = Boolean.parseBoolean(s.split("@")[1].split("#")[0]);
                                for (int amt = Integer.parseInt(s.split("#")[1]), i = 0; i < amt; ++i) {
                                    Spawners.this.spawnMob(loc, type, tier, elite);
                                }
                            }
                        }
                        else {
                            final String type2 = data.split(":")[0];
                            final int tier2 = Integer.parseInt(data.split(":")[1].split("@")[0]);
                            final boolean elite2 = Boolean.parseBoolean(data.split("@")[1].split("#")[0]);
                            for (int amt2 = Integer.parseInt(data.split("#")[1]), j = 0; j < amt2; ++j) {
                                Spawners.this.spawnMob(loc, type2, tier2, elite2);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 100L, 20L);
        new BukkitRunnable() {
            public void run() {
                for (final Entity e : Bukkit.getWorlds().get(0).getEntities()) {
                    if (e instanceof LivingEntity && !(e instanceof Player) && !Spawners.isPlayerNearby(e.getLocation())) {
                        e.remove();
                        Spawners.mobs.remove(e);
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 200L, 200L);
        new BukkitRunnable() {
            public void run() {
                for (final Entity e : Bukkit.getWorlds().get(0).getEntities()) {
                    if (e instanceof LivingEntity && !(e instanceof Player)) {
                        e.remove();
                    }
                }
                Spawners.mobs.clear();
                Spawners.respawntimer.clear();
            }
        }.runTaskLater(Main.plugin, 72000L);
    }
    
    static boolean isPlayerNearby(final Location loc) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distanceSquared(loc) < 6400.0) {
                return true;
            }
        }
        return false;
    }
    
    public void onDisable() {
        Main.log.info("[Spawners] has been disabled.");
        final File file = new File(Main.plugin.getDataFolder(), "spawners.yml");
        if (file.exists()) {
            file.delete();
        }
        final YamlConfiguration config = new YamlConfiguration();
        for (final Location loc : Spawners.spawners.keySet()) {
            final String s = String.valueOf(loc.getWorld().getName()) + "," + (int)loc.getX() + "," + (int)loc.getY() + "," + (int)loc.getZ();
            config.set(s, (Object)Spawners.spawners.get(loc));
            try {
                config.save(file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (final Entity e2 : Bukkit.getServer().getWorlds().get(0).getEntities()) {
            if ((e2 instanceof LivingEntity && !(e2 instanceof Player)) || e2 instanceof Item || e2 instanceof EnderCrystal) {
                if (e2 instanceof EnderCrystal) {
                    e2.getLocation().getWorld().getBlockAt(e2.getLocation().subtract(0.0, 1.0, 0.0)).setType(Material.CHEST);
                }
                e2.remove();
            }
        }
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player)sender;
            if (p.isOp()) {
                if (cmd.getName().equalsIgnoreCase("showms")) {
                    if (args.length != 1) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax. ").append(ChatColor.RED).append("/showms <radius>").toString());
                        return true;
                    }
                    int radius = 0;
                    try {
                        radius = Integer.parseInt(args[0]);
                    }
                    catch (Exception e2) {
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
                                if (Spawners.spawners.containsKey(loc)) {
                                    ++count;
                                    loc.getBlock().setType(Material.MOB_SPAWNER);
                                }
                            }
                        }
                    }
                    p.sendMessage(ChatColor.YELLOW + "Displaying " + count + " mob spawners in a " + radius + " block radius...");
                    p.sendMessage(ChatColor.GRAY + "Break them to unregister the spawn point.");
                }
                if (cmd.getName().equalsIgnoreCase("hidems")) {
                    if (args.length != 1) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax. ").append(ChatColor.RED).append("/hidems <radius>").toString());
                        return true;
                    }
                    int radius = 0;
                    try {
                        radius = Integer.parseInt(args[0]);
                    }
                    catch (Exception e2) {
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
                                if (Spawners.spawners.containsKey(loc) && loc.getBlock().getType() == Material.MOB_SPAWNER) {
                                    loc.getBlock().setType(Material.AIR);
                                    ++count;
                                }
                            }
                        }
                    }
                    p.sendMessage(ChatColor.YELLOW + "Hiding " + count + " mob spawners in a " + radius + " block radius...");
                }
                if (cmd.getName().equalsIgnoreCase("killall")) {
                    for (final Entity e : Bukkit.getWorlds().get(0).getEntities()) {
                        if (e instanceof LivingEntity && !(e instanceof Player)) {
                            e.remove();
                        }
                    }
                    Spawners.mobs.clear();
                    Spawners.respawntimer.clear();
                }
                if (cmd.getName().equals("monspawn")) {
                    if (args.length != 1) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax. ").append(ChatColor.RED).append("/monspawn <mobtype>").toString());
                        return true;
                    }
                    final String data = args[0];
                    final Location loc = p.getTargetBlock((HashSet)null, 100).getLocation();
                    if (isCorrectFormat(data)) {
                        if (data.contains(",")) {
                            String[] split;
                            for (int length = (split = data.split(",")).length, n = 0; n < length; ++n) {
                                final String s = split[n];
                                final String type = s.split(":")[0];
                                final int tier = Integer.parseInt(s.split(":")[1].split("@")[0]);
                                final boolean elite = Boolean.parseBoolean(s.split("@")[1].split("#")[0]);
                                for (int amt = Integer.parseInt(s.split("#")[1]), l = 0; l < amt; ++l) {
                                    this.spawnMob(loc, type, tier, elite);
                                }
                            }
                        }
                        else {
                            final String type2 = data.split(":")[0];
                            final int tier2 = Integer.parseInt(data.split(":")[1].split("@")[0]);
                            final boolean elite2 = Boolean.parseBoolean(data.split("@")[1].split("#")[0]);
                            for (int amt2 = Integer.parseInt(data.split("#")[1]), m = 0; m < amt2; ++m) {
                                this.spawnMob(loc, type2, tier2, elite2);
                            }
                        }
                        p.sendMessage(ChatColor.GRAY + "Spawned " + ChatColor.YELLOW + data + ChatColor.GRAY + " at " + ChatColor.YELLOW + loc.toVector().toString());
                    }
                    else {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax. ").append(ChatColor.RED).append("/monspawn <mobtype>").toString());
                    }
                }
            }
        }
        return false;
    }
    
    public static int getHp(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            if (lore.size() > 1 && lore.get(1).contains("HP")) {
                try {
                    return Integer.parseInt(lore.get(1).split(": +")[1]);
                }
                catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    public int hpCheck(final LivingEntity s) {
        int a = 0;
        ItemStack[] armorContents;
        for (int length = (armorContents = s.getEquipment().getArmorContents()).length, i = 0; i < length; ++i) {
            final ItemStack is = armorContents[i];
            if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                final int health = getHp(is);
                a += health;
            }
        }
        return a;
    }
    
    public LivingEntity mob(final Location loc, final String type) {
        if (type.equalsIgnoreCase("skeleton") || type.equalsIgnoreCase("impa") || type.equalsIgnoreCase("skeletonking") || type.equalsIgnoreCase("kingofgreed")) {
            final Skeleton skeleton = (Skeleton)loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
            return (LivingEntity)skeleton;
        }
        if (type.equalsIgnoreCase("witherskeleton") || type.equalsIgnoreCase("kilatan")) {
            final Skeleton skeleton = (Skeleton)loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
            skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
            return (LivingEntity)skeleton;
        }
        if (type.equalsIgnoreCase("zombie") || type.equalsIgnoreCase("mitsuki") || type.equalsIgnoreCase("blayshan") || type.equalsIgnoreCase("bloodbutcher") || type.equalsIgnoreCase("copjak")) {
            final Zombie zombie = (Zombie)loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
            zombie.setBaby(false);
            zombie.setVillager(false);
            return (LivingEntity)zombie;
        }
        if (type.equalsIgnoreCase("magmacube")) {
            final MagmaCube cube = (MagmaCube)loc.getWorld().spawnEntity(loc, EntityType.MAGMA_CUBE);
            cube.setSize(3);
            return (LivingEntity)cube;
        }
        if (type.equalsIgnoreCase("spider")) {
            final Spider spider = (Spider)loc.getWorld().spawnEntity(loc, EntityType.SPIDER);
            return (LivingEntity)spider;
        }
        if (type.equalsIgnoreCase("cavespider")) {
            final CaveSpider cspider = (CaveSpider)loc.getWorld().spawnEntity(loc, EntityType.CAVE_SPIDER);
            return (LivingEntity)cspider;
        }
        if (type.equalsIgnoreCase("daemon")) {
            final PigZombie daemon = (PigZombie)loc.getWorld().spawnEntity(loc, EntityType.PIG_ZOMBIE);
            daemon.setAngry(true);
            daemon.setBaby(false);
            return (LivingEntity)daemon;
        }
        if (type.equalsIgnoreCase("imp")) {
            final PigZombie imp = (PigZombie)loc.getWorld().spawnEntity(loc, EntityType.PIG_ZOMBIE);
            imp.setAngry(true);
            imp.setBaby(true);
            return (LivingEntity)imp;
        }
        return null;
    }
    
    public void spawnMob(final Location loc, final String type, final int tier, final boolean elite) {
        final Random r = new Random();
        final int randX = r.nextInt(7) - 3;
        final int randZ = r.nextInt(7) - 3;
        Location sloc = new Location(loc.getWorld(), loc.getX() + randX + 0.5, loc.getY() + 2.0, loc.getZ() + randZ + 0.5);
        if (sloc.getWorld().getBlockAt(sloc).getType() != Material.AIR || sloc.getWorld().getBlockAt(sloc.add(0.0, 1.0, 0.0)).getType() != Material.AIR) {
            sloc = loc.clone().add(0.0, 1.0, 0.0);
        }
        else {
            sloc.subtract(0.0, 1.0, 0.0);
        }
        final LivingEntity s = this.mob(sloc, type);
        String name = "";
        int gearcheck = r.nextInt(3) + 1;
        if (tier == 3) {
            final int m_type = r.nextInt(2);
            if (m_type == 0) {
                gearcheck = 3;
            }
            if (m_type == 1) {
                gearcheck = 4;
            }
        }
        if (tier >= 4 || elite) {
            gearcheck = 4;
        }
        int held = r.nextInt(2) + 2;
        if (s.getType() == EntityType.SKELETON || s.getType() == EntityType.ZOMBIE) {
            held = r.nextInt(4);
        }
        final ItemStack hand = Drops.createDrop(tier, held);
        if (elite) {
            hand.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
        }
        ItemStack head = null;
        ItemStack chest = null;
        ItemStack legs = null;
        ItemStack boots = null;
        int a_type = 0;
        while (gearcheck > 0) {
            a_type = r.nextInt(4) + 1;
            if (a_type == 1 && head == null) {
                head = Drops.createDrop(tier, 4);
                if (elite) {
                    head.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
                }
                --gearcheck;
            }
            if (a_type == 2 && chest == null) {
                chest = Drops.createDrop(tier, 5);
                if (elite) {
                    chest.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
                }
                --gearcheck;
            }
            if (a_type == 3 && legs == null) {
                legs = Drops.createDrop(tier, 6);
                if (elite) {
                    legs.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
                }
                --gearcheck;
            }
            if (a_type == 4 && boots == null) {
                boots = Drops.createDrop(tier, 7);
                if (elite) {
                    boots.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
                }
                --gearcheck;
            }
        }
        s.setCanPickupItems(false);
        s.setRemoveWhenFarAway(false);
        if (type.equalsIgnoreCase("skeleton")) {
            if (tier == 1) {
                name = "Broken Skeleton";
            }
            if (tier == 2) {
                name = "Wandering Cracking Skeleton";
            }
            if (tier == 3) {
                name = "Demonic Skeleton";
            }
            if (tier == 4) {
                name = "Skeleton Guardian";
            }
            if (tier == 5) {
                name = "Infernal Skeleton";
            }
        }
        if (type.equalsIgnoreCase("witherskeleton")) {
            if (tier == 1) {
                name = "Broken Chaos Skeleton";
            }
            if (tier == 2) {
                name = "Wandering Cracking Chaos Skeleton";
            }
            if (tier == 3) {
                name = "Demonic Chaos Skeleton";
            }
            if (tier == 4) {
                name = "Skeleton Chaos Guardian";
            }
            if (tier == 5) {
                name = "Infernal Chaos Skeleton";
            }
        }
        if (type.equalsIgnoreCase("imp")) {
            if (tier == 1) {
                name = "Ugly Imp";
            }
            if (tier == 2) {
                name = "Angry Imp";
            }
            if (tier == 3) {
                name = "Warrior Imp";
            }
            if (tier == 4) {
                name = "Armoured Imp";
            }
            if (tier == 5) {
                name = "Infernal Imp";
            }
        }
        if (type.equalsIgnoreCase("daemon")) {
            if (tier == 1) {
                name = "Broken Daemon";
            }
            if (tier == 2) {
                name = "Wandering Cracking Daemon";
            }
            if (tier == 3) {
                name = "Demonic Daemon";
            }
            if (tier == 4) {
                name = "Daemon Guardian";
            }
            if (tier == 5) {
                name = "Infernal Daemon";
            }
        }
        if (type.equalsIgnoreCase("zombie")) {
            if (tier == 1) {
                name = "Rotting Zombie";
            }
            if (tier == 2) {
                name = "Savaged Zombie";
            }
            if (tier == 3) {
                name = "Greater Zombie";
            }
            if (tier == 4) {
                name = "Demonic Zombie";
            }
            if (tier == 5) {
                name = "Infernal Zombie";
            }
        }
        if (type.equalsIgnoreCase("magmacube")) {
            if (tier == 1) {
                name = "Weak Magma Cube";
            }
            if (tier == 2) {
                name = "Bubbling Magma Cube";
            }
            if (tier == 3) {
                name = "Unstable Magma Cube";
            }
            if (tier == 4) {
                name = "Boiling Magma Cube";
            }
            if (tier == 5) {
                name = "Unstoppable Magma Cube";
            }
        }
        if (type.equalsIgnoreCase("spider") || type.equalsIgnoreCase("cavespider")) {
            if (tier == 1) {
                name = "Harmless ";
            }
            if (tier == 2) {
                name = "Wild ";
            }
            if (tier == 3) {
                name = "Fierce ";
            }
            if (tier == 4) {
                name = "Dangerous ";
            }
            if (tier == 5) {
                name = "Lethal ";
            }
            if (type.equalsIgnoreCase("cavespider")) {
                name = String.valueOf(name) + "Cave ";
            }
            name = String.valueOf(name) + "Spider";
        }
        if (type.equalsIgnoreCase("mitsuki")) {
            name = "Mitsuki The Dominator";
        }
        if (type.equalsIgnoreCase("copjak")) {
            name = "Cop'jak";
        }
        if (type.equalsIgnoreCase("kingofgreed")) {
            name = "The King Of Greed";
        }
        if (type.equalsIgnoreCase("skeletonking")) {
            name = "The Skeleton King";
        }
        if (type.equalsIgnoreCase("impa")) {
            name = "Impa The Impaler";
        }
        if (type.equalsIgnoreCase("bloodbutcher")) {
            name = "The Blood Butcher";
        }
        if (type.equalsIgnoreCase("blayshan")) {
            name = "Blayshan The Naga";
        }
        if (type.equalsIgnoreCase("kilatan")) {
            name = "Daemon Lord Kilatan";
        }
        String color = ChatColor.WHITE.toString();
        switch (tier) {
            case 1: {
                color = ChatColor.WHITE.toString();
                break;
            }
            case 2: {
                color = ChatColor.GREEN.toString();
                break;
            }
            case 3: {
                color = ChatColor.AQUA.toString();
                break;
            }
            case 4: {
                color = ChatColor.LIGHT_PURPLE.toString();
                break;
            }
            case 5: {
                color = ChatColor.YELLOW.toString();
                break;
            }
        }
        if (elite) {
            color = String.valueOf(ChatColor.LIGHT_PURPLE.toString()) + ChatColor.BOLD.toString();
        }
        s.setCustomName(String.valueOf(color) + name);
        s.setCustomNameVisible(true);
        s.setMetadata("name", (MetadataValue)new FixedMetadataValue(Main.plugin, (Object)(String.valueOf(color) + name)));
        s.setMetadata("type", (MetadataValue)new FixedMetadataValue(Main.plugin, (Object)type));
        if (elite) {
            s.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }
        if (tier == 4 || tier == 5) {
            final int speed_chance = tier * 15;
            final int do_i_speed = new Random().nextInt(100);
            if (speed_chance > do_i_speed) {
                s.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
            }
        }
        s.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
        s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10));
        s.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 127));
        s.getEquipment().clear();
        s.getEquipment().setItemInHand(hand);
        s.getEquipment().setHelmet(head);
        s.getEquipment().setChestplate(chest);
        s.getEquipment().setLeggings(legs);
        s.getEquipment().setBoots(boots);
        int hp = this.hpCheck(s);
        if (elite) {
            if (tier == 1) {
                hp *= (int)1.8;
            }
            if (tier == 2) {
                hp *= (int)2.5;
            }
            if (tier == 3) {
                hp *= 3;
            }
            if (tier == 4) {
                hp *= 5;
            }
            if (tier == 5) {
                hp *= 7;
            }
        }
        else {
            if (tier == 1) {
                hp *= (int)0.4;
            }
            if (tier == 2) {
                hp *= (int)0.9;
            }
            if (tier == 3) {
                hp *= (int)1.2;
            }
            if (tier == 4) {
                hp *= (int)1.4;
            }
            if (tier == 5) {
                hp *= 2;
            }
        }
        if (hp < 1) {
            hp = 1;
        }
        s.setMaxHealth((double)hp);
        s.setHealth((double)hp);
        new BukkitRunnable() {
            public void run() {
                Spawners.mobs.put(s, loc);
            }
        }.runTaskLater(Main.plugin, 1L);
    }
    
    public static boolean isCorrectFormat(final String data) {
        if (!data.contains(":") || !data.contains("@") || !data.contains("#")) {
            return false;
        }
        if (data.contains(",")) {
            String[] split;
            for (int length = (split = data.split(",")).length, i = 0; i < length; ++i) {
                final String s = split[i];
                if (!s.contains(":") || !s.contains("@") || !s.contains("#")) {
                    return false;
                }
                final String type = s.split(":")[0];
                if (!type.equalsIgnoreCase("skeleton") && !type.equalsIgnoreCase("zombie") && !type.equalsIgnoreCase("magmacube") && !type.equalsIgnoreCase("spider") && !type.equalsIgnoreCase("cavespider") && !type.equalsIgnoreCase("imp") && !type.equalsIgnoreCase("witherskeleton") && !type.equalsIgnoreCase("daemon") && !type.equalsIgnoreCase("mitsuki") && !type.equalsIgnoreCase("copjak") && !type.equalsIgnoreCase("kingofgreed") && !type.equalsIgnoreCase("skeletonking") && !type.equalsIgnoreCase("impa") && !type.equalsIgnoreCase("bloodbutcher") && !type.equalsIgnoreCase("blayshan") && !type.equalsIgnoreCase("kilatan")) {
                    return false;
                }
                try {
                    final int tier = Integer.parseInt(s.split(":")[1].split("@")[0]);
                    if (tier < 1 || tier > 5) {
                        return false;
                    }
                }
                catch (Exception e) {
                    return false;
                }
                final String elite = s.split("@")[1].split("#")[0];
                if (!elite.equalsIgnoreCase("true") && !elite.equalsIgnoreCase("false")) {
                    return false;
                }
                try {
                    final int amt = Integer.parseInt(s.split("#")[1]);
                    if (amt < 1 || amt > 10) {
                        return false;
                    }
                }
                catch (Exception e2) {
                    return false;
                }
                final int tier2 = Integer.parseInt(data.split(":")[1].split("@")[0]);
                final boolean iselite = Boolean.parseBoolean(elite);
                if ((type.equalsIgnoreCase("mitsuki") && (!iselite || tier2 != 1)) || (type.equalsIgnoreCase("copjak") && (!iselite || tier2 != 2)) || (type.equalsIgnoreCase("kingofgreed") && (!iselite || tier2 != 3)) || (type.equalsIgnoreCase("skeletonking") && (!iselite || tier2 != 3)) || (type.equalsIgnoreCase("impa") && (!iselite || tier2 != 3)) || (type.equalsIgnoreCase("bloodbutcher") && (!iselite || tier2 != 4)) || (type.equalsIgnoreCase("blayshan") && (!iselite || tier2 != 4)) || (type.equalsIgnoreCase("kilatan") && (!iselite || tier2 != 5))) {
                    return false;
                }
            }
            return true;
        }
        final String type2 = data.split(":")[0];
        if (!type2.equalsIgnoreCase("skeleton") && !type2.equalsIgnoreCase("zombie") && !type2.equalsIgnoreCase("magmacube") && !type2.equalsIgnoreCase("spider") && !type2.equalsIgnoreCase("cavespider") && !type2.equalsIgnoreCase("imp") && !type2.equalsIgnoreCase("witherskeleton") && !type2.equalsIgnoreCase("daemon") && !type2.equalsIgnoreCase("mitsuki") && !type2.equalsIgnoreCase("copjak") && !type2.equalsIgnoreCase("kingofgreed") && !type2.equalsIgnoreCase("skeletonking") && !type2.equalsIgnoreCase("impa") && !type2.equalsIgnoreCase("bloodbutcher") && !type2.equalsIgnoreCase("blayshan") && !type2.equalsIgnoreCase("kilatan")) {
            return false;
        }
        try {
            final int tier3 = Integer.parseInt(data.split(":")[1].split("@")[0]);
            if (tier3 < 1 || tier3 > 5) {
                return false;
            }
        }
        catch (Exception e3) {
            return false;
        }
        final String elite2 = data.split("@")[1].split("#")[0];
        if (!elite2.equalsIgnoreCase("true") && !elite2.equalsIgnoreCase("false")) {
            return false;
        }
        try {
            final int amt2 = Integer.parseInt(data.split("#")[1]);
            if (amt2 < 1 || amt2 > 10) {
                return false;
            }
        }
        catch (Exception e4) {
            return false;
        }
        final int tier4 = Integer.parseInt(data.split(":")[1].split("@")[0]);
        final boolean iselite2 = Boolean.parseBoolean(elite2);
        return (!type2.equalsIgnoreCase("mitsuki") || (iselite2 && tier4 == 1)) && (!type2.equalsIgnoreCase("copjak") || (iselite2 && tier4 == 2)) && (!type2.equalsIgnoreCase("kingofgreed") || (iselite2 && tier4 == 3)) && (!type2.equalsIgnoreCase("skeletonking") || (iselite2 && tier4 == 3)) && (!type2.equalsIgnoreCase("impa") || (iselite2 && tier4 == 3)) && (!type2.equalsIgnoreCase("bloodbutcher") || (iselite2 && tier4 == 4)) && (!type2.equalsIgnoreCase("blayshan") || (iselite2 && tier4 == 4)) && (!type2.equalsIgnoreCase("kilatan") || (iselite2 && tier4 == 5));
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawnerCreate(final AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (p.isOp() && this.creatingspawner.containsKey(p.getName())) {
            e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase("cancel")) {
                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("     *** SPAWNER CREATION CANCELLED ***").toString());
                this.creatingspawner.remove(p.getName());
            }
            else if (isCorrectFormat(e.getMessage())) {
                p.sendMessage(ChatColor.GRAY + "Spawner with data '" + ChatColor.YELLOW + e.getMessage() + ChatColor.GRAY + "' created at " + ChatColor.YELLOW + this.creatingspawner.get(p.getName()).toVector());
                Spawners.spawners.put(this.creatingspawner.get(p.getName()), e.getMessage());
                this.creatingspawner.remove(p.getName());
            }
            else {
                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("     *** INCORRECT FORMAT ***").toString());
                p.sendMessage(" ");
                p.sendMessage(ChatColor.YELLOW + "FORMAT: " + ChatColor.GRAY + "mobtype:tier@elite#amount");
                p.sendMessage(ChatColor.YELLOW + "EX: " + ChatColor.GRAY + "skeleton:5@true#1,zombie:4@true#1,magmacube:4@false#5");
                p.sendMessage(" ");
                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("     *** INCORRECT FORMAT ***").toString());
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent e) {
        final Player p = e.getPlayer();
        if (p.isOp() && e.getBlock().getType().equals((Object)Material.MOB_SPAWNER)) {
            p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("     *** SPAWNER CREATION STARTED ***").toString());
            p.sendMessage(" ");
            p.sendMessage(ChatColor.YELLOW + "FORMAT: " + ChatColor.GRAY + "mobtype:tier@elite#amount");
            p.sendMessage(ChatColor.YELLOW + "EX: " + ChatColor.GRAY + "skeleton:5@true#1,zombie:4@true#1,magmacube:4@false#5");
            p.sendMessage(" ");
            p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("     *** SPAWNER CREATION STARTED ***").toString());
            this.creatingspawner.put(p.getName(), e.getBlock().getLocation());
        }
    }
    
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        if (p.isOp() && e.getBlock().getType().equals((Object)Material.MOB_SPAWNER)) {
            if (Spawners.spawners.containsKey(e.getBlock().getLocation())) {
                p.sendMessage(ChatColor.GRAY + "Spawner with data '" + ChatColor.YELLOW + Spawners.spawners.get(e.getBlock().getLocation()) + ChatColor.GRAY + "' removed at " + ChatColor.YELLOW + e.getBlock().getLocation().toVector());
                Spawners.spawners.remove(e.getBlock().getLocation());
            }
            if (this.creatingspawner.containsValue(e.getBlock().getLocation())) {
                for (final String s : this.creatingspawner.keySet()) {
                    if (this.creatingspawner.get(s).equals((Object)e.getBlock().getLocation())) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("     *** SPAWNER CREATION CANCELLED ***").toString());
                        this.creatingspawner.remove(s);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockClick(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (p.isOp() && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().equals((Object)Material.MOB_SPAWNER) && Spawners.spawners.containsKey(e.getClickedBlock().getLocation())) {
            p.sendMessage(ChatColor.GRAY + "Spawner with data '" + ChatColor.YELLOW + Spawners.spawners.get(e.getClickedBlock().getLocation()) + ChatColor.GRAY + "' at " + ChatColor.YELLOW + e.getClickedBlock().getLocation().toVector());
        }
    }
    
    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent e) {
        Entity[] entities;
        for (int length = (entities = e.getChunk().getEntities()).length, i = 0; i < length; ++i) {
            final Entity ent = entities[i];
            if (ent instanceof LivingEntity && !(ent instanceof Player) && !(ent instanceof EnderCrystal)) {
                if (Spawners.mobs.containsKey(ent)) {
                    Spawners.mobs.remove(ent);
                }
                if (ent instanceof EnderCrystal) {
                    ent.getLocation().getWorld().getBlockAt(ent.getLocation().subtract(0.0, 1.0, 0.0)).setType(Material.CHEST);
                }
                ent.remove();
            }
        }
    }
    
    @EventHandler
    public void onChunkLoad(final ChunkLoadEvent e) {
        if (e.isNewChunk()) {
            e.getChunk().unload(false, false);
            return;
        }
        Entity[] entities;
        for (int length = (entities = e.getChunk().getEntities()).length, i = 0; i < length; ++i) {
            final Entity ent = entities[i];
            if ((ent instanceof LivingEntity && !(ent instanceof Player)) || ent instanceof EnderCrystal) {
                if (Spawners.mobs.containsKey(ent)) {
                    Spawners.mobs.remove(ent);
                }
                if (ent instanceof EnderCrystal) {
                    ent.getLocation().getWorld().getBlockAt(ent.getLocation().subtract(0.0, 1.0, 0.0)).setType(Material.CHEST);
                }
                ent.remove();
            }
        }
    }
    
    public static int getMobTier(final LivingEntity e) {
        if (e.getEquipment().getItemInHand() != null) {
            if (e.getEquipment().getItemInHand().getType().name().contains("WOOD_")) {
                return 1;
            }
            if (e.getEquipment().getItemInHand().getType().name().contains("STONE_")) {
                return 2;
            }
            if (e.getEquipment().getItemInHand().getType().name().contains("IRON_")) {
                return 3;
            }
            if (e.getEquipment().getItemInHand().getType().name().contains("DIAMOND_")) {
                return 4;
            }
            if (e.getEquipment().getItemInHand().getType().name().contains("GOLD_")) {
                return 5;
            }
        }
        return 0;
    }
    
    public static boolean isElite(final LivingEntity e) {
        return e.getEquipment().getItemInHand() != null && e.getEquipment().getItemInHand().getType() != Material.AIR && e.getEquipment().getItemInHand().getItemMeta().hasEnchants();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeathd(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            final LivingEntity s = (LivingEntity)e.getEntity();
            if (e.getDamage() >= s.getHealth() && Spawners.mobs.containsKey(s)) {
                long time = 30L;
                time *= getMobTier(s);
                if (isElite(s)) {
                    time *= 2L;
                }
                time *= 1000L;
                time += System.currentTimeMillis();
                if (!Spawners.respawntimer.containsKey(Spawners.mobs.get(s)) || Spawners.respawntimer.get(Spawners.mobs.get(s)) < time) {
                    Spawners.respawntimer.put(Spawners.mobs.get(s), time);
                }
                Spawners.mobs.remove(s);
            }
        }
    }
}
