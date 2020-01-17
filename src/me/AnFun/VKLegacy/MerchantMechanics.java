package me.AnFun.VKLegacy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MerchantMechanics implements Listener {
  static Logger log = Logger.getLogger("Minecraft");
  
  public static List<String> in_npc_shop = new ArrayList<>();
  
  public static ItemStack divider = new ItemStack(Material.THIN_GLASS, 1);
  
  public static ItemStack T1_scrap = Durability.scrap(1);
  
  public static ItemStack T2_scrap = Durability.scrap(2);
  
  public static ItemStack T3_scrap = Durability.scrap(3);
  
  public static ItemStack T4_scrap = Durability.scrap(4);
  
  public static ItemStack T5_scrap = Durability.scrap(5);
  
  public static ItemStack orb_of_alteration = Items.orb(false);
  
  public void onEnable() {
    Main.log.info("[MerchantMechanics] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    ItemMeta im = divider.getItemMeta();
    im.setDisplayName(" ");
    divider.setItemMeta(im);
  }
  
  public void onDisable() {
    Main.log.info("[MerchantMechanics] has been disabled.");
  }
  
  private static String generateTitle(String lPName, String rPName) {
    String title = "  " + lPName;
    while (title.length() + rPName.length() < 30)
      title = String.valueOf(title) + " "; 
    return title = String.valueOf(title) + rPName;
  }
  
  public static List<ItemStack> generateMerchantOffer(List<ItemStack> player_offer) {
    List<ItemStack> merchant_offer = new ArrayList<>();
    List<ItemStack> to_remove = new ArrayList<>();
    int t1_scraps = 0, t2_scraps = 0, t3_scraps = 0, t4_scraps = 0, t5_scraps = 0;
    int t1_ore = 0, t2_ore = 0, t3_ore = 0, t4_ore = 0, t5_ore = 0;
    for (ItemStack is : player_offer) {
      if (is == null || is.getType() == Material.AIR)
        continue; 
      if (is.getType() == Material.COAL_ORE || is.getType() == Material.EMERALD_ORE || 
        is.getType() == Material.IRON_ORE || is.getType() == Material.DIAMOND_ORE || 
        is.getType() == Material.GOLD_ORE) {
        int tier = ProfessionMechanics.getOreTier(is.getType());
        if (tier == 1)
          t1_ore += is.getAmount(); 
        if (tier == 2)
          t2_ore += is.getAmount(); 
        if (tier == 3)
          t3_ore += is.getAmount(); 
        if (tier == 4)
          t4_ore += is.getAmount(); 
        if (tier == 5)
          t5_ore += is.getAmount(); 
        to_remove.add(is);
      } 
    } 
    for (ItemStack is : player_offer) {
      if (is == null || is.getType() == Material.AIR)
        continue; 
      if (Durability.isScrap(is)) {
        int tier = Merchant.getTier(is);
        if (tier == 1)
          t1_scraps += is.getAmount(); 
        if (tier == 2)
          t2_scraps += is.getAmount(); 
        if (tier == 3)
          t3_scraps += is.getAmount(); 
        if (tier == 4)
          t4_scraps += is.getAmount(); 
        if (tier == 5)
          t5_scraps += is.getAmount(); 
        to_remove.add(is);
      } 
    } 
    for (ItemStack is : to_remove)
      player_offer.remove(is); 
    for (ItemStack is : player_offer) {
      if (is == null || is.getType() == Material.AIR)
        continue; 
      int tier = Repairing.getTier(is);
      if (is.getType() != Material.MAGMA_CREAM && is.getType() != Material.PAPER && 
        !Durability.isScrap(is)) {
        int payout = 0;
        Material m = is.getType();
        String m_name = m.name().toLowerCase();
        if (m_name.contains("axe") || m_name.contains("sword") || m_name.contains("leg") || 
          m_name.contains("bow") || m_name.contains("spade") || m_name.contains("hoe"))
          payout = 2; 
        if (m_name.contains("helmet") || m_name.contains("boot"))
          payout = 1; 
        if (m_name.contains("chest"))
          payout = 3; 
        if (tier == 1) {
          CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T2_scrap);
          craftItemStack.setAmount(payout);
          merchant_offer.add(craftItemStack);
        } 
        if (tier == 2) {
          CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T3_scrap);
          craftItemStack.setAmount(payout);
          merchant_offer.add(craftItemStack);
        } 
        if (tier == 3) {
          CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T4_scrap);
          craftItemStack.setAmount(payout);
          merchant_offer.add(craftItemStack);
        } 
        if (tier == 4) {
          CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T5_scrap);
          craftItemStack.setAmount(payout);
          merchant_offer.add(craftItemStack);
        } 
        if (tier == 5) {
          CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T5_scrap);
          craftItemStack.setAmount(payout * 2);
          merchant_offer.add(craftItemStack);
        } 
      } 
      if (is.getType() != null && is.getType() == Material.MAGMA_CREAM && is.getItemMeta().hasDisplayName() && 
        is.getItemMeta().getDisplayName().toLowerCase().contains("orb of alteration")) {
        int orb_count = is.getAmount();
        int payout = 20 * orb_count;
        while (payout > 64) {
          CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T5_scrap);
          craftItemStack1.setAmount(64);
          merchant_offer.add(craftItemStack1);
          payout -= 64;
        } 
        CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T5_scrap);
        craftItemStack.setAmount(payout);
        merchant_offer.add(craftItemStack);
      } 
    } 
    if (t1_ore > 0) {
      int payout = t1_ore * 2;
      while (payout > 64) {
        payout -= 64;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T1_scrap);
        craftItemStack1.setAmount(64);
        merchant_offer.add(craftItemStack1);
      } 
      CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T1_scrap);
      craftItemStack.setAmount(payout);
      merchant_offer.add(craftItemStack);
    } 
    if (t2_ore > 0) {
      int payout = t2_ore * 2;
      while (payout > 64) {
        payout -= 64;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T2_scrap);
        craftItemStack1.setAmount(64);
        merchant_offer.add(craftItemStack1);
      } 
      CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T2_scrap);
      craftItemStack.setAmount(payout);
      merchant_offer.add(craftItemStack);
    } 
    if (t3_ore > 0) {
      int payout = t3_ore * 2;
      while (payout > 64) {
        payout -= 64;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T3_scrap);
        craftItemStack1.setAmount(64);
        merchant_offer.add(craftItemStack1);
      } 
      CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T3_scrap);
      craftItemStack.setAmount(payout);
      merchant_offer.add(craftItemStack);
    } 
    if (t4_ore > 0) {
      int payout = t4_ore * 2;
      while (payout > 64) {
        payout -= 64;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T4_scrap);
        craftItemStack1.setAmount(64);
        merchant_offer.add(craftItemStack1);
      } 
      CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T4_scrap);
      craftItemStack.setAmount(payout);
      merchant_offer.add(craftItemStack);
    } 
    if (t5_ore > 0) {
      int payout = t5_ore * 2;
      while (payout > 64) {
        payout -= 64;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T5_scrap);
        craftItemStack1.setAmount(64);
        merchant_offer.add(craftItemStack1);
      } 
      CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T5_scrap);
      craftItemStack.setAmount(payout);
      merchant_offer.add(craftItemStack);
    } 
    if (t1_scraps > 0) {
      while (t1_scraps >= 80) {
        t1_scraps -= 80;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(Items.enchant(1, 0, false));
        merchant_offer.add(craftItemStack1);
      } 
      while (t1_scraps >= 70) {
        t1_scraps -= 70;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(Items.enchant(1, 1, false));
        merchant_offer.add(craftItemStack1);
      } 
      int payout = t1_scraps / 2;
      while (payout > 64) {
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T2_scrap);
        craftItemStack1.setAmount(64);
        merchant_offer.add(craftItemStack1);
        payout -= 64;
      } 
      CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T2_scrap);
      craftItemStack.setAmount(payout);
      merchant_offer.add(craftItemStack);
    } 
    if (t2_scraps > 0) {
      while (t2_scraps >= 140) {
        t2_scraps -= 140;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(Items.enchant(2, 0, false));
        merchant_offer.add(craftItemStack1);
      } 
      while (t2_scraps >= 125) {
        t2_scraps -= 125;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(Items.enchant(2, 1, false));
        merchant_offer.add(craftItemStack1);
      } 
      int payout = 2 * t2_scraps;
      while (payout > 64) {
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T1_scrap);
        craftItemStack1.setAmount(64);
        merchant_offer.add(craftItemStack1);
        payout -= 64;
      } 
      CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T1_scrap);
      craftItemStack.setAmount(payout);
      merchant_offer.add(craftItemStack);
    } 
    if (t3_scraps > 0) {
      while (t3_scraps >= 120) {
        t3_scraps -= 120;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(orb_of_alteration);
        merchant_offer.add(craftItemStack1);
      } 
      while (t3_scraps >= 110) {
        t3_scraps -= 110;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(Items.enchant(3, 0, false));
        merchant_offer.add(craftItemStack1);
      } 
      while (t3_scraps >= 100) {
        t3_scraps -= 100;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(Items.enchant(3, 1, false));
        merchant_offer.add(craftItemStack1);
      } 
      int payout = 2 * t3_scraps;
      while (payout > 64) {
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T2_scrap);
        craftItemStack1.setAmount(64);
        merchant_offer.add(craftItemStack1);
        payout -= 64;
      } 
      CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T2_scrap);
      craftItemStack.setAmount(payout);
      merchant_offer.add(craftItemStack);
    } 
    if (t4_scraps > 0) {
      while (t4_scraps >= 88) {
        t4_scraps -= 88;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(Items.enchant(4, 0, false));
        merchant_offer.add(craftItemStack1);
      } 
      while (t4_scraps >= 80) {
        t4_scraps -= 80;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(Items.enchant(4, 1, false));
        merchant_offer.add(craftItemStack1);
      } 
      while (t4_scraps >= 60) {
        t4_scraps -= 60;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(orb_of_alteration);
        merchant_offer.add(craftItemStack1);
      } 
      int payout = 2 * t4_scraps;
      while (payout > 64) {
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T3_scrap);
        craftItemStack1.setAmount(64);
        merchant_offer.add(craftItemStack1);
        payout -= 64;
      } 
      CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T3_scrap);
      craftItemStack.setAmount(payout);
      merchant_offer.add(craftItemStack);
    } 
    if (t5_scraps > 0) {
      while (t5_scraps >= 33) {
        t5_scraps -= 33;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(Items.enchant(5, 0, false));
        merchant_offer.add(craftItemStack1);
      } 
      while (t5_scraps >= 30) {
        t5_scraps -= 30;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(Items.enchant(5, 1, false));
        merchant_offer.add(craftItemStack1);
      } 
      while (t5_scraps >= 20) {
        t5_scraps -= 20;
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(orb_of_alteration);
        merchant_offer.add(craftItemStack1);
      } 
      int payout = 3 * t5_scraps;
      while (payout > 64) {
        CraftItemStack craftItemStack1 = CraftItemStack.asCraftCopy(T4_scrap);
        craftItemStack1.setAmount(64);
        merchant_offer.add(craftItemStack1);
        payout -= 64;
      } 
      CraftItemStack craftItemStack = CraftItemStack.asCraftCopy(T4_scrap);
      craftItemStack.setAmount(payout);
      merchant_offer.add(craftItemStack);
    } 
    return merchant_offer;
  }
  
  public static boolean isTradeButton(ItemStack is) {
    if (is == null)
      return false; 
    if (is.getType() == Material.INK_SACK && (
      is.getDurability() == 8 || is.getDurability() == 10) && 
      is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
      String item_name = is.getItemMeta().getDisplayName();
      if (item_name.contains("Trade") || item_name.contains("Duel"))
        return true; 
    } 
    return false;
  }
  
  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
    Player p = e.getPlayer();
    if (e.getRightClicked() instanceof Player) {
      Player trader = (Player)e.getRightClicked();
      if (!trader.hasMetadata("NPC"))
        return; 
      if (!ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Merchant"))
        return; 
      e.setCancelled(true);
      Inventory TradeWindow = Bukkit.createInventory(null, 27, generateTitle(p.getName(), "Merchant"));
      TradeWindow.setItem(4, divider);
      TradeWindow.setItem(13, divider);
      TradeWindow.setItem(22, divider);
      ItemStack bttn = new ItemStack(Material.INK_SACK, 1, (short)8);
      ItemMeta im = bttn.getItemMeta();
      im.setDisplayName(String.valueOf(ChatColor.YELLOW.toString()) + "Click to ACCEPT Trade");
      bttn.setItemMeta(im);
      TradeWindow.setItem(0, bttn);
      p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
      if (in_npc_shop.contains(p.getName()))
        in_npc_shop.remove(p.getName()); 
      in_npc_shop.add(p.getName());
      p.openInventory(TradeWindow);
    } 
  }
  
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onMerchantEvent(InventoryClickEvent e) {
    if (!e.getInventory().getName().contains("Merchant"))
      return; 
    final Player clicker = (Player)e.getWhoClicked();
    Inventory tradeWin = e.getInventory();
    int slot_num = e.getRawSlot();
    if (e.getClick() == ClickType.DOUBLE_CLICK) {
      e.setCancelled(true);
      clicker.updateInventory();
    } 
    if (!e.isShiftClick() || (e.isShiftClick() && slot_num < 27)) {
      if (e.getSlotType() != InventoryType.SlotType.CONTAINER)
        return; 
      if (e.getInventory().getType() == InventoryType.PLAYER)
        return; 
      if (slot_num > 26 || slot_num < 0)
        return; 
      if (slot_num != 0 && slot_num != 1 && slot_num != 2 && slot_num != 3 && slot_num != 9 && slot_num != 10 && 
        slot_num != 11 && slot_num != 12 && slot_num != 18 && slot_num != 19 && slot_num != 20 && 
        slot_num != 21 && slot_num <= 27) {
        e.setCancelled(true);
        tradeWin.setItem(slot_num, tradeWin.getItem(slot_num));
        clicker.setItemOnCursor(e.getCursor());
        clicker.updateInventory();
      } else if (!e.isShiftClick()) {
        if ((e.getCursor() == null || e.getCursor().getType() == Material.AIR) && e.getCurrentItem() != null && 
          !isTradeButton(e.getCurrentItem())) {
          e.setCancelled(true);
          ItemStack in_slot = tradeWin.getItem(slot_num);
          tradeWin.setItem(slot_num, new ItemStack(Material.AIR));
          e.setCursor(in_slot);
          clicker.updateInventory();
        } else if ((e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) && 
          e.getCursor() != null) {
          e.setCancelled(true);
          ItemStack on_cur = e.getCursor();
          tradeWin.setItem(slot_num, on_cur);
          e.setCursor(new ItemStack(Material.AIR));
          clicker.updateInventory();
        } else if (e.getCurrentItem() != null && e.getCursor() != null && 
          !isTradeButton(e.getCurrentItem())) {
          e.setCancelled(true);
          ItemStack on_cur = e.getCursor();
          ItemStack in_slot = e.getCurrentItem();
          e.setCursor(in_slot);
          e.setCurrentItem(on_cur);
          clicker.updateInventory();
        } 
      } 
    } 
    if (e.isShiftClick() && slot_num < 26)
      if (slot_num != 0 && slot_num != 1 && slot_num != 2 && slot_num != 3 && slot_num != 9 && slot_num != 10 && 
        slot_num != 11 && slot_num != 12 && slot_num != 18 && slot_num != 19 && slot_num != 20 && 
        slot_num != 21 && slot_num <= 27) {
        e.setCancelled(true);
        if (tradeWin.getItem(slot_num) != null && tradeWin.getItem(slot_num).getType() != Material.AIR) {
          tradeWin.setItem(slot_num, tradeWin.getItem(slot_num));
          clicker.updateInventory();
        } 
      } else if (!isTradeButton(e.getCurrentItem())) {
        e.setCancelled(true);
        ItemStack in_slot = e.getCurrentItem();
        if (clicker.getInventory().firstEmpty() != -1) {
          tradeWin.setItem(slot_num, new ItemStack(Material.AIR));
          clicker.getInventory().setItem(clicker.getInventory().firstEmpty(), in_slot);
          clicker.updateInventory();
        } 
      }  
    if (e.isShiftClick() && slot_num >= 27 && !e.isCancelled() && 
      e.getCurrentItem().getType() != Material.BOOK) {
      e.setCancelled(true);
      ItemStack to_move = e.getCurrentItem();
      int local_to_move_slot = e.getSlot();
      int i = -1;
      while (i < 26) {
        i++;
        if (i != 0 && i != 1 && i != 2 && i != 3 && i != 9 && i != 10 && i != 11 && i != 12 && i != 18 && 
          i != 19 && i != 20 && i != 21)
          continue; 
        ItemStack itemStack = tradeWin.getItem(i);
        if (itemStack != null)
          continue; 
        tradeWin.setItem(i, to_move);
        if (tradeWin.getItem(i) != null)
          tradeWin.getItem(i).setAmount(to_move.getAmount()); 
        clicker.getInventory().setItem(local_to_move_slot, new ItemStack(Material.AIR));
        clicker.updateInventory();
        break;
      } 
    } 
    List<ItemStack> player_offer = new ArrayList<>();
    int x = -1;
    while (x < 26) {
      x++;
      if (x != 0 && x != 1 && x != 2 && x != 3 && x != 9 && x != 10 && x != 11 && x != 12 && x != 18 && x != 19 && 
        x != 20 && x != 21)
        continue; 
      ItemStack i = tradeWin.getItem(x);
      if (i == null || i.getType() == Material.AIR || isTradeButton(i))
        continue; 
      player_offer.add(i);
    } 
    List<ItemStack> new_offer = generateMerchantOffer(player_offer);
    x = -1;
    while (x < 26) {
      x++;
      if (x == 0 || x == 1 || x == 2 || x == 3 || x == 4 || x == 9 || x == 10 || x == 11 || x == 12 || x == 13 || 
        x == 22 || x == 18 || x == 19 || x == 20 || x == 21)
        continue; 
      tradeWin.setItem(x, new ItemStack(Material.AIR));
    } 
    x = -1;
    while (x < 26) {
      x++;
      if (new_offer.size() <= 0 || 
        x == 0 || x == 1 || x == 2 || x == 3 || x == 4 || x == 9 || x == 10 || x == 11 || x == 12 || 
        x == 13 || x == 22 || x == 18 || x == 19 || x == 20 || x == 21)
        continue; 
      int index = new_offer.size() - 1;
      ItemStack i = new_offer.get(index);
      tradeWin.setItem(x, i);
      new_offer.remove(index);
    } 
    clicker.updateInventory();
    if (isTradeButton(e.getCurrentItem())) {
      e.setCancelled(true);
      if (e.getCurrentItem().getDurability() == 8) {
        int slots_available = 0;
        int slots_needed = 0;
        e.getCurrentItem().setDurability((short)10);
        ItemStack bttn = new ItemStack(Material.INK_SACK, 1, (short)10);
        ItemMeta im = bttn.getItemMeta();
        im.setDisplayName(String.valueOf(ChatColor.GREEN.toString()) + "Trade ACCEPTED.");
        bttn.setItemMeta(im);
        e.setCurrentItem(bttn);
        clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1.0F, 2.0F);
        for (ItemStack i : clicker.getInventory()) {
          if (i == null || i.getType() == Material.AIR)
            slots_available++; 
        } 
        int slot_var = -1;
        while (slot_var < 26) {
          slot_var++;
          if (slot_var != 5 && slot_var != 6 && slot_var != 7 && slot_var != 8 && slot_var != 14 && 
            slot_var != 15 && slot_var != 16 && slot_var != 17 && slot_var != 23 && slot_var != 24 && 
            slot_var != 25 && slot_var != 26)
            continue; 
          ItemStack i = tradeWin.getItem(slot_var);
          if (i == null || i.getType() == Material.AIR || isTradeButton(i) || 
            i.getType() == Material.THIN_GLASS)
            continue; 
          slots_needed++;
        } 
        if (slots_available < slots_needed) {
          clicker.sendMessage(ChatColor.RED + ChatColor.BOLD + "Not enough room.");
          clicker.sendMessage(ChatColor.GRAY + "You need " + ChatColor.BOLD + (slots_needed - slots_available) + 
              ChatColor.RED + " more free slots to complete this trade.");
          Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                public void run() {
                  InventoryCloseEvent close_clicker = new InventoryCloseEvent(clicker.getOpenInventory());
                  Bukkit.getServer().getPluginManager().callEvent((Event)close_clicker);
                }
              },  2L);
          return;
        } 
        slot_var = -1;
        while (slot_var < 26) {
          slot_var++;
          if (slot_var != 5 && slot_var != 6 && slot_var != 7 && slot_var != 8 && slot_var != 14 && 
            slot_var != 15 && slot_var != 16 && slot_var != 17 && slot_var != 23 && slot_var != 24 && 
            slot_var != 25 && slot_var != 26)
            continue; 
          ItemStack i = tradeWin.getItem(slot_var);
          if (i == null || i.getType() == Material.AIR || isTradeButton(i) || 
            i.getType() == Material.THIN_GLASS)
            continue; 
          if (i.getType() == Material.EMERALD)
            i = Money.makeGems(i.getAmount()); 
          clicker.getInventory().setItem(clicker.getInventory().firstEmpty(), i);
        } 
        clicker.sendMessage(ChatColor.GREEN + ChatColor.BOLD + "Trade accepted.");
        clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1.0F, 1.5F);
        tradeWin.clear();
        in_npc_shop.remove(clicker.getName());
        Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
              public void run() {
                clicker.updateInventory();
                clicker.closeInventory();
              }
            },  2L);
        return;
      } 
      clicker.updateInventory();
    } 
  }
  
  @EventHandler
  public void onPlayerCloseInventory(InventoryCloseEvent e) {
    Player closer = (Player)e.getPlayer();
    if (in_npc_shop.contains(closer.getName())) {
      Inventory tradeInv = e.getInventory();
      if (!tradeInv.getName().contains("Merchant")) {
        in_npc_shop.remove(closer.getName());
        return;
      } 
      int slot_var = -1;
      while (slot_var < 26) {
        slot_var++;
        if (slot_var != 0 && slot_var != 1 && slot_var != 2 && slot_var != 3 && slot_var != 9 && 
          slot_var != 10 && slot_var != 11 && slot_var != 12 && slot_var != 18 && slot_var != 19 && 
          slot_var != 20 && slot_var != 21)
          continue; 
        ItemStack i = tradeInv.getItem(slot_var);
        if (i == null || i.getType() == Material.AIR || isTradeButton(i) || 
          i.getType() == Material.THIN_GLASS)
          continue; 
        if (i.getType() == Material.EMERALD)
          i = Money.makeGems(i.getAmount()); 
        if (closer.getInventory().firstEmpty() == -1) {
          closer.getWorld().dropItemNaturally(closer.getLocation(), i);
          continue;
        } 
        closer.getInventory().setItem(closer.getInventory().firstEmpty(), i);
      } 
      closer.getOpenInventory().getTopInventory().clear();
      closer.updateInventory();
      closer.sendMessage(ChatColor.YELLOW + ChatColor.BOLD + "Trade cancelled.");
      in_npc_shop.remove(closer.getName());
    } 
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    Player closer = e.getPlayer();
    if (in_npc_shop.contains(closer.getName())) {
      Inventory tradeInv = closer.getOpenInventory().getTopInventory();
      int slot_var = -1;
      while (slot_var < 26) {
        slot_var++;
        if (slot_var != 0 && slot_var != 1 && slot_var != 2 && slot_var != 3 && slot_var != 9 && 
          slot_var != 10 && slot_var != 11 && slot_var != 12 && slot_var != 18 && slot_var != 19 && 
          slot_var != 20 && slot_var != 21)
          continue; 
        ItemStack i = tradeInv.getItem(slot_var);
        if (i == null || i.getType() == Material.AIR || isTradeButton(i) || 
          i.getType() == Material.THIN_GLASS)
          continue; 
        if (i.getType() == Material.EMERALD)
          i = Money.makeGems(i.getAmount()); 
        if (closer.getInventory().firstEmpty() == -1) {
          closer.getWorld().dropItemNaturally(closer.getLocation(), i);
          continue;
        } 
        closer.getInventory().setItem(closer.getInventory().firstEmpty(), i);
      } 
      closer.getOpenInventory().getTopInventory().clear();
      closer.closeInventory();
      closer.updateInventory();
      in_npc_shop.remove(closer.getName());
    } 
  }
}
