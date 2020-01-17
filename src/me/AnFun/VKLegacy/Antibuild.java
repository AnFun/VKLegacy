package me.AnFun.VKLegacy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Antibuild implements Listener {
    public void onEnable() {
        Main.log.info("[Antibuild] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    }

    public void onDisable() {
        Main.log.info("[Antibuild] has been disabled.");
    }

    @EventHandler
    public void onItemCraft(InventoryClickEvent e) {
        Player p = (Player)e.getWhoClicked();
        if (e.getInventory().getName().equalsIgnoreCase("container.crafting")) {
            if (e.getSlotType() == InventoryType.SlotType.CRAFTING) {
                e.setCancelled(true);
                p.updateInventory();
            }
        } else if (e.getClick() == ClickType.NUMBER_KEY) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase("container.crafting") && (
                e.getRawSlots().contains(Integer.valueOf(1)) || e.getRawSlots().contains(Integer.valueOf(2)) || e.getRawSlots().contains(Integer.valueOf(3)) ||
                        e.getRawSlots().contains(Integer.valueOf(4))))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getPlayer().isOp())
            return;
        if (e.getInventory().getName().equalsIgnoreCase("container.dropper"))
            e.setCancelled(true);
        if (e.getInventory().getName().equalsIgnoreCase("container.dispenser"))
            e.setCancelled(true);
        if (e.getInventory().getName().equalsIgnoreCase("container.hopper"))
            e.setCancelled(true);
        if (e.getInventory().getName().equalsIgnoreCase("container.minecart"))
            e.setCancelled(true);
        if (e.getInventory().getName().equalsIgnoreCase("container.beacon"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!e.getPlayer().isOp())
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().isOp())
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent e) {
        if (e.getBlock().getType() == Material.SOIL)
            e.setCancelled(true);
    }

    @EventHandler
    public void onFire(PlayerInteractEvent e) {
        if (!e.getPlayer().isOp() &&
                e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block b = e.getClickedBlock();
            BlockFace bf = e.getBlockFace();
            if (b.getRelative(bf).getType() == Material.FIRE) {
                e.setCancelled(true);
                e.getPlayer().sendBlockChange(b.getRelative(bf).getLocation(), Material.FIRE, (byte)0);
            }
        }
    }

    @EventHandler
    public void onAnvil(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && (
                e.getClickedBlock().getType() == Material.ANVIL || e.getClickedBlock().getType() == Material.WORKBENCH ||
                        e.getClickedBlock().getType() == Material.BED ||
                        e.getClickedBlock().getType() == Material.FURNACE ||
                        e.getClickedBlock().getType() == Material.BURNING_FURNACE ||
                        e.getClickedBlock().getType() == Material.DROPPER ||
                        e.getClickedBlock().getType() == Material.DISPENSER ||
                        e.getClickedBlock().getType() == Material.CHEST ||
                        e.getClickedBlock().getType() == Material.TRAPPED_CHEST ||
                        e.getClickedBlock().getType() == Material.BREWING_STAND ||
                        e.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE ||
                        e.getClickedBlock().getType() == Material.DRAGON_EGG) &&
                !e.getPlayer().isOp())
            e.setCancelled(true);
        if (e.getAction() == Action.LEFT_CLICK_BLOCK &&
                !e.getPlayer().isOp() &&
                !e.getClickedBlock().getType().name().contains("_ORE"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onItemFrameClick(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof org.bukkit.entity.ItemFrame &&
                !e.getPlayer().isOp())
            e.setCancelled(true);
    }

    @EventHandler
    public void onPaintingBreak(HangingBreakByEntityEvent e) {
        if (e.getRemover() instanceof Player) {
            Player p = (Player)e.getRemover();
            if (!p.isOp())
                e.setCancelled(true);
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemFrameHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && (e.getEntity() instanceof org.bukkit.entity.ItemFrame || e.getEntity() instanceof org.bukkit.entity.Minecart ||
                e.getEntity() instanceof org.bukkit.entity.Painting))
            if (e.getDamager() instanceof Player) {
                Player p = (Player)e.getDamager();
                if (!p.isOp()) {
                    e.setCancelled(true);
                    e.setDamage(0.0D);
                }
            } else {
                e.setCancelled(true);
                e.setDamage(0.0D);
            }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        if (!e.getPlayer().isOp())
            e.setCancelled(true);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (!e.getPlayer().isOp())
            e.setCancelled(true);
    }
}
