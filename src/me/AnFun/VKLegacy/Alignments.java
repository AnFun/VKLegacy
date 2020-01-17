package me.AnFun.VKLegacy;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class Alignments implements Listener {
    public static HashMap<String, Integer> neutral = new HashMap<>();

    public static HashMap<String, Integer> chaotic = new HashMap<>();

    public static HashMap<String, Long> tagged = new HashMap<>();

    public void onEnable() {
        Main.log.info("[Alignments] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
        (new BukkitRunnable() {
            public void run() {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (p.isOnline()) {
                        if (Alignments.chaotic.containsKey(p.getName())) {
                            int time = ((Integer)Alignments.chaotic.get(p.getName())).intValue();
                            if (time <= 1) {
                                Alignments.chaotic.remove(p.getName());
                                Alignments.neutral.put(p.getName(), Integer.valueOf(120));
                                Alignments.updatePlayerAlignment(p);
                                p.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" +
                                        ChatColor.YELLOW + " ALIGNMENT *");
                                p.sendMessage(ChatColor.GRAY +
                                        "While neutral, players who kill you will not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equiped armor on death. Neutral alignment will expire 2 minutes after last hit on player.");
                                p.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" +
                                        ChatColor.YELLOW + " ALIGNMENT *");
                            } else {
                                time--;
                                Alignments.chaotic.put(p.getName(), Integer.valueOf(time));
                            }
                        }
                        if (Alignments.neutral.containsKey(p.getName())) {
                            int time = ((Integer)Alignments.neutral.get(p.getName())).intValue();
                            if (time == 1) {
                                Alignments.neutral.remove(p.getName());
                                Alignments.updatePlayerAlignment(p);
                                p.sendMessage(ChatColor.GREEN + "          * YOU ARE NOW " + ChatColor.BOLD + "LAWFUL" +
                                        ChatColor.GREEN + " ALIGNMENT *");
                                p.sendMessage(ChatColor.GRAY +
                                        "While lawful, you will not lose any equipped armor on death, instead, all armor will lose 30% of its durability when you die. Any players who kill you while you're lawfully aligned will become chaotic.");
                                p.sendMessage(ChatColor.GREEN + "          * YOU ARE NOW " + ChatColor.BOLD + "LAWFUL" +
                                        ChatColor.GREEN + " ALIGNMENT *");
                            } else {
                                time--;
                                Alignments.neutral.put(p.getName(), Integer.valueOf(time--));
                            }
                        }
                    }
                    if ((!Alignments.tagged.containsKey(p.getName()) || (Alignments.tagged.containsKey(p.getName()) &&
                            System.currentTimeMillis() - ((Long)Alignments.tagged.get(p.getName())).longValue() > 10000L)) &&
                            p.getHealth() > 0.0D) {
                        PlayerInventory i = p.getInventory();
                        double amt = 5.0D;
                        byte b;
                        int j;
                        ItemStack[] arrayOfItemStack;
                        for (j = (arrayOfItemStack = i.getArmorContents()).length, b = 0; b < j; ) {
                            ItemStack is = arrayOfItemStack[b];
                            if (is != null && is.getType() != Material.AIR && is.hasItemMeta() &&
                                    is.getItemMeta().hasLore()) {
                                double added = Damage.getHps(is);
                                amt += added;
                            }
                            b++;
                        }
                        if (p.getHealth() + amt > p.getMaxHealth()) {
                            p.setHealth(p.getMaxHealth());
                            continue;
                        }
                        p.setHealth(p.getHealth() + amt);
                    }
                }
            }
        }).runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
        File file = new File(Main.plugin.getDataFolder(), "alignments.yml");
        YamlConfiguration config = new YamlConfiguration();
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (config.getConfigurationSection("chaotic") != null)
            for (String key : config.getConfigurationSection("chaotic").getKeys(false)) {
                int time = config.getConfigurationSection("chaotic").getInt(key);
                chaotic.put(key, Integer.valueOf(time));
            }
        if (config.getConfigurationSection("neutral") != null)
            for (String key : config.getConfigurationSection("neutral").getKeys(false)) {
                int time = config.getConfigurationSection("neutral").getInt(key);
                neutral.put(key, Integer.valueOf(time));
            }
    }

    public void onDisable() {
        Main.log.info("[Alignments] has been disabled.");
        File file = new File(Main.plugin.getDataFolder(), "alignments.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (String s : chaotic.keySet())
            config.set("chaotic." + s, chaotic.get(s));
        for (String s : neutral.keySet())
            config.set("neutral." + s, neutral.get(s));
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChaoticSpawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (chaotic.containsKey(p.getName())) {
            p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter " +
                    ChatColor.BOLD + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
            e.setRespawnLocation(TeleportBooks.generateRandomSpawnPoint(p.getName()));
        } else {
            e.setRespawnLocation(TeleportBooks.Cyrennica);
        }
    }

    @EventHandler
    public void onZoneMessage(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (isSafeZone(e.getFrom()) && chaotic.containsKey(p.getName())) {
            p.sendMessage(ChatColor.RED + "The guards have kicked you out of the " + ChatColor.UNDERLINE +
                    "protected area" + ChatColor.RED + " due to your chaotic alignment.");
            p.teleport(TeleportBooks.generateRandomSpawnPoint(p.getName()));
            return;
        }
        if (isSafeZone(e.getTo())) {
            if (chaotic.containsKey(p.getName())) {
                p.teleport(e.getFrom());
                p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter " +
                        ChatColor.BOLD + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
                return;
            }
            if (Listeners.combat.containsKey(p.getName()) &&
                    System.currentTimeMillis() - ((Long)Listeners.combat.get(p.getName())).longValue() <= 10000L) {
                p.teleport(e.getFrom());
                long combattime = ((Long)Listeners.combat.get(p.getName())).longValue();
                double left = ((System.currentTimeMillis() - combattime) / 1000L);
                int time = (int)(10L - Math.round(left));
                p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED +
                        " leave a chaotic zone while in combat.");
                p.sendMessage(ChatColor.GRAY + "Out of combat in: " + ChatColor.BOLD + time + "s");
                return;
            }
        }
        if (!isSafeZone(e.getFrom()) && isSafeZone(e.getTo())) {
            p.sendMessage(ChatColor.GREEN + ChatColor.BOLD + "          *** SAFE ZONE (DMG-OFF) ***");
            p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 0.25F, 0.3F);
        }
        if (isSafeZone(e.getFrom()) && !isSafeZone(e.getTo())) {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "          *** CHAOTIC ZONE (PVP-ON) ***");
            p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 0.25F, 0.3F);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleportChaotic(PlayerTeleportEvent e) {
        if (e.isCancelled())
            return;
        Player p = e.getPlayer();
        if (isSafeZone(e.getTo())) {
            if (chaotic.containsKey(p.getName())) {
                p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter " +
                        ChatColor.BOLD + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
                e.setCancelled(true);
                return;
            }
            if (Listeners.combat.containsKey(p.getName()) &&
                    System.currentTimeMillis() - ((Long)Listeners.combat.get(p.getName())).longValue() <= 10000L) {
                long combattime = ((Long)Listeners.combat.get(p.getName())).longValue();
                double left = ((System.currentTimeMillis() - combattime) / 1000L);
                int time = (int)(10L - Math.round(left));
                p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED +
                        " leave a chaotic zone while in combat.");
                p.sendMessage(ChatColor.GRAY + "Out of combat in: " + ChatColor.BOLD + time + "s");
                e.setCancelled(true);
                return;
            }
        }
        if (!isSafeZone(e.getFrom()) && isSafeZone(e.getTo())) {
            p.sendMessage(ChatColor.GREEN + ChatColor.BOLD + "          *** SAFE ZONE (DMG-OFF) ***");
            p.playSound(e.getTo(), Sound.WITHER_SHOOT, 0.25F, 0.3F);
        }
        if (isSafeZone(e.getFrom()) && !isSafeZone(e.getTo())) {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "          *** CHAOTIC ZONE (PVP-ON) ***");
            p.playSound(e.getTo(), Sound.WITHER_SHOOT, 0.25F, 0.3F);
        }
    }

    public static boolean isSafeZone(Location loc) {
        ApplicableRegionSet locset = WGBukkit.getRegionManager(loc.getWorld()).getApplicableRegions(loc);
        if (locset.queryState(null, new StateFlag[] { DefaultFlag.PVP }) == StateFlag.State.DENY)
            return true;
        return false;
    }

    static void updatePlayerAlignment(Player p) {
        ChatColor cc = ChatColor.GRAY;
        if (p.isOp()) {
            cc = ChatColor.AQUA;
        } else if (neutral.containsKey(p.getName())) {
            cc = ChatColor.YELLOW;
        } else if (chaotic.containsKey(p.getName())) {
            cc = ChatColor.RED;
        } else {
            cc = ChatColor.GRAY;
        }
        p.setDisplayName(String.valueOf(getPlayerPrefix(p)) + cc + p.getName());
        p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 10.0F, 1.0F);
        Scoreboards.updateAllColors();
    }

    static String getPlayerPrefix(Player p) {
        String prefix = "";
        String rank = "";
        if (ModerationMechanics.ranks.containsKey(p.getName()))
            rank = ModerationMechanics.ranks.get(p.getName());
        if (rank.equals("sub"))
            prefix = ChatColor.GREEN + ChatColor.BOLD + "S ";
        if (rank.equals("sub+"))
            prefix = ChatColor.GOLD + ChatColor.BOLD + "S+ ";
        if (rank.equals("sub++"))
            prefix = ChatColor.DARK_AQUA + ChatColor.BOLD + "S++ ";
        if (rank.equals("pmod"))
            prefix = ChatColor.WHITE + ChatColor.BOLD + "PMOD ";
        if (p.isOp())
            prefix = ChatColor.AQUA + ChatColor.BOLD + "GM ";
        return prefix;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        updatePlayerAlignment(p);
        Scoreboards.updatePlayerHealth();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onNeutral(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player d = (Player)e.getDamager();
            if (e.getDamage() <= 0.0D)
                return;
            if (!chaotic.containsKey(d.getName()))
                if (neutral.containsKey(d.getName())) {
                    neutral.put(d.getName(), Integer.valueOf(120));
                } else {
                    d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" +
                            ChatColor.YELLOW + " ALIGNMENT *");
                    d.sendMessage(ChatColor.GRAY +
                            "While neutral, players who kill you will\t not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equiped armor on death. Neutral alignment will expire 2 minutes after last hit on player.");
                    d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" +
                            ChatColor.YELLOW + " ALIGNMENT *");
                    neutral.put(d.getName(), Integer.valueOf(120));
                    updatePlayerAlignment(d);
                }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChaotic(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (!Damage.lastphit.containsKey(p))
            return;
        if (Damage.lasthit.containsKey(p))
            if (System.currentTimeMillis() - ((Long)Damage.lasthit.get(p)).longValue() > 8000L)
                return;
        Player d = Damage.lastphit.get(p);
        if (!neutral.containsKey(p.getName()) && !chaotic.containsKey(p.getName()))
            if (chaotic.containsKey(d.getName())) {
                int time = ((Integer)chaotic.get(d.getName())).intValue();
                chaotic.put(d.getName(), Integer.valueOf(time + 600));
                d.sendMessage("player slain, to Chaotic timer");
                neutral.remove(d.getName());
                updatePlayerAlignment(d);
            } else {
                d.sendMessage(ChatColor.RED + "          * YOU ARE NOW " + ChatColor.BOLD + "CHAOTIC" + ChatColor.RED +
                        " ALIGNMENT *");
                d.sendMessage(ChatColor.GRAY +
                        "While chaotic, you cannot enter any major cities or safe zones. If you are killed while chaotic, you will lose everything in your inventory. Chaotic alignment will expire 10 minutes after your last player kill.");
                d.sendMessage(ChatColor.RED + "          * YOU ARE NOW " + ChatColor.BOLD + "CHAOTIC" + ChatColor.RED +
                        " ALIGNMENT *");
                d.sendMessage(ChatColor.RED + "LAWFUL player slain, " + ChatColor.BOLD + "+600s" + ChatColor.RED +
                        " added to Chaotic timer.");
                chaotic.put(d.getName(), Integer.valueOf(600));
                neutral.remove(d.getName());
                updatePlayerAlignment(d);
            }
        if (neutral.containsKey(p.getName()) && !chaotic.containsKey(p.getName()) &&
                chaotic.containsKey(d.getName())) {
            int time = ((Integer)chaotic.get(d.getName())).intValue();
            chaotic.put(d.getName(), Integer.valueOf(time + 300));
            d.sendMessage(ChatColor.RED + "NEUTRAL player slain, " + ChatColor.BOLD + "+300s" + ChatColor.RED +
                    " added to Chaotic timer.");
            neutral.remove(d.getName());
            updatePlayerAlignment(d);
        }
        if (chaotic.containsKey(p.getName()) && chaotic.containsKey(d.getName())) {
            int time = ((Integer)chaotic.get(d.getName())).intValue();
            if (time <= 300) {
                chaotic.remove(d.getName());
                neutral.put(d.getName(), Integer.valueOf(120));
                updatePlayerAlignment(d);
                d.sendMessage("player slain, to Chaotic timer");
                d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" +
                        ChatColor.YELLOW + " ALIGNMENT *");
                d.sendMessage(ChatColor.GRAY +
                        "While neutral, players who kill you will not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equiped armor on death. Neutral alignment will expire 2 minutes after last hit on player.");
                d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL" +
                        ChatColor.YELLOW + " ALIGNMENT *");
            } else {
                time -= 300;
                chaotic.put(d.getName(), Integer.valueOf(time));
                d.sendMessage(ChatColor.GREEN + "Chaotic player slain, " + ChatColor.BOLD + "-300s" + ChatColor.GREEN +
                        " removed from Chatoic timer.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathMessage(PlayerDeathEvent e) {
        Player p = e.getEntity();
        String reason = " has died";
        if (p.getLastDamageCause() != null && p.getLastDamageCause().getCause() != null) {
            if (p.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.LAVA) ||
                    p.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FIRE) ||
                    p.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK))
                reason = " burned to death";
            if (p.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.SUICIDE))
                reason = " ended their own life";
            if (p.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FALL))
                reason = " fell to their death";
            if (p.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))
                reason = " was crushed to death";
            if (p.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.DROWNING))
                reason = " drowned to death";
            if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent et = (EntityDamageByEntityEvent)p.getLastDamageCause();
                if (et.getDamager() instanceof LivingEntity)
                    if (et.getDamager() instanceof Player) {
                        Player d = (Player)et.getDamager();
                        ItemStack wep = d.getItemInHand();
                        if (Staffs.staff.containsKey(d))
                            wep = Staffs.staff.get(d);
                        String wepname = "";
                        if (wep != null && wep.getType() != Material.AIR && wep.getItemMeta().hasDisplayName()) {
                            wepname = wep.getItemMeta().getDisplayName();
                        } else {
                            wepname = String.valueOf(wep.getType().name().substring(0, 1).toUpperCase()) +
                                    wep.getType().name().substring(1, wep.getType().name().length()).toLowerCase();
                        }
                        reason = " was killed by " + ChatColor.RESET + d.getDisplayName() + ChatColor.WHITE +
                                " with a(n) " + wepname;
                    } else if (et.getDamager() instanceof LivingEntity) {
                        LivingEntity l = (LivingEntity)et.getDamager();
                        String name = "";
                        if (l.hasMetadata("name"))
                            name = ((MetadataValue)l.getMetadata("name").get(0)).asString();
                        reason = " was killed by a(n) " + ChatColor.UNDERLINE + name;
                    }
            }
            if (Damage.lastphit.containsKey(p) && (
                    !Damage.lasthit.containsKey(p) ||
                            System.currentTimeMillis() - ((Long)Damage.lasthit.get(p)).longValue() <= 8000L)) {
                Player d = Damage.lastphit.get(p);
                ItemStack wep = d.getItemInHand();
                if (Staffs.staff.containsKey(d))
                    wep = Staffs.staff.get(d);
                String wepname = "";
                if (wep != null && wep.getType() != Material.AIR && wep.getItemMeta().hasDisplayName()) {
                    wepname = wep.getItemMeta().getDisplayName();
                } else {
                    wepname = String.valueOf(wep.getType().name().substring(0, 1).toUpperCase()) +
                            wep.getType().name().substring(1, wep.getType().name().length()).toLowerCase();
                }
                reason = " was killed by " + ChatColor.RESET + d.getDisplayName() + ChatColor.WHITE + " with a(n) " +
                        wepname;
            }
        }
        p.sendMessage(String.valueOf(p.getDisplayName()) + ChatColor.WHITE + reason);
        for (Entity near : p.getNearbyEntities(50.0D, 50.0D, 50.0D)) {
            if (near instanceof Player)
                ((Player)near).sendMessage(String.valueOf(p.getDisplayName()) + ChatColor.WHITE + reason);
        }
    }
}
