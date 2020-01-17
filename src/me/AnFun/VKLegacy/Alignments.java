package me.AnFun.VKLegacy;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.bukkit.WGBukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class Alignments implements Listener
{
    public static HashMap<String, Integer> neutral;
    public static HashMap<String, Integer> chaotic;
    public static HashMap<String, Long> tagged;
    
    static {
        Alignments.neutral = new HashMap<String, Integer>();
        Alignments.chaotic = new HashMap<String, Integer>();
        Alignments.tagged = new HashMap<String, Long>();
    }
    
    public void onEnable() {
        Main.log.info("[Alignments] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (p.isOnline()) {
                        if (Alignments.chaotic.containsKey(p.getName())) {
                            int time = Alignments.chaotic.get(p.getName());
                            if (time <= 1) {
                                Alignments.chaotic.remove(p.getName());
                                Alignments.neutral.put(p.getName(), 120);
                                Alignments.updatePlayerAlignment(p);
                                p.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" + ChatColor.YELLOW + " ALIGNMENT *");
                                p.sendMessage(ChatColor.GRAY + "While neutral, players who kill you will not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equiped armor on death. Neutral alignment will expire 2 minutes after last hit on player.");
                                p.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" + ChatColor.YELLOW + " ALIGNMENT *");
                            }
                            else {
                                --time;
                                Alignments.chaotic.put(p.getName(), time);
                            }
                        }
                        if (Alignments.neutral.containsKey(p.getName())) {
                            int time = Alignments.neutral.get(p.getName());
                            if (time == 1) {
                                Alignments.neutral.remove(p.getName());
                                Alignments.updatePlayerAlignment(p);
                                p.sendMessage(ChatColor.GREEN + "          * YOU ARE NOW " + ChatColor.BOLD + "LAWFUL" + ChatColor.GREEN + " ALIGNMENT *");
                                p.sendMessage(ChatColor.GRAY + "While lawful, you will not lose any equipped armor on death, instead, all armor will lose 30% of its durability when you die. Any players who kill you while you're lawfully aligned will become chaotic.");
                                p.sendMessage(ChatColor.GREEN + "          * YOU ARE NOW " + ChatColor.BOLD + "LAWFUL" + ChatColor.GREEN + " ALIGNMENT *");
                            }
                            else {
                                --time;
                                Alignments.neutral.put(p.getName(), time--);
                            }
                        }
                    }
                    if ((!Alignments.tagged.containsKey(p.getName()) || (Alignments.tagged.containsKey(p.getName()) && System.currentTimeMillis() - Alignments.tagged.get(p.getName()) > 10000L)) && p.getHealth() > 0.0) {
                        final PlayerInventory i = p.getInventory();
                        double amt = 5.0;
                        ItemStack[] armorContents;
                        for (int length = (armorContents = i.getArmorContents()).length, j = 0; j < length; ++j) {
                            final ItemStack is = armorContents[j];
                            if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                                final double added = Damage.getHps(is);
                                amt += added;
                            }
                        }
                        if (p.getHealth() + amt > p.getMaxHealth()) {
                            p.setHealth(p.getMaxHealth());
                        }
                        else {
                            p.setHealth(p.getHealth() + amt);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
        final File file = new File(Main.plugin.getDataFolder(), "alignments.yml");
        final YamlConfiguration config = new YamlConfiguration();
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            config.load(file);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        if (config.getConfigurationSection("chaotic") != null) {
            for (final String key : config.getConfigurationSection("chaotic").getKeys(false)) {
                final int time = config.getConfigurationSection("chaotic").getInt(key);
                Alignments.chaotic.put(key, time);
            }
        }
        if (config.getConfigurationSection("neutral") != null) {
            for (final String key : config.getConfigurationSection("neutral").getKeys(false)) {
                final int time = config.getConfigurationSection("neutral").getInt(key);
                Alignments.neutral.put(key, time);
            }
        }
    }
    
    public void onDisable() {
        Main.log.info("[Alignments] has been disabled.");
        final File file = new File(Main.plugin.getDataFolder(), "alignments.yml");
        final YamlConfiguration config = new YamlConfiguration();
        for (final String s : Alignments.chaotic.keySet()) {
            config.set("chaotic." + s, (Object)Alignments.chaotic.get(s));
        }
        for (final String s : Alignments.neutral.keySet()) {
            config.set("neutral." + s, (Object)Alignments.neutral.get(s));
        }
        try {
            config.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChaoticSpawn(final PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        if (Alignments.chaotic.containsKey(p.getName())) {
            p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter " + ChatColor.BOLD + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
            e.setRespawnLocation(TeleportBooks.generateRandomSpawnPoint(p.getName()));
        }
        else {
            e.setRespawnLocation(TeleportBooks.Cyrennica);
        }
    }
    
    @EventHandler
    public void onZoneMessage(final PlayerMoveEvent e) {
        final Player p = e.getPlayer();
        if (isSafeZone(e.getFrom()) && Alignments.chaotic.containsKey(p.getName())) {
            p.sendMessage(ChatColor.RED + "The guards have kicked you out of the " + ChatColor.UNDERLINE + "protected area" + ChatColor.RED + " due to your chaotic alignment.");
            p.teleport(TeleportBooks.generateRandomSpawnPoint(p.getName()));
            return;
        }
        if (isSafeZone(e.getTo())) {
            if (Alignments.chaotic.containsKey(p.getName())) {
                p.teleport(e.getFrom());
                p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter " + ChatColor.BOLD + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
                return;
            }
            if (Listeners.combat.containsKey(p.getName()) && System.currentTimeMillis() - Listeners.combat.get(p.getName()) <= 10000L) {
                p.teleport(e.getFrom());
                final long combattime = Listeners.combat.get(p.getName());
                final double left = (double)((System.currentTimeMillis() - combattime) / 1000L);
                final int time = (int)(10L - Math.round(left));
                p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " leave a chaotic zone while in combat.");
                p.sendMessage(ChatColor.GRAY + "Out of combat in: " + ChatColor.BOLD + time + "s");
                return;
            }
        }
        if (!isSafeZone(e.getFrom()) && isSafeZone(e.getTo())) {
            p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("          *** SAFE ZONE (DMG-OFF) ***").toString());
            p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 0.25f, 0.3f);
        }
        if (isSafeZone(e.getFrom()) && !isSafeZone(e.getTo())) {
            p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("          *** CHAOTIC ZONE (PVP-ON) ***").toString());
            p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 0.25f, 0.3f);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleportChaotic(final PlayerTeleportEvent e) {
        if (e.isCancelled()) {
            return;
        }
        final Player p = e.getPlayer();
        if (isSafeZone(e.getTo())) {
            if (Alignments.chaotic.containsKey(p.getName())) {
                p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter " + ChatColor.BOLD + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
                e.setCancelled(true);
                return;
            }
            if (Listeners.combat.containsKey(p.getName()) && System.currentTimeMillis() - Listeners.combat.get(p.getName()) <= 10000L) {
                final long combattime = Listeners.combat.get(p.getName());
                final double left = (double)((System.currentTimeMillis() - combattime) / 1000L);
                final int time = (int)(10L - Math.round(left));
                p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " leave a chaotic zone while in combat.");
                p.sendMessage(ChatColor.GRAY + "Out of combat in: " + ChatColor.BOLD + time + "s");
                e.setCancelled(true);
                return;
            }
        }
        if (!isSafeZone(e.getFrom()) && isSafeZone(e.getTo())) {
            p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("          *** SAFE ZONE (DMG-OFF) ***").toString());
            p.playSound(e.getTo(), Sound.WITHER_SHOOT, 0.25f, 0.3f);
        }
        if (isSafeZone(e.getFrom()) && !isSafeZone(e.getTo())) {
            p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("          *** CHAOTIC ZONE (PVP-ON) ***").toString());
            p.playSound(e.getTo(), Sound.WITHER_SHOOT, 0.25f, 0.3f);
        }
    }
    
    public static boolean isSafeZone(final Location loc) {
        final ApplicableRegionSet locset = WGBukkit.getRegionManager(loc.getWorld()).getApplicableRegions(loc);
        return locset.queryState(null, new StateFlag[] { DefaultFlag.PVP }) == StateFlag.State.DENY;
    }
    
    static void updatePlayerAlignment(final Player p) {
        ChatColor cc = ChatColor.GRAY;
        if (p.isOp()) {
            cc = ChatColor.AQUA;
        }
        else if (Alignments.neutral.containsKey(p.getName())) {
            cc = ChatColor.YELLOW;
        }
        else if (Alignments.chaotic.containsKey(p.getName())) {
            cc = ChatColor.RED;
        }
        else {
            cc = ChatColor.GRAY;
        }
        p.setDisplayName(String.valueOf(getPlayerPrefix(p)) + cc + p.getName());
        p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 10.0f, 1.0f);
        Scoreboards.updateAllColors();
    }
    
    static String getPlayerPrefix(final Player p) {
        String prefix = "";
        String rank = "";
        if (ModerationMechanics.ranks.containsKey(p.getName())) {
            rank = ModerationMechanics.ranks.get(p.getName());
        }
        if (rank.equals("sub")) {
            prefix = new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("S ").toString();
        }
        if (rank.equals("sub+")) {
            prefix = new StringBuilder().append(ChatColor.GOLD).append(ChatColor.BOLD).append("S+ ").toString();
        }
        if (rank.equals("sub++")) {
            prefix = new StringBuilder().append(ChatColor.DARK_AQUA).append(ChatColor.BOLD).append("S++ ").toString();
        }
        if (rank.equals("pmod")) {
            prefix = new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append("PMOD ").toString();
        }
        if (p.isOp()) {
            prefix = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.BOLD).append("GM ").toString();
        }
        return prefix;
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        updatePlayerAlignment(p);
        Scoreboards.updatePlayerHealth();
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onNeutral(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            final Player d = (Player)e.getDamager();
            if (e.getDamage() <= 0.0) {
                return;
            }
            if (!Alignments.chaotic.containsKey(d.getName())) {
                if (Alignments.neutral.containsKey(d.getName())) {
                    Alignments.neutral.put(d.getName(), 120);
                }
                else {
                    d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" + ChatColor.YELLOW + " ALIGNMENT *");
                    d.sendMessage(ChatColor.GRAY + "While neutral, players who kill you will\t not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equiped armor on death. Neutral alignment will expire 2 minutes after last hit on player.");
                    d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" + ChatColor.YELLOW + " ALIGNMENT *");
                    Alignments.neutral.put(d.getName(), 120);
                    updatePlayerAlignment(d);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onChaotic(final PlayerDeathEvent e) {
        final Player p = e.getEntity();
        if (!Damage.lastphit.containsKey(p)) {
            return;
        }
        if (Damage.lasthit.containsKey(p) && System.currentTimeMillis() - Damage.lasthit.get(p) > 8000L) {
            return;
        }
        final Player d = Damage.lastphit.get(p);
        if (!Alignments.neutral.containsKey(p.getName()) && !Alignments.chaotic.containsKey(p.getName())) {
            if (Alignments.chaotic.containsKey(d.getName())) {
                final int time = Alignments.chaotic.get(d.getName());
                Alignments.chaotic.put(d.getName(), time + 600);
                d.sendMessage("§cLAWFUL player slain, §l+600s §cadded to Chaotic timer");
                Alignments.neutral.remove(d.getName());
                updatePlayerAlignment(d);
            }
            else {
                d.sendMessage(ChatColor.RED + "          * YOU ARE NOW " + ChatColor.BOLD + "CHAOTIC" + ChatColor.RED + " ALIGNMENT *");
                d.sendMessage(ChatColor.GRAY + "While chaotic, you cannot enter any major cities or safe zones. If you are killed while chaotic, you will lose everything in your inventory. Chaotic alignment will expire 10 minutes after your last player kill.");
                d.sendMessage(ChatColor.RED + "          * YOU ARE NOW " + ChatColor.BOLD + "CHAOTIC" + ChatColor.RED + " ALIGNMENT *");
                d.sendMessage(ChatColor.RED + "LAWFUL player slain, " + ChatColor.BOLD + "+600s" + ChatColor.RED + " added to Chaotic timer.");
                Alignments.chaotic.put(d.getName(), 600);
                Alignments.neutral.remove(d.getName());
                updatePlayerAlignment(d);
            }
        }
        if (Alignments.neutral.containsKey(p.getName()) && !Alignments.chaotic.containsKey(p.getName()) && Alignments.chaotic.containsKey(d.getName())) {
            final int time = Alignments.chaotic.get(d.getName());
            Alignments.chaotic.put(d.getName(), time + 300);
            d.sendMessage(ChatColor.RED + "NEUTRAL player slain, " + ChatColor.BOLD + "+300s" + ChatColor.RED + " added to Chaotic timer.");
            Alignments.neutral.remove(d.getName());
            updatePlayerAlignment(d);
        }
        if (Alignments.chaotic.containsKey(p.getName()) && Alignments.chaotic.containsKey(d.getName())) {
            int time = Alignments.chaotic.get(d.getName());
            if (time <= 300) {
                Alignments.chaotic.remove(d.getName());
                Alignments.neutral.put(d.getName(), 120);
                updatePlayerAlignment(d);
                d.sendMessage("§cCHAOTIC player slain, §l-300s §ctaken to Chaotic timer");
                d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" + ChatColor.YELLOW + " ALIGNMENT *");
                d.sendMessage(ChatColor.GRAY + "While neutral, players who kill you will not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equiped armor on death. Neutral alignment will expire 2 minutes after last hit on player.");
                d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" + ChatColor.YELLOW + " ALIGNMENT *");
            }
            else {
                time -= 300;
                Alignments.chaotic.put(d.getName(), time);
                d.sendMessage(ChatColor.GREEN + "Chaotic player slain, " + ChatColor.BOLD + "-300s" + ChatColor.GREEN + " removed from Chatoic timer.");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathMessage(final PlayerDeathEvent e) {
        final Player p = e.getEntity();
        String reason = " has died";
        if (p.getLastDamageCause() != null && p.getLastDamageCause().getCause() != null) {
            if (p.getLastDamageCause().getCause().equals((Object)EntityDamageEvent.DamageCause.LAVA) || p.getLastDamageCause().getCause().equals((Object)EntityDamageEvent.DamageCause.FIRE) || p.getLastDamageCause().getCause().equals((Object)EntityDamageEvent.DamageCause.FIRE_TICK)) {
                reason = " burned to death";
            }
            if (p.getLastDamageCause().getCause().equals((Object)EntityDamageEvent.DamageCause.SUICIDE)) {
                reason = " ended their own life";
            }
            if (p.getLastDamageCause().getCause().equals((Object)EntityDamageEvent.DamageCause.FALL)) {
                reason = " fell to their death";
            }
            if (p.getLastDamageCause().getCause().equals((Object)EntityDamageEvent.DamageCause.SUFFOCATION)) {
                reason = " was crushed to death";
            }
            if (p.getLastDamageCause().getCause().equals((Object)EntityDamageEvent.DamageCause.DROWNING)) {
                reason = " drowned to death";
            }
            if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                final EntityDamageByEntityEvent et = (EntityDamageByEntityEvent)p.getLastDamageCause();
                if (et.getDamager() instanceof LivingEntity) {
                    if (et.getDamager() instanceof Player) {
                        final Player d = (Player)et.getDamager();
                        ItemStack wep = d.getItemInHand();
                        if (Staffs.staff.containsKey(d)) {
                            wep = Staffs.staff.get(d);
                        }
                        String wepname = "";
                        if (wep != null && wep.getType() != Material.AIR && wep.getItemMeta().hasDisplayName()) {
                            wepname = wep.getItemMeta().getDisplayName();
                        }
                        else {
                            wepname = String.valueOf(wep.getType().name().substring(0, 1).toUpperCase()) + wep.getType().name().substring(1, wep.getType().name().length()).toLowerCase();
                        }
                        reason = " was killed by " + ChatColor.RESET + d.getDisplayName() + ChatColor.WHITE + " with a(n) " + wepname;
                    }
                    else if (et.getDamager() instanceof LivingEntity) {
                        final LivingEntity l = (LivingEntity)et.getDamager();
                        String name = "";
                        if (l.hasMetadata("name")) {
                            name = l.getMetadata("name").get(0).asString();
                        }
                        reason = " was killed by a(n) " + ChatColor.UNDERLINE + name;
                    }
                }
            }
            if (Damage.lastphit.containsKey(p) && (!Damage.lasthit.containsKey(p) || System.currentTimeMillis() - Damage.lasthit.get(p) <= 8000L)) {
                final Player d2 = Damage.lastphit.get(p);
                ItemStack wep2 = d2.getItemInHand();
                if (Staffs.staff.containsKey(d2)) {
                    wep2 = Staffs.staff.get(d2);
                }
                String wepname2 = "";
                if (wep2 != null && wep2.getType() != Material.AIR && wep2.getItemMeta().hasDisplayName()) {
                    wepname2 = wep2.getItemMeta().getDisplayName();
                }
                else {
                    wepname2 = String.valueOf(wep2.getType().name().substring(0, 1).toUpperCase()) + wep2.getType().name().substring(1, wep2.getType().name().length()).toLowerCase();
                }
                reason = " was killed by " + ChatColor.RESET + d2.getDisplayName() + ChatColor.WHITE + " with a(n) " + wepname2;
            }
        }
        p.sendMessage(String.valueOf(p.getDisplayName()) + ChatColor.WHITE + reason);
        for (final Entity near : p.getNearbyEntities(50.0, 50.0, 50.0)) {
            if (near instanceof Player) {
                ((Player)near).sendMessage(String.valueOf(p.getDisplayName()) + ChatColor.WHITE + reason);
            }
        }
    }
}
