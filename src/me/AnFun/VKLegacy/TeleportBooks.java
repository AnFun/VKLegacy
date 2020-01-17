package me.AnFun.VKLegacy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportBooks implements Listener {
  public static Location Cyrennica;
  
  public static Location Harrison_Field;
  
  public static Location Dark_Oak_Tavern;
  
  public static Location Deadpeaks_Mountain_Camp;
  
  public static Location Trollsbane_Tavern;
  
  public static Location Tripoli;
  
  public static Location Gloomy_Hollows;
  
  public static Location Crestguard_Keep;
  
  public static Location CrestWatch;
  
  static HashMap<String, Location> teleporting_loc = new HashMap<>();
  
  static HashMap<String, Location> casting_loc = new HashMap<>();
  
  static HashMap<String, Integer> casting_time = new HashMap<>();
  
  public void onEnable() {
    Cyrennica = new Location(Bukkit.getWorlds().get(0), -367.0D, 83.0D, 390.0D);
    Harrison_Field = new Location(Bukkit.getWorlds().get(0), -594.0D, 58.0D, 687.0D, 92.0F, 1.0F);
    Dark_Oak_Tavern = new Location(Bukkit.getWorlds().get(0), 280.0D, 58.0D, 1132.0D, 2.0F, 1.0F);
    Deadpeaks_Mountain_Camp = new Location(Bukkit.getWorlds().get(0), -1173.0D, 105.0D, 1030.0D, -88.0F, 1.0F);
    Trollsbane_Tavern = new Location(Bukkit.getWorlds().get(0), 962.0D, 94.0D, 1069.0D, -153.0F, 1.0F);
    Tripoli = new Location(Bukkit.getWorlds().get(0), -1320.0D, 90.0D, 370.0D, 153.0F, 1.0F);
    Gloomy_Hollows = new Location(Bukkit.getWorlds().get(0), -590.0D, 43.0D, 0.0D, 144.0F, 1.0F);
    Crestguard_Keep = new Location(Bukkit.getWorlds().get(0), -1428.0D, 115.0D, -489.0D, 95.0F, 1.0F);
    CrestWatch = new Location(Bukkit.getWorlds().get(0), -544.0D, 60.0D, -418.0D, 95.0F, 1.0F);
    Main.log.info("[TeleportBooks] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    (new BukkitRunnable() {
        public void run() {
          for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (TeleportBooks.casting_time.containsKey(p.getName())) {
              if (((Integer)TeleportBooks.casting_time.get(p.getName())).intValue() == 0) {
                ParticleEffect.SPELL_WITCH.display(0.0F, 0.0F, 0.0F, 0.2F, 200, p.getLocation().add(0.0D, 1.0D, 0.0D), 20.0D);
                p.eject();
                p.teleport(TeleportBooks.teleporting_loc.get(p.getName()));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
                TeleportBooks.casting_time.remove(p.getName());
                TeleportBooks.casting_loc.remove(p.getName());
                TeleportBooks.teleporting_loc.remove(p.getName());
                continue;
              } 
              p.sendMessage(ChatColor.BOLD + "CASTING" + ChatColor.WHITE + " ... " + 
                  TeleportBooks.casting_time.get(p.getName()) + ChatColor.BOLD + "s");
              TeleportBooks.casting_time.put(p.getName(), Integer.valueOf(((Integer)TeleportBooks.casting_time.get(p.getName())).intValue() - 1));
              ParticleEffect.PORTAL.display(0.0F, 0.0F, 0.0F, 4.0F, 300, p.getLocation(), 20.0D);
              ParticleEffect.SPELL_WITCH.display(0.0F, 0.0F, 0.0F, 1.0F, 200, p.getLocation(), 20.0D);
            } 
          } 
        }
      }).runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
  }
  
  public void onDisable() {
    Main.log.info("[TeleportBooks] has been disabled.");
  }
  
  public static ItemStack cyrennica_book() {
    ItemStack is = new ItemStack(Material.BOOK);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Cyrennica");
    im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Teleports the user to the grand City of Cyrennica." }));
    is.setItemMeta(im);
    return is;
  }
  
  public static ItemStack harrison_book() {
    ItemStack is = new ItemStack(Material.BOOK);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Harrison Field");
    im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Teleports the user to Harrison Field." }));
    is.setItemMeta(im);
    return is;
  }
  
  public static ItemStack dark_oak_book() {
    ItemStack is = new ItemStack(Material.BOOK);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Dark Oak Tavern");
    im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Teleports the user to the tavern in Dark Oak Forest." }));
    is.setItemMeta(im);
    return is;
  }
  
  public static ItemStack deadpeaks_book() {
    ItemStack is = new ItemStack(Material.BOOK);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(
        ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Deadpeaks Mountain Camp");
    im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Teleports the user to the Deadpeaks.", 
            ChatColor.RED + ChatColor.BOLD + "WARNING:" + ChatColor.RED.toString() + " CHAOTIC ZONE" }));
    is.setItemMeta(im);
    return is;
  }
  
  public static ItemStack trollsbane_book() {
    ItemStack is = new ItemStack(Material.BOOK);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Trollsbane Tavern");
    im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Teleports the user to Trollsbane Tavern." }));
    is.setItemMeta(im);
    return is;
  }
  
  public static ItemStack tripoli_book() {
    ItemStack is = new ItemStack(Material.BOOK);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Tripoli");
    im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Teleports the user to Tripoli." }));
    is.setItemMeta(im);
    return is;
  }
  
  public static ItemStack gloomy_book() {
    ItemStack is = new ItemStack(Material.BOOK);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Gloomy Hollows");
    im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Teleports the user to the Gloomy Hollows." }));
    is.setItemMeta(im);
    return is;
  }
  
  public static ItemStack crestguard_book() {
    ItemStack is = new ItemStack(Material.BOOK);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Crestguard Keep");
    im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Teleports the user to the Crestguard Keep." }));
    is.setItemMeta(im);
    return is;
  }
  
  public static ItemStack crestwatch_book() {
    ItemStack is = new ItemStack(Material.BOOK);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " CrestWatch");
    im.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Teleports the user to the CrestWatch." }));
    is.setItemMeta(im);
    return is;
  }
  
  @EventHandler
  public void onRightClick(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && 
      p.getItemInHand() != null && p.getItemInHand().getType() == Material.BOOK && 
      p.getItemInHand().getItemMeta().hasDisplayName() && 
      p.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains("teleport:") && 
      !casting_time.containsKey(p.getName()) && 
      !Horses.mounting.containsKey(p.getName())) {
      String type = ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName());
      Location loc = getLocationFromString(type);
      if (Alignments.chaotic.containsKey(p.getName()) && loc != Deadpeaks_Mountain_Camp) {
        p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + 
            " teleport to non-chaotic zones while chaotic.");
        p.sendMessage(ChatColor.GRAY + "Neutral in " + ChatColor.BOLD + 
            Alignments.chaotic.get(p.getName()) + "s");
      } else {
        if (p.getItemInHand().getAmount() > 1) {
          p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
        } else {
          p.setItemInHand(null);
        } 
        int seconds = 5;
        if (Alignments.tagged.containsKey(p.getName()) && 
          System.currentTimeMillis() - ((Long)Alignments.tagged.get(p.getName())).longValue() < 10000L)
          seconds = 10; 
        p.sendMessage(ChatColor.WHITE + ChatColor.BOLD + "CASTING " + ChatColor.WHITE + 
            getTeleportMessage(type) + " ... " + seconds + ChatColor.BOLD + "s");
        teleporting_loc.put(p.getName(), loc);
        casting_loc.put(p.getName(), p.getLocation());
        casting_time.put(p.getName(), Integer.valueOf(seconds));
        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (seconds + 3) * 20, 1));
        p.playSound(p.getLocation(), Sound.AMBIENCE_CAVE, 1.0F, 1.0F);
      } 
    } 
  }
  
  @EventHandler
  public void onCancelDamager(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player && e.getEntity() instanceof org.bukkit.entity.LivingEntity) {
      Player p = (Player)e.getDamager();
      if (casting_time.containsKey(p.getName())) {
        casting_time.remove(p.getName());
        casting_loc.remove(p.getName());
        teleporting_loc.remove(p.getName());
        p.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
        p.removePotionEffect(PotionEffectType.CONFUSION);
      } 
    } 
  }
  
  @EventHandler
  public void onCancelDamage(EntityDamageEvent e) {
    if (e.getDamage() <= 0.0D)
      return; 
    if (e.getEntity() instanceof Player) {
      Player p = (Player)e.getEntity();
      if (casting_time.containsKey(p.getName())) {
        casting_time.remove(p.getName());
        casting_loc.remove(p.getName());
        teleporting_loc.remove(p.getName());
        p.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
        p.removePotionEffect(PotionEffectType.CONFUSION);
      } 
    } 
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    if (casting_time.containsKey(p.getName())) {
      casting_time.remove(p.getName());
      casting_loc.remove(p.getName());
      teleporting_loc.remove(p.getName());
    } 
  }
  
  @EventHandler
  public void onCancelMove(PlayerMoveEvent e) {
    Player p = e.getPlayer();
    if (casting_time.containsKey(p.getName())) {
      Location loc = casting_loc.get(p.getName());
      if (loc.distanceSquared(e.getTo()) >= 2.0D) {
        casting_time.remove(p.getName());
        casting_loc.remove(p.getName());
        teleporting_loc.remove(p.getName());
        p.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
        p.removePotionEffect(PotionEffectType.CONFUSION);
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerPickupItemEvent(PlayerPickupItemEvent e) {
    Player p = e.getPlayer();
    if (e.isCancelled())
      return; 
    if (e.getItem().getItemStack().getType() == Material.BOOK || 
      e.getItem().getItemStack().getType() == Material.PAPER || (
      e.getItem().getItemStack().getType() == Material.INK_SACK && 
      e.getItem().getItemStack().getDurability() == 0)) {
      e.setCancelled(true);
      if (p.getInventory().firstEmpty() != -1) {
        int amount = e.getItem().getItemStack().getAmount();
        CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(e.getItem().getItemStack());
        craftItemStack.setAmount(1);
        while (amount > 0 && p.getInventory().firstEmpty() != -1) {
          p.getInventory().setItem(p.getInventory().firstEmpty(), (ItemStack)craftItemStack);
          p.updateInventory();
          amount--;
          if (amount > 0) {
            ItemStack new_stack = e.getItem().getItemStack();
            new_stack.setAmount(amount);
            e.getItem().setItemStack(new_stack);
          } 
        } 
        if (amount <= 0)
          e.getItem().remove(); 
        p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
      } 
    } 
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();
    if (e.getCursor() != null && e.getCursor().getType() == Material.BOOK && 
      e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BOOK) {
      e.setCancelled(true);
      p.updateInventory();
    } 
    if (e.isShiftClick() && e.getCurrentItem() != null && (e.getCurrentItem().getType() == Material.BOOK || 
      e.getCurrentItem().getType() == Material.PAPER || GemPouches.isGemPouch(e.getCurrentItem()))) {
      if (e.getInventory().getName().contains("Bank Chest")) {
        if (e.getRawSlot() < 63 && 
          p.getInventory().firstEmpty() != -1) {
          p.getInventory().setItem(p.getInventory().firstEmpty(), e.getCurrentItem());
          e.setCurrentItem(null);
        } 
        if (!GemPouches.isGemPouch(e.getCurrentItem()) && 
          e.getRawSlot() > 53 && 
          e.getInventory().firstEmpty() != -1) {
          e.getInventory().setItem(e.getInventory().firstEmpty(), e.getCurrentItem());
          e.setCurrentItem(null);
        } 
      } 
      e.setCancelled(true);
      p.updateInventory();
    } 
    if (e.getClick() == ClickType.DOUBLE_CLICK && e.getCursor() != null && (e.getCursor().getType() == Material.BOOK || 
      e.getCursor().getType() == Material.PAPER || GemPouches.isGemPouch(e.getCursor()))) {
      e.setCancelled(true);
      p.updateInventory();
    } 
    if (e.getCursor() != null && e.getCursor().getType() == Material.PAPER && 
      e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.PAPER) {
      e.setCancelled(true);
      p.updateInventory();
    } 
    if (e.getCursor() != null && GemPouches.isGemPouch(e.getCursor()) && 
      e.getCurrentItem() != null && GemPouches.isGemPouch(e.getCurrentItem())) {
      e.setCancelled(true);
      p.updateInventory();
    } 
  }
  
  Location getLocationFromString(String s) {
    s = s.toLowerCase();
    if (s.contains("cyrennica"))
      return Cyrennica; 
    if (s.contains("harrison field"))
      return Harrison_Field; 
    if (s.contains("dark oak tavern"))
      return Dark_Oak_Tavern; 
    if (s.contains("deadpeaks mountain camp"))
      return Deadpeaks_Mountain_Camp; 
    if (s.contains("trollsbane tavern"))
      return Trollsbane_Tavern; 
    if (s.contains("tripoli"))
      return Tripoli; 
    if (s.contains("gloomy hollows"))
      return Gloomy_Hollows; 
    if (s.contains("crestguard keep"))
      return Crestguard_Keep; 
    if (s.contains("crestwatch"))
      return CrestWatch; 
    return Cyrennica;
  }
  
  String getTeleportMessage(String s) {
    s = s.toLowerCase();
    if (s.contains("cyrennica"))
      return "Teleport Scroll: Cyrennica"; 
    if (s.contains("harrison field"))
      return "Teleport Scroll: Harrison's Field"; 
    if (s.contains("dark oak tavern"))
      return "Teleport Scroll: Dark Oak Tavern"; 
    if (s.contains("deadpeaks mountain camp"))
      return "Teleport Scroll: Deadpeaks Mountain Camp"; 
    if (s.contains("trollsbane tavern"))
      return "Teleport Scroll: Trollsbane Tavern"; 
    if (s.contains("tripoli"))
      return "Teleport Scroll: Tripoli"; 
    if (s.contains("gloomy hollows"))
      return "Teleport Scroll: Gloomy Hollows"; 
    if (s.contains("crestguard keep"))
      return "Teleport Scroll: Crestguard Keep"; 
    if (s.contains("crestwatch"))
      return "Teleport Scroll: CrestWatch"; 
    return "Teleport Scroll: Cyrennica";
  }
  
  @EventHandler
  public void onAvalonTp(PlayerMoveEvent e) {
    Location to = e.getTo();
    Location enter = new Location(Bukkit.getWorlds().get(0), -357.5D, 171.0D, -3440.5D);
    Location exit = new Location(Bukkit.getWorlds().get(0), -1158.5D, 95.0D, -515.5D);
    if (to.getX() > -1155.0D && to.getX() < -1145.0D && to.getY() > 90.0D && to.getY() < 100.0D && to.getZ() < -500.0D && 
      to.getZ() > -530.0D)
      e.getPlayer().teleport(enter.setDirection(to.getDirection())); 
    if (to.getX() < -360.0D && to.getX() > -370.0D && to.getY() > 165.0D && to.getY() < 190.0D && to.getZ() < -3426.0D && 
      to.getZ() > -3455.0D)
      e.getPlayer().teleport(exit.setDirection(to.getDirection())); 
  }
  
  public static Location generateRandomSpawnPoint(String s) {
    ArrayList<Location> spawns = new ArrayList<>();
    if (Alignments.chaotic.containsKey(s)) {
      spawns.add(new Location(Bukkit.getWorlds().get(0), -382.0D, 68.0D, 867.0D));
      spawns.add(new Location(Bukkit.getWorlds().get(0), -350.0D, 67.0D, 883.0D));
      spawns.add(new Location(Bukkit.getWorlds().get(0), -330.0D, 65.0D, 898.0D));
      spawns.add(new Location(Bukkit.getWorlds().get(0), -419.0D, 61.0D, 830.0D));
      return spawns.get((new Random()).nextInt(spawns.size()));
    } 
    return Cyrennica;
  }
}
