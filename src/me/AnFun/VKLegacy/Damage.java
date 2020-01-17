package me.AnFun.VKLegacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Damage implements Listener {
  HashMap<Player, Long> playerslow = new HashMap<>();
  
  public static HashMap<Player, Long> lasthit = new HashMap<>();
  
  public static HashMap<Player, Player> lastphit = new HashMap<>();
  
  public void onEnable() {
    Main.log.info("[Damage] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    (new BukkitRunnable() {
        public void run() {
          for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            float pcnt = (float)(p.getHealth() / p.getMaxHealth() * 100.0D);
            BarAPI.setMessage(p, 
                ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "HP " + ChatColor.LIGHT_PURPLE + 
                (int)p.getHealth() + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " / " + 
                ChatColor.LIGHT_PURPLE + (int)p.getMaxHealth(), 
                pcnt);
          } 
        }
      }).runTaskTimerAsynchronously(Main.plugin, 1L, 1L);
    (new BukkitRunnable() {
        public void run() {
          for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (Damage.this.playerslow.containsKey(p)) {
              if (System.currentTimeMillis() - ((Long)Damage.this.playerslow.get(p)).longValue() > 3000L)
                p.setWalkSpeed(0.2F); 
              continue;
            } 
            if (p.getWalkSpeed() != 0.2F)
              p.setWalkSpeed(0.2F); 
          } 
        }
      }).runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
  }
  
  public void onDisable() {
    Main.log.info("[Damage] has been disabled.");
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
  
  public static int getArmor(ItemStack is) {
    if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
      List<String> lore = is.getItemMeta().getLore();
      if (lore.size() > 0 && (
        (String)lore.get(0)).contains("ARMOR"))
        try {
          return Integer.parseInt(((String)lore.get(0)).split(" - ")[1].split("%")[0]);
        } catch (Exception e) {
          return 0;
        }  
    } 
    return 0;
  }
  
  public static int getDps(ItemStack is) {
    if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
      List<String> lore = is.getItemMeta().getLore();
      if (lore.size() > 0 && (
        (String)lore.get(0)).contains("DPS"))
        try {
          return Integer.parseInt(((String)lore.get(0)).split(" - ")[1].split("%")[0]);
        } catch (Exception e) {
          return 0;
        }  
    } 
    return 0;
  }
  
  public static int getEnergy(ItemStack is) {
    if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
      List<String> lore = is.getItemMeta().getLore();
      if (lore.size() > 2 && (
        (String)lore.get(2)).contains("ENERGY REGEN"))
        try {
          return Integer.parseInt(((String)lore.get(2)).split(": +")[1].split("%")[0]);
        } catch (Exception e) {
          return 0;
        }  
    } 
    return 0;
  }
  
  public static int getHps(ItemStack is) {
    if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
      List<String> lore = is.getItemMeta().getLore();
      if (lore.size() > 2 && (
        (String)lore.get(2)).contains("HP REGEN"))
        try {
          return Integer.parseInt(((String)lore.get(2)).split(": +")[1].split(" HP/s")[0]);
        } catch (Exception e) {
          return 0;
        }  
    } 
    return 0;
  }
  
  public static int getPercent(ItemStack is, String type) {
    if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
      List<String> lore = is.getItemMeta().getLore();
      for (String s : lore) {
        if (s.contains(type))
          try {
            return Integer.parseInt(s.split(": ")[1].split("%")[0]);
          } catch (Exception e) {
            return 0;
          }  
      } 
    } 
    return 0;
  }
  
  public static int getElem(ItemStack is, String type) {
    if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
      List<String> lore = is.getItemMeta().getLore();
      for (String s : lore) {
        if (s.contains(type))
          try {
            return Integer.parseInt(s.split(": +")[1]);
          } catch (Exception e) {
            return 0;
          }  
      } 
    } 
    return 0;
  }
  
  public static List<Integer> getDamageRange(ItemStack is) {
    List<Integer> dmg = new ArrayList<>();
    dmg.add(Integer.valueOf(1));
    dmg.add(Integer.valueOf(1));
    if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
      List<String> lore = is.getItemMeta().getLore();
      if (lore.size() > 0 && (
        (String)lore.get(0)).contains("DMG"))
        try {
          int min = 1;
          int max = 1;
          min = Integer.parseInt(((String)lore.get(0)).split("DMG: ")[1].split(" - ")[0]);
          max = Integer.parseInt(((String)lore.get(0)).split(" - ")[1]);
          dmg.set(0, Integer.valueOf(min));
          dmg.set(1, Integer.valueOf(max));
        } catch (Exception e) {
          dmg.set(0, Integer.valueOf(1));
          dmg.set(1, Integer.valueOf(1));
        }  
    } 
    return dmg;
  }
  
  public static int getCrit(Player p) {
    int crit = 0;
    ItemStack wep = p.getItemInHand();
    if (Staffs.staff.containsKey(p))
      wep = Staffs.staff.get(p); 
    if (wep != null && wep.getType() != Material.AIR && wep.getItemMeta().hasLore()) {
      List<String> lore = wep.getItemMeta().getLore();
      for (String line : lore) {
        if (line.contains("CRITICAL HIT"))
          crit = getPercent(wep, "CRITICAL HIT"); 
      } 
      if (wep.getType().name().contains("_AXE"))
        crit += 3; 
      int intel = 0;
      byte b;
      int i;
      ItemStack[] arrayOfItemStack;
      for (i = (arrayOfItemStack = p.getInventory().getArmorContents()).length, b = 0; b < i; ) {
        ItemStack is = arrayOfItemStack[b];
        if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
          int addint = getElem(is, "INT");
          intel += addint;
        } 
        b++;
      } 
      if (intel > 0)
        crit = (int)(crit + Math.round(intel * 0.014D)); 
    } 
    return crit;
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onNpcDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player) {
      Player p = (Player)e.getEntity();
      if (p.hasMetadata("NPC") || p.getPlayerListName().equals("")) {
        e.setCancelled(true);
        e.setDamage(0.0D);
      } 
      if (p.isOp() || p.getGameMode() == GameMode.CREATIVE || p.isFlying()) {
        e.setCancelled(true);
        e.setDamage(0.0D);
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onNoOpDamage(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
      Player p = (Player)e.getDamager();
      if (p.isOp() || p.getGameMode() == GameMode.CREATIVE || p.isFlying()) {
        e.setCancelled(true);
        e.setDamage(0.0D);
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onBlodge(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Player) {
      if (e.getDamage() <= 0.0D)
        return; 
      Player p = (Player)e.getEntity();
      PlayerInventory i = p.getInventory();
      int block = 0;
      int dodge = 0;
      if (p.getHealth() > 0.0D) {
        byte b;
        int j;
        ItemStack[] arrayOfItemStack1;
        for (j = (arrayOfItemStack1 = i.getArmorContents()).length, b = 0; b < j; ) {
          ItemStack is = arrayOfItemStack1[b];
          if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            int addedblock = getPercent(is, "BLOCK");
            block += addedblock;
            int addeddodge = getPercent(is, "DODGE");
            dodge += addeddodge;
          } 
          b++;
        } 
        int str = 0;
        ItemStack[] arrayOfItemStack2;
        for (int k = (arrayOfItemStack2 = p.getInventory().getArmorContents()).length; j < k; ) {
          ItemStack is = arrayOfItemStack2[j];
          if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            int addstr = getElem(is, "STR");
            str += addstr;
          } 
          j++;
        } 
        if (str > 0)
          block = (int)(block + Math.round(str * 0.015D)); 
        Random random = new Random();
        int dodger = random.nextInt(100) + 1;
        int blockr = random.nextInt(100) + 1;
        if (e.getDamager() instanceof Player) {
          Player d = (Player)e.getDamager();
          ItemStack wep = d.getItemInHand();
          if (Staffs.staff.containsKey(d))
            wep = Staffs.staff.get(d); 
          int accuracy = getPercent(wep, "ACCURACY");
          if (accuracy > 0) {
            int b4block = block;
            int b4dodge = dodge;
            if (accuracy > 0) {
              block -= accuracy;
              accuracy -= b4block;
            } 
            if (accuracy > 0) {
              dodge -= accuracy;
              accuracy -= b4dodge;
            } 
          } 
          if (blockr <= block) {
            e.setDamage(0.0D);
            e.setCancelled(true);
            p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1.0F, 1.0F);
            d.sendMessage("          " + ChatColor.RED + ChatColor.BOLD + "*OPPONENT BLOCKED* (" + 
                p.getName() + ")");
            p.sendMessage(
                "          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + d.getName() + ")");
          } else if (dodger <= dodge) {
            e.setDamage(0.0D);
            e.setCancelled(true);
            p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 1.0F, 1.0F);
            d.sendMessage("          " + ChatColor.RED + ChatColor.BOLD + "*OPPONENT DODGED* (" + 
                p.getName() + ")");
            p.sendMessage(
                "          " + ChatColor.GREEN + ChatColor.BOLD + "*DODGE* (" + d.getName() + ")");
          } else if (blockr <= 80 && p.isBlocking()) {
            e.setDamage(((int)e.getDamage() / 2));
            p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1.0F, 1.0F);
            d.sendMessage("          " + ChatColor.RED + ChatColor.BOLD + "*OPPONENT BLOCKED* (" + 
                p.getName() + ")");
            p.sendMessage(
                "          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + d.getName() + ")");
          } 
        } else if (e.getDamager() instanceof LivingEntity) {
          LivingEntity li = (LivingEntity)e.getDamager();
          String mname = "";
          if (li.hasMetadata("name"))
            mname = ((MetadataValue)li.getMetadata("name").get(0)).asString(); 
          if (blockr <= block) {
            e.setDamage(0.0D);
            e.setCancelled(true);
            p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1.0F, 1.0F);
            p.sendMessage("          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + mname + 
                ChatColor.DARK_GREEN + ")");
          } else if (dodger <= dodge) {
            e.setDamage(0.0D);
            e.setCancelled(true);
            p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 1.0F, 1.0F);
            p.sendMessage("          " + ChatColor.GREEN + ChatColor.BOLD + "*DODGE* (" + mname + 
                ChatColor.GREEN + ")");
          } else if (blockr <= 80 && p.isBlocking()) {
            e.setDamage(((int)e.getDamage() / 2));
            p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1.0F, 1.0F);
            p.sendMessage("          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + mname + 
                ChatColor.DARK_GREEN + ")");
          } 
        } 
      } 
    } 
    if (e.getDamage() <= 0.0D)
      return; 
    if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
      Player p = (Player)e.getDamager();
      LivingEntity li = (LivingEntity)e.getEntity();
      ItemStack wep = p.getItemInHand();
      if (Staffs.staff.containsKey(p))
        wep = Staffs.staff.get(p); 
      if (wep != null && wep.getType() != Material.AIR && wep.getItemMeta().hasLore()) {
        int min = ((Integer)getDamageRange(wep).get(0)).intValue();
        int max = ((Integer)getDamageRange(wep).get(1)).intValue();
        Random random = new Random();
        int dmg = random.nextInt(max - min + 1) + min;
        int tier = Merchant.getTier(wep);
        List<String> lore = wep.getItemMeta().getLore();
        for (String line : lore) {
          if (line.contains("ICE DMG")) {
            li.getWorld().playEffect(li.getLocation().add(0.0D, 1.3D, 0.0D), Effect.POTION_BREAK, 8194);
            int eldmg = getElem(wep, "ICE DMG");
            dmg += eldmg;
            if (tier == 1)
              li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0)); 
            if (tier == 2)
              li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 0)); 
            if (tier == 3)
              li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1)); 
            if (tier == 4)
              li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1)); 
            if (tier == 5)
              li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1)); 
          } 
          if (line.contains("POISON DMG")) {
            li.getWorld().playEffect(li.getLocation().add(0.0D, 1.3D, 0.0D), Effect.POTION_BREAK, 8196);
            int eldmg = getElem(wep, "POISON DMG");
            dmg += eldmg;
            if (tier == 1)
              li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 15, 0)); 
            if (tier == 2)
              li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 25, 0)); 
            if (tier == 3)
              li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30, 1)); 
            if (tier == 4)
              li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 35, 1)); 
            if (tier == 5)
              li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1)); 
          } 
          if (line.contains("FIRE DMG")) {
            int eldmg = getElem(wep, "FIRE DMG");
            dmg += eldmg;
            if (tier == 1)
              li.setFireTicks(15); 
            if (tier == 2)
              li.setFireTicks(25); 
            if (tier == 3)
              li.setFireTicks(30); 
            if (tier == 4)
              li.setFireTicks(35); 
            if (tier == 5)
              li.setFireTicks(40); 
          } 
          if (line.contains("PURE DMG")) {
            int eldmg = getElem(wep, "PURE DMG");
            dmg += eldmg;
          } 
        } 
        int crit = getCrit(p);
        int drop = random.nextInt(100) + 1;
        if (drop <= crit) {
          dmg *= 2;
          p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.5F, 0.5F);
          ParticleEffect.CRIT_MAGIC.display(0.0F, 0.0F, 0.0F, 1.0F, 50, li.getLocation(), 20.0D);
        } 
        PlayerInventory i = p.getInventory();
        double dps = 0.0D;
        double vit = 0.0D;
        double str = 0.0D;
        byte b;
        int j;
        ItemStack[] arrayOfItemStack;
        for (j = (arrayOfItemStack = i.getArmorContents()).length, b = 0; b < j; ) {
          ItemStack is = arrayOfItemStack[b];
          if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            int adddps = getDps(is);
            dps += adddps;
            int addvit = getElem(is, "VIT");
            vit += addvit;
            int addstr = getElem(is, "STR");
            str += addstr;
          } 
          b++;
        } 
        if (vit > 0.0D && wep.getType().name().contains("_SWORD")) {
          double divide = vit / 5000.0D;
          double pre = dmg * divide;
          int cleaned = (int)(dmg + pre);
          dmg = cleaned;
        } 
        if (str > 0.0D && wep.getType().name().contains("_AXE")) {
          double divide = str / 3800.0D;
          double pre = dmg * divide;
          int cleaned = (int)(dmg + pre);
          dmg = cleaned;
        } 
        if (dps > 0.0D) {
          double divide = dps / 100.0D;
          double pre = dmg * divide;
          int cleaned = (int)(dmg + pre);
          dmg = cleaned;
        } 
        for (String line : lore) {
          if (line.contains("LIFE STEAL")) {
            li.getWorld().playEffect(li.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
            double base = getPercent(wep, "LIFE STEAL");
            double pcnt = base / 100.0D;
            int life = 1;
            if ((int)(pcnt * dmg) > 0)
              life = (int)(pcnt * dmg); 
            if (p.getHealth() < p.getMaxHealth() - life) {
              p.setHealth(p.getHealth() + life);
              ArrayList<String> toggles = Toggles.getToggles(p.getName());
              if (toggles.contains("debug"))
                p.sendMessage(ChatColor.GREEN + ChatColor.BOLD + "            +" + ChatColor.GREEN + 
                    life + ChatColor.GREEN + ChatColor.BOLD + " HP " + ChatColor.GRAY + "[" + 
                    (int)p.getHealth() + "/" + (int)p.getMaxHealth() + "HP]"); 
              continue;
            } 
            if (p.getHealth() >= p.getMaxHealth() - life) {
              p.setHealth(p.getMaxHealth());
              ArrayList<String> toggles = Toggles.getToggles(p.getName());
              if (toggles.contains("debug"))
                p.sendMessage(ChatColor.GREEN + ChatColor.BOLD + "            +" + ChatColor.GREEN + 
                    life + ChatColor.GREEN + ChatColor.BOLD + " HP " + ChatColor.GRAY + "[" + 
                    (int)p.getMaxHealth() + "/" + (int)p.getMaxHealth() + "HP]"); 
            } 
          } 
        } 
        e.setDamage(dmg);
        return;
      } 
      e.setDamage(1.0D);
    } 
  }
  
  @EventHandler(priority = EventPriority.NORMAL)
  public void onArmor(EntityDamageEvent e) {
    if (e.getDamage() <= 0.0D)
      return; 
    if (e.getEntity() instanceof Player) {
      Player p = (Player)e.getEntity();
      PlayerInventory i = p.getInventory();
      double dmg = e.getDamage();
      double arm = 0.0D;
      byte b;
      int j;
      ItemStack[] arrayOfItemStack;
      for (j = (arrayOfItemStack = i.getArmorContents()).length, b = 0; b < j; ) {
        ItemStack is = arrayOfItemStack[b];
        if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
          int addarm = getArmor(is);
          arm += addarm;
        } 
        b++;
      } 
      if (arm > 0.0D) {
        double divide = arm / 100.0D;
        double pre = dmg * divide;
        int cleaned = (int)(dmg - pre);
        if (cleaned <= 1)
          cleaned = 1; 
        dmg = cleaned;
        int health = 0;
        if (p.getHealth() - cleaned > 0.0D)
          health = (int)(p.getHealth() - cleaned); 
        ArrayList<String> toggles = Toggles.getToggles(p.getName());
        if (toggles.contains("debug")) {
          if (health < 0)
            health = 0; 
          p.sendMessage(ChatColor.RED + "            -" + cleaned + ChatColor.RED + ChatColor.BOLD + "HP " + 
              ChatColor.GRAY + "[-" + (int)arm + "%A -> -" + (int)pre + ChatColor.BOLD + "DMG" + 
              ChatColor.GRAY + "] " + ChatColor.GREEN + "[" + health + ChatColor.BOLD + "HP" + 
              ChatColor.GREEN + "]");
        } 
        e.setDamage(cleaned);
      } else {
        ArrayList<String> toggles = Toggles.getToggles(p.getName());
        if (toggles.contains("debug")) {
          int health = (int)(p.getHealth() - dmg);
          if (health < 0)
            health = 0; 
          p.sendMessage(ChatColor.RED + "            -" + (int)dmg + ChatColor.RED + ChatColor.BOLD + "HP " + 
              ChatColor.GRAY + "[-0%A -> -0" + ChatColor.BOLD + "DMG" + ChatColor.GRAY + "] " + 
              ChatColor.GREEN + "[" + health + ChatColor.BOLD + "HP" + ChatColor.GREEN + "]");
        } 
        e.setDamage(dmg);
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onDebug(EntityDamageByEntityEvent e) {
    if (e.getDamage() <= 0.0D)
      return; 
    if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
      Player p = (Player)e.getEntity();
      Player d = (Player)e.getDamager();
      int dmg = (int)e.getDamage();
      ArrayList<String> toggles = Toggles.getToggles(d.getName());
      if (toggles.contains("debug"))
        d.sendMessage(ChatColor.RED + "            " + dmg + ChatColor.RED + ChatColor.BOLD + " DMG " + 
            ChatColor.RED + "-> " + p.getName()); 
      lastphit.put(p, d);
      lasthit.put(p, Long.valueOf(System.currentTimeMillis()));
    } else if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {
      LivingEntity p = (LivingEntity)e.getEntity();
      Player d = (Player)e.getDamager();
      int dmg = (int)e.getDamage();
      int health = 0;
      if (p.getHealth() - dmg > 0.0D)
        health = (int)(p.getHealth() - dmg); 
      String name = "";
      if (p.hasMetadata("name"))
        name = ((MetadataValue)p.getMetadata("name").get(0)).asString(); 
      ArrayList<String> toggles = Toggles.getToggles(d.getName());
      if (toggles.contains("debug"))
        d.sendMessage(ChatColor.RED + "            " + dmg + ChatColor.RED + ChatColor.BOLD + " DMG " + 
            ChatColor.RED + "-> " + ChatColor.RESET + name + " [" + health + "HP]"); 
    } 
  }
  
  HashMap<UUID, Long> kb = new HashMap<>();
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onKnockback(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof LivingEntity) {
      LivingEntity p = (LivingEntity)e.getEntity();
      LivingEntity d = (LivingEntity)e.getDamager();
      if (e.getDamage() <= 0.0D)
        return; 
      if (p instanceof Player) {
        Vector v = p.getLocation().toVector().subtract(d.getLocation().toVector());
        if (v.getX() != 0.0D || v.getY() != 0.0D || v.getZ() != 0.0D)
          v.normalize(); 
        p.setVelocity(v.multiply(0.15F));
      } else if (!this.kb.containsKey(p.getUniqueId()) || (this.kb.containsKey(p.getUniqueId()) && 
        System.currentTimeMillis() - ((Long)this.kb.get(p.getUniqueId())).longValue() > 500L)) {
        this.kb.put(p.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
        Vector v = p.getLocation().toVector().subtract(d.getLocation().toVector());
        if (v.getX() != 0.0D || v.getY() != 0.0D || v.getZ() != 0.0D)
          v.normalize(); 
        if (d instanceof Player) {
          Player dam = (Player)d;
          if (dam.getItemInHand() != null && dam.getItemInHand().getType().name().contains("_SPADE")) {
            p.setVelocity(v.multiply(1.25F).setY(0.4D));
          } else {
            p.setVelocity(v.multiply(0.5F).setY(0.35D));
          } 
        } else {
          p.setVelocity(v.multiply(0.5F).setY(0.35D));
        } 
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDeath(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity) {
      LivingEntity s = (LivingEntity)e.getEntity();
      if (e.getDamage() >= s.getHealth() && 
        this.kb.containsKey(s.getUniqueId()))
        this.kb.remove(s.getUniqueId()); 
    } 
  }
  
  ArrayList<String> p_arm = new ArrayList<>();
  
  @EventHandler(priority = EventPriority.HIGH)
  public void onPolearmAOE(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {
      LivingEntity p = (LivingEntity)e.getEntity();
      Player d = (Player)e.getDamager();
      if (e.getDamage() <= 0.0D)
        return; 
      if (d.getItemInHand() != null && d.getItemInHand().getType().name().contains("_SPADE") && 
        !this.p_arm.contains(d.getName()))
        for (Entity near : p.getNearbyEntities(2.5D, 3.0D, 2.5D)) {
          if (near instanceof LivingEntity && 
            near != p && near != d && 
            near != null) {
            LivingEntity n = (LivingEntity)near;
            if (Energy.nodamage.containsKey(d.getName()))
              Energy.nodamage.remove(d.getName()); 
            this.p_arm.add(d.getName());
            n.damage(1.0D, (Entity)d);
            this.p_arm.remove(d.getName());
          } 
        }  
    } 
  }
  
  @EventHandler
  public void onDamageSound(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
      if (e.getDamage() <= 0.0D)
        return; 
      Player p = (Player)e.getDamager();
      p.playSound(p.getLocation(), Sound.HURT_FLESH, 1.0F, 1.0F);
      if (e.getEntity() instanceof Player)
        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.WOOD_CLICK, 1.0F, 1.6F); 
    } 
    if (e.getEntity() instanceof Player && !(e.getDamager() instanceof Player) && 
      e.getDamager() instanceof LivingEntity) {
      Player p = (Player)e.getEntity();
      p.setWalkSpeed(0.165F);
      this.playerslow.put(p, Long.valueOf(System.currentTimeMillis()));
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBypassArmor(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity) {
      LivingEntity li = (LivingEntity)e.getEntity();
      if (e.getDamage() <= 0.0D)
        return; 
      int dmg = (int)e.getDamage();
      e.setDamage(0.0D);
      e.setCancelled(true);
      li.playEffect(EntityEffect.HURT);
      li.setLastDamageCause(e);
      if (li.getHealth() - dmg <= 0.0D) {
        li.setHealth(0.0D);
      } else {
        li.setHealth(li.getHealth() - dmg);
      } 
    } 
  }
}
