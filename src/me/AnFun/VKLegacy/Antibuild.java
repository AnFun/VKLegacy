package me.AnFun.VKLegacy;

import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Minecart;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Antibuild implements Listener
{
    public void onEnable() {
        Main.log.info("[Antibuild] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
    }
    
    public void onDisable() {
        Main.log.info("[Antibuild] has been disabled.");
    }
    
    @EventHandler
    public void onItemCraft(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        if (e.getInventory().getName().equalsIgnoreCase("container.crafting")) {
            if (e.getSlotType() == InventoryType.SlotType.CRAFTING) {
                e.setCancelled(true);
                p.updateInventory();
            }
        }
        else if (e.getClick() == ClickType.NUMBER_KEY) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase("container.crafting") && (e.getRawSlots().contains(1) || e.getRawSlots().contains(2) || e.getRawSlots().contains(3) || e.getRawSlots().contains(4))) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(final InventoryOpenEvent e) {
        if (e.getPlayer().isOp()) {
            return;
        }
        if (e.getInventory().getName().equalsIgnoreCase("container.dropper")) {
            e.setCancelled(true);
        }
        if (e.getInventory().getName().equalsIgnoreCase("container.dispenser")) {
            e.setCancelled(true);
        }
        if (e.getInventory().getName().equalsIgnoreCase("container.hopper")) {
            e.setCancelled(true);
        }
        if (e.getInventory().getName().equalsIgnoreCase("container.minecart")) {
            e.setCancelled(true);
        }
        if (e.getInventory().getName().equalsIgnoreCase("container.beacon")) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBreak(final BlockBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlace(final BlockPlaceEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockChange(final EntityChangeBlockEvent e) {
        if (e.getBlock().getType() == Material.SOIL) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFire(final PlayerInteractEvent e) {
        if (!e.getPlayer().isOp() && e.getAction() == Action.LEFT_CLICK_BLOCK) {
            final Block b = e.getClickedBlock();
            final BlockFace bf = e.getBlockFace();
            if (b.getRelative(bf).getType() == Material.FIRE) {
                e.setCancelled(true);
                e.getPlayer().sendBlockChange(b.getRelative(bf).getLocation(), Material.FIRE, (byte)0);
            }
        }
    }
    
    @EventHandler
    public void onAnvil(final PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && (e.getClickedBlock().getType() == Material.ANVIL || e.getClickedBlock().getType() == Material.WORKBENCH || e.getClickedBlock().getType() == Material.BED || e.getClickedBlock().getType() == Material.FURNACE || e.getClickedBlock().getType() == Material.BURNING_FURNACE || e.getClickedBlock().getType() == Material.DROPPER || e.getClickedBlock().getType() == Material.DISPENSER || e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST || e.getClickedBlock().getType() == Material.BREWING_STAND || e.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE || e.getClickedBlock().getType() == Material.DRAGON_EGG) && !e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
        if (e.getAction() == Action.LEFT_CLICK_BLOCK && !e.getPlayer().isOp() && !e.getClickedBlock().getType().name().contains("_ORE")) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onItemFrameClick(final PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof ItemFrame && !e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPaintingBreak(final HangingBreakByEntityEvent e) {
        if (e.getRemover() instanceof Player) {
            final Player p = (Player)e.getRemover();
            if (!p.isOp()) {
                e.setCancelled(true);
            }
        }
        else {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onItemFrameHit(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && (e.getEntity() instanceof ItemFrame || e.getEntity() instanceof Minecart || e.getEntity() instanceof Painting)) {
            if (e.getDamager() instanceof Player) {
                final Player p = (Player)e.getDamager();
                if (!p.isOp()) {
                    e.setCancelled(true);
                    e.setDamage(0.0);
                }
            }
            else {
                e.setCancelled(true);
                e.setDamage(0.0);
            }
        }
    }
    
    @EventHandler
    public void onBucketFill(final PlayerBucketFillEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBucketEmpty(final PlayerBucketEmptyEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
}
