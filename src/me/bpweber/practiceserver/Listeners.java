package me.bpweber.practiceserver;

import org.bukkit.inventory.meta.PotionMeta;
import java.util.ArrayList;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.Random;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.entity.MagmaCube;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.mcsg.double0negative.tabapi.TabAPI;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.server.ServerListPingEvent;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class Listeners implements Listener
{
    public static HashMap<UUID, Long> named;
    HashMap<String, Long> update;
    public static HashMap<String, Long> combat;
    public static HashMap<UUID, Long> mobd;
    HashMap<UUID, Long> firedmg;
    
    static {
        Listeners.named = new HashMap<UUID, Long>();
        Listeners.combat = new HashMap<String, Long>();
        Listeners.mobd = new HashMap<UUID, Long>();
    }
    
    public Listeners() {
        this.update = new HashMap<String, Long>();
        this.firedmg = new HashMap<UUID, Long>();
    }
    
    public void onEnable() {
        Main.log.info("[Listeners] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    Listeners.this.updateTabList(p);
                    if (Alignments.isSafeZone(p.getLocation())) {
                        p.setFoodLevel(20);
                        p.setSaturation(20.0f);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 200L, 100L);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (ModerationMechanics.isSub(p) && !ModerationMechanics.toggletrail.contains(p.getName().toLowerCase())) {
                        if (ModerationMechanics.ranks.get(p.getName()).equalsIgnoreCase("sub")) {
                            ParticleEffect.VILLAGER_HAPPY.display(0.125f, 0.125f, 0.125f, 0.02f, 10, p.getLocation().add(0.0, 0.1, 0.0), 20.0);
                        }
                        if (ModerationMechanics.ranks.get(p.getName()).equalsIgnoreCase("sub+")) {
                            ParticleEffect.FLAME.display(0.0f, 0.0f, 0.0f, 0.02f, 10, p.getLocation().add(0.0, 0.1, 0.0), 20.0);
                        }
                        if (!ModerationMechanics.ranks.get(p.getName()).equalsIgnoreCase("sub++")) {
                            continue;
                        }
                        ParticleEffect.SPELL_WITCH.display(0.0f, 0.0f, 0.0f, 1.0f, 10, p.getLocation().add(0.0, 0.25, 0.0), 20.0);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 1L, 1L);
    }
    
    public void onDisable() {
        Main.log.info("[Listeners] has been disabled.");
    }
    
    @EventHandler
    public void onMOTD(final ServerListPingEvent e) {
        String motd = new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("Viking Legacy").toString();
        for (int i = 0; i < 30; ++i) {
            motd = String.valueOf(motd) + " ";
        }
        motd = String.valueOf(motd) + ChatColor.GRAY + "Patch " + Main.plugin.getDescription().getVersion();
        e.setMotd(motd);
        e.setMaxPlayers(60);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void a(final ServerCommandEvent e) {
        if (e.getCommand().equalsIgnoreCase("save-all") || e.getCommand().equalsIgnoreCase("/save-all")) {
            e.setCommand("");
        }
    }
    
    @EventHandler
    public void onJoinBanned(final PlayerLoginEvent e) {
        final Player p = e.getPlayer();
        if (ModerationMechanics.banned.containsKey(p.getName().toLowerCase())) {
            if (ModerationMechanics.banned.get(p.getName().toLowerCase()) == -1) {
                e.setKickMessage(ChatColor.RED + "Your account has been PERMANENTLY disabled." + "\n" + ChatColor.GRAY + "For further information about this suspension, please visit " + ChatColor.UNDERLINE + "https://twitter.com/VikingLegacyMC");
            }
            else {
                e.setKickMessage(ChatColor.RED + "Your account has been TEMPORARILY locked due to suspisious activity." + "\n" + ChatColor.GRAY + "For further information about this suspension, please visit " + ChatColor.UNDERLINE + "https://twitter.com/VikingLegacyMC");
            }
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        if (Bukkit.getServer().getOnlinePlayers().size() >= 60) {
            if (!ModerationMechanics.isSub(p) && !p.isOp()) {
                e.setKickMessage(String.valueOf(ChatColor.RED.toString()) + "This Viking Legacy server is currently FULL." + "\n" + ChatColor.GRAY.toString() + "You can subscribe at http://vikinglegacy.buycraft.net/ to get instant access.");
                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                return;
            }
            e.allow();
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        p.setLevel(100);
        p.setExp(1.0f);
        p.setHealthScale(20.0);
        p.setHealthScaled(true);
        p.getInventory().setHeldItemSlot(0);
        if (p.getInventory().getItem(0) != null && (p.getInventory().getItem(0).getType().name().contains("_AXE") || p.getInventory().getItem(0).getType().name().contains("_SWORD") || p.getInventory().getItem(0).getType().name().contains("_HOE") || p.getInventory().getItem(0).getType().name().contains("_SPADE"))) {
            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.5f);
        }
        for (int i = 0; i < 20; ++i) {
            p.sendMessage(" ");
        }
        p.sendMessage(new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("          Viking Legacy Patch ").append(Main.plugin.getDescription().getVersion()).toString());
        p.sendMessage(ChatColor.GRAY + "                  https://twitter.com/VikingLegacyMC");
        p.sendMessage("");
        p.sendMessage(ChatColor.YELLOW + "                   You are on the " + ChatColor.BOLD + "US-1" + ChatColor.YELLOW + " shard.");
        p.sendMessage(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("       To manage your gameplay settings, use ").append(ChatColor.YELLOW).append(ChatColor.UNDERLINE).append("/toggles").toString());
        if (ModerationMechanics.isSub(p)) {
            p.sendMessage(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("       To toggle your subscriber trail, use ").append(ChatColor.YELLOW).append(ChatColor.UNDERLINE).append("/toggletrail").toString());
        }
        p.sendMessage("");
        e.setJoinMessage((String)null);
        if (!p.hasPlayedBefore()) {
            this.Kit(e.getPlayer());
            p.teleport(TeleportBooks.Cyrennica);
        }
        this.updateTabList(p);
        hpCheck(p);
        if (p.isOp()) {
            for (final Player pl : Bukkit.getOnlinePlayers()) {
                if (pl != p && !pl.isOp()) {
                    pl.hidePlayer(p);
                }
            }
            if (!ModerationMechanics.vanished.contains(p.getName().toLowerCase())) {
                ModerationMechanics.vanished.add(p.getName().toLowerCase());
            }
            p.sendMessage(new StringBuilder().append(ChatColor.AQUA).append(ChatColor.BOLD).append("               GM INVISIBILITY (infinite)").toString());
            p.sendMessage(ChatColor.GREEN + "                      You are now " + ChatColor.BOLD + "invisible.");
            p.setMaxHealth(9999.0);
            p.setHealth(9999.0);
        }
        else {
            for (final Player pl : Bukkit.getOnlinePlayers()) {
                if (pl != p && pl.isOp()) {
                    p.hidePlayer(pl);
                }
            }
        }
    }
    
    public void updateTabList(final Player p) {
        TabAPI.setTabString(Main.plugin, p, 0, 0, ChatColor.GRAY + "*-------------");
        TabAPI.setTabString(Main.plugin, p, 0, 1, new StringBuilder().append(ChatColor.DARK_AQUA).append(ChatColor.BOLD).append("    Guild UI").toString());
        TabAPI.setTabString(Main.plugin, p, 0, 2, ChatColor.GRAY + "-------------*");
        TabAPI.setTabString(Main.plugin, p, 2, 1, ChatColor.DARK_AQUA + "Guild Name");
        TabAPI.setTabString(Main.plugin, p, 3, 1, ChatColor.GRAY + "N/A");
        TabAPI.setTabString(Main.plugin, p, 18, 1, ChatColor.DARK_AQUA + "Shard " + ChatColor.GRAY.toString() + "US-1", 0);
        final int online = Bukkit.getOnlinePlayers().size();
        final int max = 60;
        TabAPI.setTabString(Main.plugin, p, 19, 1, String.valueOf(ChatColor.GRAY.toString()) + online + " / " + max, 0);
        TabAPI.updatePlayer(p);
    }
    
    @EventHandler
    public void onLeave(final PlayerKickEvent e) {
        e.setLeaveMessage((String)null);
    }
    
    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent e) {
        e.setQuitMessage((String)null);
    }
    
    @EventHandler
    public void onRespawn(final PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        Alignments.tagged.remove(p.getName());
        Listeners.combat.remove(p.getName());
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            final LivingEntity s = (LivingEntity)e.getEntity();
            if (e.getDamage() >= s.getHealth()) {
                if (Listeners.mobd.containsKey(s.getUniqueId())) {
                    Listeners.mobd.remove(s.getUniqueId());
                }
                if (this.firedmg.containsKey(s.getUniqueId())) {
                    this.firedmg.remove(s.getUniqueId());
                }
                if (Mobs.sound.containsKey(s.getUniqueId())) {
                    Mobs.sound.remove(s.getUniqueId());
                }
                if (Listeners.named.containsKey(s.getUniqueId())) {
                    Listeners.named.remove(s.getUniqueId());
                }
            }
        }
    }
    
    @EventHandler
    public void onHealthBar(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player) && e.getDamage() > 0.0) {
            final LivingEntity s = (LivingEntity)e.getEntity();
            final double max = s.getMaxHealth();
            final double hp = s.getHealth() - e.getDamage();
            s.setCustomName(Mobs.generateOverheadBar((Entity)s, hp, max, Mobs.getMobTier(s), Mobs.isElite(s)));
            s.setCustomNameVisible(true);
            Listeners.named.put(s.getUniqueId(), System.currentTimeMillis());
        }
    }
    
    @EventHandler
    public void onPotDrink(final PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player p = e.getPlayer();
            if (p.getItemInHand().getType() == Material.POTION && p.getItemInHand() != null) {
                e.setCancelled(true);
                if (p.getItemInHand().getItemMeta().hasLore()) {
                    String l = ChatColor.stripColor((String)p.getItemInHand().getItemMeta().getLore().get(0));
                    l = l.split("HP")[0];
                    int hp = 0;
                    try {
                        hp = Integer.parseInt(l.split(" ")[4]);
                    }
                    catch (Exception ex) {
                        hp = 0;
                    }
                    if (hp > 0) {
                        p.playSound(p.getLocation(), Sound.DRINK, 1.0f, 1.0f);
                        p.setItemInHand((ItemStack)null);
                        if (p.getHealth() + hp > p.getMaxHealth()) {
                            p.sendMessage("               " + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + hp + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + (int)p.getMaxHealth() + "/" + (int)p.getMaxHealth() + "HP]");
                            p.setHealth(p.getMaxHealth());
                        }
                        else {
                            p.sendMessage("               " + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + hp + ChatColor.BOLD + " HP" + ChatColor.GRAY + " [" + (int)(p.getHealth() + hp) + "/" + (int)p.getMaxHealth() + "HP]");
                            p.setHealth(p.getHealth() + hp);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBookOpen(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final PlayerInventory i = p.getInventory();
        if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.WRITTEN_BOOK && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            final BookMeta bm = (BookMeta)book.getItemMeta();
            String s = new StringBuilder().append(ChatColor.DARK_GREEN).append(ChatColor.UNDERLINE).append("Lawful").toString();
            String desc = new StringBuilder().append(ChatColor.BLACK).append(ChatColor.ITALIC).append("-30% Durability Arm/Wep on Death").toString();
            if (Alignments.chaotic.containsKey(p.getName())) {
                s = new StringBuilder().append(ChatColor.DARK_RED).append(ChatColor.UNDERLINE).append("Chaotic\n").append(ChatColor.BLACK).append(ChatColor.BOLD).append("Neutral").append(ChatColor.BLACK).append(" in ").append(Alignments.chaotic.get(p.getName())).append("s").toString();
                desc = new StringBuilder().append(ChatColor.BLACK).append(ChatColor.ITALIC).append("Inventory LOST on Death").toString();
            }
            if (Alignments.neutral.containsKey(p.getName())) {
                s = new StringBuilder().append(ChatColor.GOLD).append(ChatColor.UNDERLINE).append("Neutral\n").append(ChatColor.BLACK).append(ChatColor.BOLD).append("Lawful").append(ChatColor.BLACK).append(" in ").append(Alignments.neutral.get(p.getName())).append("s").toString();
                desc = new StringBuilder().append(ChatColor.BLACK).append(ChatColor.ITALIC).append("25%/50% Arm/Wep LOST on Death").toString();
            }
            int dps = 0;
            int arm = 0;
            int amt = 5;
            int nrg = 100;
            int block = 0;
            int dodge = 0;
            int intel = 0;
            int str = 0;
            int vit = 0;
            int sword_dmg = 0;
            int axe_dmg = 0;
            int block_pcnt = 0;
            int health_pcnt = 0;
            int nrg_pcnt = 0;
            int crit_pcnt = 0;
            ItemStack[] armorContents;
            for (int length = (armorContents = i.getArmorContents()).length, j = 0; j < length; ++j) {
                final ItemStack is = armorContents[j];
                if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                    final int adddps = Damage.getDps(is);
                    dps += adddps;
                    final int addarm = Damage.getArmor(is);
                    arm += addarm;
                    final int added = Damage.getHps(is);
                    amt += added;
                    final int addednrg = Damage.getEnergy(is);
                    nrg += addednrg;
                    final int addeddodge = Damage.getPercent(is, "DODGE");
                    dodge += addeddodge;
                    final int addedblock = Damage.getPercent(is, "BLOCK");
                    block += addedblock;
                    final int addedint = Damage.getElem(is, "INT");
                    intel += addedint;
                    final int addedstr = Damage.getElem(is, "STR");
                    str += addedstr;
                    final int addedvit = Damage.getElem(is, "VIT");
                    vit += addedvit;
                }
            }
            if (intel > 0) {
                nrg += Math.round((float)(intel / 125));
                nrg_pcnt = (int)Math.round(intel * 0.016);
                crit_pcnt = (int)Math.round(intel * 0.014);
            }
            if (vit > 0) {
                sword_dmg = Math.round((float)(vit / 50));
                health_pcnt = (int)Math.round(vit * 0.05);
            }
            if (str > 0) {
                axe_dmg = Math.round((float)(str / 38));
                block_pcnt = (int)Math.round(str * 0.015);
                block += (int)Math.round(str * 0.015);
            }
            bm.addPage(new String[] { new StringBuilder().append(ChatColor.UNDERLINE).append(ChatColor.BOLD).append("  Your Character  \n\n").append(ChatColor.RESET).append(ChatColor.BOLD).append("Alignment: ").append(s).append("\n").append(desc).append("\n\n").append(ChatColor.BLACK).append("  ").append((int)p.getHealth()).append(" / ").append((int)p.getMaxHealth()).append(ChatColor.BOLD).append(" HP\n").append(ChatColor.BLACK).append("  ").append(arm).append(" - ").append(arm).append("%").append(ChatColor.BOLD).append(" Armor\n").append(ChatColor.BLACK).append("  ").append(dps).append(" - ").append(dps).append("%").append(ChatColor.BOLD).append(" DPS\n").append(ChatColor.BLACK).append("  ").append(amt).append(ChatColor.BOLD).append(" HP/s\n").append(ChatColor.BLACK).append("  ").append(nrg).append("% ").append(ChatColor.BOLD).append("Energy\n").append(ChatColor.BLACK).append("  ").append(dodge).append("% ").append(ChatColor.BOLD).append("Dodge\n").append(ChatColor.BLACK).append("  ").append(block).append("% ").append(ChatColor.BOLD).append("Block").toString() });
            bm.addPage(new String[] { new StringBuilder().append(ChatColor.BLACK).append(ChatColor.BOLD).append("+ ").append(str).append(" Strength\n").append("  ").append(ChatColor.BLACK).append(ChatColor.UNDERLINE).append("'The Warrior'\n").append(ChatColor.BLACK).append("+").append(block_pcnt).append("% Block\n").append(ChatColor.BLACK).append("+").append(axe_dmg).append("% Axe DMG\n\n").append(ChatColor.BLACK).append(ChatColor.BOLD).append("+ ").append(vit).append(" Vitality\n").append("  ").append(ChatColor.BLACK).append(ChatColor.UNDERLINE).append("'The Defender'\n").append(ChatColor.BLACK).append("+").append(health_pcnt).append("% Health\n").append(ChatColor.BLACK).append("+").append(sword_dmg).append("% Sword DMG\n\n").append(ChatColor.BLACK).append(ChatColor.BOLD).append("+ ").append(intel).append(" Intellect\n").append("  ").append(ChatColor.BLACK).append(ChatColor.UNDERLINE).append("'The Mage'\n").append(ChatColor.BLACK).append("+").append(nrg_pcnt).append("% Energy\n").append(ChatColor.BLACK).append("+").append(crit_pcnt).append("% Critical Hit\n\n").toString() });
            bm.setDisplayName(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("Character Journal").toString());
            bm.setLore((List)Arrays.asList(ChatColor.GRAY + "A book that displays", ChatColor.GRAY + "your character's stats"));
            book.setItemMeta((ItemMeta)bm);
            p.setItemInHand(book);
            p.updateInventory();
            p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1.0f, 1.25f);
            if (!this.update.containsKey(p.getName()) || System.currentTimeMillis() - this.update.get(p.getName()) > 2000L) {
                p.closeInventory();
            }
            this.update.put(p.getName(), System.currentTimeMillis());
        }
    }
    
    @EventHandler
    public void onCloseChest(final InventoryCloseEvent e) {
        if (e.getInventory().getName().contains("Bank Chest") && e.getPlayer() instanceof Player) {
            final Player p = (Player)e.getPlayer();
            p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
        }
    }
    
    @EventHandler
    public void onArmourPutOn(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (p.getItemInHand() != null && (p.getItemInHand().getType().name().contains("HELMET") || p.getItemInHand().getType().name().contains("CHESTPLATE") || p.getItemInHand().getType().name().contains("LEGGINGS") || p.getItemInHand().getType().name().contains("BOOTS")) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            e.setCancelled(true);
            p.updateInventory();
        }
    }
    
    @EventHandler
    public void onToggleSprint(final PlayerToggleSprintEvent e) {
        if (Energy.getEnergy(e.getPlayer()) <= 0.0f) {
            e.setCancelled(true);
            e.getPlayer().setSprinting(false);
        }
    }
    
    @EventHandler
    public void onSprint(final PlayerMoveEvent e) {
        if (Energy.getEnergy(e.getPlayer()) <= 0.0f) {
            e.getPlayer().setSprinting(false);
        }
    }
    
    @EventHandler
    public void onCombatTag(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            if (e.getDamage() <= 0.0) {
                return;
            }
            final Player p = (Player)e.getDamager();
            Listeners.combat.put(p.getName(), System.currentTimeMillis());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNoAutoclick(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof LivingEntity && !(e.getDamager() instanceof Player)) {
            final LivingEntity s = (LivingEntity)e.getDamager();
            if (!Listeners.mobd.containsKey(s.getUniqueId()) || (Listeners.mobd.containsKey(s.getUniqueId()) && System.currentTimeMillis() - Listeners.mobd.get(s.getUniqueId()) > 1000L)) {
                Listeners.mobd.put(s.getUniqueId(), System.currentTimeMillis());
            }
            else if (!(e.getDamager() instanceof MagmaCube)) {
                e.setDamage(0.0);
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNoEnergyDamage(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {
            final Player d = (Player)e.getDamager();
            if (d.getExp() <= 0.0f) {
                e.setDamage(0.0);
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNoDamager(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof LivingEntity) {
            final LivingEntity p = (LivingEntity)e.getDamager();
            if (Alignments.isSafeZone(p.getLocation())) {
                e.setDamage(0.0);
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNoDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            final LivingEntity p = (LivingEntity)e.getEntity();
            if (Alignments.isSafeZone(p.getLocation())) {
                e.setDamage(0.0);
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onLoginShiny(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        ItemStack[] contents;
        for (int length = (contents = p.getInventory().getContents()).length, i = 0; i < length; ++i) {
            final ItemStack is = contents[i];
            if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && Enchants.getPlus(is) > 3) {
                is.addUnsafeEnchantment(Enchants.glow, 1);
            }
        }
        ItemStack[] armorContents;
        for (int length2 = (armorContents = p.getInventory().getArmorContents()).length, j = 0; j < length2; ++j) {
            final ItemStack is = armorContents[j];
            if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && Enchants.getPlus(is) > 3) {
                is.addUnsafeEnchantment(Enchants.glow, 1);
            }
        }
    }
    
    @EventHandler
    public void onOpenShinyShiny(final InventoryOpenEvent e) {
        if (e.getInventory().getName().contains("Bank Chest")) {
            ItemStack[] contents;
            for (int length = (contents = e.getInventory().getContents()).length, i = 0; i < length; ++i) {
                final ItemStack is = contents[i];
                if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && Enchants.getPlus(is) > 3) {
                    is.addUnsafeEnchantment(Enchants.glow, 1);
                }
            }
        }
    }
    
    @EventHandler
    public void onMapOpen(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && p.getItemInHand().getType() == Material.EMPTY_MAP) {
            e.setCancelled(true);
            p.updateInventory();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGemPickup(final PlayerPickupItemEvent e) {
        final Player p = e.getPlayer();
        if (!e.isCancelled() && e.getItem().getItemStack().getType() == Material.EMERALD) {
            p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("                    +").append(ChatColor.GREEN).append(e.getItem().getItemStack().getAmount()).append(ChatColor.GREEN).append(ChatColor.BOLD).append("G").toString());
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onDamagePercent(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            if (e.getDamage() <= 0.0) {
                return;
            }
            final LivingEntity p = (LivingEntity)e.getEntity();
            if (e.getCause().equals((Object)EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals((Object)EntityDamageEvent.DamageCause.LAVA) || e.getCause().equals((Object)EntityDamageEvent.DamageCause.FIRE_TICK)) {
                if (!this.firedmg.containsKey(p.getUniqueId()) || (this.firedmg.containsKey(p.getUniqueId()) && System.currentTimeMillis() - this.firedmg.get(p.getUniqueId()) > 500L)) {
                    this.firedmg.put(p.getUniqueId(), System.currentTimeMillis());
                    double multiplier = 0.01;
                    if (e.getCause().equals((Object)EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals((Object)EntityDamageEvent.DamageCause.LAVA)) {
                        multiplier = 0.03;
                    }
                    if (p.getMaxHealth() * multiplier < 1.0) {
                        e.setDamage(1.0);
                    }
                    else {
                        e.setDamage(p.getMaxHealth() * multiplier);
                    }
                }
                else {
                    e.setDamage(0.0);
                    e.setCancelled(true);
                }
            }
            else if (e.getCause().equals((Object)EntityDamageEvent.DamageCause.POISON)) {
                if (p.getMaxHealth() * 0.01 >= p.getHealth()) {
                    e.setDamage(p.getHealth() - 1.0);
                }
                else if (p.getMaxHealth() * 0.01 < 1.0) {
                    e.setDamage(1.0);
                }
                else {
                    e.setDamage(p.getMaxHealth() * 0.01);
                }
            }
            else if (e.getCause().equals((Object)EntityDamageEvent.DamageCause.DROWNING)) {
                if (p.getMaxHealth() * 0.04 < 1.0) {
                    e.setDamage(1.0);
                }
                else {
                    e.setDamage(p.getMaxHealth() * 0.04);
                }
            }
            else if (e.getCause().equals((Object)EntityDamageEvent.DamageCause.WITHER)) {
                e.setCancelled(true);
                e.setDamage(0.0);
                if (p.hasPotionEffect(PotionEffectType.WITHER)) {
                    p.removePotionEffect(PotionEffectType.WITHER);
                }
            }
            else if (e.getCause().equals((Object)EntityDamageEvent.DamageCause.SUFFOCATION)) {
                e.setDamage(0.0);
                e.setCancelled(true);
                final Location loc = p.getLocation();
                while ((loc.getBlock().getType() != Material.AIR || loc.add(0.0, 1.0, 0.0).getBlock().getType() != Material.AIR) && loc.getY() < 255.0) {
                    loc.add(0.0, 1.0, 0.0);
                }
                p.teleport(loc);
            }
            else if (e.getCause().equals((Object)EntityDamageEvent.DamageCause.VOID)) {
                e.setDamage(0.0);
                e.setCancelled(true);
                if (p instanceof Player) {
                    final Player pl = (Player)p;
                    if (Alignments.chaotic.containsKey(pl.getName())) {
                        p.teleport(TeleportBooks.generateRandomSpawnPoint(pl.getName()));
                    }
                    else {
                        p.teleport(TeleportBooks.Cyrennica);
                    }
                }
            }
            else if (e.getCause().equals((Object)EntityDamageEvent.DamageCause.FALL)) {
                if (e.getDamage() * p.getMaxHealth() * 0.02 >= p.getHealth()) {
                    e.setDamage(p.getHealth() - 1.0);
                }
                else if (e.getDamage() * p.getMaxHealth() * 0.02 < 1.0) {
                    e.setDamage(1.0);
                }
                else {
                    e.setDamage(e.getDamage() * p.getMaxHealth() * 0.02);
                }
            }
            else if (e.getCause().equals((Object)EntityDamageEvent.DamageCause.CONTACT)) {
                if (!this.firedmg.containsKey(p.getUniqueId()) || (this.firedmg.containsKey(p.getUniqueId()) && System.currentTimeMillis() - this.firedmg.get(p.getUniqueId()) > 500L)) {
                    this.firedmg.put(p.getUniqueId(), System.currentTimeMillis());
                    e.setDamage(1.0);
                }
                else {
                    e.setDamage(0.0);
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onHungerLoss(final FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            if (Alignments.isSafeZone(p.getLocation())) {
                p.setFoodLevel(20);
                p.setSaturation(20.0f);
                e.setCancelled(true);
            }
            else if (e.getFoodLevel() < p.getFoodLevel()) {
                final Random r = new Random();
                final int doitakefood = r.nextInt(5);
                if (doitakefood != 0) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player p = e.getEntity();
        p.playSound(p.getLocation(), Sound.WITHER_SPAWN, 1.0f, 1.0f);
        e.setDroppedExp(0);
        e.setDeathMessage((String)null);
        Alignments.tagged.remove(p.getName());
        Listeners.combat.remove(p.getName());
    }
    
    public static void hpCheck(final Player p) {
        if (p.isOp()) {
            return;
        }
        final PlayerInventory i = p.getInventory();
        double a = 50.0;
        double vital = 0.0;
        ItemStack[] armorContents;
        for (int length = (armorContents = i.getArmorContents()).length, j = 0; j < length; ++j) {
            final ItemStack is = armorContents[j];
            if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                final double health = Damage.getHp(is);
                final int vit = Damage.getElem(is, "VIT");
                a += health;
                vital += vit;
            }
        }
        if (vital > 0.0) {
            final double mod = vital * 0.05;
            a += a * (mod / 100.0);
            p.setMaxHealth((double)(int)a);
        }
        else {
            p.setMaxHealth(a);
        }
        p.setHealthScale(20.0);
        p.setHealthScaled(true);
    }
    
    boolean isArmor(final ItemStack is) {
        if (is != null) {
            if (is.getType().name().contains("_HELMET")) {
                return true;
            }
            if (is.getType().name().contains("_CHESTPLATE")) {
                return true;
            }
            if (is.getType().name().contains("_LEGGINGS")) {
                return true;
            }
            if (is.getType().name().contains("_BOOTS")) {
                return true;
            }
        }
        return false;
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        if (e.getSlotType() == InventoryType.SlotType.ARMOR && (e.isLeftClick() || e.isRightClick() || e.isShiftClick()) && ((this.isArmor(e.getCurrentItem()) && this.isArmor(e.getCursor())) || (this.isArmor(e.getCurrentItem()) && (e.getCursor() == null || e.getCursor().getType() == Material.AIR)) || ((e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) && this.isArmor(e.getCursor())))) {
            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
        }
        if (e.getInventory().getHolder() == p) {
            if (e.isShiftClick() && e.getCurrentItem().getType().name().contains("_HELMET") && p.getInventory().getHelmet() == null) {
                p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
            }
            if (e.isShiftClick() && e.getCurrentItem().getType().name().contains("_CHESTPLATE") && p.getInventory().getChestplate() == null) {
                p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
            }
            if (e.isShiftClick() && e.getCurrentItem().getType().name().contains("_LEGGINGS") && p.getInventory().getLeggings() == null) {
                p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
            }
            if (e.isShiftClick() && e.getCurrentItem().getType().name().contains("_BOOTS") && p.getInventory().getBoots() == null) {
                p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
            }
        }
        new BukkitRunnable() {
            public void run() {
                Listeners.hpCheck(p);
            }
        }.runTaskLater(Main.plugin, 1L);
    }
    
    @EventHandler
    public void onWeaponSwitch(final PlayerItemHeldEvent e) {
        final Player p = e.getPlayer();
        final ItemStack newItem = p.getInventory().getItem(e.getNewSlot());
        if (newItem != null && (newItem.getType().name().contains("_SWORD") || newItem.getType().name().contains("_AXE") || newItem.getType().name().contains("_HOE") || newItem.getType().name().contains("_SPADE"))) {
            p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.5f);
        }
    }
    
    @EventHandler
    public void onTag(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getDamage() <= 0.0) {
                return;
            }
            final Player p = (Player)e.getEntity();
            if (e.getCause() != EntityDamageEvent.DamageCause.FALL) {
                Alignments.tagged.put(p.getName(), System.currentTimeMillis());
            }
        }
    }
    
    @EventHandler
    public void onHitTag(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            if (e.getDamage() <= 0.0) {
                return;
            }
            final Player p = (Player)e.getDamager();
            Alignments.tagged.put(p.getName(), System.currentTimeMillis());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKickLog(final PlayerKickEvent e) {
        final Player p = e.getPlayer();
        if (!Alignments.isSafeZone(p.getLocation()) && Alignments.tagged.containsKey(p.getName()) && System.currentTimeMillis() - Alignments.tagged.get(p.getName()) < 10000L) {
            p.setHealth(0.0);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuitLog(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        if (!Alignments.isSafeZone(p.getLocation()) && Alignments.tagged.containsKey(p.getName()) && System.currentTimeMillis() - Alignments.tagged.get(p.getName()) < 10000L) {
            p.setHealth(0.0);
        }
    }
    
    @EventHandler
    public void onHealthRegen(final EntityRegainHealthEvent e) {
        e.setCancelled(true);
    }
    
    public void Kit(final Player p) {
        final Random random = new Random();
        final int wep = random.nextInt(2) + 1;
        if (wep == 1) {
            final ItemStack S = new ItemStack(Material.WOOD_SWORD);
            final ItemMeta smeta = S.getItemMeta();
            smeta.setDisplayName(ChatColor.WHITE + "Training Sword");
            final List<String> slore = new ArrayList<String>();
            slore.add(ChatColor.RED + "DMG: 3 - 4");
            slore.add(ChatColor.GRAY + "Untradeable");
            smeta.setLore((List)slore);
            S.setItemMeta(smeta);
            p.getInventory().addItem(new ItemStack[] { S });
        }
        if (wep == 2) {
            final ItemStack S = new ItemStack(Material.WOOD_AXE);
            final ItemMeta smeta = S.getItemMeta();
            smeta.setDisplayName(ChatColor.WHITE + "Training Hatchet");
            final List<String> slore = new ArrayList<String>();
            slore.add(ChatColor.RED + "DMG: 2 - 5");
            slore.add(ChatColor.GRAY + "Untradeable");
            smeta.setLore((List)slore);
            S.setItemMeta(smeta);
            p.getInventory().addItem(new ItemStack[] { S });
        }
        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)1);
        final ItemMeta potmeta = pot.getItemMeta();
        potmeta.setDisplayName(ChatColor.WHITE + "Minor Health Potion");
        potmeta.setLore((List)Arrays.asList(ChatColor.GRAY + "A potion that restores " + ChatColor.WHITE + "15HP", ChatColor.GRAY + "Untradeable"));
        final PotionMeta pm = (PotionMeta)potmeta;
        pm.clearCustomEffects();
        pm.addCustomEffect(PotionEffectType.HEAL.createEffect(0, 0), true);
        pot.setItemMeta((ItemMeta)pm);
        for (int t = 0; t < 3; ++t) {
            p.getInventory().addItem(new ItemStack[] { pot });
        }
        final ItemStack bread = new ItemStack(Material.BREAD);
        final ItemMeta breadmeta = bread.getItemMeta();
        breadmeta.setLore((List)Arrays.asList(ChatColor.GRAY + "Untradeable"));
        bread.setItemMeta(breadmeta);
        for (int t2 = 0; t2 < 2; ++t2) {
            p.getInventory().setItem(p.getInventory().firstEmpty(), bread);
        }
        p.getInventory().addItem(new ItemStack[] { Hearthstone.hearthstone() });
        p.getInventory().addItem(new ItemStack[] { Journal.journal() });
        p.setMaxHealth(50.0);
        p.setHealth(50.0);
        p.setHealthScale(20.0);
        p.setHealthScaled(true);
    }
}
