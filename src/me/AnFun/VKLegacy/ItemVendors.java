package me.AnFun.VKLegacy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemVendors implements Listener {
  public static HashMap<String, ItemStack> buyingitem = new HashMap<>();
  
  public static HashMap<String, Integer> buyingprice = new HashMap<>();
  
  public void onEnable() {
    Main.log.info("[ItemVendors] has been enabled.");
    Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
  }
  
  public void onDisable() {
    Main.log.info("[ItemVendors] has been disabled.");
  }
  
  public static Integer getPriceFromLore(ItemStack is) {
    int price = 0;
    if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore())
      for (String line : is.getItemMeta().getLore()) {
        if (line.contains("Price: ")) {
          String val = line;
          val = ChatColor.stripColor(val);
          val = val.substring(7, val.length() - 1);
          try {
            price = Integer.parseInt(val);
          } catch (Exception e) {
            price = 0;
          } 
        } 
      }  
    return Integer.valueOf(price);
  }
  
  ItemStack food(int type) {
    ItemStack is = new ItemStack(Material.BREAD);
    int price = 2;
    if (type == 0) {
      is.setType(Material.BREAD);
      price = 5;
    } 
    if (type == 1) {
      is.setType(Material.BAKED_POTATO);
      price = 5;
    } 
    if (type == 2) {
      is.setType(Material.COOKED_BEEF);
      price = 10;
    } 
    ItemMeta im = is.getItemMeta();
    im.setLore(Arrays.asList(new String[] { ChatColor.GREEN + "Price: " + ChatColor.WHITE + price + "g" }));
    is.setItemMeta(im);
    return is;
  }
  
  @EventHandler
  public void onBankClick(PlayerInteractEntityEvent e) {
    if (e.getRightClicked() instanceof HumanEntity) {
      HumanEntity p = (HumanEntity)e.getRightClicked();
      if (p.getName() == null)
        return; 
      if (!p.hasMetadata("NPC"))
        return; 
      if (p.getName().equals("Banker")) {
        e.getPlayer().sendMessage(ChatColor.GRAY + "Banker: " + ChatColor.WHITE + 
            "A banker is a fellow who lends you his umbrella when the sun is shining, but wants it back the minute it begins to rain.");
      } else if (p.getName().equals("Shopkeeper")) {
        e.getPlayer().sendMessage(
            ChatColor.GRAY + "Shopkeeper: " + ChatColor.WHITE + "Andalucia is a nation of shopkeepers.");
        Inventory inv = Bukkit.getServer().createInventory(null, 18, "Shopkeeper");
        inv.addItem(new ItemStack[] { Items.orb(true) });
        inv.addItem(new ItemStack[] { Items.enchant(1, 0, true) });
        inv.addItem(new ItemStack[] { Items.enchant(1, 1, true) });
        inv.addItem(new ItemStack[] { Items.enchant(2, 0, true) });
        inv.addItem(new ItemStack[] { Items.enchant(2, 1, true) });
        inv.addItem(new ItemStack[] { Items.enchant(3, 0, true) });
        inv.addItem(new ItemStack[] { Items.enchant(3, 1, true) });
        inv.addItem(new ItemStack[] { Items.enchant(4, 0, true) });
        inv.addItem(new ItemStack[] { Items.enchant(4, 1, true) });
        inv.addItem(new ItemStack[] { Items.enchant(5, 0, true) });
        inv.addItem(new ItemStack[] { Items.enchant(5, 1, true) });
        e.getPlayer().openInventory(inv);
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
      } else if (p.getName().equals("Fishmonger")) {
        e.getPlayer().sendMessage(ChatColor.GRAY + "Fishmonger: " + ChatColor.WHITE + 
            "Many men go fishing all of their lives without knowing that it is not fish they are after.");
        Inventory inv = Bukkit.getServer().createInventory(null, 9, "Fishmonger");
        inv.addItem(new ItemStack[] { Speedfish.fish(1, true) });
        inv.addItem(new ItemStack[] { Speedfish.fish(2, true) });
        inv.addItem(new ItemStack[] { Speedfish.fish(3, true) });
        inv.addItem(new ItemStack[] { Speedfish.fish(4, true) });
        inv.addItem(new ItemStack[] { Speedfish.fish(5, true) });
        e.getPlayer().openInventory(inv);
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
      } else if (p.getName().equals("Butcher")) {
        e.getPlayer().sendMessage(ChatColor.GRAY + "Butcher: " + ChatColor.WHITE + 
            "It is not from the benevolence of the butcher, the brewer, or the baker that we expect our dinner, but from their regard to their own interest.");
        Inventory inv = Bukkit.getServer().createInventory(null, 9, "Butcher");
        inv.addItem(new ItemStack[] { food(0) });
        inv.addItem(new ItemStack[] { food(1) });
        inv.addItem(new ItemStack[] { food(2) });
        e.getPlayer().openInventory(inv);
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.WOOD_CLICK, 1.0F, 1.0F);
      } 
    } 
  }
  
  @EventHandler
  public void onInvClick(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();
    if (e.getInventory().getTitle().equals("Shopkeeper")) {
      e.setCancelled(true);
      if (e.getCurrentItem() != null && (
        e.getCurrentItem().getType() == Material.MAGMA_CREAM || 
        e.getCurrentItem().getType() == Material.EMPTY_MAP) && 
        e.getCurrentItem().getItemMeta().hasLore()) {
        List<String> lore = e.getCurrentItem().getItemMeta().getLore();
        if (((String)lore.get(lore.size() - 1)).contains("Price:")) {
          int price = getPriceFromLore(e.getCurrentItem()).intValue();
          if (Money.hasEnoughGems(p, price)) {
            ItemStack is = new ItemStack(e.getCurrentItem().getType());
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(e.getCurrentItem().getItemMeta().getDisplayName());
            lore.remove(lore.size() - 1);
            im.setLore(lore);
            is.setItemMeta(im);
            buyingitem.put(p.getName(), is);
            buyingprice.put(p.getName(), Integer.valueOf(price));
            p.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + 
                " you'd like to purchase.");
            p.sendMessage(ChatColor.GRAY + "MAX: 64X (" + (price * 64) + "g), OR " + price + "g/each.");
            p.closeInventory();
          } else {
            p.sendMessage(ChatColor.RED + "You do NOT have enough gems to purchase this " + 
                e.getCurrentItem().getItemMeta().getDisplayName());
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "COST: " + ChatColor.RED + price + 
                ChatColor.BOLD + "G");
            p.closeInventory();
          } 
        } 
      } 
    } else if (e.getCurrentItem() != null && e.getInventory().getTitle().equals("Fishmonger")) {
      e.setCancelled(true);
      if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.RAW_FISH && 
        e.getCurrentItem().getItemMeta().hasLore()) {
        List<String> lore = e.getCurrentItem().getItemMeta().getLore();
        if (((String)lore.get(lore.size() - 1)).contains("Price:")) {
          int price = getPriceFromLore(e.getCurrentItem()).intValue();
          if (Money.hasEnoughGems(p, price)) {
            ItemStack is = new ItemStack(e.getCurrentItem().getType());
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(e.getCurrentItem().getItemMeta().getDisplayName());
            lore.remove(lore.size() - 1);
            im.setLore(lore);
            is.setItemMeta(im);
            buyingitem.put(p.getName(), is);
            buyingprice.put(p.getName(), Integer.valueOf(price));
            p.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + 
                " you'd like to purchase.");
            p.sendMessage(ChatColor.GRAY + "MAX: 64X (" + (price * 64) + "g), OR " + price + "g/each.");
            p.closeInventory();
          } else {
            p.sendMessage(ChatColor.RED + "You do NOT have enough gems to purchase this " + 
                e.getCurrentItem().getItemMeta().getDisplayName());
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "COST: " + ChatColor.RED + price + 
                ChatColor.BOLD + "G");
            p.closeInventory();
          } 
        } 
      } 
    } else if (e.getCurrentItem() != null && e.getInventory().getTitle().equals("Butcher")) {
      e.setCancelled(true);
      if (e.getCurrentItem() != null && (
        e.getCurrentItem().getType() == Material.BREAD || 
        e.getCurrentItem().getType() == Material.BAKED_POTATO || 
        e.getCurrentItem().getType() == Material.COOKED_BEEF) && 
        e.getCurrentItem().getItemMeta().hasLore()) {
        List<String> lore = e.getCurrentItem().getItemMeta().getLore();
        if (((String)lore.get(0)).contains("Price:")) {
          int price = getPriceFromLore(e.getCurrentItem()).intValue();
          if (Money.hasEnoughGems(p, price)) {
            ItemStack is = new ItemStack(e.getCurrentItem().getType());
            buyingitem.put(p.getName(), is);
            buyingprice.put(p.getName(), Integer.valueOf(price));
            p.sendMessage(ChatColor.GREEN + "Enter the " + ChatColor.BOLD + "QUANTITY" + ChatColor.GREEN + 
                " you'd like to purchase.");
            p.sendMessage(ChatColor.GRAY + "MAX: 64X (" + (price * 64) + "g), OR " + price + "g/each.");
            p.closeInventory();
          } else {
            p.sendMessage(ChatColor.RED + "You do NOT have enough gems to purchase this " + 
                e.getCurrentItem().getType().name());
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "COST: " + ChatColor.RED + price + 
                ChatColor.BOLD + "G");
            p.closeInventory();
          } 
        } 
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
      int amt = 0;
      if (e.getMessage().equalsIgnoreCase("cancel")) {
        p.sendMessage(ChatColor.RED + "Purchase of item - " + ChatColor.BOLD + "CANCELLED");
        buyingprice.remove(p.getName());
        buyingitem.remove(p.getName());
        return;
      } 
      try {
        amt = Integer.parseInt(e.getMessage());
      } catch (Exception ex) {
        p.sendMessage(
            ChatColor.RED + "Please enter a valid integer, or type 'cancel' to void this item purchase.");
        return;
      } 
      if (amt < 1) {
        p.sendMessage(ChatColor.RED + "You cannot purchase a NON-POSITIVE number.");
        return;
      } 
      if (amt > 64) {
        p.sendMessage(
            ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " buy MORE than " + 
            ChatColor.BOLD + "64x" + ChatColor.RED + " of a material per transaction.");
        return;
      } 
      if (!Money.hasEnoughGems(p, amt * price)) {
        p.sendMessage(ChatColor.RED + "You do not have enough GEM(s) to complete this purchase.");
        p.sendMessage(ChatColor.GRAY + amt + " X " + price + " gem(s)/ea = " + (amt * price) + " gem(s).");
        return;
      } 
      int empty = 0;
      if (is.getMaxStackSize() == 1) {
        int i;
        for (i = 0; i < p.getInventory().getSize(); i++) {
          if (p.getInventory().getItem(i) == null || 
            p.getInventory().getItem(i).getType() == Material.AIR)
            empty++; 
        } 
        if (amt > empty) {
          p.sendMessage(
              ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
        } else {
          for (i = 0; i < amt; i++)
            p.getInventory().setItem(p.getInventory().firstEmpty(), is); 
          p.sendMessage(ChatColor.RED + "-" + (amt * price) + ChatColor.BOLD + "G");
          p.sendMessage(ChatColor.GREEN + "Transaction successful.");
          Money.takeGems(p, amt * price);
          buyingprice.remove(p.getName());
          buyingitem.remove(p.getName());
        } 
      } else {
        if (p.getInventory().firstEmpty() == -1) {
          p.sendMessage(
              ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
          return;
        } 
        p.sendMessage(ChatColor.RED + "-" + (amt * price) + ChatColor.BOLD + "G");
        p.sendMessage(ChatColor.GREEN + "Transaction successful.");
        Money.takeGems(p, amt * price);
        is.setAmount(amt);
        p.getInventory().setItem(p.getInventory().firstEmpty(), is);
        buyingprice.remove(p.getName());
        buyingitem.remove(p.getName());
      } 
    } 
  }
}