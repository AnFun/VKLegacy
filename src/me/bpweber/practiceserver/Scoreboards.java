package me.bpweber.practiceserver;

import java.util.Iterator;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.entity.Player;
import java.util.HashMap;

public class Scoreboards
{
    public static HashMap<Player, Scoreboard> boards;
    
    static {
        Scoreboards.boards = new HashMap<Player, Scoreboard>();
    }
    
    public static Scoreboard getBoard(final Player p) {
        if (!Scoreboards.boards.containsKey(p)) {
            final Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
            final Team red = sb.registerNewTeam("red");
            red.setPrefix(ChatColor.RED.toString());
            final Team yellow = sb.registerNewTeam("yellow");
            yellow.setPrefix(ChatColor.YELLOW.toString());
            final Team white = sb.registerNewTeam("white");
            white.setPrefix(ChatColor.WHITE.toString());
            final Team gm = sb.registerNewTeam("gm");
            gm.setPrefix(String.valueOf(ChatColor.AQUA.toString()) + ChatColor.BOLD + "GM " + ChatColor.AQUA);
            final Team dev = sb.registerNewTeam("dev");
            dev.setPrefix(String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + "DEV " + ChatColor.DARK_AQUA);
            final Objective o = sb.registerNewObjective("showHealth", "health");
            o.setDisplaySlot(DisplaySlot.BELOW_NAME);
            o.setDisplayName(ChatColor.RED + "\u2764");
            Scoreboards.boards.put(p, sb);
            return sb;
        }
        return Scoreboards.boards.get(p);
    }
    
    public static void updateAllColors() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            final Scoreboard sb = getBoard(p);
            for (final Player pl : Bukkit.getOnlinePlayers()) {
                if (pl.isOp()) {
                    sb.getTeam("gm").addPlayer((OfflinePlayer)pl);
                }
                else if (Alignments.neutral.containsKey(pl.getName())) {
                    sb.getTeam("yellow").addPlayer((OfflinePlayer)pl);
                }
                else if (Alignments.chaotic.containsKey(pl.getName())) {
                    sb.getTeam("red").addPlayer((OfflinePlayer)pl);
                }
                else {
                    sb.getTeam("white").addPlayer((OfflinePlayer)pl);
                }
            }
            p.setScoreboard(sb);
            Scoreboards.boards.put(p, sb);
        }
    }
    
    public static void updatePlayerHealth() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            final Scoreboard sb = getBoard(p);
            for (final Player pl : Bukkit.getOnlinePlayers()) {
                final Objective o = sb.getObjective(DisplaySlot.BELOW_NAME);
                o.getScore((OfflinePlayer)pl).setScore((int)pl.getHealth());
            }
            p.setScoreboard(sb);
            Scoreboards.boards.put(p, sb);
        }
    }
}
