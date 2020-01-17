package me.AnFun.VKLegacy;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.Sound;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Entity;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class Horses implements Listener
{
    static HashMap<String, Integer> mounting;
    static HashMap<String, Integer> horsetier;
    static HashMap<String, Location> mountingloc;
    static HashMap<String, ItemStack> buyingitem;
    static HashMap<String, Integer> buyingprice;
    
    static {
        Horses.mounting = new HashMap<String, Integer>();
        Horses.horsetier = new HashMap<String, Integer>();
        Horses.mountingloc = new HashMap<String, Location>();
        Horses.buyingitem = new HashMap<String, ItemStack>();
        Horses.buyingprice = new HashMap<String, Integer>();
    }
    
    public void onEnable() {
        Main.log.info("[Horses] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (p.isOnline() && Horses.mounting.containsKey(p.getName())) {
                        ParticleEffect.SPELL.display(0.0f, 0.0f, 0.0f, 0.5f, 80, p.getLocation().add(0.0, 0.15, 0.0), 20.0);
                        if (Horses.mounting.get(p.getName()) == 0) {
                            ParticleEffect.CRIT.display(0.0f, 0.0f, 0.0f, 0.5f, 80, p.getLocation().add(0.0, 1.0, 0.0), 20.0);
                            Horses.mounting.remove(p.getName());
                            Horses.mountingloc.remove(p.getName());
                            Horses.horse(p, Horses.horsetier.get(p.getName()));
                        }
                        else if (Horses.mounting.get(p.getName()) == 5) {
                            final String name = Horses.mount(Horses.horsetier.get(p.getName()), false).getItemMeta().getDisplayName();
                            p.sendMessage(ChatColor.BOLD + "SUMMONING " + name + ChatColor.WHITE + " ... " + Horses.mounting.get(p.getName()) + "s");
                            Horses.mounting.put(p.getName(), Horses.mounting.get(p.getName()) - 1);
                        }
                        else {
                            p.sendMessage(ChatColor.BOLD + "SUMMONING" + ChatColor.WHITE + " ... " + Horses.mounting.get(p.getName()) + "s");
                            Horses.mounting.put(p.getName(), Horses.mounting.get(p.getName()) - 1);
                        }
                    }
                }
            }
        }.runTaskTimer(Main.plugin, 20L, 20L);
    }
    
    public void onDisable() {
        Main.log.info("[Horses] has been disabled.");
    }
    
    public static ItemStack mount(final int tier, final boolean inshop) {
        final ItemStack is = new ItemStack(Material.SADDLE);
        final ItemMeta im = is.getItemMeta();
        String name = ChatColor.GREEN + "Old Horse Mount";
        String req = "";
        final List<String> lore = new ArrayList<String>();
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
        if (jump > 0) {
            lore.add(ChatColor.RED + "Jump: " + jump + "%");
        }
        if (req != "" && inshop) {
            lore.add(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("REQ: ").append(req).toString());
        }
        lore.add(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append(line).toString());
        lore.add(ChatColor.GRAY + "Permenant Untradeable");
        if (inshop) {
            lore.add(ChatColor.GREEN + "Price: " + ChatColor.WHITE + price + "g");
        }
        im.setLore((List)lore);
        is.setItemMeta(im);
        return is;
    }
    
    public static int getMountTier(final ItemStack is) {
        if (is != null && is.getType() == Material.SADDLE && is.getItemMeta().hasDisplayName()) {
            final String name = is.getItemMeta().getDisplayName();
            if (name.contains(ChatColor.GREEN.toString())) {
                return 2;
            }
            if (name.contains(ChatColor.AQUA.toString())) {
                return 3;
            }
            if (name.contains(ChatColor.LIGHT_PURPLE.toString())) {
                return 4;
            }
            if (name.contains(ChatColor.YELLOW.toString())) {
                return 5;
            }
        }
        return 0;
    }
    
    public static Horse horse(final Player p, final int tier) {
        double speed = 0.25;
        double jump = 0.75;
        if (tier == 3) {
            speed = 0.3;
            jump = 0.85;
        }
        if (tier == 4) {
            speed = 0.35;
            jump = 0.95;
        }
        if (tier == 5) {
            speed = 0.4;
            jump = 1.05;
        }
        final Horse h = (Horse)p.getWorld().spawnEntity(p.getLocation(), EntityType.HORSE);
        h.setVariant(Horse.Variant.HORSE);
        h.setAdult();
        h.setTamed(true);
        h.setOwner((AnimalTamer)p);
        h.setColor(Horse.Color.BROWN);
        h.setAgeLock(true);
        h.setStyle(Horse.Style.NONE);
        h.setDomestication(100);
        h.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        if (tier == 3) {
            h.getInventory().setArmor(new ItemStack(Material.IRON_BARDING));
        }
        if (tier == 4) {
            h.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING));
        }
        if (tier == 5) {
            h.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING));
        }
        if (ModerationMechanics.isSub(p)) {
            h.getInventory().setArmor((ItemStack)null);
            if (ModerationMechanics.ranks.get(p.getName()).equalsIgnoreCase("sub")) {
                h.setVariant(Horse.Variant.UNDEAD_HORSE);
            }
            if (ModerationMechanics.ranks.get(p.getName()).equalsIgnoreCase("sub+")) {
                h.setColor(Horse.Color.GRAY);
                h.setFireTicks(Integer.MAX_VALUE);
                h.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
            }
            if (ModerationMechanics.ranks.get(p.getName()).equalsIgnoreCase("sub++")) {
                h.setVariant(Horse.Variant.SKELETON_HORSE);
            }
        }
        h.setMaxHealth(20.0);
        h.setHealth(20.0);
        h.setJumpStrength(jump);
        ((CraftLivingEntity)h).getHandle().getAttributeInstance(GenericAttributes.d).setValue(speed);
        h.setPassenger((Entity)p);
        return h;
    }
    
    @EventHandler
    public void onAnimalTamerClick(final PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Player && e.getRightClicked().hasMetadata("NPC")) {
            final Player at = (Player)e.getRightClicked();
            final Player p = e.getPlayer();
            if (at.getName().equalsIgnoreCase("animal tamer")) {
                final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 9, "Animal Tamer");
                inv.addItem(new ItemStack[] { mount(2, true) });
                inv.addItem(new ItemStack[] { mount(3, true) });
                inv.addItem(new ItemStack[] { mount(4, true) });
                inv.addItem(new ItemStack[] { mount(5, true) });
                p.openInventory(inv);
                p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.0f, 1.0f);
            }
        }
    }
    
    @EventHandler
    public void onBuyHorse(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        if (p.getOpenInventory().getTopInventory().getTitle().contains("Animal Tamer")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.SADDLE && e.getCurrentItem().getItemMeta().hasLore()) {
                final List<String> lore = (List<String>)e.getCurrentItem().getItemMeta().getLore();
                if (lore.get(lore.size() - 1).contains("Price:")) {
                    final int price = ItemVendors.getPriceFromLore(e.getCurrentItem());
                    if (Money.hasEnoughGems(p, price)) {
                        int currtier = 0;
                        ItemStack[] contents;
                        for (int length = (contents = p.getInventory().getContents()).length, i = 0; i < length; ++i) {
                            final ItemStack is = contents[i];
                            if (getMountTier(is) > currtier) {
                                currtier = getMountTier(is);
                            }
                        }
                        final int newtier = getMountTier(e.getCurrentItem());
                        if (currtier == 0) {
                            currtier = 1;
                        }
                        if (newtier == currtier + 1) {
                            p.sendMessage(ChatColor.GRAY + "The '" + e.getCurrentItem().getItemMeta().getDisplayName() + ChatColor.GRAY + "' costs " + ChatColor.GREEN + ChatColor.BOLD + price + " GEM(s)" + ChatColor.GRAY + ".");
                            p.sendMessage(ChatColor.GRAY + "This item is non-refundable. type " + ChatColor.GREEN + ChatColor.BOLD + "Y" + ChatColor.GRAY + " to confirm.");
                            Horses.buyingitem.put(p.getName(), mount(newtier, false));
                            Horses.buyingprice.put(p.getName(), price);
                            p.closeInventory();
                        }
                    }
                    else {
                        p.sendMessage(ChatColor.RED + "You do not have enough gems to purchase this mount.");
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("COST: ").append(ChatColor.RED).append(price).append(ChatColor.BOLD).append("G").toString());
                        p.closeInventory();
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onInvClick(final InventoryClickEvent e) {
        if (e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().contains("Horse")) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(final EntityDamageEvent e) {
        if (e.getDamage() <= 0.0) {
            return;
        }
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL || e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
                if (p.isInsideVehicle() && p.getVehicle().getType() == EntityType.HORSE) {
                    e.setDamage(0.0);
                    e.setCancelled(true);
                }
            }
            else if (p.isInsideVehicle() && p.getVehicle().getType() == EntityType.HORSE) {
                p.getVehicle().remove();
                p.teleport(p.getVehicle().getLocation().add(0.0, 1.0, 0.0));
            }
        }
        if (e.getEntity() instanceof Horse) {
            final Horse h = (Horse)e.getEntity();
            if (e.getCause() != EntityDamageEvent.DamageCause.FALL && e.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION) {
                final Entity p2 = h.getPassenger();
                if (e instanceof EntityDamageByEntityEvent) {
                    final EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent)e;
                    if (evt.getDamager() instanceof Player && p2 instanceof Player) {
                        final Player d = (Player)evt.getDamager();
                        final ArrayList<String> toggles = Toggles.getToggles(d.getName());
                        final ArrayList<String> buddies = Buddies.getBuddies(d.getName());
                        if (buddies.contains(((Player)p2).getName().toLowerCase()) && !toggles.contains("ff")) {
                            e.setDamage(0.0);
                            e.setCancelled(true);
                            return;
                        }
                        if (toggles.contains("pvp")) {
                            e.setDamage(0.0);
                            e.setCancelled(true);
                            return;
                        }
                        if (!Alignments.neutral.containsKey(((Player)p2).getName()) && !Alignments.chaotic.containsKey(((Player)p2).getName()) && toggles.contains("chaos")) {
                            e.setDamage(0.0);
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                h.remove();
                if (p2 != null) {
                    p2.teleport(h.getLocation().add(0.0, 1.0, 0.0));
                }
            }
            e.setDamage(0.0);
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDamager(final EntityDamageByEntityEvent e) {
        if (e.getDamage() <= 0.0) {
            return;
        }
        if (e.getDamager() instanceof Player) {
            final Player p = (Player)e.getDamager();
            if (p.getVehicle() != null && p.getVehicle().getType() == EntityType.HORSE) {
                p.getVehicle().remove();
                p.teleport(p.getVehicle().getLocation().add(0.0, 1.0, 0.0));
            }
        }
    }
    
    @EventHandler
    public void onDismount(final VehicleExitEvent e) {
        if (e.getExited() instanceof Player && e.getVehicle() instanceof Horse) {
            e.getVehicle().remove();
        }
    }
    
    @EventHandler
    public void onMountSummon(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && p.getItemInHand() != null && getMountTier(p.getItemInHand()) > 0 && p.getVehicle() == null && !Horses.mounting.containsKey(p.getName())) {
            Horses.mounting.put(p.getName(), 5);
            Horses.mountingloc.put(p.getName(), p.getLocation());
            Horses.horsetier.put(p.getName(), getMountTier(p.getItemInHand()));
        }
    }
    
    @EventHandler
    public void onCancelDamager(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            final Player p = (Player)e.getDamager();
            if (Horses.mounting.containsKey(p.getName())) {
                Horses.mounting.remove(p.getName());
                Horses.mountingloc.remove(p.getName());
                p.sendMessage(ChatColor.RED + "Mount Summon - " + ChatColor.BOLD + "CANCELLED");
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
            if (Horses.mounting.containsKey(p.getName())) {
                Horses.mounting.remove(p.getName());
                Horses.mountingloc.remove(p.getName());
                p.sendMessage(ChatColor.RED + "Mount Summon - " + ChatColor.BOLD + "CANCELLED");
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        if (Horses.mounting.containsKey(p.getName())) {
            Horses.mounting.remove(p.getName());
            Horses.mountingloc.remove(p.getName());
        }
        if (p.getVehicle() != null && p.getVehicle().getType() == EntityType.HORSE) {
            p.getVehicle().remove();
            p.teleport(p.getVehicle().getLocation().add(0.0, 1.0, 0.0));
        }
    }
    
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent e) {
        final Player p = e.getPlayer();
        if (Horses.mounting.containsKey(p.getName())) {
            Horses.mounting.remove(p.getName());
            Horses.mountingloc.remove(p.getName());
        }
        p.eject();
    }
    
    @EventHandler
    public void onCancelMove(final PlayerMoveEvent e) {
        final Player p = e.getPlayer();
        if (Horses.mounting.containsKey(p.getName())) {
            final Location loc = Horses.mountingloc.get(p.getName());
            if (loc.distanceSquared(e.getTo()) >= 2.0) {
                Horses.mounting.remove(p.getName());
                p.sendMessage(ChatColor.RED + "Mount Summon - " + ChatColor.BOLD + "CANCELLED");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPromptChat(final AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (Horses.buyingitem.containsKey(p.getName()) && Horses.buyingprice.containsKey(p.getName())) {
            e.setCancelled(true);
            final int price = Horses.buyingprice.get(p.getName());
            final ItemStack is = Horses.buyingitem.get(p.getName());
            if (!e.getMessage().equalsIgnoreCase("y")) {
                p.sendMessage(ChatColor.RED + "Purchase - " + ChatColor.BOLD + "CANCELLED");
                Horses.buyingprice.remove(p.getName());
                Horses.buyingitem.remove(p.getName());
                return;
            }
            if (!Money.hasEnoughGems(p, price)) {
                p.sendMessage(ChatColor.RED + "You do not have enough gems to purchase this mount.");
                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("COST: ").append(ChatColor.RED).append(price).append(ChatColor.BOLD).append("G").toString());
                Horses.buyingprice.remove(p.getName());
                Horses.buyingitem.remove(p.getName());
                return;
            }
            if (p.getInventory().contains(Material.SADDLE)) {
                p.getInventory().remove(Material.SADDLE);
            }
            if (p.getInventory().firstEmpty() == -1) {
                p.sendMessage(ChatColor.RED + "No space available in inventory. Type 'cancel' or clear some room.");
                return;
            }
            Money.takeGems(p, price);
            p.getInventory().setItem(p.getInventory().firstEmpty(), is);
            p.sendMessage(ChatColor.RED + "-" + price + ChatColor.BOLD + "G");
            p.sendMessage(ChatColor.GREEN + "Transaction successful.");
            p.sendMessage(ChatColor.GRAY + "You are now the proud owner of a mount -- " + ChatColor.UNDERLINE + "to summon your new mount, simply right click with the saddle in your player's hand.");
            Horses.buyingprice.remove(p.getName());
            Horses.buyingitem.remove(p.getName());
        }
    }
}
