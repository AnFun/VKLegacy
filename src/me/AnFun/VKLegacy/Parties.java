package me.AnFun.VKLegacy;

import java.util.Arrays;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.event.Listener;
import org.bukkit.command.CommandExecutor;

public class Parties implements CommandExecutor, Listener
{
    public static ConcurrentHashMap<Player, ArrayList<Player>> parties;
    static ConcurrentHashMap<Player, Player> invite;
    static ConcurrentHashMap<Player, Long> invitetime;
    
    static {
        Parties.parties = new ConcurrentHashMap<Player, ArrayList<Player>>();
        Parties.invite = new ConcurrentHashMap<Player, Player>();
        Parties.invitetime = new ConcurrentHashMap<Player, Long>();
    }
    
    public void onEnable() {
        Main.log.info("[Parties] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    Parties.refreshScoreboard(p);
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 1L, 1L);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Parties.invite.keySet()) {
                    if (Parties.invitetime.containsKey(p) && System.currentTimeMillis() - Parties.invitetime.get(p) > 30000L) {
                        if (p.isOnline()) {
                            p.sendMessage(ChatColor.RED + "Party invite from " + ChatColor.BOLD + Parties.invite.get(p).getName() + ChatColor.RED + " expired.");
                        }
                        if (p.isOnline()) {
                            Parties.invite.get(p).sendMessage(ChatColor.RED + "Party invite to " + ChatColor.BOLD + p.getName() + ChatColor.RED + " has expired.");
                        }
                        Parties.invite.remove(p);
                        Parties.invitetime.remove(p);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
    }
    
    public void onDisable() {
        Main.log.info("[Parties] has been disabled.");
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("pinvite")) {
                if (args.length == 1) {
                    final String player = args[0];
                    if (Bukkit.getPlayer(player) != null) {
                        inviteToParty(Bukkit.getPlayer(player), p);
                    }
                    else {
                        p.sendMessage(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD + player + ChatColor.RED + " is OFFLINE");
                    }
                }
                else {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Invalid Syntax. ").append(ChatColor.RED).append("/pinvite <player>").toString());
                    p.sendMessage(ChatColor.GRAY + "You can also " + ChatColor.UNDERLINE + "LEFT CLICK" + ChatColor.GRAY + " players with your " + ChatColor.ITALIC + "Character Journal" + ChatColor.GRAY + " to invite them.");
                }
            }
            if (cmd.getName().equalsIgnoreCase("paccept")) {
                if (args.length == 0) {
                    if (!Parties.invite.containsKey(p)) {
                        p.sendMessage(ChatColor.RED + "No pending party invites.");
                        return true;
                    }
                    final Player leader = getParty(Parties.invite.get(p));
                    if (Parties.invite.get(p) == null || leader == null) {
                        p.sendMessage(ChatColor.RED + "This party invite is no longer available.");
                        Parties.invite.remove(p);
                        Parties.invitetime.remove(p);
                        return true;
                    }
                    if (Parties.parties.get(leader).size() == 8) {
                        p.sendMessage(ChatColor.RED + "This party is currently full (8/8).");
                        Parties.invite.remove(p);
                        Parties.invitetime.remove(p);
                        return true;
                    }
                    final ArrayList<Player> mem = Parties.parties.get(leader);
                    for (final Player pl : mem) {
                        pl.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE.toString()) + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + p.getName() + ChatColor.GRAY.toString() + " has " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + "joined" + ChatColor.GRAY + " your party.");
                    }
                    addPlayer(p, leader);
                    p.sendMessage("");
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "You have joined " + ChatColor.BOLD + leader.getName() + "'s" + ChatColor.LIGHT_PURPLE + " party.");
                    p.sendMessage(ChatColor.GRAY + "To chat with your party, use " + ChatColor.BOLD + "/p" + ChatColor.GRAY + " OR " + ChatColor.BOLD + " /p <message>");
                    Parties.invite.remove(p);
                    Parties.invitetime.remove(p);
                    return true;
                }
                else {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Invalid Syntax. ").append(ChatColor.RED).append("/paccept").toString());
                }
            }
            if (cmd.getName().equalsIgnoreCase("pquit")) {
                if (args.length == 0) {
                    if (!isInParty(p)) {
                        p.sendMessage(ChatColor.RED + "You are not in a party.");
                        return true;
                    }
                    p.sendMessage(String.valueOf(ChatColor.RED.toString()) + "You have left the party.");
                    removePlayer(p);
                }
                else {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Invalid Syntax. ").append(ChatColor.RED).append("/pquit").toString());
                }
            }
            if (cmd.getName().equalsIgnoreCase("pkick")) {
                if (args.length == 1) {
                    final String player = args[0];
                    if (!isPartyLeader(p)) {
                        p.sendMessage(String.valueOf(ChatColor.RED.toString()) + "You are NOT the leader of your party.");
                        p.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + "Type " + ChatColor.BOLD.toString() + "/pquit" + ChatColor.GRAY + " to quit your current party.");
                        return true;
                    }
                    if (Bukkit.getPlayer(player) == null) {
                        p.sendMessage(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD + player + " is not in your party.");
                        return true;
                    }
                    if (getParty(Bukkit.getPlayer(player)) != p) {
                        p.sendMessage(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD + player + " is not in your party.");
                        return true;
                    }
                    Bukkit.getPlayer(player).sendMessage(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD.toString() + "You have been kicked out of the party.");
                    removePlayer(Bukkit.getPlayer(player));
                }
                else {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Invalid Syntax. ").append(ChatColor.RED).append("/pkick <player>").toString());
                }
            }
            if (cmd.getName().equalsIgnoreCase("p")) {
                if (!isInParty(p)) {
                    p.sendMessage(ChatColor.RED + "You are not in a party.");
                    return true;
                }
                if (args.length == 0) {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Invalid Syntax. ").append(ChatColor.RED).append("/p <MSG>").toString());
                    return true;
                }
                String msg = "";
                for (final String s : args) {
                    msg = String.valueOf(msg) + s + " ";
                }
                final ArrayList<Player> mem = Parties.parties.get(getParty(p));
                for (final Player pl : mem) {
                    pl.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE.toString()) + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + " " + p.getDisplayName() + ": " + ChatColor.GRAY + msg);
                }
            }
            if (cmd.getName().equalsIgnoreCase("pdecline")) {
                if (args.length != 0) {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Invalid Syntax. ").append(ChatColor.RED).append("/pdecline").toString());
                    return true;
                }
                if (!Parties.invite.containsKey(p)) {
                    p.sendMessage(ChatColor.RED + "No pending party invites.");
                    return true;
                }
                p.sendMessage(ChatColor.RED + "Declined " + ChatColor.BOLD + Parties.invite.get(p).getName() + "'s" + ChatColor.RED + " party invitation.");
                if (Parties.invite.get(p).isOnline()) {
                    Parties.invite.get(p).sendMessage(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD + p.getName() + ChatColor.RED.toString() + " has " + ChatColor.UNDERLINE + "DECLINED" + ChatColor.RED + " your party invitation.");
                }
                Parties.invite.remove(p);
                Parties.invitetime.remove(p);
            }
        }
        return true;
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        if (isInParty(p)) {
            removePlayer(p);
        }
    }
    
    public static boolean isPartyLeader(final Player p) {
        return Parties.parties.containsKey(p);
    }
    
    public static void refreshScoreboard(final Player p) {
        if (isInParty(p)) {
            final ArrayList<Player> mem = Parties.parties.get(getParty(p));
            final Scoreboard sb = Scoreboards.getBoard(p);
            if (sb.getObjective(DisplaySlot.SIDEBAR) != null) {
                final Objective o = sb.getObjective(DisplaySlot.SIDEBAR);
                for (final Player pl : mem) {
                    if (Parties.parties.containsKey(pl)) {
                        String name = ChatColor.BOLD + pl.getName();
                        if (name.length() > 16) {
                            name = name.substring(0, 16);
                        }
                        o.getScore(name).setScore((int)pl.getHealth());
                    }
                    else {
                        String name = pl.getName();
                        if (name.length() > 16) {
                            name = name.substring(0, 16);
                        }
                        o.getScore(name).setScore((int)pl.getHealth());
                    }
                }
                p.setScoreboard(sb);
                Scoreboards.boards.put(p, sb);
            }
            else {
                updateScoreboard(p);
            }
        }
    }
    
    public static void updateScoreboard(final Player p) {
        if (isInParty(p)) {
            final ArrayList<Player> mem = Parties.parties.get(getParty(p));
            final Scoreboard sb = Scoreboards.getBoard(p);
            if (sb.getObjective(DisplaySlot.SIDEBAR) != null) {
                sb.getObjective(DisplaySlot.SIDEBAR).unregister();
            }
            final Objective o = sb.registerNewObjective("party_data", "dummy");
            o.setDisplayName(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Party").toString());
            o.setDisplaySlot(DisplaySlot.SIDEBAR);
            for (final Player pl : mem) {
                if (Parties.parties.containsKey(pl)) {
                    String name = ChatColor.BOLD + pl.getName();
                    if (name.length() > 16) {
                        name = name.substring(0, 16);
                    }
                    o.getScore(name).setScore((int)pl.getHealth());
                }
                else {
                    String name = pl.getName();
                    if (name.length() > 16) {
                        name = name.substring(0, 16);
                    }
                    o.getScore(name).setScore((int)pl.getHealth());
                }
            }
            p.setScoreboard(sb);
            Scoreboards.boards.put(p, sb);
        }
        else {
            final Scoreboard sb2 = Scoreboards.getBoard(p);
            if (sb2.getObjective(DisplaySlot.SIDEBAR) != null) {
                sb2.getObjective(DisplaySlot.SIDEBAR).unregister();
            }
        }
    }
    
    public static void createParty(final Player p) {
        Parties.parties.put(p, new ArrayList<Player>(Arrays.asList(p)));
        updateScoreboard(p);
    }
    
    public static void addPlayer(final Player added, final Player leader) {
        if (Parties.parties.containsKey(leader)) {
            final ArrayList<Player> mem = Parties.parties.get(leader);
            if (!mem.contains(added)) {
                mem.add(added);
            }
            Parties.parties.put(leader, mem);
            for (final Player p : mem) {
                updateScoreboard(p);
            }
        }
    }
    
    public static void removePlayer(final Player p) {
        if (isInParty(p)) {
            if (isPartyLeader(p)) {
                if (Parties.parties.get(p).size() > 1) {
                    final ArrayList<Player> mem = Parties.parties.get(p);
                    mem.remove(p);
                    final Player newleader = mem.get(0);
                    Parties.parties.put(newleader, mem);
                    Parties.parties.remove(p);
                    newleader.sendMessage(ChatColor.RED + "You have been made the party leader!");
                    for (final Player pl : mem) {
                        pl.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE.toString()) + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + p.getName() + ChatColor.GRAY.toString() + " has " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + "left" + ChatColor.GRAY.toString() + " your party.");
                        pl.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE.toString()) + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + "> " + ChatColor.GRAY + ChatColor.LIGHT_PURPLE.toString() + newleader.getName() + ChatColor.GRAY.toString() + " has been promoted to " + ChatColor.UNDERLINE + "Party Leader");
                        updateScoreboard(pl);
                    }
                }
                else {
                    Parties.parties.remove(p);
                }
            }
            else {
                for (final Player key : Parties.parties.keySet()) {
                    if (Parties.parties.get(key).contains(p)) {
                        final ArrayList<Player> mem2 = Parties.parties.get(key);
                        mem2.remove(p);
                        Parties.parties.put(key, mem2);
                        for (final Player pl2 : mem2) {
                            pl2.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE.toString()) + "<" + ChatColor.BOLD + "P" + ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + p.getName() + ChatColor.GRAY.toString() + " has " + ChatColor.RED + ChatColor.UNDERLINE + "left" + ChatColor.GRAY.toString() + " your party.");
                            updateScoreboard(pl2);
                        }
                    }
                }
            }
            updateScoreboard(p);
        }
    }
    
    public static boolean isInParty(final Player p) {
        for (final Player key : Parties.parties.keySet()) {
            if (Parties.parties.get(key).contains(p)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean arePartyMembers(final Player p1, final Player p2) {
        for (final Player key : Parties.parties.keySet()) {
            if (Parties.parties.get(key).contains(p1) && Parties.parties.get(key).contains(p2)) {
                return true;
            }
        }
        return false;
    }
    
    public static Player getParty(final Player p) {
        if (Parties.parties.containsKey(p)) {
            return p;
        }
        if (isInParty(p)) {
            for (final Player key : Parties.parties.keySet()) {
                if (Parties.parties.get(key).contains(p)) {
                    return key;
                }
            }
        }
        return null;
    }
    
    public static void inviteToParty(final Player p, final Player owner) {
        if (p == owner) {
            p.sendMessage(ChatColor.RED + "You cannot invite yourself to your own party.");
            return;
        }
        if (!isPartyLeader(owner) && isInParty(owner)) {
            owner.sendMessage(String.valueOf(ChatColor.RED.toString()) + "You are NOT the leader of your party.");
            owner.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + "Type " + ChatColor.BOLD.toString() + "/pquit" + ChatColor.GRAY + " to quit your current party.");
            return;
        }
        if (isInParty(owner) && isPartyLeader(owner) && Parties.parties.get(owner).size() == 8) {
            owner.sendMessage(ChatColor.RED + "You cannot have more than " + ChatColor.ITALIC + "8 players" + ChatColor.RED + " in a party.");
            owner.sendMessage(ChatColor.GRAY + "You may use /pkick to kick out unwanted members.");
            return;
        }
        if (isInParty(p)) {
            if (getParty(p) == owner) {
                owner.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append(p.getName()).append(ChatColor.RED).append(" is already in your party.").toString());
                owner.sendMessage(ChatColor.GRAY + "Type /pkick " + p.getName() + " to kick them out.");
            }
            else {
                owner.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append(p.getName()).append(ChatColor.RED).append(" is already in another party.").toString());
            }
            return;
        }
        if (Parties.invite.containsKey(p)) {
            owner.sendMessage(ChatColor.RED + p.getName() + " has a pending party invite.");
            return;
        }
        if (!isInParty(owner)) {
            owner.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("Party created.").toString());
            owner.sendMessage(ChatColor.GRAY + "To invite more people to join your party, " + ChatColor.UNDERLINE + "Left Click" + ChatColor.GRAY.toString() + " them with your character journal or use " + ChatColor.BOLD + "/pinvite" + ChatColor.GRAY + ". To kick, use " + ChatColor.BOLD + "/pkick" + ChatColor.GRAY + ".");
            createParty(owner);
        }
        p.sendMessage(String.valueOf(ChatColor.LIGHT_PURPLE.toString()) + ChatColor.UNDERLINE + owner.getName() + ChatColor.GRAY + " has invited you to join their party. To accept, type " + ChatColor.LIGHT_PURPLE.toString() + "/paccept" + ChatColor.GRAY + " or to decline, type " + ChatColor.LIGHT_PURPLE.toString() + "/pdecline");
        owner.sendMessage(ChatColor.GRAY + "You have invited " + ChatColor.LIGHT_PURPLE.toString() + p.getName() + ChatColor.GRAY + " to join your party.");
        Parties.invite.put(p, owner);
        Parties.invitetime.put(p, System.currentTimeMillis());
    }
}
