package me.AnFun.VKLegacy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Respawn implements Listener {
  List<Player> dead = new ArrayList<>();
  
  public void onEnable() {
    Main.log.info("[Respawn] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    File file = new File(Main.plugin.getDataFolder(), "respawndata");
    if (!file.exists())
      file.mkdirs(); 
  }
  
  public void onDisable() {
    Main.log.info("[Respawn] has been disabled.");
    File file = new File(Main.plugin.getDataFolder(), "respawndata");
    if (!file.exists())
      file.mkdirs(); 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onDeath(PlayerDeathEvent e) {
    Player p = e.getEntity();
    if (!this.dead.contains(p)) {
      this.dead.add(p);
      Random random = new Random();
      int wepdrop = random.nextInt(2) + 1;
      int armor = random.nextInt(4) + 1;
      List<ItemStack> newInventory = new ArrayList<>();
      if (!Alignments.neutral.containsKey(p.getName()) && !Alignments.chaotic.containsKey(p.getName())) {
        byte b1;
        int k;
        ItemStack[] arrayOfItemStack1;
        for (k = (arrayOfItemStack1 = p.getInventory().getArmorContents()).length, b1 = 0; b1 < k; ) {
          ItemStack is = arrayOfItemStack1[b1];
          if (is != null && is.getType() != Material.AIR)
            if (Durability.getDuraPercent(is) > 30.0F) {
              newInventory.add(Durability.takeDura(is, 30.0F));
            } else {
              e.getDrops().remove(is);
              p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
            }  
          b1++;
        } 
        if (p.getInventory().getItem(0) != null && 
          !p.getInventory().getItem(0).getType().name().contains("_PICKAXE")) {
          ItemStack is = p.getInventory().getItem(0);
          if (is.getType().name().contains("_SWORD") || is.getType().name().contains("_AXE") || 
            is.getType().name().contains("_SPADE") || is.getType().name().contains("_HOE") || 
            is.getType().name().contains("_HELMET") || 
            is.getType().name().contains("_CHESTPLATE") || 
            is.getType().name().contains("_BOOTS")) {
            if (Durability.getDuraPercent(is) > 30.0F) {
              newInventory.add(Durability.takeDura(is, 30.0F));
            } else {
              e.getDrops().remove(is);
              p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
            } 
          } else {
            newInventory.add(is);
          } 
        } 
        for (k = (arrayOfItemStack1 = p.getInventory().getContents()).length, b1 = 0; b1 < k; ) {
          ItemStack is = arrayOfItemStack1[b1];
          if (is != null && is.getType() != Material.AIR && 
            is.getType().name().contains("_PICKAXE"))
            if (Durability.getDuraPercent(is) > 30.0F) {
              newInventory.add(Durability.takeDura(is, 30.0F));
            } else {
              e.getDrops().remove(is);
              p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
            }  
          b1++;
        } 
      } else if (Alignments.neutral.containsKey(p.getName()) && !Alignments.chaotic.containsKey(p.getName())) {
        byte b1;
        int k;
        ItemStack[] arrayOfItemStack1;
        for (k = (arrayOfItemStack1 = p.getInventory().getArmorContents()).length, b1 = 0; b1 < k; ) {
          ItemStack is = arrayOfItemStack1[b1];
          if (is != null && is.getType() != Material.AIR)
            if (Durability.getDuraPercent(is) > 30.0F) {
              newInventory.add(Durability.takeDura(is, 30.0F));
            } else {
              e.getDrops().remove(is);
              p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
            }  
          b1++;
        } 
        for (k = (arrayOfItemStack1 = p.getInventory().getContents()).length, b1 = 0; b1 < k; ) {
          ItemStack is = arrayOfItemStack1[b1];
          if (is != null && is.getType() != Material.AIR && 
            is.getType().name().contains("_PICKAXE"))
            if (Durability.getDuraPercent(is) > 30.0F) {
              newInventory.add(Durability.takeDura(is, 30.0F));
            } else {
              e.getDrops().remove(is);
              p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
            }  
          b1++;
        } 
      } else if (Alignments.neutral.containsKey(p.getName()) && !Alignments.chaotic.containsKey(p.getName())) {
        List<ItemStack> arm = new ArrayList<>();
        byte b1;
        int k;
        ItemStack[] arrayOfItemStack1;
        for (k = (arrayOfItemStack1 = p.getInventory().getArmorContents()).length, b1 = 0; b1 < k; ) {
          ItemStack is = arrayOfItemStack1[b1];
          if (is != null && is.getType() != Material.AIR)
            arm.add(is); 
          b1++;
        } 
        if (armor == 1 && arm.size() > 0)
          arm.remove(arm.get(random.nextInt(arm.size()))); 
        if (arm.size() > 0)
          for (ItemStack is : arm) {
            if (Durability.getDuraPercent(is) > 30.0F) {
              newInventory.add(Durability.takeDura(is, 30.0F));
              continue;
            } 
            e.getDrops().remove(is);
            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
          }  
        if (wepdrop == 1 && 
          p.getInventory().getItem(0) != null && 
          !p.getInventory().getItem(0).getType().name().contains("_PICKAXE")) {
          ItemStack is = p.getInventory().getItem(0);
          if (is.getType().name().contains("_SWORD") || is.getType().name().contains("_AXE") || 
            is.getType().name().contains("_SPADE") || is.getType().name().contains("_HOE") || 
            is.getType().name().contains("_HELMET") || 
            is.getType().name().contains("_CHESTPLATE") || 
            is.getType().name().contains("_BOOTS")) {
            if (Durability.getDuraPercent(is) > 30.0F) {
              newInventory.add(Durability.takeDura(is, 30.0F));
            } else {
              e.getDrops().remove(is);
              p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
            } 
          } else {
            newInventory.add(is);
          } 
        } 
        for (k = (arrayOfItemStack1 = p.getInventory().getContents()).length, b1 = 0; b1 < k; ) {
          ItemStack is = arrayOfItemStack1[b1];
          if (is != null && is.getType() != Material.AIR && 
            is.getType().name().contains("_PICKAXE"))
            if (Durability.getDuraPercent(is) > 30.0F) {
              newInventory.add(Durability.takeDura(is, 30.0F));
            } else {
              e.getDrops().remove(is);
              p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
            }  
          b1++;
        } 
      } 
      byte b;
      int j;
      ItemStack[] arrayOfItemStack;
      for (j = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < j; ) {
        ItemStack is = arrayOfItemStack[b];
        if (is != null && is.getType() != Material.AIR && 
          is.getItemMeta().hasLore() && 
          is.getItemMeta().getLore().contains(ChatColor.GRAY + "Permenant Untradeable") && 
          !newInventory.contains(is))
          newInventory.add(is); 
        b++;
      } 
      File file = new File(Main.plugin.getDataFolder() + "/respawndata", String.valueOf(p.getName()) + ".yml");
      YamlConfiguration config = new YamlConfiguration();
      for (int i = 0; i < newInventory.size(); i++)
        config.set(i, newInventory.get(i)); 
      try {
        config.save(file);
      } catch (Exception e1) {
        e1.printStackTrace();
      } 
      for (ItemStack is : newInventory) {
        if (is != null) {
          ItemMeta meta = is.getItemMeta();
          meta.setLore(Arrays.asList(new String[] { "notarealitem" }));
          is.setItemMeta(meta);
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    final Player p = e.getPlayer();
    if (this.dead.contains(p))
      this.dead.remove(p); 
    File file = new File(Main.plugin.getDataFolder() + "/respawndata", String.valueOf(p.getName()) + ".yml");
    YamlConfiguration config = new YamlConfiguration();
    try {
      config.load(file);
    } catch (Exception e1) {
      e1.printStackTrace();
    } 
    for (int i = 0; i < p.getInventory().getSize(); i++) {
      if (config.contains(i))
        e.getPlayer().getInventory().addItem(new ItemStack[] { config.getItemStack(i) }); 
    } 
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
    for (int j = 0; j < 2; j++)
      p.getInventory().setItem(p.getInventory().firstEmpty(), bread); 
    if (!p.getInventory().contains(Material.QUARTZ))
      p.getInventory().addItem(new ItemStack[] { Hearthstone.hearthstone() }); 
    if (!p.getInventory().contains(Material.WRITTEN_BOOK))
      p.getInventory().addItem(new ItemStack[] { Journal.journal() }); 
    e.getPlayer().setMaxHealth(50.0D);
    e.getPlayer().setHealth(50.0D);
    p.getInventory().setHeldItemSlot(0);
    (new BukkitRunnable() {
        public void run() {
          p.setLevel(100);
          p.setExp(1.0F);
          p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
          p.playSound(p.getLocation(), Sound.ZOMBIE_UNFECT, 1.0F, 1.5F);
        }
      }).runTaskLater(Main.plugin, 1L);
  }
}
