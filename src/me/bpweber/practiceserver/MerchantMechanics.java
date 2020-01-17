package me.bpweber.practiceserver;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.Sound;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import java.util.Iterator;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.event.Listener;

public class MerchantMechanics implements Listener
{
    static Logger log;
    public static List<String> in_npc_shop;
    public static ItemStack divider;
    public static ItemStack T1_scrap;
    public static ItemStack T2_scrap;
    public static ItemStack T3_scrap;
    public static ItemStack T4_scrap;
    public static ItemStack T5_scrap;
    public static ItemStack orb_of_alteration;
    
    static {
        MerchantMechanics.log = Logger.getLogger("Minecraft");
        MerchantMechanics.in_npc_shop = new ArrayList<String>();
        MerchantMechanics.divider = new ItemStack(Material.THIN_GLASS, 1);
        MerchantMechanics.T1_scrap = Durability.scrap(1);
        MerchantMechanics.T2_scrap = Durability.scrap(2);
        MerchantMechanics.T3_scrap = Durability.scrap(3);
        MerchantMechanics.T4_scrap = Durability.scrap(4);
        MerchantMechanics.T5_scrap = Durability.scrap(5);
        MerchantMechanics.orb_of_alteration = Items.orb(false);
    }
    
    public void onEnable() {
        Main.log.info("[MerchantMechanics] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        final ItemMeta im = MerchantMechanics.divider.getItemMeta();
        im.setDisplayName(" ");
        MerchantMechanics.divider.setItemMeta(im);
    }
    
    public void onDisable() {
        Main.log.info("[MerchantMechanics] has been disabled.");
    }
    
    private static String generateTitle(final String lPName, final String rPName) {
        String title;
        for (title = "  " + lPName; title.length() + rPName.length() < 30; title = String.valueOf(title) + " ") {}
        return title = String.valueOf(title) + rPName;
    }
    
    public static List<ItemStack> generateMerchantOffer(final List<ItemStack> player_offer) {
        final List<ItemStack> merchant_offer = new ArrayList<ItemStack>();
        final List<ItemStack> to_remove = new ArrayList<ItemStack>();
        int t1_scraps = 0;
        int t2_scraps = 0;
        int t3_scraps = 0;
        int t4_scraps = 0;
        int t5_scraps = 0;
        int t1_ore = 0;
        int t2_ore = 0;
        int t3_ore = 0;
        int t4_ore = 0;
        int t5_ore = 0;
        for (final ItemStack is : player_offer) {
            if (is != null) {
                if (is.getType() == Material.AIR) {
                    continue;
                }
                if (is.getType() != Material.COAL_ORE && is.getType() != Material.EMERALD_ORE && is.getType() != Material.IRON_ORE && is.getType() != Material.DIAMOND_ORE && is.getType() != Material.GOLD_ORE) {
                    continue;
                }
                final int tier = ProfessionMechanics.getOreTier(is.getType());
                if (tier == 1) {
                    t1_ore += is.getAmount();
                }
                if (tier == 2) {
                    t2_ore += is.getAmount();
                }
                if (tier == 3) {
                    t3_ore += is.getAmount();
                }
                if (tier == 4) {
                    t4_ore += is.getAmount();
                }
                if (tier == 5) {
                    t5_ore += is.getAmount();
                }
                to_remove.add(is);
            }
        }
        for (final ItemStack is : player_offer) {
            if (is != null) {
                if (is.getType() == Material.AIR) {
                    continue;
                }
                if (!Durability.isScrap(is)) {
                    continue;
                }
                final int tier = Merchant.getTier(is);
                if (tier == 1) {
                    t1_scraps += is.getAmount();
                }
                if (tier == 2) {
                    t2_scraps += is.getAmount();
                }
                if (tier == 3) {
                    t3_scraps += is.getAmount();
                }
                if (tier == 4) {
                    t4_scraps += is.getAmount();
                }
                if (tier == 5) {
                    t5_scraps += is.getAmount();
                }
                to_remove.add(is);
            }
        }
        for (final ItemStack is : to_remove) {
            player_offer.remove(is);
        }
        for (final ItemStack is : player_offer) {
            if (is != null) {
                if (is.getType() == Material.AIR) {
                    continue;
                }
                final int tier = Repairing.getTier(is);
                if (is.getType() != Material.MAGMA_CREAM && is.getType() != Material.PAPER && !Durability.isScrap(is)) {
                    int payout = 0;
                    final Material m = is.getType();
                    final String m_name = m.name().toLowerCase();
                    if (m_name.contains("axe") || m_name.contains("sword") || m_name.contains("leg") || m_name.contains("bow") || m_name.contains("spade") || m_name.contains("hoe")) {
                        payout = 2;
                    }
                    if (m_name.contains("helmet") || m_name.contains("boot")) {
                        payout = 1;
                    }
                    if (m_name.contains("chest")) {
                        payout = 3;
                    }
                    if (tier == 1) {
                        final ItemStack scrap = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T2_scrap);
                        scrap.setAmount(payout);
                        merchant_offer.add(scrap);
                    }
                    if (tier == 2) {
                        final ItemStack scrap = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T3_scrap);
                        scrap.setAmount(payout);
                        merchant_offer.add(scrap);
                    }
                    if (tier == 3) {
                        final ItemStack scrap = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T4_scrap);
                        scrap.setAmount(payout);
                        merchant_offer.add(scrap);
                    }
                    if (tier == 4) {
                        final ItemStack scrap = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T5_scrap);
                        scrap.setAmount(payout);
                        merchant_offer.add(scrap);
                    }
                    if (tier == 5) {
                        final ItemStack scrap = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T5_scrap);
                        scrap.setAmount(payout * 2);
                        merchant_offer.add(scrap);
                    }
                }
                if (is.getType() == null || is.getType() != Material.MAGMA_CREAM || !is.getItemMeta().hasDisplayName() || !is.getItemMeta().getDisplayName().toLowerCase().contains("orb of alteration")) {
                    continue;
                }
                final int orb_count = is.getAmount();
                int payout2;
                for (payout2 = 20 * orb_count; payout2 > 64; payout2 -= 64) {
                    final ItemStack scrap2 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T5_scrap);
                    scrap2.setAmount(64);
                    merchant_offer.add(scrap2);
                }
                final ItemStack scrap2 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T5_scrap);
                scrap2.setAmount(payout2);
                merchant_offer.add(scrap2);
            }
        }
        if (t1_ore > 0) {
            int payout3 = t1_ore * 2;
            while (payout3 > 64) {
                payout3 -= 64;
                final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T1_scrap);
                scrap3.setAmount(64);
                merchant_offer.add(scrap3);
            }
            final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T1_scrap);
            scrap3.setAmount(payout3);
            merchant_offer.add(scrap3);
        }
        if (t2_ore > 0) {
            int payout3 = t2_ore * 2;
            while (payout3 > 64) {
                payout3 -= 64;
                final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T2_scrap);
                scrap3.setAmount(64);
                merchant_offer.add(scrap3);
            }
            final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T2_scrap);
            scrap3.setAmount(payout3);
            merchant_offer.add(scrap3);
        }
        if (t3_ore > 0) {
            int payout3 = t3_ore * 2;
            while (payout3 > 64) {
                payout3 -= 64;
                final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T3_scrap);
                scrap3.setAmount(64);
                merchant_offer.add(scrap3);
            }
            final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T3_scrap);
            scrap3.setAmount(payout3);
            merchant_offer.add(scrap3);
        }
        if (t4_ore > 0) {
            int payout3 = t4_ore * 2;
            while (payout3 > 64) {
                payout3 -= 64;
                final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T4_scrap);
                scrap3.setAmount(64);
                merchant_offer.add(scrap3);
            }
            final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T4_scrap);
            scrap3.setAmount(payout3);
            merchant_offer.add(scrap3);
        }
        if (t5_ore > 0) {
            int payout3 = t5_ore * 2;
            while (payout3 > 64) {
                payout3 -= 64;
                final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T5_scrap);
                scrap3.setAmount(64);
                merchant_offer.add(scrap3);
            }
            final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T5_scrap);
            scrap3.setAmount(payout3);
            merchant_offer.add(scrap3);
        }
        if (t1_scraps > 0) {
            while (t1_scraps >= 80) {
                t1_scraps -= 80;
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(Items.enchant(1, 0, false));
                merchant_offer.add(scroll);
            }
            while (t1_scraps >= 70) {
                t1_scraps -= 70;
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(Items.enchant(1, 1, false));
                merchant_offer.add(scroll);
            }
            int payout3;
            for (payout3 = t1_scraps / 2; payout3 > 64; payout3 -= 64) {
                final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T2_scrap);
                scrap3.setAmount(64);
                merchant_offer.add(scrap3);
            }
            final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T2_scrap);
            scrap3.setAmount(payout3);
            merchant_offer.add(scrap3);
        }
        if (t2_scraps > 0) {
            while (t2_scraps >= 140) {
                t2_scraps -= 140;
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(Items.enchant(2, 0, false));
                merchant_offer.add(scroll);
            }
            while (t2_scraps >= 125) {
                t2_scraps -= 125;
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(Items.enchant(2, 1, false));
                merchant_offer.add(scroll);
            }
            int payout3;
            for (payout3 = 2 * t2_scraps; payout3 > 64; payout3 -= 64) {
                final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T1_scrap);
                scrap3.setAmount(64);
                merchant_offer.add(scrap3);
            }
            final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T1_scrap);
            scrap3.setAmount(payout3);
            merchant_offer.add(scrap3);
        }
        if (t3_scraps > 0) {
            while (t3_scraps >= 120) {
                t3_scraps -= 120;
                final ItemStack orb = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.orb_of_alteration);
                merchant_offer.add(orb);
            }
            while (t3_scraps >= 110) {
                t3_scraps -= 110;
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(Items.enchant(3, 0, false));
                merchant_offer.add(scroll);
            }
            while (t3_scraps >= 100) {
                t3_scraps -= 100;
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(Items.enchant(3, 1, false));
                merchant_offer.add(scroll);
            }
            int payout3;
            for (payout3 = 2 * t3_scraps; payout3 > 64; payout3 -= 64) {
                final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T2_scrap);
                scrap3.setAmount(64);
                merchant_offer.add(scrap3);
            }
            final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T2_scrap);
            scrap3.setAmount(payout3);
            merchant_offer.add(scrap3);
        }
        if (t4_scraps > 0) {
            while (t4_scraps >= 88) {
                t4_scraps -= 88;
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(Items.enchant(4, 0, false));
                merchant_offer.add(scroll);
            }
            while (t4_scraps >= 80) {
                t4_scraps -= 80;
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(Items.enchant(4, 1, false));
                merchant_offer.add(scroll);
            }
            while (t4_scraps >= 60) {
                t4_scraps -= 60;
                final ItemStack orb = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.orb_of_alteration);
                merchant_offer.add(orb);
            }
            int payout3;
            for (payout3 = 2 * t4_scraps; payout3 > 64; payout3 -= 64) {
                final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T3_scrap);
                scrap3.setAmount(64);
                merchant_offer.add(scrap3);
            }
            final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T3_scrap);
            scrap3.setAmount(payout3);
            merchant_offer.add(scrap3);
        }
        if (t5_scraps > 0) {
            while (t5_scraps >= 33) {
                t5_scraps -= 33;
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(Items.enchant(5, 0, false));
                merchant_offer.add(scroll);
            }
            while (t5_scraps >= 30) {
                t5_scraps -= 30;
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(Items.enchant(5, 1, false));
                merchant_offer.add(scroll);
            }
            while (t5_scraps >= 20) {
                t5_scraps -= 20;
                final ItemStack orb = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.orb_of_alteration);
                merchant_offer.add(orb);
            }
            int payout3;
            for (payout3 = 3 * t5_scraps; payout3 > 64; payout3 -= 64) {
                final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T4_scrap);
                scrap3.setAmount(64);
                merchant_offer.add(scrap3);
            }
            final ItemStack scrap3 = (ItemStack)CraftItemStack.asCraftCopy(MerchantMechanics.T4_scrap);
            scrap3.setAmount(payout3);
            merchant_offer.add(scrap3);
        }
        return merchant_offer;
    }
    
    public static boolean isTradeButton(final ItemStack is) {
        if (is == null) {
            return false;
        }
        if (is.getType() == Material.INK_SACK && (is.getDurability() == 8 || is.getDurability() == 10) && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            final String item_name = is.getItemMeta().getDisplayName();
            if (item_name.contains("Trade") || item_name.contains("Duel")) {
                return true;
            }
        }
        return false;
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent e) {
        final Player p = e.getPlayer();
        if (e.getRightClicked() instanceof Player) {
            final Player trader = (Player)e.getRightClicked();
            if (!trader.hasMetadata("NPC")) {
                return;
            }
            if (!ChatColor.stripColor(trader.getName()).equalsIgnoreCase("Merchant")) {
                return;
            }
            e.setCancelled(true);
            final Inventory TradeWindow = Bukkit.createInventory((InventoryHolder)null, 27, generateTitle(p.getName(), "Merchant"));
            TradeWindow.setItem(4, MerchantMechanics.divider);
            TradeWindow.setItem(13, MerchantMechanics.divider);
            TradeWindow.setItem(22, MerchantMechanics.divider);
            final ItemStack bttn = new ItemStack(Material.INK_SACK, 1, (short)8);
            final ItemMeta im = bttn.getItemMeta();
            im.setDisplayName(String.valueOf(ChatColor.YELLOW.toString()) + "Click to ACCEPT Trade");
            bttn.setItemMeta(im);
            TradeWindow.setItem(0, bttn);
            p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0f, 1.0f);
            if (MerchantMechanics.in_npc_shop.contains(p.getName())) {
                MerchantMechanics.in_npc_shop.remove(p.getName());
            }
            MerchantMechanics.in_npc_shop.add(p.getName());
            p.openInventory(TradeWindow);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMerchantEvent(final InventoryClickEvent e) {
        if (!e.getInventory().getName().contains("Merchant")) {
            return;
        }
        final Player clicker = (Player)e.getWhoClicked();
        final Inventory tradeWin = e.getInventory();
        final int slot_num = e.getRawSlot();
        if (e.getClick() == ClickType.DOUBLE_CLICK) {
            e.setCancelled(true);
            clicker.updateInventory();
        }
        if (!e.isShiftClick() || (e.isShiftClick() && slot_num < 27)) {
            if (e.getSlotType() != InventoryType.SlotType.CONTAINER) {
                return;
            }
            if (e.getInventory().getType() == InventoryType.PLAYER) {
                return;
            }
            if (slot_num > 26 || slot_num < 0) {
                return;
            }
            if (slot_num != 0 && slot_num != 1 && slot_num != 2 && slot_num != 3 && slot_num != 9 && slot_num != 10 && slot_num != 11 && slot_num != 12 && slot_num != 18 && slot_num != 19 && slot_num != 20 && slot_num != 21 && slot_num <= 27) {
                e.setCancelled(true);
                tradeWin.setItem(slot_num, tradeWin.getItem(slot_num));
                clicker.setItemOnCursor(e.getCursor());
                clicker.updateInventory();
            }
            else if (!e.isShiftClick()) {
                if ((e.getCursor() == null || e.getCursor().getType() == Material.AIR) && e.getCurrentItem() != null && !isTradeButton(e.getCurrentItem())) {
                    e.setCancelled(true);
                    final ItemStack in_slot = tradeWin.getItem(slot_num);
                    tradeWin.setItem(slot_num, new ItemStack(Material.AIR));
                    e.setCursor(in_slot);
                    clicker.updateInventory();
                }
                else if ((e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) && e.getCursor() != null) {
                    e.setCancelled(true);
                    final ItemStack on_cur = e.getCursor();
                    tradeWin.setItem(slot_num, on_cur);
                    e.setCursor(new ItemStack(Material.AIR));
                    clicker.updateInventory();
                }
                else if (e.getCurrentItem() != null && e.getCursor() != null && !isTradeButton(e.getCurrentItem())) {
                    e.setCancelled(true);
                    final ItemStack on_cur = e.getCursor();
                    final ItemStack in_slot2 = e.getCurrentItem();
                    e.setCursor(in_slot2);
                    e.setCurrentItem(on_cur);
                    clicker.updateInventory();
                }
            }
        }
        if (e.isShiftClick() && slot_num < 26) {
            if (slot_num != 0 && slot_num != 1 && slot_num != 2 && slot_num != 3 && slot_num != 9 && slot_num != 10 && slot_num != 11 && slot_num != 12 && slot_num != 18 && slot_num != 19 && slot_num != 20 && slot_num != 21 && slot_num <= 27) {
                e.setCancelled(true);
                if (tradeWin.getItem(slot_num) != null && tradeWin.getItem(slot_num).getType() != Material.AIR) {
                    tradeWin.setItem(slot_num, tradeWin.getItem(slot_num));
                    clicker.updateInventory();
                }
            }
            else if (!isTradeButton(e.getCurrentItem())) {
                e.setCancelled(true);
                final ItemStack in_slot = e.getCurrentItem();
                if (clicker.getInventory().firstEmpty() != -1) {
                    tradeWin.setItem(slot_num, new ItemStack(Material.AIR));
                    clicker.getInventory().setItem(clicker.getInventory().firstEmpty(), in_slot);
                    clicker.updateInventory();
                }
            }
        }
        if (e.isShiftClick() && slot_num >= 27 && !e.isCancelled() && e.getCurrentItem().getType() != Material.BOOK) {
            e.setCancelled(true);
            final ItemStack to_move = e.getCurrentItem();
            final int local_to_move_slot = e.getSlot();
            int x = -1;
            while (x < 26) {
                if (++x != 0 && x != 1 && x != 2 && x != 3 && x != 9 && x != 10 && x != 11 && x != 12 && x != 18 && x != 19 && x != 20 && x != 21) {
                    continue;
                }
                final ItemStack i = tradeWin.getItem(x);
                if (i != null) {
                    continue;
                }
                tradeWin.setItem(x, to_move);
                if (tradeWin.getItem(x) != null) {
                    tradeWin.getItem(x).setAmount(to_move.getAmount());
                }
                clicker.getInventory().setItem(local_to_move_slot, new ItemStack(Material.AIR));
                clicker.updateInventory();
                break;
            }
        }
        final List<ItemStack> player_offer = new ArrayList<ItemStack>();
        int x2 = -1;
        while (x2 < 26) {
            if (++x2 != 0 && x2 != 1 && x2 != 2 && x2 != 3 && x2 != 9 && x2 != 10 && x2 != 11 && x2 != 12 && x2 != 18 && x2 != 19 && x2 != 20 && x2 != 21) {
                continue;
            }
            final ItemStack j = tradeWin.getItem(x2);
            if (j == null || j.getType() == Material.AIR) {
                continue;
            }
            if (isTradeButton(j)) {
                continue;
            }
            player_offer.add(j);
        }
        final List<ItemStack> new_offer = generateMerchantOffer(player_offer);
        x2 = -1;
        while (x2 < 26) {
            if (++x2 != 0 && x2 != 1 && x2 != 2 && x2 != 3 && x2 != 4 && x2 != 9 && x2 != 10 && x2 != 11 && x2 != 12 && x2 != 13 && x2 != 22 && x2 != 18 && x2 != 19 && x2 != 20) {
                if (x2 == 21) {
                    continue;
                }
                tradeWin.setItem(x2, new ItemStack(Material.AIR));
            }
        }
        x2 = -1;
        while (x2 < 26) {
            ++x2;
            if (new_offer.size() > 0 && x2 != 0 && x2 != 1 && x2 != 2 && x2 != 3 && x2 != 4 && x2 != 9 && x2 != 10 && x2 != 11 && x2 != 12 && x2 != 13 && x2 != 22 && x2 != 18 && x2 != 19 && x2 != 20) {
                if (x2 == 21) {
                    continue;
                }
                final int index = new_offer.size() - 1;
                final ItemStack k = new_offer.get(index);
                tradeWin.setItem(x2, k);
                new_offer.remove(index);
            }
        }
        clicker.updateInventory();
        if (isTradeButton(e.getCurrentItem())) {
            e.setCancelled(true);
            if (e.getCurrentItem().getDurability() == 8) {
                int slots_available = 0;
                int slots_needed = 0;
                e.getCurrentItem().setDurability((short)10);
                final ItemStack bttn = new ItemStack(Material.INK_SACK, 1, (short)10);
                final ItemMeta im = bttn.getItemMeta();
                im.setDisplayName(String.valueOf(ChatColor.GREEN.toString()) + "Trade ACCEPTED.");
                bttn.setItemMeta(im);
                e.setCurrentItem(bttn);
                clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1.0f, 2.0f);
                for (final ItemStack l : clicker.getInventory()) {
                    if (l == null || l.getType() == Material.AIR) {
                        ++slots_available;
                    }
                }
                int slot_var = -1;
                while (slot_var < 26) {
                    if (++slot_var != 5 && slot_var != 6 && slot_var != 7 && slot_var != 8 && slot_var != 14 && slot_var != 15 && slot_var != 16 && slot_var != 17 && slot_var != 23 && slot_var != 24 && slot_var != 25 && slot_var != 26) {
                        continue;
                    }
                    final ItemStack m = tradeWin.getItem(slot_var);
                    if (m == null || m.getType() == Material.AIR || isTradeButton(m)) {
                        continue;
                    }
                    if (m.getType() == Material.THIN_GLASS) {
                        continue;
                    }
                    ++slots_needed;
                }
                if (slots_available < slots_needed) {
                    clicker.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Not enough room.").toString());
                    clicker.sendMessage(ChatColor.GRAY + "You need " + ChatColor.BOLD + (slots_needed - slots_available) + ChatColor.RED + " more free slots to complete this trade.");
                    Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            final InventoryCloseEvent close_clicker = new InventoryCloseEvent(clicker.getOpenInventory());
                            Bukkit.getServer().getPluginManager().callEvent((Event)close_clicker);
                        }
                    }, 2L);
                    return;
                }
                slot_var = -1;
                while (slot_var < 26) {
                    if (++slot_var != 5 && slot_var != 6 && slot_var != 7 && slot_var != 8 && slot_var != 14 && slot_var != 15 && slot_var != 16 && slot_var != 17 && slot_var != 23 && slot_var != 24 && slot_var != 25 && slot_var != 26) {
                        continue;
                    }
                    ItemStack m = tradeWin.getItem(slot_var);
                    if (m == null || m.getType() == Material.AIR || isTradeButton(m)) {
                        continue;
                    }
                    if (m.getType() == Material.THIN_GLASS) {
                        continue;
                    }
                    if (m.getType() == Material.EMERALD) {
                        m = Money.makeGems(m.getAmount());
                    }
                    clicker.getInventory().setItem(clicker.getInventory().firstEmpty(), m);
                }
                clicker.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("Trade accepted.").toString());
                clicker.playSound(clicker.getLocation(), Sound.BLAZE_HIT, 1.0f, 1.5f);
                tradeWin.clear();
                MerchantMechanics.in_npc_shop.remove(clicker.getName());
                Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        clicker.updateInventory();
                        clicker.closeInventory();
                    }
                }, 2L);
            }
            else {
                clicker.updateInventory();
            }
        }
    }
    
    @EventHandler
    public void onPlayerCloseInventory(final InventoryCloseEvent e) {
        final Player closer = (Player)e.getPlayer();
        if (MerchantMechanics.in_npc_shop.contains(closer.getName())) {
            final Inventory tradeInv = e.getInventory();
            if (!tradeInv.getName().contains("Merchant")) {
                MerchantMechanics.in_npc_shop.remove(closer.getName());
                return;
            }
            int slot_var = -1;
            while (slot_var < 26) {
                if (++slot_var != 0 && slot_var != 1 && slot_var != 2 && slot_var != 3 && slot_var != 9 && slot_var != 10 && slot_var != 11 && slot_var != 12 && slot_var != 18 && slot_var != 19 && slot_var != 20 && slot_var != 21) {
                    continue;
                }
                ItemStack i = tradeInv.getItem(slot_var);
                if (i == null || i.getType() == Material.AIR || isTradeButton(i)) {
                    continue;
                }
                if (i.getType() == Material.THIN_GLASS) {
                    continue;
                }
                if (i.getType() == Material.EMERALD) {
                    i = Money.makeGems(i.getAmount());
                }
                if (closer.getInventory().firstEmpty() == -1) {
                    closer.getWorld().dropItemNaturally(closer.getLocation(), i);
                }
                else {
                    closer.getInventory().setItem(closer.getInventory().firstEmpty(), i);
                }
            }
            closer.getOpenInventory().getTopInventory().clear();
            closer.updateInventory();
            closer.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append(ChatColor.BOLD).append("Trade cancelled.").toString());
            MerchantMechanics.in_npc_shop.remove(closer.getName());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player closer = e.getPlayer();
        if (MerchantMechanics.in_npc_shop.contains(closer.getName())) {
            final Inventory tradeInv = closer.getOpenInventory().getTopInventory();
            int slot_var = -1;
            while (slot_var < 26) {
                if (++slot_var != 0 && slot_var != 1 && slot_var != 2 && slot_var != 3 && slot_var != 9 && slot_var != 10 && slot_var != 11 && slot_var != 12 && slot_var != 18 && slot_var != 19 && slot_var != 20 && slot_var != 21) {
                    continue;
                }
                ItemStack i = tradeInv.getItem(slot_var);
                if (i == null || i.getType() == Material.AIR || isTradeButton(i)) {
                    continue;
                }
                if (i.getType() == Material.THIN_GLASS) {
                    continue;
                }
                if (i.getType() == Material.EMERALD) {
                    i = Money.makeGems(i.getAmount());
                }
                if (closer.getInventory().firstEmpty() == -1) {
                    closer.getWorld().dropItemNaturally(closer.getLocation(), i);
                }
                else {
                    closer.getInventory().setItem(closer.getInventory().firstEmpty(), i);
                }
            }
            closer.getOpenInventory().getTopInventory().clear();
            closer.closeInventory();
            closer.updateInventory();
            MerchantMechanics.in_npc_shop.remove(closer.getName());
        }
    }
}
