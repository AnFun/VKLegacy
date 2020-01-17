package me.AnFun.VKLegacy;

import net.minecraft.server.v1_7_R4.Packet;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.HashMap;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public class ChatMechanics implements Listener, CommandExecutor
{
    private static HashMap<Player, Player> reply;
    
    static {
        ChatMechanics.reply = new HashMap<Player, Player>();
    }
    
    public void onEnable() {
        Main.log.info("[ChatMechanics] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
    }
    
    public void onDisable() {
        Main.log.info("[ChatMechanics] has been disabled.");
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("gl")) {
                if (ModerationMechanics.muted.containsKey(p.getName().toLowerCase())) {
                    final int seconds = ModerationMechanics.muted.get(p.getName().toLowerCase());
                    int minutes = 0;
                    int hours = 0;
                    minutes = seconds / 60;
                    hours = minutes / 60;
                    if (hours >= 1) {
                        p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + ". You will be unmuted in " + hours + " hours(s).");
                        return false;
                    }
                    if (minutes >= 1) {
                        p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + ". You will be unmuted in " + minutes + " minute(s).");
                        return false;
                    }
                    p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + ". You will be unmuted in " + seconds + " seconds(s).");
                }
                else if (args.length == 0) {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax. You must supply a message!").append(ChatColor.RED).append(" /gl <MESSAGE>").toString());
                }
                else {
                    String message = "";
                    for (final String s : args) {
                        message = String.valueOf(message) + s + " ";
                    }
                    if (message.toLowerCase().startsWith("wtb") || message.toLowerCase().startsWith("wts") || message.toLowerCase().startsWith("wtt") || message.toLowerCase().startsWith("buying") || message.toLowerCase().startsWith("selling") || message.toLowerCase().startsWith("trading")) {
                        if (message.contains("@i@") && p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
                            for (final Player pl : Bukkit.getServer().getOnlinePlayers()) {
                                this.sendShowString(p, p.getItemInHand(), ChatColor.GREEN + "<" + ChatColor.BOLD + "T" + ChatColor.GREEN + "> " + ChatColor.RESET, message, pl);
                            }
                        }
                        else {
                            for (final Player pl : Bukkit.getServer().getOnlinePlayers()) {
                                pl.sendMessage(ChatColor.GREEN + "<" + ChatColor.BOLD + "T" + ChatColor.GREEN + "> " + ChatColor.RESET + p.getDisplayName() + ": " + ChatColor.WHITE + message);
                            }
                        }
                        Main.log.info(ChatColor.GREEN + "<" + ChatColor.BOLD + "T" + ChatColor.GREEN + "> " + ChatColor.RESET + p.getDisplayName() + ": " + ChatColor.WHITE + message);
                    }
                    else {
                        if (message.contains("@i@") && p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
                            for (final Player pl : Bukkit.getServer().getOnlinePlayers()) {
                                this.sendShowString(p, p.getItemInHand(), ChatColor.AQUA + "<" + ChatColor.BOLD + "G" + ChatColor.AQUA + "> " + ChatColor.RESET, message, pl);
                            }
                        }
                        else {
                            for (final Player pl : Bukkit.getServer().getOnlinePlayers()) {
                                pl.sendMessage(ChatColor.AQUA + "<" + ChatColor.BOLD + "G" + ChatColor.AQUA + "> " + ChatColor.RESET + p.getDisplayName() + ": " + ChatColor.WHITE + message);
                            }
                        }
                        Main.log.info(ChatColor.AQUA + "<" + ChatColor.BOLD + "G" + ChatColor.AQUA + "> " + ChatColor.RESET + p.getDisplayName() + ": " + ChatColor.WHITE + message);
                    }
                }
            }
            if (cmd.getName().equalsIgnoreCase("br") && p.isOp()) {
                if (args.length == 0) {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax. You must supply a message!").append(ChatColor.RED).append(" /br <MESSAGE>").toString());
                }
                else {
                    String message = "";
                    for (final String s : args) {
                        message = String.valueOf(message) + s + " ";
                    }
                    for (final Player pl : Bukkit.getServer().getOnlinePlayers()) {
                        pl.sendMessage(new StringBuilder().append(ChatColor.AQUA).append(ChatColor.BOLD).append(">> ").append(ChatColor.AQUA).append(message).toString());
                    }
                    Main.log.info(new StringBuilder().append(ChatColor.AQUA).append(ChatColor.BOLD).append(">> ").append(ChatColor.AQUA).append(message).toString());
                }
            }
            if (cmd.getName().equalsIgnoreCase("message")) {
                if (args.length == 1) {
                    final Player sendee = Bukkit.getServer().getPlayer(args[0]);
                    if (sendee == null || (sendee.isOp() && !p.isOp())) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append(args[0]).append(ChatColor.RED).append(" is OFFLINE.").toString());
                    }
                    else {
                        sendee.sendMessage(new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("FROM ").append(p.getDisplayName()).append(": ").append(ChatColor.WHITE).append("/").append(label).append(" ").append(args[0]).toString());
                        p.sendMessage(new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("TO ").append(sendee.getDisplayName()).append(": ").append(ChatColor.WHITE).append("/").append(label).append(" ").append(args[0]).toString());
                        sendee.playSound(sendee.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 1.0f);
                        ChatMechanics.reply.put(sendee, p);
                    }
                }
                else if (args.length >= 2) {
                    final Player sendee = Bukkit.getServer().getPlayer(args[0]);
                    if (sendee == null || (ModerationMechanics.vanished.contains(sendee.getName().toLowerCase()) && !p.isOp())) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append(args[0]).append(ChatColor.RED).append(" is OFFLINE.").toString());
                    }
                    else {
                        String message2 = "";
                        for (int i = 1; i < args.length; ++i) {
                            message2 = String.valueOf(message2) + args[i] + " ";
                        }
                        if (message2.contains("@i@") && p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
                            this.sendShowString(p, p.getItemInHand(), new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("FROM ").toString(), message2, sendee);
                            this.sendShowString(sendee, p.getItemInHand(), new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("TO ").toString(), message2, p);
                        }
                        else {
                            sendee.sendMessage(new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("FROM ").append(p.getDisplayName()).append(": ").append(ChatColor.WHITE).append(message2).toString());
                            p.sendMessage(new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("TO ").append(sendee.getDisplayName()).append(": ").append(ChatColor.WHITE).append(message2).toString());
                        }
                        sendee.playSound(sendee.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 1.0f);
                        ChatMechanics.reply.put(sendee, p);
                    }
                }
                else {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect syntax. ").append("/").append(label).append(" <PLAYER> <MESSAGE>").toString());
                }
            }
            if (cmd.getName().equalsIgnoreCase("reply")) {
                if (ChatMechanics.reply.containsKey(p)) {
                    final Player sendee = ChatMechanics.reply.get(p);
                    if (sendee == null || (ModerationMechanics.vanished.contains(sendee.getName().toLowerCase()) && !p.isOp())) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append(ChatMechanics.reply.get(p).getName()).append(ChatColor.RED).append(" is OFFLINE.").toString());
                    }
                    else if (args.length == 0) {
                        sendee.sendMessage(new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("FROM ").append(p.getDisplayName()).append(": ").append(ChatColor.WHITE).append("/").append(label).append(" ").append(sendee.getName()).toString());
                        p.sendMessage(new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("TO ").append(sendee.getDisplayName()).append(": ").append(ChatColor.WHITE).append("/").append(label).append(" ").append(sendee.getName()).toString());
                        sendee.playSound(sendee.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 1.0f);
                        ChatMechanics.reply.put(sendee, p);
                    }
                    else if (args.length >= 1) {
                        String message2 = "";
                        for (final String s2 : args) {
                            message2 = String.valueOf(message2) + s2 + " ";
                        }
                        if (message2.contains("@i@") && p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
                            this.sendShowString(p, p.getItemInHand(), new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("FROM ").toString(), message2, sendee);
                            this.sendShowString(sendee, p.getItemInHand(), new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("TO ").toString(), message2, p);
                        }
                        else {
                            sendee.sendMessage(new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("FROM ").append(p.getDisplayName()).append(": ").append(ChatColor.WHITE).append(message2).toString());
                            p.sendMessage(new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("TO ").append(sendee.getDisplayName()).append(": ").append(ChatColor.WHITE).append(message2).toString());
                            sendee.playSound(sendee.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 1.0f);
                            ChatMechanics.reply.put(sendee, p);
                        }
                    }
                }
                else {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("ERROR: ").append(ChatColor.RED).append("You have no conversation to respond to!").toString());
                }
            }
            if (cmd.getName().equalsIgnoreCase("roll")) {
                if (args.length < 1 || args.length > 1) {
                    p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax.").append(ChatColor.GRAY).append(" /roll <1 - 10000>").toString());
                }
                else if (args.length == 1) {
                    int max = 0;
                    try {
                        max = Integer.parseInt(args[0]);
                    }
                    catch (NumberFormatException e) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Non-Numeric Max Number. /roll <1 - 10000>").toString());
                        return true;
                    }
                    if (max < 1 || max > 10000) {
                        p.sendMessage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("Incorrect Syntax.").append(ChatColor.GRAY).append(" /roll <1 - 10000>").toString());
                    }
                    else {
                        final Random random = new Random();
                        final int roll = random.nextInt(max + 1);
                        p.sendMessage(String.valueOf(p.getDisplayName()) + ChatColor.GRAY + " has rolled a " + ChatColor.GRAY + ChatColor.BOLD + ChatColor.UNDERLINE + roll + ChatColor.GRAY + " out of " + ChatColor.GRAY + ChatColor.BOLD + ChatColor.UNDERLINE + max + ".");
                        final List<Player> to_send = new ArrayList<Player>();
                        for (final Player pl2 : Bukkit.getServer().getOnlinePlayers()) {
                            if (pl2 != null && pl2 != p && pl2.getLocation().distance(p.getLocation()) < 50.0) {
                                to_send.add(pl2);
                            }
                        }
                        if (to_send.size() > 0) {
                            for (final Player pl2 : to_send) {
                                pl2.sendMessage(String.valueOf(p.getDisplayName()) + ChatColor.GRAY + " has rolled a " + ChatColor.GRAY + ChatColor.BOLD + ChatColor.UNDERLINE + roll + ChatColor.GRAY + " out of " + ChatColor.GRAY + ChatColor.BOLD + ChatColor.UNDERLINE + max + ".");
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(final PlayerCommandPreprocessEvent e) {
        final Player p = e.getPlayer();
        String s = e.getMessage().toLowerCase();
        if (s.startsWith("/")) {
            s = s.replace("/", "");
        }
        if (s.contains(" ")) {
            s = s.split(" ")[0];
        }
        if (s.equals("i") || s.equals("give")) {
            e.setCancelled(true);
            if (p.isOp()) {
                p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.MOB_SPAWNER) });
            }
            p.sendMessage(ChatColor.WHITE + "Unknown command. View your Character Journal's Index for a list of commands.");
            return;
        }
        if (s.equals("save-all") || s.equals("stop") || s.equals("restart") || s.equals("reload") || s.equals("ban") || s.equals("tpall") || s.equals("kill") || s.equals("vanish") || s.equals("mute") || s.equals("more")) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.WHITE + "Unknown command. View your Character Journal's Index for a list of commands.");
            return;
        }
        if (!p.isOp()) {
            String rank = "";
            if (ModerationMechanics.ranks.containsKey(p.getName())) {
                rank = ModerationMechanics.ranks.get(p.getName());
            }
            if (rank.equals("pmod")) {
                if (!s.equals("roll") && !s.equals("gl") && !s.equals("toggle") && !s.equals("toggles") && !s.equals("togglepvp") && !s.equals("togglechaos") && !s.equals("toggledebug") && !s.equals("debug") && !s.equals("toggleff") && !s.equals("add") && !s.equals("del") && !s.equals("delete") && !s.equals("message") && !s.equals("msg") && !s.equals("m") && !s.equals("whisper") && !s.equals("w") && !s.equals("tell") && !s.equals("t") && !s.equals("reply") && !s.equals("r") && !s.equals("logout") && !s.equals("sync") && !s.equals("reboot") && !s.equals("pinvite") && !s.equals("paccept") && !s.equals("pquit") && !s.equals("pkick") && !s.equals("pdecline") && !s.equals("p") && !s.equals("psban") && !s.equals("psunban") && !s.equals("psmute") && !s.equals("psunmute")) {
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.WHITE + "Unknown command. View your Character Journal's Index for a list of commands.");
                }
            }
            else if (ModerationMechanics.isSub(p)) {
                if (!s.equals("roll") && !s.equals("gl") && !s.equals("toggle") && !s.equals("toggles") && !s.equals("togglepvp") && !s.equals("togglechaos") && !s.equals("toggledebug") && !s.equals("debug") && !s.equals("toggleff") && !s.equals("add") && !s.equals("del") && !s.equals("delete") && !s.equals("message") && !s.equals("msg") && !s.equals("m") && !s.equals("whisper") && !s.equals("w") && !s.equals("tell") && !s.equals("t") && !s.equals("reply") && !s.equals("r") && !s.equals("logout") && !s.equals("sync") && !s.equals("reboot") && !s.equals("pinvite") && !s.equals("paccept") && !s.equals("pquit") && !s.equals("pkick") && !s.equals("pdecline") && !s.equals("p") && !s.equals("toggletrail")) {
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.WHITE + "Unknown command. View your Character Journal's Index for a list of commands.");
                }
            }
            else if (!s.equals("roll") && !s.equals("gl") && !s.equals("toggle") && !s.equals("toggles") && !s.equals("togglepvp") && !s.equals("togglechaos") && !s.equals("toggledebug") && !s.equals("debug") && !s.equals("toggleff") && !s.equals("add") && !s.equals("del") && !s.equals("delete") && !s.equals("message") && !s.equals("msg") && !s.equals("m") && !s.equals("whisper") && !s.equals("w") && !s.equals("tell") && !s.equals("t") && !s.equals("reply") && !s.equals("r") && !s.equals("logout") && !s.equals("sync") && !s.equals("reboot") && !s.equals("pinvite") && !s.equals("paccept") && !s.equals("pquit") && !s.equals("pkick") && !s.equals("pdecline") && !s.equals("p")) {
                e.setCancelled(true);
                p.sendMessage(ChatColor.WHITE + "Unknown command. View your Character Journal's Index for a list of commands.");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatTabComplete(final PlayerChatTabCompleteEvent e) {
        final Player p = e.getPlayer();
        if (e.getChatMessage() != null) {
            p.closeInventory();
            p.performCommand("gl " + e.getChatMessage());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        if (!e.isCancelled()) {
            final Player p = e.getPlayer();
            e.setCancelled(true);
            final String message = e.getMessage();
            if (message.contains("@i@") && p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
                this.sendShowString(p, p.getItemInHand(), "", message, p);
                final List<Player> to_send = new ArrayList<Player>();
                for (final Player pl : Bukkit.getServer().getOnlinePlayers()) {
                    if (!ModerationMechanics.vanished.contains(pl.getName().toLowerCase()) && pl != null && pl != p && pl.getLocation().distance(p.getLocation()) < 50.0) {
                        to_send.add(pl);
                    }
                }
                if (to_send.size() <= 0) {
                    p.sendMessage(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("No one heard you.").toString());
                }
                else {
                    for (final Player pl : to_send) {
                        this.sendShowString(p, p.getItemInHand(), "", message, pl);
                    }
                }
                for (final Player op : Bukkit.getServer().getOnlinePlayers()) {
                    if (op.isOp() && ModerationMechanics.vanished.contains(op.getName().toLowerCase()) && op != p) {
                        this.sendShowString(p, p.getItemInHand(), "", message, op);
                    }
                }
                Main.log.info(String.valueOf(p.getDisplayName()) + ": " + ChatColor.WHITE + message);
            }
            else {
                p.sendMessage(String.valueOf(p.getDisplayName()) + ": " + ChatColor.WHITE + message);
                final List<Player> to_send = new ArrayList<Player>();
                for (final Player pl : Bukkit.getServer().getOnlinePlayers()) {
                    if (!ModerationMechanics.vanished.contains(pl.getName().toLowerCase()) && pl != null && pl != p && pl.getLocation().distance(p.getLocation()) < 50.0) {
                        to_send.add(pl);
                    }
                }
                if (to_send.size() <= 0) {
                    p.sendMessage(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("No one heard you.").toString());
                }
                else {
                    for (final Player pl : to_send) {
                        pl.sendMessage(String.valueOf(p.getDisplayName()) + ": " + ChatColor.WHITE + message);
                    }
                }
                for (final Player op : Bukkit.getServer().getOnlinePlayers()) {
                    if (op.isOp() && ModerationMechanics.vanished.contains(op.getName().toLowerCase()) && op != p) {
                        op.sendMessage(String.valueOf(p.getDisplayName()) + ": " + ChatColor.WHITE + message);
                    }
                }
                Main.log.info(String.valueOf(p.getDisplayName()) + ": " + ChatColor.WHITE + message);
            }
        }
    }
    
    public void sendShowString(final Player sender, final ItemStack is, final String prefix, final String message, final Player p) {
        if (message.contains("@i@") && is != null && is.getType() != Material.AIR) {
            String before = "";
            try {
                before = message.split("@i@")[0];
            }
            catch (Exception e) {
                before = "";
            }
            String after = "";
            try {
                after = message.split("@i@")[1];
            }
            catch (Exception e2) {
                after = "";
            }
            before = String.valueOf(prefix) + sender.getDisplayName() + ": " + ChatColor.WHITE + before;
            after = ChatColor.WHITE + after;
            String item = "{id:" + is.getTypeId() + "}";
            if (is.getItemMeta().hasLore()) {
                String lore = "";
                for (final String s : is.getItemMeta().getLore()) {
                    lore = String.valueOf(lore) + ("\\\"" + s + "\\\", ").replace(":", "|");
                }
                if (lore.endsWith(", ")) {
                    lore = lore.substring(0, lore.length() - 2);
                }
                if (is.getItemMeta().hasDisplayName()) {
                    item = "{id:1,tag:{display:{Name:\\\"" + is.getItemMeta().getDisplayName() + "\\\",Lore:[" + lore + "]}}}";
                }
                else {
                    item = "{id:" + is.getTypeId() + ",tag:{display:{Lore:[" + lore + "]}}}";
                }
            }
            else if (is.getItemMeta().hasDisplayName()) {
                item = "{id:1,tag:{display:{Name:\\\"" + is.getItemMeta().getDisplayName() + "\\\"}}}";
            }
            final String msg = "[\"\",{\"text\":\"" + before + "\"},{\"text\":\"§f§l§nSHOW\",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"" + item + "\"}},{\"text\":\"" + after + "\"}]";
            final PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(msg));
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket((Packet)packet);
        }
    }
}
