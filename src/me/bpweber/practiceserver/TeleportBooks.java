package me.bpweber.practiceserver;

import java.util.Random;
import java.util.ArrayList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.Sound;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.event.Listener;

public class TeleportBooks implements Listener
{
    public static Location Cyrennica;
    public static Location Harrison_Field;
    public static Location Dark_Oak_Tavern;
    public static Location Deadpeaks_Mountain_Camp;
    public static Location Trollsbane_Tavern;
    public static Location Tripoli;
    public static Location Gloomy_Hollows;
    public static Location Crestguard_Keep;
    public static Location CrestWatch;
    static HashMap<String, Location> teleporting_loc;
    static HashMap<String, Location> casting_loc;
    static HashMap<String, Integer> casting_time;
    
    static {
        TeleportBooks.teleporting_loc = new HashMap<String, Location>();
        TeleportBooks.casting_loc = new HashMap<String, Location>();
        TeleportBooks.casting_time = new HashMap<String, Integer>();
    }
    
    public void onEnable() {
        TeleportBooks.Cyrennica = new Location((World)Bukkit.getWorlds().get(0), -367.0, 83.0, 390.0);
        TeleportBooks.Harrison_Field = new Location((World)Bukkit.getWorlds().get(0), -594.0, 58.0, 687.0, 92.0f, 1.0f);
        TeleportBooks.Dark_Oak_Tavern = new Location((World)Bukkit.getWorlds().get(0), 280.0, 58.0, 1132.0, 2.0f, 1.0f);
        TeleportBooks.Deadpeaks_Mountain_Camp = new Location((World)Bukkit.getWorlds().get(0), -1173.0, 105.0, 1030.0, -88.0f, 1.0f);
        TeleportBooks.Trollsbane_Tavern = new Location((World)Bukkit.getWorlds().get(0), 962.0, 94.0, 1069.0, -153.0f, 1.0f);
        TeleportBooks.Tripoli = new Location((World)Bukkit.getWorlds().get(0), -1320.0, 90.0, 370.0, 153.0f, 1.0f);
        TeleportBooks.Gloomy_Hollows = new Location((World)Bukkit.getWorlds().get(0), -590.0, 43.0, 0.0, 144.0f, 1.0f);
        TeleportBooks.Crestguard_Keep = new Location((World)Bukkit.getWorlds().get(0), -1428.0, 115.0, -489.0, 95.0f, 1.0f);
        TeleportBooks.CrestWatch = new Location((World)Bukkit.getWorlds().get(0), -544.0, 60.0, -418.0, 95.0f, 1.0f);
        Main.log.info("[TeleportBooks] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (TeleportBooks.casting_time.containsKey(p.getName())) {
                        if (TeleportBooks.casting_time.get(p.getName()) == 0) {
                            ParticleEffect.SPELL_WITCH.display(0.0f, 0.0f, 0.0f, 0.2f, 200, p.getLocation().add(0.0, 1.0, 0.0), 20.0);
                            p.eject();
                            p.teleport((Location)TeleportBooks.teleporting_loc.get(p.getName()));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
                            TeleportBooks.casting_time.remove(p.getName());
                            TeleportBooks.casting_loc.remove(p.getName());
                            TeleportBooks.teleporting_loc.remove(p.getName());
                        }
                        else {
                            p.sendMessage(ChatColor.BOLD + "CASTING" + ChatColor.WHITE + " ... " + TeleportBooks.casting_time.get(p.getName()) + ChatColor.BOLD + "s");
                            TeleportBooks.casting_time.put(p.getName(), TeleportBooks.casting_time.get(p.getName()) - 1);
                            ParticleEffect.PORTAL.display(0.0f, 0.0f, 0.0f, 4.0f, 300, p.getLocation(), 20.0);
                            ParticleEffect.SPELL_WITCH.display(0.0f, 0.0f, 0.0f, 1.0f, 200, p.getLocation(), 20.0);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
    }
    
    public void onDisable() {
        Main.log.info("[TeleportBooks] has been disabled.");
    }
    
    public static ItemStack cyrennica_book() {
        final ItemStack is = new ItemStack(Material.BOOK);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Teleport:").append(ChatColor.WHITE).append(" Cyrennica").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Teleports the user to the grand City of Cyrennica."));
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack harrison_book() {
        final ItemStack is = new ItemStack(Material.BOOK);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Teleport:").append(ChatColor.WHITE).append(" Harrison Field").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Teleports the user to Harrison Field."));
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack dark_oak_book() {
        final ItemStack is = new ItemStack(Material.BOOK);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Teleport:").append(ChatColor.WHITE).append(" Dark Oak Tavern").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Teleports the user to the tavern in Dark Oak Forest."));
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack deadpeaks_book() {
        final ItemStack is = new ItemStack(Material.BOOK);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Teleport:").append(ChatColor.WHITE).append(" Deadpeaks Mountain Camp").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Teleports the user to the Deadpeaks.", new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("WARNING:").append(ChatColor.RED.toString()).append(" CHAOTIC ZONE").toString()));
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack trollsbane_book() {
        final ItemStack is = new ItemStack(Material.BOOK);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Teleport:").append(ChatColor.WHITE).append(" Trollsbane Tavern").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Teleports the user to Trollsbane Tavern."));
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack tripoli_book() {
        final ItemStack is = new ItemStack(Material.BOOK);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Teleport:").append(ChatColor.WHITE).append(" Tripoli").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Teleports the user to Tripoli."));
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack gloomy_book() {
        final ItemStack is = new ItemStack(Material.BOOK);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Teleport:").append(ChatColor.WHITE).append(" Gloomy Hollows").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Teleports the user to the Gloomy Hollows."));
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack crestguard_book() {
        final ItemStack is = new ItemStack(Material.BOOK);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Teleport:").append(ChatColor.WHITE).append(" Crestguard Keep").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Teleports the user to the Crestguard Keep."));
        is.setItemMeta(im);
        return is;
    }
    
    public static ItemStack crestwatch_book() {
        final ItemStack is = new ItemStack(Material.BOOK);
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Teleport:").append(ChatColor.WHITE).append(" CrestWatch").toString());
        im.setLore((List)Arrays.asList(ChatColor.GRAY + "Teleports the user to the CrestWatch."));
        is.setItemMeta(im);
        return is;
    }
    
    @EventHandler
    public void onRightClick(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && p.getItemInHand() != null && p.getItemInHand().getType() == Material.BOOK && p.getItemInHand().getItemMeta().hasDisplayName() && p.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains("teleport:") && !TeleportBooks.casting_time.containsKey(p.getName()) && !Horses.mounting.containsKey(p.getName())) {
            final String type = ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName());
            final Location loc = this.getLocationFromString(type);
            if (Alignments.chaotic.containsKey(p.getName()) && loc != TeleportBooks.Deadpeaks_Mountain_Camp) {
                p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " teleport to non-chaotic zones while chaotic.");
                p.sendMessage(ChatColor.GRAY + "Neutral in " + ChatColor.BOLD + Alignments.chaotic.get(p.getName()) + "s");
            }
            else {
                if (p.getItemInHand().getAmount() > 1) {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
                else {
                    p.setItemInHand((ItemStack)null);
                }
                int seconds = 5;
                if (Alignments.tagged.containsKey(p.getName()) && System.currentTimeMillis() - Alignments.tagged.get(p.getName()) < 10000L) {
                    seconds = 10;
                }
                p.sendMessage(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("CASTING ").append(ChatColor.WHITE).append(this.getTeleportMessage(type)).append(" ... ").append(seconds).append(ChatColor.BOLD).append("s").toString());
                TeleportBooks.teleporting_loc.put(p.getName(), loc);
                TeleportBooks.casting_loc.put(p.getName(), p.getLocation());
                TeleportBooks.casting_time.put(p.getName(), seconds);
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (seconds + 3) * 20, 1));
                p.playSound(p.getLocation(), Sound.AMBIENCE_CAVE, 1.0f, 1.0f);
            }
        }
    }
    
    @EventHandler
    public void onCancelDamager(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            final Player p = (Player)e.getDamager();
            if (TeleportBooks.casting_time.containsKey(p.getName())) {
                TeleportBooks.casting_time.remove(p.getName());
                TeleportBooks.casting_loc.remove(p.getName());
                TeleportBooks.teleporting_loc.remove(p.getName());
                p.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
                p.removePotionEffect(PotionEffectType.CONFUSION);
            }
        }
    }
    
    @EventHandler
    public void onCancelDamage(final EntityDamageEvent e) {
        if (e.getDamage() <= 0.0) {
            return;
        }
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            if (TeleportBooks.casting_time.containsKey(p.getName())) {
                TeleportBooks.casting_time.remove(p.getName());
                TeleportBooks.casting_loc.remove(p.getName());
                TeleportBooks.teleporting_loc.remove(p.getName());
                p.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
                p.removePotionEffect(PotionEffectType.CONFUSION);
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        if (TeleportBooks.casting_time.containsKey(p.getName())) {
            TeleportBooks.casting_time.remove(p.getName());
            TeleportBooks.casting_loc.remove(p.getName());
            TeleportBooks.teleporting_loc.remove(p.getName());
        }
    }
    
    @EventHandler
    public void onCancelMove(final PlayerMoveEvent e) {
        final Player p = e.getPlayer();
        if (TeleportBooks.casting_time.containsKey(p.getName())) {
            final Location loc = TeleportBooks.casting_loc.get(p.getName());
            if (loc.distanceSquared(e.getTo()) >= 2.0) {
                TeleportBooks.casting_time.remove(p.getName());
                TeleportBooks.casting_loc.remove(p.getName());
                TeleportBooks.teleporting_loc.remove(p.getName());
                p.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
                p.removePotionEffect(PotionEffectType.CONFUSION);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItemEvent(final PlayerPickupItemEvent e) {
        final Player p = e.getPlayer();
        if (e.isCancelled()) {
            return;
        }
        if (e.getItem().getItemStack().getType() == Material.BOOK || e.getItem().getItemStack().getType() == Material.PAPER || (e.getItem().getItemStack().getType() == Material.INK_SACK && e.getItem().getItemStack().getDurability() == 0)) {
            e.setCancelled(true);
            if (p.getInventory().firstEmpty() != -1) {
                int amount = e.getItem().getItemStack().getAmount();
                final ItemStack scroll = (ItemStack)CraftItemStack.asCraftCopy(e.getItem().getItemStack());
                scroll.setAmount(1);
                while (amount > 0 && p.getInventory().firstEmpty() != -1) {
                    p.getInventory().setItem(p.getInventory().firstEmpty(), scroll);
                    p.updateInventory();
                    if (--amount > 0) {
                        final ItemStack new_stack = e.getItem().getItemStack();
                        new_stack.setAmount(amount);
                        e.getItem().setItemStack(new_stack);
                    }
                }
                if (amount <= 0) {
                    e.getItem().remove();
                }
                p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1.0f, 1.0f);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        if (e.getCursor() != null && e.getCursor().getType() == Material.BOOK && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BOOK) {
            e.setCancelled(true);
            p.updateInventory();
        }
        if (e.isShiftClick() && e.getCurrentItem() != null && (e.getCurrentItem().getType() == Material.BOOK || e.getCurrentItem().getType() == Material.PAPER || GemPouches.isGemPouch(e.getCurrentItem()))) {
            if (e.getInventory().getName().contains("Bank Chest")) {
                if (e.getRawSlot() < 63 && p.getInventory().firstEmpty() != -1) {
                    p.getInventory().setItem(p.getInventory().firstEmpty(), e.getCurrentItem());
                    e.setCurrentItem((ItemStack)null);
                }
                if (!GemPouches.isGemPouch(e.getCurrentItem()) && e.getRawSlot() > 53 && e.getInventory().firstEmpty() != -1) {
                    e.getInventory().setItem(e.getInventory().firstEmpty(), e.getCurrentItem());
                    e.setCurrentItem((ItemStack)null);
                }
            }
            e.setCancelled(true);
            p.updateInventory();
        }
        if (e.getClick() == ClickType.DOUBLE_CLICK && e.getCursor() != null && (e.getCursor().getType() == Material.BOOK || e.getCursor().getType() == Material.PAPER || GemPouches.isGemPouch(e.getCursor()))) {
            e.setCancelled(true);
            p.updateInventory();
        }
        if (e.getCursor() != null && e.getCursor().getType() == Material.PAPER && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.PAPER) {
            e.setCancelled(true);
            p.updateInventory();
        }
        if (e.getCursor() != null && GemPouches.isGemPouch(e.getCursor()) && e.getCurrentItem() != null && GemPouches.isGemPouch(e.getCurrentItem())) {
            e.setCancelled(true);
            p.updateInventory();
        }
    }
    
    Location getLocationFromString(String s) {
        s = s.toLowerCase();
        if (s.contains("cyrennica")) {
            return TeleportBooks.Cyrennica;
        }
        if (s.contains("harrison field")) {
            return TeleportBooks.Harrison_Field;
        }
        if (s.contains("dark oak tavern")) {
            return TeleportBooks.Dark_Oak_Tavern;
        }
        if (s.contains("deadpeaks mountain camp")) {
            return TeleportBooks.Deadpeaks_Mountain_Camp;
        }
        if (s.contains("trollsbane tavern")) {
            return TeleportBooks.Trollsbane_Tavern;
        }
        if (s.contains("tripoli")) {
            return TeleportBooks.Tripoli;
        }
        if (s.contains("gloomy hollows")) {
            return TeleportBooks.Gloomy_Hollows;
        }
        if (s.contains("crestguard keep")) {
            return TeleportBooks.Crestguard_Keep;
        }
        if (s.contains("crestwatch")) {
            return TeleportBooks.CrestWatch;
        }
        return TeleportBooks.Cyrennica;
    }
    
    String getTeleportMessage(String s) {
        s = s.toLowerCase();
        if (s.contains("cyrennica")) {
            return "Teleport Scroll: Cyrennica";
        }
        if (s.contains("harrison field")) {
            return "Teleport Scroll: Harrison's Field";
        }
        if (s.contains("dark oak tavern")) {
            return "Teleport Scroll: Dark Oak Tavern";
        }
        if (s.contains("deadpeaks mountain camp")) {
            return "Teleport Scroll: Deadpeaks Mountain Camp";
        }
        if (s.contains("trollsbane tavern")) {
            return "Teleport Scroll: Trollsbane Tavern";
        }
        if (s.contains("tripoli")) {
            return "Teleport Scroll: Tripoli";
        }
        if (s.contains("gloomy hollows")) {
            return "Teleport Scroll: Gloomy Hollows";
        }
        if (s.contains("crestguard keep")) {
            return "Teleport Scroll: Crestguard Keep";
        }
        if (s.contains("crestwatch")) {
            return "Teleport Scroll: CrestWatch";
        }
        return "Teleport Scroll: Cyrennica";
    }
    
    @EventHandler
    public void onAvalonTp(final PlayerMoveEvent e) {
        final Location to = e.getTo();
        final Location enter = new Location((World)Bukkit.getWorlds().get(0), -357.5, 171.0, -3440.5);
        final Location exit = new Location((World)Bukkit.getWorlds().get(0), -1158.5, 95.0, -515.5);
        if (to.getX() > -1155.0 && to.getX() < -1145.0 && to.getY() > 90.0 && to.getY() < 100.0 && to.getZ() < -500.0 && to.getZ() > -530.0) {
            e.getPlayer().teleport(enter.setDirection(to.getDirection()));
        }
        if (to.getX() < -360.0 && to.getX() > -370.0 && to.getY() > 165.0 && to.getY() < 190.0 && to.getZ() < -3426.0 && to.getZ() > -3455.0) {
            e.getPlayer().teleport(exit.setDirection(to.getDirection()));
        }
    }
    
    public static Location generateRandomSpawnPoint(final String s) {
        final ArrayList<Location> spawns = new ArrayList<Location>();
        if (Alignments.chaotic.containsKey(s)) {
            spawns.add(new Location((World)Bukkit.getWorlds().get(0), -382.0, 68.0, 867.0));
            spawns.add(new Location((World)Bukkit.getWorlds().get(0), -350.0, 67.0, 883.0));
            spawns.add(new Location((World)Bukkit.getWorlds().get(0), -330.0, 65.0, 898.0));
            spawns.add(new Location((World)Bukkit.getWorlds().get(0), -419.0, 61.0, 830.0));
            return spawns.get(new Random().nextInt(spawns.size()));
        }
        return TeleportBooks.Cyrennica;
    }
}
