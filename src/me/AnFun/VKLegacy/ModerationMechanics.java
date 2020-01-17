package me.AnFun.VKLegacy;

import org.bukkit.inventory.Inventory;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.Sound;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.command.CommandExecutor;

public class ModerationMechanics implements CommandExecutor
{
    public static ConcurrentHashMap<String, Integer> muted;
    public static ConcurrentHashMap<String, Integer> banned;
    static List<String> toggletrail;
    static List<String> vanished;
    static ConcurrentHashMap<String, String> ranks;
    
    static {
        ModerationMechanics.muted = new ConcurrentHashMap<String, Integer>();
        ModerationMechanics.banned = new ConcurrentHashMap<String, Integer>();
        ModerationMechanics.toggletrail = new ArrayList<String>();
        ModerationMechanics.vanished = new ArrayList<String>();
        ModerationMechanics.ranks = new ConcurrentHashMap<String, String>();
    }
    
    public void onEnable() {
        Main.log.info("[ModerationMechanics] has been enabled.");
        new BukkitRunnable() {
            public void run() {
                for (final String s : ModerationMechanics.muted.keySet()) {
                    if (s != null) {
                        if (ModerationMechanics.muted.get(s) <= 0) {
                            ModerationMechanics.muted.remove(s);
                            if (Bukkit.getPlayer(s) == null) {
                                continue;
                            }
                            final Player p = Bukkit.getPlayer(s);
                            if (!p.isOnline()) {
                                continue;
                            }
                            p.sendMessage(ChatColor.GREEN + "Your " + ChatColor.BOLD + "GLOBAL MUTE" + ChatColor.GREEN + " has expired.");
                        }
                        else {
                            ModerationMechanics.muted.put(s, ModerationMechanics.muted.get(s) - 1);
                        }
                    }
                }
                for (final String s : ModerationMechanics.banned.keySet()) {
                    if (s != null) {
                        if (ModerationMechanics.banned.get(s) < 0) {
                            ModerationMechanics.banned.put(s, -1);
                        }
                        else if (ModerationMechanics.banned.get(s) == 0) {
                            ModerationMechanics.banned.remove(s);
                        }
                        else {
                            ModerationMechanics.banned.put(s, ModerationMechanics.banned.get(s) - 1);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
        final File file = new File(Main.plugin.getDataFolder(), "bans.yml");
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
        if (config.getConfigurationSection("banned") != null) {
            for (final String key : config.getConfigurationSection("banned").getKeys(false)) {
                final int time = config.getConfigurationSection("banned").getInt(key);
                ModerationMechanics.banned.put(key, time);
            }
        }
        if (config.getConfigurationSection("muted") != null) {
            for (final String key : config.getConfigurationSection("muted").getKeys(false)) {
                final int time = config.getConfigurationSection("muted").getInt(key);
                ModerationMechanics.muted.put(key, time);
            }
        }
        this.loadRanks();
    }
    
    public void onDisable() {
        Main.log.info("[ModerationMechanics] has been disabled.");
        final File file = new File(Main.plugin.getDataFolder(), "bans.yml");
        final YamlConfiguration config = new YamlConfiguration();
        for (final String s : ModerationMechanics.banned.keySet()) {
            config.set("banned." + s, (Object)ModerationMechanics.banned.get(s));
        }
        for (final String s : ModerationMechanics.muted.keySet()) {
            config.set("muted." + s, (Object)ModerationMechanics.muted.get(s));
        }
        try {
            config.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.saveRanks();
    }
    
    static boolean isSub(final Player p) {
        if (ModerationMechanics.ranks.containsKey(p.getName())) {
            final String rank = ModerationMechanics.ranks.get(p.getName());
            if (rank.equalsIgnoreCase("sub") || rank.equalsIgnoreCase("sub+") || rank.equalsIgnoreCase("sub++")) {
                return true;
            }
        }
        return false;
    }
    
    void loadRanks() {
        final File file = new File(Main.plugin.getDataFolder(), "ranks.yml");
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
        if (config.getConfigurationSection("ranks") != null) {
            for (final String key : config.getConfigurationSection("ranks").getKeys(false)) {
                final String p = config.getConfigurationSection("ranks").getString(key);
                ModerationMechanics.ranks.put(key, p);
            }
        }
    }
    
    void saveRanks() {
        final File file = new File(Main.plugin.getDataFolder(), "ranks.yml");
        final YamlConfiguration config = new YamlConfiguration();
        for (final String s : ModerationMechanics.ranks.keySet()) {
            config.set("ranks." + s, (Object)ModerationMechanics.ranks.get(s));
        }
        try {
            config.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("setrank") && p.isOp()) {
                if (args.length == 2) {
                    final String player = args[0];
                    final String r = args[1].toLowerCase();
                    if (r.equals("pmod") || r.equals("sub") || r.equals("sub+") || r.equals("sub++") || r.equals("default")) {
                        if (r.equals("default")) {
                            if (ModerationMechanics.ranks.containsKey(player)) {
                                ModerationMechanics.ranks.remove(player);
                            }
                        }
                        else {
                            ModerationMechanics.ranks.put(player, r);
                        }
                        if (Bukkit.getServer().getPlayer(player) != null) {
                            p.sendMessage(ChatColor.GREEN + "You have set the user " + player + " to the rank of " + r + " on all Viking Legacy servers.");
                            Alignments.updatePlayerAlignment(Bukkit.getServer().getPlayer(player));
                        }
                    }
                    else {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax").toString());
                        p.sendMessage(ChatColor.RED + "/setrank <PLAYER> <RANK>");
                        p.sendMessage(ChatColor.RED + "Ranks: " + ChatColor.GRAY + "Default | SUB | SUB+ | SUB++ | PMOD");
                    }
                }
                else {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax").toString());
                    p.sendMessage(ChatColor.RED + "/setrank <PLAYER> <RANK>");
                    p.sendMessage(ChatColor.RED + "Ranks: " + ChatColor.GRAY + "Default | SUB | SUB+ | SUB++ | PMOD");
                }
            }
            if (cmd.getName().equalsIgnoreCase("psban")) {
                String rank = "";
                if (ModerationMechanics.ranks.containsKey(p.getName())) {
                    rank = ModerationMechanics.ranks.get(p.getName());
                }
                if (rank.equals("pmod") || p.isOp()) {
                    if (args.length == 2) {
                        final String player2 = args[0];
                        try {
                            final int seconds = Integer.parseInt(args[1]) * 60 * 60;
                            ModerationMechanics.banned.put(player2.toLowerCase(), seconds);
                            p.sendMessage(ChatColor.AQUA + "You have banned the user '" + args[0] + "' for " + Integer.parseInt(args[1]) + " hours.");
                            if (Bukkit.getServer().getPlayer(player2) != null && Bukkit.getServer().getPlayer(player2).isOnline()) {
                                if (args[1] == "-1") {
                                    if (Alignments.tagged.containsKey(Bukkit.getServer().getPlayer(player2).getName())) {
                                        Alignments.tagged.remove(Bukkit.getServer().getPlayer(player2).getName());
                                    }
                                    Bukkit.getServer().getPlayer(player2).kickPlayer(ChatColor.RED + "Your account has been PERMANENTLY disabled." + "\n" + ChatColor.GRAY + "For further information about this suspension, please visit " + ChatColor.UNDERLINE + "https://twitter.com/VikingLegacyMC");
                                }
                                else {
                                    if (Alignments.tagged.containsKey(Bukkit.getServer().getPlayer(player2).getName())) {
                                        Alignments.tagged.remove(Bukkit.getServer().getPlayer(player2).getName());
                                    }
                                    Bukkit.getServer().getPlayer(player2).kickPlayer(ChatColor.RED + "Your account has been TEMPORARILY locked due to suspisious activity." + "\n" + ChatColor.GRAY + "For further information about this suspension, please visit " + ChatColor.UNDERLINE + "https://twitter.com/VikingLegacyMC");
                                }
                            }
                        }
                        catch (Exception e) {
                            p.sendMessage(ChatColor.RED + "Invalid time entired for hours of duration for the ban.");
                            p.sendMessage(ChatColor.GRAY + "You entered: " + args[1] + ", which is not a numberic value.");
                        }
                    }
                    else {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Invalid Syntax. ").append(ChatColor.RED).append("/psban <PLAYER> <TIME(in hours)> <REASON>").toString());
                        p.sendMessage(ChatColor.GRAY + "Insert -1 for <TIME> to permentantly lock.");
                    }
                }
            }
            if (cmd.getName().equalsIgnoreCase("psunban")) {
                String rank = "";
                if (ModerationMechanics.ranks.containsKey(p.getName())) {
                    rank = ModerationMechanics.ranks.get(p.getName());
                }
                if (rank.equals("pmod") || p.isOp()) {
                    if (args.length == 1) {
                        if (ModerationMechanics.banned.containsKey(args[0].toLowerCase())) {
                            ModerationMechanics.banned.remove(args[0].toLowerCase());
                            p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("UNBANNED").append(ChatColor.RED).append(" player ").append(args[0]).toString());
                        }
                    }
                    else {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Invalid Syntax. ").append(ChatColor.RED).append("/psunban <PLAYER> <REASON>").toString());
                    }
                }
            }
            if (cmd.getName().equalsIgnoreCase("psmute")) {
                String rank = "";
                if (ModerationMechanics.ranks.containsKey(p.getName())) {
                    rank = ModerationMechanics.ranks.get(p.getName());
                }
                if (rank.equals("pmod") || p.isOp()) {
                    if (args.length == 2) {
                        final String player2 = args[0];
                        if (ModerationMechanics.muted.containsKey(player2.toLowerCase())) {
                            p.sendMessage(ChatColor.RED + "You cannot " + ChatColor.UNDERLINE + "overwrite" + ChatColor.RED + " a players mute.");
                        }
                        else {
                            try {
                                ModerationMechanics.muted.put(player2.toLowerCase(), Integer.parseInt(args[1]) * 60);
                                p.sendMessage(ChatColor.AQUA + "You have issued a " + args[1] + " minute " + ChatColor.BOLD + "MUTE" + ChatColor.AQUA + " on the user " + ChatColor.BOLD + player2);
                                p.sendMessage(ChatColor.GRAY + "If this was made in error, type '/psunmute " + player2 + "'");
                                if (Bukkit.getServer().getPlayer(player2) != null && Bukkit.getServer().getPlayer(player2).isOnline()) {
                                    Bukkit.getServer().getPlayer(player2).sendMessage("");
                                    Bukkit.getServer().getPlayer(player2).sendMessage(ChatColor.RED + "You have been " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + " by " + ChatColor.BOLD + p.getName() + ChatColor.RED + " for " + args[1] + " minute(s).");
                                    Bukkit.getServer().getPlayer(player2).sendMessage("");
                                }
                            }
                            catch (Exception e) {
                                p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Non-Numeric Time. ").append(ChatColor.RED).append("/psmute <PLAYER> <TIME(in minutes)>").toString());
                            }
                        }
                    }
                    else {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Invalid Syntax. ").append(ChatColor.RED).append("/psmute <PLAYER> <TIME(in minutes)>").toString());
                    }
                }
            }
            if (cmd.getName().equalsIgnoreCase("psunmute")) {
                String rank = "";
                if (ModerationMechanics.ranks.containsKey(p.getName())) {
                    rank = ModerationMechanics.ranks.get(p.getName());
                }
                if (rank.equals("pmod") || p.isOp()) {
                    if (args.length == 1) {
                        if (ModerationMechanics.muted.containsKey(args[0].toLowerCase())) {
                            ModerationMechanics.muted.remove(args[0].toLowerCase());
                            p.sendMessage(ChatColor.AQUA + "You have " + ChatColor.BOLD + "UNMUTED " + ChatColor.AQUA + args[0]);
                            if (Bukkit.getPlayer(args[0]) != null) {
                                final Player m = Bukkit.getPlayer(args[0]);
                                if (m.isOnline()) {
                                    m.sendMessage("");
                                    m.sendMessage(ChatColor.GREEN + "Your " + ChatColor.BOLD + "GLOBAL MUTE" + ChatColor.GREEN + " has been removed.");
                                    m.sendMessage("");
                                }
                            }
                        }
                    }
                    else {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Invalid Syntax. ").append(ChatColor.RED).append("/psunmute <PLAYER>").toString());
                    }
                }
            }
            if (cmd.getName().equalsIgnoreCase("banksee") && p.isOp()) {
                if (args.length == 1) {
                    final File file = new File(Main.plugin.getDataFolder() + "/banks", String.valueOf(args[0]) + ".yml");
                    if (file.exists()) {
                        Banks.banksee.put(p, args[0]);
                        Inventory inv = Banks.getBank(p);
                        if (inv == null) {
                            inv = Bukkit.createInventory((InventoryHolder)null, 63, "Bank Chest (1/1)");
                        }
                        p.openInventory(inv);
                        p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1.0f, 1.0f);
                    }
                    else {
                        p.sendMessage(ChatColor.RED + args[0] + " does not have a bank!");
                    }
                }
                else {
                    p.sendMessage(ChatColor.RED + "/banksee <player>");
                }
            }
            if (cmd.getName().equalsIgnoreCase("psvanish") && p.isOp()) {
                if (ModerationMechanics.vanished.contains(p.getName().toLowerCase())) {
                    ModerationMechanics.vanished.remove(p.getName().toLowerCase());
                    for (final Player pl : Bukkit.getOnlinePlayers()) {
                        if (pl != p) {
                            pl.showPlayer(p);
                        }
                    }
                    p.sendMessage(ChatColor.RED + "You are now " + ChatColor.BOLD + "visible.");
                }
                else {
                    ModerationMechanics.vanished.add(p.getName().toLowerCase());
                    for (final Player pl : Bukkit.getOnlinePlayers()) {
                        if (pl != p && !pl.isOp()) {
                            pl.hidePlayer(p);
                        }
                    }
                    p.sendMessage(ChatColor.GREEN + "You are now " + ChatColor.BOLD + "invisible.");
                }
            }
            if (cmd.getName().equalsIgnoreCase("toggletrail") && isSub(p)) {
                if (ModerationMechanics.toggletrail.contains(p.getName().toLowerCase())) {
                    ModerationMechanics.toggletrail.remove(p.getName().toLowerCase());
                    p.sendMessage(ChatColor.GREEN + "Subscriber Trail - " + ChatColor.BOLD + "ENABLED");
                }
                else {
                    ModerationMechanics.toggletrail.add(p.getName().toLowerCase());
                    p.sendMessage(ChatColor.RED + "Subscriber Trail - " + ChatColor.BOLD + "DISABLED");
                }
            }
        }
        if (sender instanceof ConsoleCommandSender && cmd.getName().equalsIgnoreCase("setrank")) {
            if (args.length == 2) {
                final String player3 = args[0];
                final String r2 = args[1].toLowerCase();
                if (r2.equals("pmod") || r2.equals("sub") || r2.equals("sub+") || r2.equals("sub++") || r2.equals("default")) {
                    if (r2.equals("default")) {
                        if (ModerationMechanics.ranks.containsKey(player3)) {
                            ModerationMechanics.ranks.remove(player3);
                        }
                    }
                    else {
                        ModerationMechanics.ranks.put(player3, r2);
                    }
                    if (Bukkit.getServer().getPlayer(player3) != null) {
                        Alignments.updatePlayerAlignment(Bukkit.getServer().getPlayer(player3));
                    }
                }
                else {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax").toString());
                    sender.sendMessage(ChatColor.RED + "/setrank <PLAYER> <RANK>");
                    sender.sendMessage(ChatColor.RED + "Ranks: " + ChatColor.GRAY + "Default | SUB | SUB+ | SUB++ | PMOD");
                }
            }
            else {
                sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax").toString());
                sender.sendMessage(ChatColor.RED + "/setrank <PLAYER> <RANK>");
                sender.sendMessage(ChatColor.RED + "Ranks: " + ChatColor.GRAY + "Default | SUB | SUB+ | SUB++ | PMOD");
            }
        }
        return true;
    }
}
