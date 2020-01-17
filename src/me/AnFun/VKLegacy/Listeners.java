package me.AnFun.VKLegacy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcsg.double0negative.tabapi.TabAPI;

public class Listeners implements Listener {
  public void onEnable() {
    Main.log.info("[Listeners] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    (new BukkitRunnable() {
        public void run() {
          for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Listeners.this.updateTabList(p);
            if (Alignments.isSafeZone(p.getLocation())) {
              p.setFoodLevel(20);
              p.setSaturation(20.0F);
            } 
          } 
        }
      }).runTaskTimerAsynchronously(Main.plugin, 200L, 100L);
    (new BukkitRunnable() {
        public void run() {
          for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (ModerationMechanics.isSub(p) && 
              !ModerationMechanics.toggletrail.contains(p.getName().toLowerCase())) {
              if (((String)ModerationMechanics.ranks.get(p.getName())).equalsIgnoreCase("sub"))
                ParticleEffect.VILLAGER_HAPPY.display(0.125F, 0.125F, 0.125F, 0.02F, 10, 
                    p.getLocation().add(0.0D, 0.1D, 0.0D), 20.0D); 
              if (((String)ModerationMechanics.ranks.get(p.getName())).equalsIgnoreCase("sub+"))
                ParticleEffect.FLAME.display(0.0F, 0.0F, 0.0F, 0.02F, 10, p.getLocation().add(0.0D, 0.1D, 0.0D), 20.0D); 
              if (((String)ModerationMechanics.ranks.get(p.getName())).equalsIgnoreCase("sub++"))
                ParticleEffect.SPELL_WITCH.display(0.0F, 0.0F, 0.0F, 1.0F, 10, p.getLocation().add(0.0D, 0.25D, 0.0D), 20.0D); 
            } 
          } 
        }
      }).runTaskTimerAsynchronously(Main.plugin, 1L, 1L);
  }
  
  public void onDisable() {
    Main.log.info("[Listeners] has been disabled.");
  }
  
  @EventHandler
  public void onMOTD(ServerListPingEvent e) {
    String motd = ChatColor.WHITE + ChatColor.BOLD + "Viking Legacy";
    for (int i = 0; i < 30; i++)
      motd = String.valueOf(motd) + " "; 
    motd = String.valueOf(motd) + ChatColor.GRAY + "Patch " + Main.plugin.getDescription().getVersion();
    e.setMotd(motd);
    e.setMaxPlayers(60);
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void a(ServerCommandEvent e) {
    if (e.getCommand().equalsIgnoreCase("save-all") || e.getCommand().equalsIgnoreCase("/save-all"))
      e.setCommand(""); 
  }
  
  @EventHandler
  public void onJoinBanned(PlayerLoginEvent e) {
    Player p = e.getPlayer();
    if (ModerationMechanics.banned.containsKey(p.getName().toLowerCase())) {
      if (((Integer)ModerationMechanics.banned.get(p.getName().toLowerCase())).intValue() == -1) {
        e.setKickMessage(ChatColor.RED + "Your account has been PERMANENTLY disabled." + "\n" + ChatColor.GRAY + 
            "For further information about this suspension, please visit " + ChatColor.UNDERLINE + 
            "https://twitter.com/VikingLegacyMC");
      } else {
        e.setKickMessage(ChatColor.RED + "Your account has been TEMPORARILY locked due to suspisious activity." + 
            "\n" + ChatColor.GRAY + "For further information about this suspension, please visit " + 
            ChatColor.UNDERLINE + "https://twitter.com/VikingLegacyMC");
      } 
      e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
      return;
    } 
    if (Bukkit.getServer().getOnlinePlayers().size() >= 60)
      if (ModerationMechanics.isSub(p) || p.isOp()) {
        e.allow();
      } else {
        e.setKickMessage(String.valueOf(ChatColor.RED.toString()) + "This Viking Legacy server is currently FULL." + "\n" + 
            ChatColor.GRAY.toString() + 
            "You can subscribe at http://vikinglegacy.buycraft.net/ to get instant access.");
        e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        return;
      }  
  }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    p.setLevel(100);
    p.setExp(1.0F);
    p.setHealthScale(20.0D);
    p.setHealthScaled(true);
    p.getInventory().setHeldItemSlot(0);
    if (p.getInventory().getItem(0) != null && (p.getInventory().getItem(0).getType().name().contains("_AXE") || 
      p.getInventory().getItem(0).getType().name().contains("_SWORD") || 
      p.getInventory().getItem(0).getType().name().contains("_HOE") || 
      p.getInventory().getItem(0).getType().name().contains("_SPADE")))
      p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.5F); 
    for (int i = 0; i < 20; i++)
      p.sendMessage(" "); 
    p.sendMessage(ChatColor.WHITE + ChatColor.BOLD + "          Viking Legacy Patch " + 
        Main.plugin.getDescription().getVersion());
    p.sendMessage(ChatColor.GRAY + "                  https://twitter.com/VikingLegacyMC");
    p.sendMessage("");
    p.sendMessage(ChatColor.YELLOW + "                   You are on the " + ChatColor.BOLD + "US-1" + 
        ChatColor.YELLOW + " shard.");
    p.sendMessage(ChatColor.GRAY + ChatColor.ITALIC + "       To manage your gameplay settings, use " + 
        ChatColor.YELLOW + ChatColor.UNDERLINE + "/toggles");
    if (ModerationMechanics.isSub(p))
      p.sendMessage(ChatColor.GRAY + ChatColor.ITALIC + "       To toggle your subscriber trail, use " + 
          ChatColor.YELLOW + ChatColor.UNDERLINE + "/toggletrail"); 
    p.sendMessage("");
    e.setJoinMessage(null);
    if (!p.hasPlayedBefore()) {
      Kit(e.getPlayer());
      p.teleport(TeleportBooks.Cyrennica);
    } 
    updateTabList(p);
    hpCheck(p);
    if (p.isOp()) {
      for (Player pl : Bukkit.getOnlinePlayers()) {
        if (pl != p && 
          !pl.isOp())
          pl.hidePlayer(p); 
      } 
      if (!ModerationMechanics.vanished.contains(p.getName().toLowerCase()))
        ModerationMechanics.vanished.add(p.getName().toLowerCase()); 
      p.sendMessage(ChatColor.AQUA + ChatColor.BOLD + "               GM INVISIBILITY (infinite)");
      p.sendMessage(ChatColor.GREEN + "                      You are now " + ChatColor.BOLD + "invisible.");
      p.setMaxHealth(9999.0D);
      p.setHealth(9999.0D);
    } else {
      for (Player pl : Bukkit.getOnlinePlayers()) {
        if (pl != p && 
          pl.isOp())
          p.hidePlayer(pl); 
      } 
    } 
  }
  
  public void updateTabList(Player p) {
    TabAPI.setTabString(Main.plugin, p, 0, 0, ChatColor.GRAY + "*-------------");
    TabAPI.setTabString(Main.plugin, p, 0, 1, ChatColor.DARK_AQUA + ChatColor.BOLD + "    Guild UI");
    TabAPI.setTabString(Main.plugin, p, 0, 2, ChatColor.GRAY + "-------------*");
    TabAPI.setTabString(Main.plugin, p, 2, 1, ChatColor.DARK_AQUA + "Guild Name");
    TabAPI.setTabString(Main.plugin, p, 3, 1, ChatColor.GRAY + "N/A");
    TabAPI.setTabString(Main.plugin, p, 18, 1, ChatColor.DARK_AQUA + "Shard " + ChatColor.GRAY.toString() + "US-1", 
        0);
    int online = Bukkit.getOnlinePlayers().size();
    int max = 60;
    TabAPI.setTabString(Main.plugin, p, 19, 1, String.valueOf(ChatColor.GRAY.toString()) + online + " / " + max, 0);
    TabAPI.updatePlayer(p);
  }
  
  @EventHandler
  public void onLeave(PlayerKickEvent e) {
    e.setLeaveMessage(null);
  }
  
  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent e) {
    e.setQuitMessage(null);
  }
  
  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    Player p = e.getPlayer();
    Alignments.tagged.remove(p.getName());
    combat.remove(p.getName());
  }
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDeath(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity) {
      LivingEntity s = (LivingEntity)e.getEntity();
      if (e.getDamage() >= s.getHealth()) {
        if (mobd.containsKey(s.getUniqueId()))
          mobd.remove(s.getUniqueId()); 
        if (this.firedmg.containsKey(s.getUniqueId()))
          this.firedmg.remove(s.getUniqueId()); 
        if (Mobs.sound.containsKey(s.getUniqueId()))
          Mobs.sound.remove(s.getUniqueId()); 
        if (named.containsKey(s.getUniqueId()))
          named.remove(s.getUniqueId()); 
      } 
    } 
  }
  
  public static HashMap<UUID, Long> named = new HashMap<>();
  
  @EventHandler
  public void onHealthBar(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player) && 
      e.getDamage() > 0.0D) {
      LivingEntity s = (LivingEntity)e.getEntity();
      double max = s.getMaxHealth();
      double hp = s.getHealth() - e.getDamage();
      s.setCustomName(Mobs.generateOverheadBar((Entity)s, hp, max, Mobs.getMobTier(s), Mobs.isElite(s)));
      s.setCustomNameVisible(true);
      named.put(s.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
    } 
  }
  
  @EventHandler
  public void onPotDrink(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      Player p = e.getPlayer();
      if (p.getItemInHand().getType() == Material.POTION && p.getItemInHand() != null) {
        e.setCancelled(true);
        if (p.getItemInHand().getItemMeta().hasLore()) {
          String l = ChatColor.stripColor(p.getItemInHand().getItemMeta().getLore().get(0));
          l = l.split("HP")[0];
          int hp = 0;
          try {
            hp = Integer.parseInt(l.split(" ")[4]);
          } catch (Exception ex) {
            hp = 0;
          } 
          if (hp > 0) {
            p.playSound(p.getLocation(), Sound.DRINK, 1.0F, 1.0F);
            p.setItemInHand(null);
            if (p.getHealth() + hp > p.getMaxHealth()) {
              p.sendMessage("               " + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + 
                  hp + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + (int)p.getMaxHealth() + "/" + 
                  (int)p.getMaxHealth() + "HP]");
              p.setHealth(p.getMaxHealth());
            } else {
              p.sendMessage("               " + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + 
                  hp + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + (int)(p.getHealth() + hp) + 
                  "/" + (int)p.getMaxHealth() + "HP]");
              p.setHealth(p.getHealth() + hp);
            } 
          } 
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onWeatherChange(WeatherChangeEvent e) {
    if (e.toWeatherState())
      e.setCancelled(true); 
  }
  
  HashMap<String, Long> update = new HashMap<>();
  
  @EventHandler
  public void onBookOpen(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    PlayerInventory i = p.getInventory();
    if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.WRITTEN_BOOK && (
      e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
      BookMeta bm = (BookMeta)book.getItemMeta();
      String s = ChatColor.DARK_GREEN + ChatColor.UNDERLINE + "Lawful";
      String desc = ChatColor.BLACK + ChatColor.ITALIC + "-30% Durability Arm/Wep on Death";
      if (Alignments.chaotic.containsKey(p.getName())) {
        s = ChatColor.DARK_RED + ChatColor.UNDERLINE + "Chaotic\n" + ChatColor.BLACK + ChatColor.BOLD + 
          "Neutral" + ChatColor.BLACK + " in " + Alignments.chaotic.get(p.getName()) + "s";
        desc = ChatColor.BLACK + ChatColor.ITALIC + "Inventory LOST on Death";
      } 
      if (Alignments.neutral.containsKey(p.getName())) {
        s = ChatColor.GOLD + ChatColor.UNDERLINE + "Neutral\n" + ChatColor.BLACK + ChatColor.BOLD + 
          "Lawful" + ChatColor.BLACK + " in " + Alignments.neutral.get(p.getName()) + "s";
        desc = ChatColor.BLACK + ChatColor.ITALIC + "25%/50% Arm/Wep LOST on Death";
      } 
      int dps = 0;
      int arm = 0;
      int amt = 5;
      int nrg = 100;
      int block = 0;
      int dodge = 0;
      int intel = 0;
      int str = 0;
      int vit = 0;
      int sword_dmg = 0;
      int axe_dmg = 0;
      int block_pcnt = 0;
      int health_pcnt = 0;
      int nrg_pcnt = 0;
      int crit_pcnt = 0;
      byte b;
      int j;
      ItemStack[] arrayOfItemStack;
      for (j = (arrayOfItemStack = i.getArmorContents()).length, b = 0; b < j; ) {
        ItemStack is = arrayOfItemStack[b];
        if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
          int adddps = Damage.getDps(is);
          dps += adddps;
          int addarm = Damage.getArmor(is);
          arm += addarm;
          int added = Damage.getHps(is);
          amt += added;
          int addednrg = Damage.getEnergy(is);
          nrg += addednrg;
          int addeddodge = Damage.getPercent(is, "DODGE");
          dodge += addeddodge;
          int addedblock = Damage.getPercent(is, "BLOCK");
          block += addedblock;
          int addedint = Damage.getElem(is, "INT");
          intel += addedint;
          int addedstr = Damage.getElem(is, "STR");
          str += addedstr;
          int addedvit = Damage.getElem(is, "VIT");
          vit += addedvit;
        } 
        b++;
      } 
      if (intel > 0) {
        nrg += Math.round((intel / 125));
        nrg_pcnt = (int)Math.round(intel * 0.016D);
        crit_pcnt = (int)Math.round(intel * 0.014D);
      } 
      if (vit > 0) {
        sword_dmg = Math.round((vit / 50));
        health_pcnt = (int)Math.round(vit * 0.05D);
      } 
      if (str > 0) {
        axe_dmg = Math.round((str / 38));
        block_pcnt = (int)Math.round(str * 0.015D);
        block = (int)(block + Math.round(str * 0.015D));
      } 
      bm.addPage(new String[] { ChatColor.UNDERLINE + ChatColor.BOLD + "  Your Character  \n\n" + ChatColor.RESET + 
            ChatColor.BOLD + "Alignment: " + s + "\n" + desc + "\n\n" + ChatColor.BLACK + "  " + 
            (int)p.getHealth() + " / " + (int)p.getMaxHealth() + ChatColor.BOLD + " HP\n" + 
            ChatColor.BLACK + "  " + arm + " - " + arm + "%" + ChatColor.BOLD + " Armor\n" + 
            ChatColor.BLACK + "  " + dps + " - " + dps + "%" + ChatColor.BOLD + " DPS\n" + ChatColor.BLACK + 
            "  " + amt + ChatColor.BOLD + " HP/s\n" + ChatColor.BLACK + "  " + nrg + "% " + ChatColor.BOLD + 
            "Energy\n" + ChatColor.BLACK + "  " + dodge + "% " + ChatColor.BOLD + "Dodge\n" + 
            ChatColor.BLACK + "  " + block + "% " + ChatColor.BOLD + "Block" });
      bm.addPage(new String[] { ChatColor.BLACK + ChatColor.BOLD + "+ " + str + " Strength\n" + "  " + ChatColor.BLACK + 
            ChatColor.UNDERLINE + "'The Warrior'\n" + ChatColor.BLACK + "+" + block_pcnt + "% Block\n" + 
            ChatColor.BLACK + "+" + axe_dmg + "% Axe DMG\n\n" + ChatColor.BLACK + ChatColor.BOLD + "+ " + 
            vit + " Vitality\n" + "  " + ChatColor.BLACK + ChatColor.UNDERLINE + "'The Defender'\n" + 
            ChatColor.BLACK + "+" + health_pcnt + "% Health\n" + 
            
            ChatColor.BLACK + "+" + sword_dmg + "% Sword DMG\n\n" + ChatColor.BLACK + ChatColor.BOLD + 
            "+ " + intel + " Intellect\n" + "  " + ChatColor.BLACK + ChatColor.UNDERLINE + "'The Mage'\n" + 
            ChatColor.BLACK + "+" + nrg_pcnt + "% Energy\n" + ChatColor.BLACK + "+" + crit_pcnt + 
            "% Critical Hit\n\n" });
      bm.setDisplayName(ChatColor.GREEN + ChatColor.BOLD + "Character Journal");
      bm.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "A book that displays", 
              ChatColor.GRAY + "your character's stats" }));
      book.setItemMeta((ItemMeta)bm);
      p.setItemInHand(book);
      p.updateInventory();
      p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1.0F, 1.25F);
      if (!this.update.containsKey(p.getName()) || 
        System.currentTimeMillis() - ((Long)this.update.get(p.getName())).longValue() > 2000L)
        p.closeInventory(); 
      this.update.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
    } 
  }
  
  @EventHandler
  public void onCloseChest(InventoryCloseEvent e) {
    if (e.getInventory().getName().contains("Bank Chest") && 
      e.getPlayer() instanceof Player) {
      Player p = (Player)e.getPlayer();
      p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F);
    } 
  }
  
  @EventHandler
  public void onArmourPutOn(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    if (p.getItemInHand() != null && (p.getItemInHand().getType().name().contains("HELMET") || 
      p.getItemInHand().getType().name().contains("CHESTPLATE") || 
      p.getItemInHand().getType().name().contains("LEGGINGS") || 
      p.getItemInHand().getType().name().contains("BOOTS")) && (
      e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      e.setCancelled(true);
      p.updateInventory();
    } 
  }
  
  @EventHandler
  public void onToggleSprint(PlayerToggleSprintEvent e) {
    if (Energy.getEnergy(e.getPlayer()) <= 0.0F) {
      e.setCancelled(true);
      e.getPlayer().setSprinting(false);
    } 
  }
  
  @EventHandler
  public void onSprint(PlayerMoveEvent e) {
    if (Energy.getEnergy(e.getPlayer()) <= 0.0F)
      e.getPlayer().setSprinting(false); 
  }
  
  public static HashMap<String, Long> combat = new HashMap<>();
  
  @EventHandler
  public void onCombatTag(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
      if (e.getDamage() <= 0.0D)
        return; 
      Player p = (Player)e.getDamager();
      combat.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
    } 
  }
  
  public static HashMap<UUID, Long> mobd = new HashMap<>();
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onNoAutoclick(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Player && e.getDamager() instanceof LivingEntity && 
      !(e.getDamager() instanceof Player)) {
      LivingEntity s = (LivingEntity)e.getDamager();
      if (!mobd.containsKey(s.getUniqueId()) || (mobd.containsKey(s.getUniqueId()) && 
        System.currentTimeMillis() - ((Long)mobd.get(s.getUniqueId())).longValue() > 1000L)) {
        mobd.put(s.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
      } else if (!(e.getDamager() instanceof org.bukkit.entity.MagmaCube)) {
        e.setDamage(0.0D);
        e.setCancelled(true);
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onNoEnergyDamage(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {
      Player d = (Player)e.getDamager();
      if (d.getExp() <= 0.0F) {
        e.setDamage(0.0D);
        e.setCancelled(true);
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onNoDamager(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof LivingEntity) {
      LivingEntity p = (LivingEntity)e.getDamager();
      if (Alignments.isSafeZone(p.getLocation())) {
        e.setDamage(0.0D);
        e.setCancelled(true);
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onNoDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity) {
      LivingEntity p = (LivingEntity)e.getEntity();
      if (Alignments.isSafeZone(p.getLocation())) {
        e.setDamage(0.0D);
        e.setCancelled(true);
      } 
    } 
  }
  
  @EventHandler
  public void onLoginShiny(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    byte b;
    int i;
    ItemStack[] arrayOfItemStack;
    for (i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i; ) {
      ItemStack is = arrayOfItemStack[b];
      if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && 
        Enchants.getPlus(is) > 3)
        is.addUnsafeEnchantment(Enchants.glow, 1); 
      b++;
    } 
    for (i = (arrayOfItemStack = p.getInventory().getArmorContents()).length, b = 0; b < i; ) {
      ItemStack is = arrayOfItemStack[b];
      if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && 
        Enchants.getPlus(is) > 3)
        is.addUnsafeEnchantment(Enchants.glow, 1); 
      b++;
    } 
  }
  
  @EventHandler
  public void onOpenShinyShiny(InventoryOpenEvent e) {
    if (e.getInventory().getName().contains("Bank Chest")) {
      byte b;
      int i;
      ItemStack[] arrayOfItemStack;
      for (i = (arrayOfItemStack = e.getInventory().getContents()).length, b = 0; b < i; ) {
        ItemStack is = arrayOfItemStack[b];
        if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && 
          Enchants.getPlus(is) > 3)
          is.addUnsafeEnchantment(Enchants.glow, 1); 
        b++;
      } 
    } 
  }
  
  @EventHandler
  public void onMapOpen(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && 
      p.getItemInHand().getType() == Material.EMPTY_MAP) {
      e.setCancelled(true);
      p.updateInventory();
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onGemPickup(PlayerPickupItemEvent e) {
    Player p = e.getPlayer();
    if (!e.isCancelled() && 
      e.getItem().getItemStack().getType() == Material.EMERALD) {
      p.sendMessage(ChatColor.GREEN + ChatColor.BOLD + "                    +" + ChatColor.GREEN + 
          e.getItem().getItemStack().getAmount() + ChatColor.GREEN + ChatColor.BOLD + "G");
      p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
    } 
  }
  
  HashMap<UUID, Long> firedmg = new HashMap<>();
  
  @EventHandler(priority = EventPriority.LOW)
  public void onDamagePercent(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity) {
      if (e.getDamage() <= 0.0D)
        return; 
      LivingEntity p = (LivingEntity)e.getEntity();
      if (e.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || 
        e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
        if (!this.firedmg.containsKey(p.getUniqueId()) || (this.firedmg.containsKey(p.getUniqueId()) && 
          System.currentTimeMillis() - ((Long)this.firedmg.get(p.getUniqueId())).longValue() > 500L)) {
          this.firedmg.put(p.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
          double multiplier = 0.01D;
          if (e.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals(EntityDamageEvent.DamageCause.LAVA))
            multiplier = 0.03D; 
          if (p.getMaxHealth() * multiplier < 1.0D) {
            e.setDamage(1.0D);
          } else {
            e.setDamage(p.getMaxHealth() * multiplier);
          } 
        } else {
          e.setDamage(0.0D);
          e.setCancelled(true);
        } 
      } else if (e.getCause().equals(EntityDamageEvent.DamageCause.POISON)) {
        if (p.getMaxHealth() * 0.01D >= p.getHealth()) {
          e.setDamage(p.getHealth() - 1.0D);
        } else if (p.getMaxHealth() * 0.01D < 1.0D) {
          e.setDamage(1.0D);
        } else {
          e.setDamage(p.getMaxHealth() * 0.01D);
        } 
      } else if (e.getCause().equals(EntityDamageEvent.DamageCause.DROWNING)) {
        if (p.getMaxHealth() * 0.04D < 1.0D) {
          e.setDamage(1.0D);
        } else {
          e.setDamage(p.getMaxHealth() * 0.04D);
        } 
      } else if (e.getCause().equals(EntityDamageEvent.DamageCause.WITHER)) {
        e.setCancelled(true);
        e.setDamage(0.0D);
        if (p.hasPotionEffect(PotionEffectType.WITHER))
          p.removePotionEffect(PotionEffectType.WITHER); 
      } else if (e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
        e.setDamage(0.0D);
        e.setCancelled(true);
        Location loc = p.getLocation();
        while ((loc.getBlock().getType() != Material.AIR || 
          loc.add(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.AIR) && loc.getY() < 255.0D)
          loc.add(0.0D, 1.0D, 0.0D); 
        p.teleport(loc);
      } else if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
        e.setDamage(0.0D);
        e.setCancelled(true);
        if (p instanceof Player) {
          Player pl = (Player)p;
          if (Alignments.chaotic.containsKey(pl.getName())) {
            p.teleport(TeleportBooks.generateRandomSpawnPoint(pl.getName()));
          } else {
            p.teleport(TeleportBooks.Cyrennica);
          } 
        } 
      } else if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
        if (e.getDamage() * p.getMaxHealth() * 0.02D >= p.getHealth()) {
          e.setDamage(p.getHealth() - 1.0D);
        } else if (e.getDamage() * p.getMaxHealth() * 0.02D < 1.0D) {
          e.setDamage(1.0D);
        } else {
          e.setDamage(e.getDamage() * p.getMaxHealth() * 0.02D);
        } 
      } else if (e.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)) {
        if (!this.firedmg.containsKey(p.getUniqueId()) || (this.firedmg.containsKey(p.getUniqueId()) && 
          System.currentTimeMillis() - ((Long)this.firedmg.get(p.getUniqueId())).longValue() > 500L)) {
          this.firedmg.put(p.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
          e.setDamage(1.0D);
        } else {
          e.setDamage(0.0D);
          e.setCancelled(true);
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onHungerLoss(FoodLevelChangeEvent e) {
    if (e.getEntity() instanceof Player) {
      Player p = (Player)e.getEntity();
      if (Alignments.isSafeZone(p.getLocation())) {
        p.setFoodLevel(20);
        p.setSaturation(20.0F);
        e.setCancelled(true);
      } else if (e.getFoodLevel() < p.getFoodLevel()) {
        Random r = new Random();
        int doitakefood = r.nextInt(5);
        if (doitakefood != 0)
          e.setCancelled(true); 
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDeath(PlayerDeathEvent e) {
    Player p = e.getEntity();
    p.playSound(p.getLocation(), Sound.WITHER_SPAWN, 1.0F, 1.0F);
    e.setDroppedExp(0);
    e.setDeathMessage(null);
    Alignments.tagged.remove(p.getName());
    combat.remove(p.getName());
  }
  
  public static void hpCheck(Player p) {
    if (p.isOp())
      return; 
    PlayerInventory i = p.getInventory();
    double a = 50.0D;
    double vital = 0.0D;
    byte b;
    int j;
    ItemStack[] arrayOfItemStack;
    for (j = (arrayOfItemStack = i.getArmorContents()).length, b = 0; b < j; ) {
      ItemStack is = arrayOfItemStack[b];
      if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
        double health = Damage.getHp(is);
        int vit = Damage.getElem(is, "VIT");
        a += health;
        vital += vit;
      } 
      b++;
    } 
    if (vital > 0.0D) {
      double mod = vital * 0.05D;
      a += a * mod / 100.0D;
      p.setMaxHealth((int)a);
    } else {
      p.setMaxHealth(a);
    } 
    p.setHealthScale(20.0D);
    p.setHealthScaled(true);
  }
  
  boolean isArmor(ItemStack is) {
    if (is != null) {
      if (is.getType().name().contains("_HELMET"))
        return true; 
      if (is.getType().name().contains("_CHESTPLATE"))
        return true; 
      if (is.getType().name().contains("_LEGGINGS"))
        return true; 
      if (is.getType().name().contains("_BOOTS"))
        return true; 
    } 
    return false;
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    final Player p = (Player)e.getWhoClicked();
    if (e.getSlotType() == InventoryType.SlotType.ARMOR && (e.isLeftClick() || e.isRightClick() || e.isShiftClick()) && ((
      isArmor(e.getCurrentItem()) && isArmor(e.getCursor())) || (
      isArmor(e.getCurrentItem()) && (
      e.getCursor() == null || e.getCursor().getType() == Material.AIR)) || ((
      e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) && 
      isArmor(e.getCursor()))))
      p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F); 
    if (e.getInventory().getHolder() == p) {
      if (e.isShiftClick() && e.getCurrentItem().getType().name().contains("_HELMET") && 
        p.getInventory().getHelmet() == null)
        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F); 
      if (e.isShiftClick() && e.getCurrentItem().getType().name().contains("_CHESTPLATE") && 
        p.getInventory().getChestplate() == null)
        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F); 
      if (e.isShiftClick() && e.getCurrentItem().getType().name().contains("_LEGGINGS") && 
        p.getInventory().getLeggings() == null)
        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F); 
      if (e.isShiftClick() && e.getCurrentItem().getType().name().contains("_BOOTS") && 
        p.getInventory().getBoots() == null)
        p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F); 
    } 
    (new BukkitRunnable() {
        public void run() {
          Listeners.hpCheck(p);
        }
      }).runTaskLater(Main.plugin, 1L);
  }
  
  @EventHandler
  public void onWeaponSwitch(PlayerItemHeldEvent e) {
    Player p = e.getPlayer();
    ItemStack newItem = p.getInventory().getItem(e.getNewSlot());
    if (newItem != null && (newItem.getType().name().contains("_SWORD") || newItem.getType().name().contains("_AXE") || 
      newItem.getType().name().contains("_HOE") || newItem.getType().name().contains("_SPADE")))
      p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.5F); 
  }
  
  @EventHandler
  public void onTag(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player) {
      if (e.getDamage() <= 0.0D)
        return; 
      Player p = (Player)e.getEntity();
      if (e.getCause() != EntityDamageEvent.DamageCause.FALL)
        Alignments.tagged.put(p.getName(), Long.valueOf(System.currentTimeMillis())); 
    } 
  }
  
  @EventHandler
  public void onHitTag(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
      if (e.getDamage() <= 0.0D)
        return; 
      Player p = (Player)e.getDamager();
      Alignments.tagged.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onKickLog(PlayerKickEvent e) {
    Player p = e.getPlayer();
    if (!Alignments.isSafeZone(p.getLocation()) && 
      Alignments.tagged.containsKey(p.getName()) && 
      System.currentTimeMillis() - ((Long)Alignments.tagged.get(p.getName())).longValue() < 10000L)
      p.setHealth(0.0D); 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onQuitLog(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    if (!Alignments.isSafeZone(p.getLocation()) && 
      Alignments.tagged.containsKey(p.getName()) && 
      System.currentTimeMillis() - ((Long)Alignments.tagged.get(p.getName())).longValue() < 10000L)
      p.setHealth(0.0D); 
  }
  
  @EventHandler
  public void onHealthRegen(EntityRegainHealthEvent e) {
    e.setCancelled(true);
  }
  
  public void Kit(Player p) {
    Random random = new Random();
    int wep = random.nextInt(2) + 1;
    if (wep == 1) {
      ItemStack S = new ItemStack(Material.WOOD_SWORD);
      ItemMeta smeta = S.getItemMeta();
      smeta.setDisplayName(ChatColor.WHITE + "Training Sword");
      List<String> slore = new ArrayList<>();
      slore.add(ChatColor.RED + "DMG: 3 - 4");
      slore.add(ChatColor.GRAY + "Untradeable");
      smeta.setLore(slore);
      S.setItemMeta(smeta);
      p.getInventory().addItem(new ItemStack[] { S });
    } 
    if (wep == 2) {
      ItemStack S = new ItemStack(Material.WOOD_AXE);
      ItemMeta smeta = S.getItemMeta();
      smeta.setDisplayName(ChatColor.WHITE + "Training Hatchet");
      List<String> slore = new ArrayList<>();
      slore.add(ChatColor.RED + "DMG: 2 - 5");
      slore.add(ChatColor.GRAY + "Untradeable");
      smeta.setLore(slore);
      S.setItemMeta(smeta);
      p.getInventory().addItem(new ItemStack[] { S });
    } 
    ItemStack pot = new ItemStack(Material.POTION, 1, (short)1);
    ItemMeta potmeta = pot.getItemMeta();
    potmeta.setDisplayName(ChatColor.WHITE + "Minor Health Potion");
    potmeta.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "A potion that restores " + ChatColor.WHITE + "15HP", 
            ChatColor.GRAY + "Untradeable" }));
    PotionMeta pm = (PotionMeta)potmeta;
    pm.clearCustomEffects();
    pm.addCustomEffect(PotionEffectType.HEAL.createEffect(0, 0), true);
    pot.setItemMeta((ItemMeta)pm);
    for (int t = 0; t < 3; t++) {
      p.getInventory().addItem(new ItemStack[] { pot });
    } 
    ItemStack bread = new ItemStack(Material.BREAD);
    ItemMeta breadmeta = bread.getItemMeta();
    breadmeta.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Untradeable" }));
    bread.setItemMeta(breadmeta);
    for (int i = 0; i < 2; i++)
      p.getInventory().setItem(p.getInventory().firstEmpty(), bread); 
    p.getInventory().addItem(new ItemStack[] { Hearthstone.hearthstone() });
    p.getInventory().addItem(new ItemStack[] { Journal.journal() });
    p.setMaxHealth(50.0D);
    p.setHealth(50.0D);
    p.setHealthScale(20.0D);
    p.setHealthScaled(true);
  }
}
