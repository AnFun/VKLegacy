package me.AnFun.VKLegacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Horses implements Listener {
  static HashMap<String, Integer> mounting = new HashMap<>();
  
  static HashMap<String, Integer> horsetier = new HashMap<>();
  
  static HashMap<String, Location> mountingloc = new HashMap<>();
  
  static HashMap<String, ItemStack> buyingitem = new HashMap<>();
  
  static HashMap<String, Integer> buyingprice = new HashMap<>();
  
  public void onEnable() {
    Main.log.info("[Horses] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    (new BukkitRunnable() {
        public void run() {
          for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.isOnline() && 
              Horses.mounting.containsKey(p.getName())) {
              ParticleEffect.SPELL.display(0.0F, 0.0F, 0.0F, 0.5F, 80, p.getLocation().add(0.0D, 0.15D, 0.0D), 20.0D);
              if (((Integer)Horses.mounting.get(p.getName())).intValue() == 0) {
                ParticleEffect.CRIT.display(0.0F, 0.0F, 0.0F, 0.5F, 80, p.getLocation().add(0.0D, 1.0D, 0.0D), 20.0D);
                Horses.mounting.remove(p.getName());
                Horses.mountingloc.remove(p.getName());
                Horses.horse(p, ((Integer)Horses.horsetier.get(p.getName())).intValue());
                continue;
              } 
              if (((Integer)Horses.mounting.get(p.getName())).intValue() == 5) {
                String name = Horses.mount(((Integer)Horses.horsetier.get(p.getName())).intValue(), false).getItemMeta().getDisplayName();
                p.sendMessage(ChatColor.BOLD + "SUMMONING " + name + ChatColor.WHITE + " ... " + 
                    Horses.mounting.get(p.getName()) + "s");
                Horses.mounting.put(p.getName(), Integer.valueOf(((Integer)Horses.mounting.get(p.getName())).intValue() - 1));
                continue;
              } 
              p.sendMessage(ChatColor.BOLD + "SUMMONING" + ChatColor.WHITE + " ... " + 
                  Horses.mounting.get(p.getName()) + "s");
              Horses.mounting.put(p.getName(), Integer.valueOf(((Integer)Horses.mounting.get(p.getName())).intValue() - 1));
            } 
          } 
        }
      }).runTaskTimer(Main.plugin, 20L, 20L);
  }
  
  public void onDisable() {
    Main.log.info("[Horses] has been disabled.");
  }
  
  public static ItemStack mount(int tier, boolean inshop) {
    ItemStack is = new ItemStack(Material.SADDLE);
    ItemMeta im = is.getItemMeta();
    String name = ChatColor.GREEN + "Old Horse Mount";
    String req = "";
    List<String> lore = new ArrayList<>();
    String line = "An old brown starter horse.";
    int speed = 120;
    int jump = 0;
    int price = 3000;
    if (tier == 3) {
      name = ChatColor.AQUA + "Traveler's Horse Mount";
      req = ChatColor.GREEN + "Old Horse Mount";
      line = "A standard healthy horse.";
      speed = 140;
      jump = 105;
      price = 7000;
    } 
    if (tier == 4) {
      name = ChatColor.LIGHT_PURPLE + "Knight's Horse Mount";
      req = ChatColor.AQUA + "Traveler's Horse Mount";
      line = "A fast well-bred horse.";
      speed = 160;
      jump = 110;
      price = 15000;
    } 
    if (tier == 5) {
      name = ChatColor.YELLOW + "War Stallion Mount";
      req = ChatColor.LIGHT_PURPLE + "Knight's Horse Mount";
      line = "A trusty powerful steed.";
      speed = 200;
      jump = 120;
      price = 30000;
    } 
    im.setDisplayName(name);
    lore.add(ChatColor.RED + "Speed: " + speed + "%");
    if (jump > 0)
      lore.add(ChatColor.RED + "Jump: " + jump + "%"); 
    if (req != "" && inshop)
      lore.add(ChatColor.RED + ChatColor.BOLD + "REQ: " + req); 
    lore.add(ChatColor.GRAY + ChatColor.ITALIC + line);
    lore.add(ChatColor.GRAY + "Permenant Untradeable");
    if (inshop)
      lore.add(ChatColor.GREEN + "Price: " + ChatColor.WHITE + price + "g"); 
    im.setLore(lore);
    is.setItemMeta(im);
    return is;
  }
  
  public static int getMountTier(ItemStack is) {
    if (is != null && is.getType() == Material.SADDLE && 
      is.getItemMeta().hasDisplayName()) {
      String name = is.getItemMeta().getDisplayName();
      if (name.contains(ChatColor.GREEN.toString()))
        return 2; 
      if (name.contains(ChatColor.AQUA.toString()))
        return 3; 
      if (name.contains(ChatColor.LIGHT_PURPLE.toString()))
        return 4; 
      if (name.contains(ChatColor.YELLOW.toString()))
        return 5; 
    } 
    return 0;
  }
  
  public static Horse horse(Player p, int tier) {
    double speed = 0.25D;
    double jump = 0.75D;
    if (tier == 3) {
      speed = 0.3D;
      jump = 0.85D;
    } 
    if (tier == 4) {
      speed = 0.35D;
      jump = 0.95D;
    } 
    if (tier == 5) {
      speed = 0.4D;
      jump = 1.05D;
    } 
    Horse h = (Horse)p.getWorld().spawnEntity(p.getLocation(), EntityType.HORSE);
    h.setVariant(Horse.Variant.HORSE);
    h.setAdult();
    h.setTamed(true);
    h.setOwner((AnimalTamer)p);
    h.setColor(Horse.Color.BROWN);
    h.setAgeLock(true);
    h.setStyle(Horse.Style.NONE);
    h.setDomestication(100);
    h.getInventory().setSaddle(new ItemStack(Material.SADDLE));
    if (tier == 3)
      h.getInventory().setArmor(new ItemStack(Material.IRON_BARDING)); 
    if (tier == 4)
      h.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING)); 
    if (tier == 5)
      h.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING)); 
    if (ModerationMechanics.isSub(p)) {
      h.getInventory().setArmor(null);
      if (((String)ModerationMechanics.ranks.get(p.getName())).equalsIgnoreCase("sub"))
        h.setVariant(Horse.Variant.UNDEAD_HORSE); 
      if (((String)ModerationMechanics.ranks.get(p.getName())).equalsIgnoreCase("sub+")) {
        h.setColor(Horse.Color.GRAY);
        h.setFireTicks(2147483647);
        h.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2147483647, 1));
      } 
      if (((String)ModerationMechanics.ranks.get(p.getName())).equalsIgnoreCase("sub++"))
        h.setVariant(Horse.Variant.SKELETON_HORSE); 
    } 
    h.setMaxHealth(20.0D);
    h.setHealth(20.0D);
    h.setJumpStrength(jump);
    ((CraftLivingEntity)h).getHandle().getAttributeInstance(GenericAttributes.d).setValue(speed);
    h.setPassenger((Entity)p);
    return h;
  }
  
  @EventHandler
  public void onAnimalTamerClick(PlayerInteractEntityEvent e) {
    if (e.getRightClicked() instanceof Player && e.getRightClicked().hasMetadata("NPC")) {
      Player at = (Player)e.getRightClicked();
      Player p = e.getPlayer();
      if (at.getName().equalsIgnoreCase("animal tamer")) {
        Inventory inv = Bukkit.createInventory(null, 9, "Animal Tamer");
        inv.addItem(new ItemStack[] { mount(2, true) });
        inv.addItem(new ItemStack[] { mount(3, true) });
        inv.addItem(new ItemStack[] { mount(4, true) });
        inv.addItem(new ItemStack[] { mount(5, true) });
        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
      } 
    } 
  }
  
  @EventHandler
  public void onBuyHorse(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();
    if (p.getOpenInventory().getTopInventory().getTitle().contains("Animal Tamer")) {
      e.setCancelled(true);
      if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.SADDLE && 
        e.getCurrentItem().getItemMeta().hasLore()) {
        List<String> lore = e.getCurrentItem().getItemMeta().getLore();
        if (((String)lore.get(lore.size() - 1)).contains("Price:")) {
          int price = ItemVendors.getPriceFromLore(e.getCurrentItem()).intValue();
          if (Money.hasEnoughGems(p, price)) {
            int currtier = 0;
            byte b;
            int i;
            ItemStack[] arrayOfItemStack;
            for (i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i; ) {
              ItemStack is = arrayOfItemStack[b];
              if (getMountTier(is) > currtier)
                currtier = getMountTier(is); 
              b++;
            } 
            int newtier = getMountTier(e.getCurrentItem());
            if (currtier == 0)
              currtier = 1; 
            if (newtier == currtier + 1) {
              p.sendMessage(ChatColor.GRAY + "The '" + e.getCurrentItem().getItemMeta().getDisplayName() + 
                  ChatColor.GRAY + "' costs " + ChatColor.GREEN + ChatColor.BOLD + price + " GEM(s)" + 
                  ChatColor.GRAY + ".");
              p.sendMessage(ChatColor.GRAY + "This item is non-refundable. type " + ChatColor.GREEN + 
                  ChatColor.BOLD + "Y" + ChatColor.GRAY + " to confirm.");
              buyingitem.put(p.getName(), mount(newtier, false));
              buyingprice.put(p.getName(), Integer.valueOf(price));
              p.closeInventory();
            } 
          } else {
            p.sendMessage(ChatColor.RED + "You do not have enough gems to purchase this mount.");
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "COST: " + ChatColor.RED + price + 
                ChatColor.BOLD + "G");
            p.closeInventory();
          } 
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onInvClick(InventoryClickEvent e) {
    if (e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().contains("Horse"))
      e.setCancelled(true); 
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onDamage(EntityDamageEvent e) {
    if (e.getDamage() <= 0.0D)
      return; 
    if (e.getEntity() instanceof Player) {
      Player p = (Player)e.getEntity();
      if (e.getCause() == EntityDamageEvent.DamageCause.FALL || e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
        if (p.isInsideVehicle() && p.getVehicle().getType() == EntityType.HORSE) {
          e.setDamage(0.0D);
          e.setCancelled(true);
        } 
      } else if (p.isInsideVehicle() && p.getVehicle().getType() == EntityType.HORSE) {
        p.getVehicle().remove();
        p.teleport(p.getVehicle().getLocation().add(0.0D, 1.0D, 0.0D));
      } 
    } 
    if (e.getEntity() instanceof Horse) {
      Horse h = (Horse)e.getEntity();
      if (e.getCause() != EntityDamageEvent.DamageCause.FALL && e.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION) {
        Entity p = h.getPassenger();
        if (e instanceof EntityDamageByEntityEvent) {
          EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent)e;
          if (evt.getDamager() instanceof Player && p instanceof Player) {
            Player d = (Player)evt.getDamager();
            ArrayList<String> toggles = Toggles.getToggles(d.getName());
            ArrayList<String> buddies = Buddies.getBuddies(d.getName());
            if (buddies.contains(((Player)p).getName().toLowerCase()) && 
              !toggles.contains("ff")) {
              e.setDamage(0.0D);
              e.setCancelled(true);
              return;
            } 
            if (toggles.contains("pvp")) {
              e.setDamage(0.0D);
              e.setCancelled(true);
              return;
            } 
            if (!Alignments.neutral.containsKey(((Player)p).getName()) && 
              !Alignments.chaotic.containsKey(((Player)p).getName()) && 
              toggles.contains("chaos")) {
              e.setDamage(0.0D);
              e.setCancelled(true);
              return;
            } 
          } 
        } 
        h.remove();
        if (p != null)
          p.teleport(h.getLocation().add(0.0D, 1.0D, 0.0D)); 
      } 
      e.setDamage(0.0D);
      e.setCancelled(true);
    } 
  }
  
  @EventHandler
  public void onDamager(EntityDamageByEntityEvent e) {
    if (e.getDamage() <= 0.0D)
      return; 
    if (e.getDamager() instanceof Player) {
      Player p = (Player)e.getDamager();
      if (p.getVehicle() != null && p.getVehicle().getType() == EntityType.HORSE) {
        p.getVehicle().remove();
        p.teleport(p.getVehicle().getLocation().add(0.0D, 1.0D, 0.0D));
      } 
    } 
  }
  
  @EventHandler
  public void onDismount(VehicleExitEvent e) {
    if (e.getExited() instanceof Player && e.getVehicle() instanceof Horse)
      e.getVehicle().remove(); 
  }
  
  @EventHandler
  public void onMountSummon(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && 
      p.getItemInHand() != null && getMountTier(p.getItemInHand()) > 0 && 
      p.getVehicle() == null && 
      !mounting.containsKey(p.getName())) {
      mounting.put(p.getName(), Integer.valueOf(5));
      mountingloc.put(p.getName(), p.getLocation());
      horsetier.put(p.getName(), Integer.valueOf(getMountTier(p.getItemInHand())));
    } 
  }
  
  @EventHandler
  public void onCancelDamager(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player && e.getEntity() instanceof org.bukkit.entity.LivingEntity) {
      Player p = (Player)e.getDamager();
      if (mounting.containsKey(p.getName())) {
        mounting.remove(p.getName());
        mountingloc.remove(p.getName());
        p.sendMessage(ChatColor.RED + "Mount Summon - " + ChatColor.BOLD + "CANCELLED");
      } 
    } 
  }
  
  @EventHandler
  public void onCancelDamage(EntityDamageEvent e) {
    if (e.getDamage() <= 0.0D)
      return; 
    if (e.getEntity() instanceof Player) {
      Player p = (Player)e.getEntity();
      if (mounting.containsKey(p.getName())) {
        mounting.remove(p.getName());
        mountingloc.remove(p.getName());
        p.sendMessage(ChatColor.RED + "Mount Summon - " + ChatColor.BOLD + "CANCELLED");
      } 
    } 
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    if (mounting.containsKey(p.getName())) {
      mounting.remove(p.getName());
      mountingloc.remove(p.getName());
    } 
    if (p.getVehicle() != null && p.getVehicle().getType() == EntityType.HORSE) {
      p.getVehicle().remove();
      p.teleport(p.getVehicle().getLocation().add(0.0D, 1.0D, 0.0D));
    } 
  }
  
  @EventHandler
  public void onTeleport(PlayerTeleportEvent e) {
    Player p = e.getPlayer();
    if (mounting.containsKey(p.getName())) {
      mounting.remove(p.getName());
      mountingloc.remove(p.getName());
    } 
    p.eject();
  }
  
  @EventHandler
  public void onCancelMove(PlayerMoveEvent e) {
    Player p = e.getPlayer();
    if (mounting.containsKey(p.getName())) {
      Location loc = mountingloc.get(p.getName());
      if (loc.distanceSquared(e.getTo()) >= 2.0D) {
        mounting.remove(p.getName());
        p.sendMessage(ChatColor.RED + "Mount Summon - " + ChatColor.BOLD + "CANCELLED");
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPromptChat(AsyncPlayerChatEvent e) {
    Player p = e.getPlayer();
    if (buyingitem.containsKey(p.getName()) && buyingprice.containsKey(p.getName())) {
      e.setCancelled(true);
      int price = ((Integer)buyingprice.get(p.getName())).intValue();
      ItemStack is = buyingitem.get(p.getName());
      if (e.getMessage().equalsIgnoreCase("y")) {
        if (!Money.hasEnoughGems(p, price)) {
          p.sendMessage(ChatColor.RED + "You do not have enough gems to purchase this mount.");
          p.sendMessage(ChatColor.RED + ChatColor.BOLD + "COST: " + ChatColor.RED + price + 
              ChatColor.BOLD + "G");
          buyingprice.remove(p.getName());
          buyingitem.remove(p.getName());
          return;
        } 
        if (p.getInventory().contains(Material.SADDLE))
          p.getInventory().remove(Material.SADDLE); 
        if (p.getInventory().firstEmpty() == -1) {
          p.sendMessage(
              ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
          return;
        } 
        Money.takeGems(p, price);
        p.getInventory().setItem(p.getInventory().firstEmpty(), is);
        p.sendMessage(ChatColor.RED + "-" + price + ChatColor.BOLD + "G");
        p.sendMessage(ChatColor.GREEN + "Transaction successful.");
        p.sendMessage(ChatColor.GRAY + "You are now the proud owner of a mount -- " + 
            ChatColor.UNDERLINE + 
            "to summon your new mount, simply right click with the saddle in your player's hand.");
        buyingprice.remove(p.getName());
        buyingitem.remove(p.getName());
      } else {
        p.sendMessage(ChatColor.RED + "Purchase - " + ChatColor.BOLD + "CANCELLED");
        buyingprice.remove(p.getName());
        buyingitem.remove(p.getName());
        return;
      } 
    } 
  }
}
