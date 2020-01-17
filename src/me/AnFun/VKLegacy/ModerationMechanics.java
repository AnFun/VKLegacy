package me.AnFun.VKLegacy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class ModerationMechanics implements CommandExecutor {
  public static ConcurrentHashMap<String, Integer> muted = new ConcurrentHashMap<>();
  
  public static ConcurrentHashMap<String, Integer> banned = new ConcurrentHashMap<>();
  
  static List<String> toggletrail = new ArrayList<>();
  
  static List<String> vanished = new ArrayList<>();
  
  static ConcurrentHashMap<String, String> ranks = new ConcurrentHashMap<>();
  
  public void onEnable() {
    Main.log.info("[ModerationMechanics] has been enabled.");
    (new BukkitRunnable() {
        public void run() {
          for (String s : ModerationMechanics.muted.keySet()) {
            if (s != null) {
              if (((Integer)ModerationMechanics.muted.get(s)).intValue() <= 0) {
                ModerationMechanics.muted.remove(s);
                if (Bukkit.getPlayer(s) != null) {
                  Player p = Bukkit.getPlayer(s);
                  if (p.isOnline())
                    p.sendMessage(ChatColor.GREEN + "Your " + ChatColor.BOLD + "GLOBAL MUTE" + 
                        ChatColor.GREEN + " has expired."); 
                } 
                continue;
              } 
              ModerationMechanics.muted.put(s, Integer.valueOf(((Integer)ModerationMechanics.muted.get(s)).intValue() - 1));
            } 
          } 
          for (String s : ModerationMechanics.banned.keySet()) {
            if (s != null) {
              if (((Integer)ModerationMechanics.banned.get(s)).intValue() < 0) {
                ModerationMechanics.banned.put(s, Integer.valueOf(-1));
                continue;
              } 
              if (((Integer)ModerationMechanics.banned.get(s)).intValue() == 0) {
                ModerationMechanics.banned.remove(s);
                continue;
              } 
              ModerationMechanics.banned.put(s, Integer.valueOf(((Integer)ModerationMechanics.banned.get(s)).intValue() - 1));
            } 
          } 
        }
      }).runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
    File file = new File(Main.plugin.getDataFolder(), "bans.yml");
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
    if (config.getConfigurationSection("banned") != null)
      for (String key : config.getConfigurationSection("banned").getKeys(false)) {
        int time = config.getConfigurationSection("banned").getInt(key);
        banned.put(key, Integer.valueOf(time));
      }  
    if (config.getConfigurationSection("muted") != null)
      for (String key : config.getConfigurationSection("muted").getKeys(false)) {
        int time = config.getConfigurationSection("muted").getInt(key);
        muted.put(key, Integer.valueOf(time));
      }  
    loadRanks();
  }
  
  public void onDisable() {
    Main.log.info("[ModerationMechanics] has been disabled.");
    File file = new File(Main.plugin.getDataFolder(), "bans.yml");
    YamlConfiguration config = new YamlConfiguration();
    for (String s : banned.keySet())
      config.set("banned." + s, banned.get(s)); 
    for (String s : muted.keySet())
      config.set("muted." + s, muted.get(s)); 
    try {
      config.save(file);
    } catch (Exception e) {
      e.printStackTrace();
    } 
    saveRanks();
  }
  
  static boolean isSub(Player p) {
    if (ranks.containsKey(p.getName())) {
      String rank = ranks.get(p.getName());
      if (rank.equalsIgnoreCase("sub") || rank.equalsIgnoreCase("sub+") || rank.equalsIgnoreCase("sub++"))
        return true; 
    } 
    return false;
  }
  
  void loadRanks() {
    File file = new File(Main.plugin.getDataFolder(), "ranks.yml");
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
    if (config.getConfigurationSection("ranks") != null)
      for (String key : config.getConfigurationSection("ranks").getKeys(false)) {
        String p = config.getConfigurationSection("ranks").getString(key);
        ranks.put(key, p);
      }  
  }
  
  void saveRanks() {
    File file = new File(Main.plugin.getDataFolder(), "ranks.yml");
    YamlConfiguration config = new YamlConfiguration();
    for (String s : ranks.keySet())
      config.set("ranks." + s, ranks.get(s)); 
    try {
      config.save(file);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player)sender;
      if (cmd.getName().equalsIgnoreCase("setrank") && 
        p.isOp())
        if (args.length == 2) {
          String player = args[0];
          String r = args[1].toLowerCase();
          if (r.equals("pmod") || r.equals("sub") || r.equals("sub+") || r.equals("sub++") || 
            r.equals("default")) {
            if (r.equals("default")) {
              if (ranks.containsKey(player))
                ranks.remove(player); 
            } else {
              ranks.put(player, r);
            } 
            if (Bukkit.getServer().getPlayer(player) != null) {
              p.sendMessage(ChatColor.GREEN + "You have set the user " + player + " to the rank of " + 
                  r + " on all Viking Legacy servers.");
              Alignments.updatePlayerAlignment(Bukkit.getServer().getPlayer(player));
            } 
          } else {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax");
            p.sendMessage(ChatColor.RED + "/setrank <PLAYER> <RANK>");
            p.sendMessage(
                ChatColor.RED + "Ranks: " + ChatColor.GRAY + "Default | SUB | SUB+ | SUB++ | PMOD");
          } 
        } else {
          p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax");
          p.sendMessage(ChatColor.RED + "/setrank <PLAYER> <RANK>");
          p.sendMessage(
              ChatColor.RED + "Ranks: " + ChatColor.GRAY + "Default | SUB | SUB+ | SUB++ | PMOD");
        }  
      if (cmd.getName().equalsIgnoreCase("psban")) {
        String rank = "";
        if (ranks.containsKey(p.getName()))
          rank = ranks.get(p.getName()); 
        if (rank.equals("pmod") || p.isOp())
          if (args.length == 2) {
            String player = args[0];
            try {
              int seconds = Integer.parseInt(args[1]) * 60 * 60;
              banned.put(player.toLowerCase(), Integer.valueOf(seconds));
              p.sendMessage(ChatColor.AQUA + "You have banned the user '" + args[0] + "' for " + 
                  Integer.parseInt(args[1]) + " hours.");
              if (Bukkit.getServer().getPlayer(player) != null && 
                Bukkit.getServer().getPlayer(player).isOnline())
                if (args[1] == "-1") {
                  if (Alignments.tagged.containsKey(Bukkit.getServer().getPlayer(player).getName()))
                    Alignments.tagged.remove(Bukkit.getServer().getPlayer(player).getName()); 
                  Bukkit.getServer().getPlayer(player)
                    .kickPlayer(ChatColor.RED + "Your account has been PERMANENTLY disabled." + 
                      "\n" + ChatColor.GRAY + 
                      "For further information about this suspension, please visit " + 
                      ChatColor.UNDERLINE + "https://twitter.com/VikingLegacyMC");
                } else {
                  if (Alignments.tagged.containsKey(Bukkit.getServer().getPlayer(player).getName()))
                    Alignments.tagged.remove(Bukkit.getServer().getPlayer(player).getName()); 
                  Bukkit.getServer().getPlayer(player)
                    .kickPlayer(ChatColor.RED + 
                      "Your account has been TEMPORARILY locked due to suspisious activity." + 
                      "\n" + ChatColor.GRAY + 
                      "For further information about this suspension, please visit " + 
                      ChatColor.UNDERLINE + "https://twitter.com/VikingLegacyMC");
                }  
            } catch (Exception e) {
              p.sendMessage(ChatColor.RED + "Invalid time entired for hours of duration for the ban.");
              p.sendMessage(
                  ChatColor.GRAY + "You entered: " + args[1] + ", which is not a numberic value.");
            } 
          } else {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + 
                "/psban <PLAYER> <TIME(in hours)> <REASON>");
            p.sendMessage(ChatColor.GRAY + "Insert -1 for <TIME> to permentantly lock.");
          }  
      } 
      if (cmd.getName().equalsIgnoreCase("psunban")) {
        String rank = "";
        if (ranks.containsKey(p.getName()))
          rank = ranks.get(p.getName()); 
        if (rank.equals("pmod") || p.isOp())
          if (args.length == 1) {
            if (banned.containsKey(args[0].toLowerCase())) {
              banned.remove(args[0].toLowerCase());
              p.sendMessage(ChatColor.RED + ChatColor.BOLD + "UNBANNED" + ChatColor.RED + " player " + 
                  args[0]);
            } 
          } else {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + 
                "/psunban <PLAYER> <REASON>");
          }  
      } 
      if (cmd.getName().equalsIgnoreCase("psmute")) {
        String rank = "";
        if (ranks.containsKey(p.getName()))
          rank = ranks.get(p.getName()); 
        if (rank.equals("pmod") || p.isOp())
          if (args.length == 2) {
            String player = args[0];
            if (muted.containsKey(player.toLowerCase())) {
              p.sendMessage(ChatColor.RED + "You cannot " + ChatColor.UNDERLINE + "overwrite" + 
                  ChatColor.RED + " a players mute.");
            } else {
              try {
                muted.put(player.toLowerCase(), Integer.valueOf(Integer.parseInt(args[1]) * 60));
                p.sendMessage(
                    ChatColor.AQUA + "You have issued a " + args[1] + " minute " + ChatColor.BOLD + 
                    "MUTE" + ChatColor.AQUA + " on the user " + ChatColor.BOLD + player);
                p.sendMessage(
                    ChatColor.GRAY + "If this was made in error, type '/psunmute " + player + "'");
                if (Bukkit.getServer().getPlayer(player) != null && 
                  Bukkit.getServer().getPlayer(player).isOnline()) {
                  Bukkit.getServer().getPlayer(player).sendMessage("");
                  Bukkit.getServer().getPlayer(player)
                    .sendMessage(ChatColor.RED + "You have been " + ChatColor.BOLD + 
                      "GLOBALLY MUTED" + ChatColor.RED + " by " + ChatColor.BOLD + 
                      p.getName() + ChatColor.RED + " for " + args[1] + " minute(s).");
                  Bukkit.getServer().getPlayer(player).sendMessage("");
                } 
              } catch (Exception e) {
                p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Non-Numeric Time. " + ChatColor.RED + 
                    "/psmute <PLAYER> <TIME(in minutes)>");
              } 
            } 
          } else {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + 
                "/psmute <PLAYER> <TIME(in minutes)>");
          }  
      } 
      if (cmd.getName().equalsIgnoreCase("psunmute")) {
        String rank = "";
        if (ranks.containsKey(p.getName()))
          rank = ranks.get(p.getName()); 
        if (rank.equals("pmod") || p.isOp())
          if (args.length == 1) {
            if (muted.containsKey(args[0].toLowerCase())) {
              muted.remove(args[0].toLowerCase());
              p.sendMessage(ChatColor.AQUA + "You have " + ChatColor.BOLD + "UNMUTED " + ChatColor.AQUA + 
                  args[0]);
              if (Bukkit.getPlayer(args[0]) != null) {
                Player m = Bukkit.getPlayer(args[0]);
                if (m.isOnline()) {
                  m.sendMessage("");
                  m.sendMessage(ChatColor.GREEN + "Your " + ChatColor.BOLD + "GLOBAL MUTE" + 
                      ChatColor.GREEN + " has been removed.");
                  m.sendMessage("");
                } 
              } 
            } 
          } else {
            p.sendMessage(ChatColor.RED + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + 
                "/psunmute <PLAYER>");
          }  
      } 
      if (cmd.getName().equalsIgnoreCase("banksee") && 
        p.isOp())
        if (args.length == 1) {
          File file = new File(Main.plugin.getDataFolder() + "/banks", String.valueOf(args[0]) + ".yml");
          if (file.exists()) {
            Banks.banksee.put(p, args[0]);
            Inventory inv = Banks.getBank(p);
            if (inv == null)
              inv = Bukkit.createInventory(null, 63, "Bank Chest (1/1)"); 
            p.openInventory(inv);
            p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
          } else {
            p.sendMessage(ChatColor.RED + args[0] + " does not have a bank!");
          } 
        } else {
          p.sendMessage(ChatColor.RED + "/banksee <player>");
        }  
      if (cmd.getName().equalsIgnoreCase("psvanish") && 
        p.isOp())
        if (vanished.contains(p.getName().toLowerCase())) {
          vanished.remove(p.getName().toLowerCase());
          for (Player pl : Bukkit.getOnlinePlayers()) {
            if (pl != p)
              pl.showPlayer(p); 
          } 
          p.sendMessage(ChatColor.RED + "You are now " + ChatColor.BOLD + "visible.");
        } else {
          vanished.add(p.getName().toLowerCase());
          for (Player pl : Bukkit.getOnlinePlayers()) {
            if (pl != p && 
              !pl.isOp())
              pl.hidePlayer(p); 
          } 
          p.sendMessage(ChatColor.GREEN + "You are now " + ChatColor.BOLD + "invisible.");
        }  
      if (cmd.getName().equalsIgnoreCase("toggletrail") && 
        isSub(p))
        if (toggletrail.contains(p.getName().toLowerCase())) {
          toggletrail.remove(p.getName().toLowerCase());
          p.sendMessage(ChatColor.GREEN + "Subscriber Trail - " + ChatColor.BOLD + "ENABLED");
        } else {
          toggletrail.add(p.getName().toLowerCase());
          p.sendMessage(ChatColor.RED + "Subscriber Trail - " + ChatColor.BOLD + "DISABLED");
        }  
    } 
    if (sender instanceof org.bukkit.command.ConsoleCommandSender && 
      cmd.getName().equalsIgnoreCase("setrank"))
      if (args.length == 2) {
        String player = args[0];
        String r = args[1].toLowerCase();
        if (r.equals("pmod") || r.equals("sub") || r.equals("sub+") || r.equals("sub++") || 
          r.equals("default")) {
          if (r.equals("default")) {
            if (ranks.containsKey(player))
              ranks.remove(player); 
          } else {
            ranks.put(player, r);
          } 
          if (Bukkit.getServer().getPlayer(player) != null)
            Alignments.updatePlayerAlignment(Bukkit.getServer().getPlayer(player)); 
        } else {
          sender.sendMessage(ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax");
          sender.sendMessage(ChatColor.RED + "/setrank <PLAYER> <RANK>");
          sender.sendMessage(
              ChatColor.RED + "Ranks: " + ChatColor.GRAY + "Default | SUB | SUB+ | SUB++ | PMOD");
        } 
      } else {
        sender.sendMessage(ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax");
        sender.sendMessage(ChatColor.RED + "/setrank <PLAYER> <RANK>");
        sender.sendMessage(
            ChatColor.RED + "Ranks: " + ChatColor.GRAY + "Default | SUB | SUB+ | SUB++ | PMOD");
      }  
    return true;
  }
}
