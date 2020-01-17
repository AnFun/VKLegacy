package me.AnFun.VKLegacy;

import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import org.bukkit.event.EventPriority;
import org.bukkit.Sound;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.List;
import org.bukkit.util.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class Repairing implements Listener
{
    static HashMap<String, ItemStack> repairing;
    static HashMap<String, Integer> repaircost;
    static HashMap<String, Item> ghostitem;
    
    static {
        Repairing.repairing = new HashMap<String, ItemStack>();
        Repairing.repaircost = new HashMap<String, Integer>();
        Repairing.ghostitem = new HashMap<String, Item>();
    }
    
    public void onEnable() {
        Main.log.info("[Repairing] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
    }
    
    public void onDisable() {
        Main.log.info("[Repairing] has been disabled.");
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (Repairing.repairing.containsKey(p.getName())) {
                p.getInventory().addItem(new ItemStack[] { Repairing.repairing.get(p.getName()) });
                Repairing.repairing.remove(p.getName());
                Repairing.repaircost.remove(p.getName());
                Repairing.ghostitem.get(p.getName()).remove();
                Repairing.ghostitem.remove(p.getName());
            }
        }
    }
    
    public static int getTier(final ItemStack is) {
        int tier = 0;
        if (is.getType().name().contains("WOOD_") || is.getType().name().contains("LEATHER_")) {
            tier = 1;
        }
        if (is.getType().name().contains("STONE_") || is.getType().name().contains("CHAINMAIL_")) {
            tier = 2;
        }
        if (is.getType().name().contains("IRON_")) {
            tier = 3;
        }
        if (is.getType().name().contains("DIAMOND_")) {
            tier = 4;
        }
        if (is.getType().name().contains("GOLD_")) {
            tier = 5;
        }
        return tier;
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        if (Repairing.repairing.containsKey(p.getName())) {
            p.getInventory().addItem(new ItemStack[] { Repairing.repairing.get(p.getName()) });
            Repairing.repairing.remove(p.getName());
            Repairing.repairing.remove(p.getName());
            Repairing.ghostitem.get(p.getName()).remove();
            Repairing.ghostitem.remove(p.getName());
        }
    }
    
    @EventHandler
    public void onPlayerKick(final PlayerKickEvent e) {
        final Player p = e.getPlayer();
        if (Repairing.repairing.containsKey(p.getName())) {
            p.getInventory().addItem(new ItemStack[] { Repairing.repairing.get(p.getName()) });
            Repairing.repairing.remove(p.getName());
            Repairing.repairing.remove(p.getName());
            Repairing.ghostitem.get(p.getName()).remove();
            Repairing.ghostitem.remove(p.getName());
        }
    }
    
    @EventHandler
    public void onClick(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ANVIL) {
            e.setCancelled(true);
            if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
                p.sendMessage(ChatColor.YELLOW + "Equip the item to repair and " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.YELLOW + " the ANVIL.");
            }
            else {
                if (Repairing.repairing.containsKey(p.getName()) && Repairing.repaircost.containsKey(p.getName())) {
                    p.sendMessage(ChatColor.RED + "You have a pending repair request. Type 'N' to cancel.");
                    return;
                }
                if (p.getItemInHand().getItemMeta().hasLore()) {
                    final List<String> lore = (List<String>)p.getItemInHand().getItemMeta().getLore();
                    if (lore.contains(ChatColor.GRAY + "Untradeable")) {
                        p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " repair this item.");
                        return;
                    }
                    if (p.getItemInHand().getDurability() != 0) {
                        final float percent = p.getItemInHand().getDurability() / (float)p.getItemInHand().getType().getMaxDurability();
                        if (lore.size() > 0 && lore.get(0).contains("DMG:")) {
                            final int mindmg = Damage.getDamageRange(p.getItemInHand()).get(0);
                            final int maxdmg = Damage.getDamageRange(p.getItemInHand()).get(1);
                            final int dmg = (maxdmg + mindmg) / 2;
                            final float price = getTier(p.getItemInHand()) * dmg * percent;
                            int gemamt = (int)price;
                            if (gemamt < 1) {
                                gemamt = 1;
                            }
                            if (Money.hasEnoughGems(p, gemamt)) {
                                p.sendMessage(ChatColor.YELLOW + "It will cost " + ChatColor.GREEN + ChatColor.BOLD + gemamt + "G" + ChatColor.YELLOW + " to repair '" + ChatColor.RESET + p.getItemInHand().getItemMeta().getDisplayName() + ChatColor.YELLOW + "'");
                                p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + ChatColor.BOLD + "Y" + ChatColor.GRAY + " to confirm this repair. Or type " + ChatColor.RED + ChatColor.BOLD + "N" + ChatColor.GRAY + " to cancel.");
                                Repairing.repairing.put(p.getName(), p.getItemInHand());
                                Repairing.repaircost.put(p.getName(), gemamt);
                                final Item itm = p.getWorld().dropItem(e.getClickedBlock().getLocation().add(0.5, 1.125, 0.5), this.makeFakeItem(p.getItemInHand().getType()));
                                itm.setVelocity(new Vector(0.0, 0.1, 0.0));
                                Repairing.ghostitem.put(p.getName(), itm);
                                p.setItemInHand((ItemStack)null);
                                p.updateInventory();
                            }
                            else {
                                p.sendMessage(ChatColor.RED + "You do not have enough " + ChatColor.BOLD + "GEM(s)" + ChatColor.RED + " to repair this item.");
                                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("COST: ").append(ChatColor.RED).append(gemamt).append(ChatColor.BOLD).append("G").toString());
                            }
                        }
                        else if (lore.size() > 0 && (lore.get(0).contains("DPS") || lore.get(0).contains("ARMOR"))) {
                            int arm = 0;
                            if (lore.get(0).contains("DPS")) {
                                arm = Damage.getDps(p.getItemInHand());
                            }
                            if (lore.get(0).contains("ARMOR")) {
                                arm = Damage.getArmor(p.getItemInHand());
                            }
                            final float price2 = getTier(p.getItemInHand()) * (arm * 10) * percent;
                            int gemamt2 = (int)price2;
                            if (gemamt2 < 1) {
                                gemamt2 = 1;
                            }
                            if (Money.hasEnoughGems(p, gemamt2)) {
                                p.sendMessage(ChatColor.YELLOW + "It will cost " + ChatColor.GREEN + ChatColor.BOLD + gemamt2 + "G" + ChatColor.YELLOW + " to repair '" + ChatColor.RESET + p.getItemInHand().getItemMeta().getDisplayName() + ChatColor.YELLOW + "'");
                                p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + ChatColor.BOLD + "Y" + ChatColor.GRAY + " to confirm this repair. Or type " + ChatColor.RED + ChatColor.BOLD + "N" + ChatColor.GRAY + " to cancel.");
                                Repairing.repairing.put(p.getName(), p.getItemInHand());
                                Repairing.repaircost.put(p.getName(), gemamt2);
                                final Item itm2 = p.getWorld().dropItem(e.getClickedBlock().getLocation().add(0.5, 1.125, 0.5), this.makeFakeItem(p.getItemInHand().getType()));
                                itm2.setVelocity(new Vector(0.0, 0.1, 0.0));
                                Repairing.ghostitem.put(p.getName(), itm2);
                                p.setItemInHand((ItemStack)null);
                                p.updateInventory();
                            }
                            else {
                                p.sendMessage(ChatColor.RED + "You do not have enough " + ChatColor.BOLD + "GEM(s)" + ChatColor.RED + " to repair this item.");
                                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("COST: ").append(ChatColor.RED).append(gemamt2).append(ChatColor.BOLD).append("G").toString());
                            }
                        }
                        else {
                            p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " repair this item.");
                        }
                    }
                }
                else {
                    p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " repair this item.");
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrompt(final AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (!Repairing.repairing.containsKey(p.getName()) || !Repairing.repaircost.containsKey(p.getName())) {
            return;
        }
        e.setCancelled(true);
        if (e.getMessage().equalsIgnoreCase("y")) {
            if (p.getInventory().firstEmpty() == -1) {
                p.sendMessage(ChatColor.RED + "You don't have enough room in your inventory to confirm this repair request.");
                return;
            }
            if (Money.hasEnoughGems(p, Repairing.repaircost.get(p.getName()))) {
                final ItemStack is = Repairing.repairing.get(p.getName());
                is.setDurability((short)0);
                if (p.getInventory().firstEmpty() != -1) {
                    p.getInventory().setItem(p.getInventory().firstEmpty(), is);
                }
                p.playSound(p.getLocation(), Sound.ANVIL_USE, 1.0f, 1.0f);
                p.sendMessage(ChatColor.RED + "-" + Repairing.repaircost.get(p.getName()) + ChatColor.BOLD + "G");
                p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("ITEM REPAIRED").toString());
                Money.takeGems(p, Repairing.repaircost.get(p.getName()));
                Repairing.repairing.remove(p.getName());
                Repairing.repaircost.remove(p.getName());
                if (Repairing.ghostitem.containsKey(p.getName())) {
                    if (Repairing.ghostitem.get(p.getName()) != null) {
                        Repairing.ghostitem.get(p.getName()).remove();
                    }
                    Repairing.ghostitem.remove(p.getName());
                }
                return;
            }
            p.sendMessage(ChatColor.RED + "You do not have enough " + ChatColor.BOLD + "GEM(s)" + ChatColor.RED + " to repair this item.");
            p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("COST: ").append(ChatColor.RED).append(Repairing.repaircost.get(p.getName())).append(ChatColor.BOLD).append("G").toString());
            final ItemStack is = Repairing.repairing.get(p.getName());
            if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().setItem(p.getInventory().firstEmpty(), is);
            }
            Repairing.repairing.remove(p.getName());
            Repairing.repaircost.remove(p.getName());
            if (Repairing.ghostitem.containsKey(p.getName())) {
                if (Repairing.ghostitem.get(p.getName()) != null) {
                    Repairing.ghostitem.get(p.getName()).remove();
                }
                Repairing.ghostitem.remove(p.getName());
            }
        }
        else {
            if (!e.getMessage().equalsIgnoreCase("n")) {
                p.sendMessage(ChatColor.RED + "Invalid option.");
                p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + ChatColor.BOLD + "Y" + ChatColor.GRAY + " to confirm this repair. Or type " + ChatColor.RED + ChatColor.BOLD + "N" + ChatColor.GRAY + " to cancel.");
                return;
            }
            if (p.getInventory().firstEmpty() == -1) {
                p.sendMessage(ChatColor.RED + "You don't have enough room in your inventory to cancel this repair request.");
                return;
            }
            p.sendMessage(ChatColor.RED + "Item Repair - " + ChatColor.BOLD + "CANCELLED");
            final ItemStack is = Repairing.repairing.get(p.getName());
            if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().setItem(p.getInventory().firstEmpty(), is);
            }
            Repairing.repairing.remove(p.getName());
            Repairing.repaircost.remove(p.getName());
            if (Repairing.ghostitem.containsKey(p.getName())) {
                if (Repairing.ghostitem.get(p.getName()) != null) {
                    Repairing.ghostitem.get(p.getName()).remove();
                }
                Repairing.ghostitem.remove(p.getName());
            }
        }
    }
    
    ItemStack makeFakeItem(final Material m) {
        final ItemStack is = new ItemStack(m);
        final ItemMeta im = is.getItemMeta();
        im.setLore((List)Arrays.asList("notarealitem"));
        is.setItemMeta(im);
        return is;
    }
}
