package me.AnFun.VKLegacy;

import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

public class Mobdrops implements Listener {
  public void onEnable() {
    Main.log.info("[MobDrops] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
  }
  
  public void onDisable() {
    Main.log.info("[MobDrops] has been disabled.");
  }
  
  @EventHandler
  public void onMobDeath(EntityDeathEvent e) {
    if (!(e.getEntity() instanceof org.bukkit.entity.Player))
      e.getDrops().clear(); 
    e.setDroppedExp(0);
  }
  
  @EventHandler
  public void onMobDeath(EntityDamageEvent e) {
    if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof org.bukkit.entity.Player)) {
      LivingEntity s = (LivingEntity)e.getEntity();
      if (e.getDamage() >= s.getHealth() && 
        s.getEquipment().getItemInHand() != null && 
        s.getEquipment().getItemInHand().getType() != Material.AIR) {
        Random random = new Random();
        int gems = random.nextInt(2) + 1;
        int gemamt = 0;
        int scrolldrop = random.nextInt(100);
        int sackdrop = random.nextInt(100);
        boolean dodrop = false;
        boolean elite = false;
        int rd = random.nextInt(100);
        if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants())
          elite = true; 
        if (s.getEquipment().getItemInHand().getType().name().contains("WOOD_")) {
          gemamt = random.nextInt(16) + 1;
          if (elite && !isCustomNamedElite(s)) {
            if (rd < 60)
              dodrop = true; 
          } else if (elite && isCustomNamedElite(s)) {
            if (rd < 15)
              dodrop = true; 
          } else if (rd < 30) {
            dodrop = true;
          } 
          if (scrolldrop <= 5) {
            int scrolltype = random.nextInt(2);
            if (scrolltype == 0)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.cyrennica_book()); 
            if (scrolltype == 1)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.harrison_book()); 
          } 
          if (sackdrop <= 5)
            s.getWorld().dropItemNaturally(s.getLocation(), GemPouches.gemPouch(1)); 
        } 
        if (s.getEquipment().getItemInHand().getType().name().contains("STONE_")) {
          gemamt = random.nextInt(17) + 16;
          if (elite && !isCustomNamedElite(s)) {
            if (rd < 48)
              dodrop = true; 
          } else if (elite && isCustomNamedElite(s)) {
            if (rd < 12)
              dodrop = true; 
          } else if (rd < 24) {
            dodrop = true;
          } 
          if (scrolldrop <= 5) {
            int scrolltype = random.nextInt(5);
            if (scrolltype == 0)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.cyrennica_book()); 
            if (scrolltype == 1)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.harrison_book()); 
            if (scrolltype == 2)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.dark_oak_book()); 
            if (scrolltype == 3)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.trollsbane_book()); 
            if (scrolltype == 4)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.tripoli_book()); 
          } 
          if (sackdrop <= 5)
            s.getWorld().dropItemNaturally(s.getLocation(), GemPouches.gemPouch(2)); 
        } 
        if (s.getEquipment().getItemInHand().getType().name().contains("IRON_")) {
          gemamt = random.nextInt(33) + 32;
          if (elite && !isCustomNamedElite(s)) {
            if (rd < 36)
              dodrop = true; 
          } else if (elite && isCustomNamedElite(s)) {
            if (rd < 9)
              dodrop = true; 
          } else if (rd < 18) {
            dodrop = true;
          } 
          if (scrolldrop <= 5) {
            int scrolltype = random.nextInt(5);
            if (scrolltype == 0)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.cyrennica_book()); 
            if (scrolltype == 1)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.dark_oak_book()); 
            if (scrolltype == 2)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.trollsbane_book()); 
            if (scrolltype == 3)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.gloomy_book()); 
            if (scrolltype == 4)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.crestguard_book()); 
          } 
          if (sackdrop <= 5)
            s.getWorld().dropItemNaturally(s.getLocation(), GemPouches.gemPouch(3)); 
        } 
        if (s.getEquipment().getItemInHand().getType().name().contains("DIAMOND_")) {
          gemamt = random.nextInt(129) + 128;
          if (elite && !isCustomNamedElite(s)) {
            if (rd < 24)
              dodrop = true; 
          } else if (elite && isCustomNamedElite(s)) {
            if (rd < 6)
              dodrop = true; 
          } else if (rd < 12) {
            dodrop = true;
          } 
          if (scrolldrop <= 5) {
            int scrolltype = random.nextInt(4);
            if (scrolltype == 0)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.deadpeaks_book()); 
            if (scrolltype == 1)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.gloomy_book()); 
            if (scrolltype == 2)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.crestwatch_book()); 
            if (scrolltype == 3)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.crestguard_book()); 
          } 
          if (sackdrop <= 5)
            s.getWorld().dropItemNaturally(s.getLocation(), GemPouches.gemPouch(4)); 
        } 
        if (s.getEquipment().getItemInHand().getType().name().contains("GOLD_")) {
          gemamt = random.nextInt(257) + 256;
          if (elite && !isCustomNamedElite(s)) {
            if (rd < 12)
              dodrop = true; 
          } else if (elite && isCustomNamedElite(s)) {
            if (rd < 3)
              dodrop = true; 
          } else if (rd < 6) {
            dodrop = true;
          } 
          if (scrolldrop <= 5) {
            int scrolltype = random.nextInt(4);
            if (scrolltype == 0)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.deadpeaks_book()); 
            if (scrolltype == 1)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.gloomy_book()); 
            if (scrolltype == 2)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.crestwatch_book()); 
            if (scrolltype == 3)
              s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.crestguard_book()); 
          } 
          if (sackdrop <= 5)
            s.getWorld().dropItemNaturally(s.getLocation(), GemPouches.gemPouch(4)); 
        } 
        if (elite)
          gemamt *= 3; 
        if (gems == 1) {
          while (gemamt > 0) {
            s.getWorld().dropItemNaturally(s.getLocation(), Money.makeGems(64));
            gemamt -= 64;
          } 
          s.getWorld().dropItemNaturally(s.getLocation(), Money.makeGems(gemamt));
        } 
        if (dodrop)
          if (!isCustomNamedElite(s)) {
            ArrayList<ItemStack> drops = new ArrayList<>();
            byte b;
            int i;
            ItemStack[] arrayOfItemStack;
            for (i = (arrayOfItemStack = s.getEquipment().getArmorContents()).length, b = 0; b < i; ) {
              ItemStack itemStack = arrayOfItemStack[b];
              if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.hasItemMeta() && 
                itemStack.getItemMeta().hasLore()) {
                drops.add(itemStack);
                drops.add(s.getEquipment().getItemInHand());
              } 
              b++;
            } 
            int piece = random.nextInt(drops.size());
            ItemStack is = drops.get(piece);
            if (is.getItemMeta().hasEnchants() && 
              is.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_MOBS))
              is.removeEnchantment(Enchantment.LOOT_BONUS_MOBS); 
            short dura = (short)random.nextInt(is.getType().getMaxDurability());
            if (dura == 0)
              dura = 1; 
            if (dura == is.getType().getMaxDurability())
              dura = (short)(is.getType().getMaxDurability() - 1); 
            is.setDurability(dura);
            s.getWorld().dropItemNaturally(s.getLocation(), is);
          } else if (s.hasMetadata("type")) {
            String type = ((MetadataValue)s.getMetadata("type").get(0)).asString();
            if (type.equalsIgnoreCase("mitsuki") || type.equalsIgnoreCase("copjak") || 
              type.equalsIgnoreCase("kingofgreed") || type.equalsIgnoreCase("skeletonking") || 
              type.equalsIgnoreCase("impa") || type.equalsIgnoreCase("bloodbutcher") || 
              type.equalsIgnoreCase("blayshan") || type.equalsIgnoreCase("kilatan")) {
              ItemStack is = EliteDrops.createCustomEliteDrop(type);
              s.getWorld().dropItemNaturally(s.getLocation(), is);
            } 
          }  
      } 
    } 
  }
  
  boolean isCustomNamedElite(LivingEntity l) {
    if (l.hasMetadata("type")) {
      String type = ((MetadataValue)l.getMetadata("type").get(0)).asString();
      if (type.equalsIgnoreCase("mitsuki") || type.equalsIgnoreCase("copjak") || type.equalsIgnoreCase("impa") || 
        type.equalsIgnoreCase("skeletonking") || type.equalsIgnoreCase("blayshan") || 
        type.equalsIgnoreCase("kilatan"))
        return true; 
    } 
    return false;
  }
}
