package me.AnFun.VKLegacy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.Bukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
    public static Plugin plugin;
    public static Logger log;
    private static Alignments alignments;
    private static Antibuild antibuild;
    private static Banks banks;
    private static Buddies buddies;
    private static ChatMechanics chatMechanics;
    private static Damage damage;
    private static Durability durability;
    private static Enchants enchants;
    private static Energy energy;
    private static GemPouches gemPouches;
    private static Hearthstone hearthstone;
    private static Horses horses;
    private static ItemVendors itemVendors;
    private static Listeners listeners;
    private static Logout logout;
    private static LootChests lootChests;
    private static MerchantMechanics merchantMechanics;
    private static Mining mining;
    private static Mobdrops mobdrops;
    private static Mobs mobs;
    private static ModerationMechanics moderationMechanics;
    private static Orbs orbs;
    private static Parties parties;
    private static ProfessionMechanics professionMechanics;
    private static Repairing repairing;
    private static Respawn respawn;
    private static Spawners spawners;
    private static Speedfish speedfish;
    private static Staffs staffs;
    private static TeleportBooks teleportBooks;
    private static Toggles toggles;
    private static Untradeable untradeable;
    public static Economy econ;
    
    static {
        Main.econ = null;
    }
    
    public static WorldGuardPlugin getWorldGuard() {
        final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin)plugin;
    }
    
    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (rsp == null) {
            return false;
        }
        Main.econ = (Economy)rsp.getProvider();
        return Main.econ != null;
    }
    
    public void onEnable() {
        Bukkit.getWorlds().get(0).setAutoSave(false);
        Bukkit.getWorlds().get(0).setTime(14000L);
        Bukkit.getWorlds().get(0).setGameRuleValue("doDaylightCycle", "false");
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    p.saveData();
                }
            }
        }.runTaskTimerAsynchronously((Plugin)this, 6000L, 6000L);
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        Main.plugin = (Plugin)this;
        Main.log = Main.plugin.getLogger();
        this.getCommand("gl").setExecutor((CommandExecutor)new ChatMechanics());
        this.getCommand("br").setExecutor((CommandExecutor)new ChatMechanics());
        this.getCommand("message").setExecutor((CommandExecutor)new ChatMechanics());
        this.getCommand("reply").setExecutor((CommandExecutor)new ChatMechanics());
        this.getCommand("roll").setExecutor((CommandExecutor)new ChatMechanics());
        this.getCommand("toggle").setExecutor((CommandExecutor)new Toggles());
        this.getCommand("togglepvp").setExecutor((CommandExecutor)new Toggles());
        this.getCommand("togglechaos").setExecutor((CommandExecutor)new Toggles());
        this.getCommand("toggleff").setExecutor((CommandExecutor)new Toggles());
        this.getCommand("toggledebug").setExecutor((CommandExecutor)new Toggles());
        this.getCommand("add").setExecutor((CommandExecutor)new Buddies());
        this.getCommand("del").setExecutor((CommandExecutor)new Buddies());
        this.getCommand("logout").setExecutor((CommandExecutor)new Logout());
        this.getCommand("sync").setExecutor((CommandExecutor)new Logout());
        this.getCommand("setrank").setExecutor((CommandExecutor)new ModerationMechanics());
        this.getCommand("psban").setExecutor((CommandExecutor)new ModerationMechanics());
        this.getCommand("psunban").setExecutor((CommandExecutor)new ModerationMechanics());
        this.getCommand("psmute").setExecutor((CommandExecutor)new ModerationMechanics());
        this.getCommand("psunmute").setExecutor((CommandExecutor)new ModerationMechanics());
        this.getCommand("banksee").setExecutor((CommandExecutor)new ModerationMechanics());
        this.getCommand("psvanish").setExecutor((CommandExecutor)new ModerationMechanics());
        this.getCommand("toggletrail").setExecutor((CommandExecutor)new ModerationMechanics());
        this.getCommand("showms").setExecutor((CommandExecutor)new Spawners());
        this.getCommand("hidems").setExecutor((CommandExecutor)new Spawners());
        this.getCommand("killall").setExecutor((CommandExecutor)new Spawners());
        this.getCommand("monspawn").setExecutor((CommandExecutor)new Spawners());
        this.getCommand("showloot").setExecutor((CommandExecutor)new LootChests());
        this.getCommand("hideloot").setExecutor((CommandExecutor)new LootChests());
        this.getCommand("pinvite").setExecutor((CommandExecutor)new Parties());
        this.getCommand("paccept").setExecutor((CommandExecutor)new Parties());
        this.getCommand("pkick").setExecutor((CommandExecutor)new Parties());
        this.getCommand("pquit").setExecutor((CommandExecutor)new Parties());
        this.getCommand("pdecline").setExecutor((CommandExecutor)new Parties());
        this.getCommand("p").setExecutor((CommandExecutor)new Parties());
        Main.alignments = new Alignments();
        Main.antibuild = new Antibuild();
        Main.banks = new Banks();
        Main.buddies = new Buddies();
        Main.chatMechanics = new ChatMechanics();
        Main.damage = new Damage();
        Main.durability = new Durability();
        Main.enchants = new Enchants();
        Main.energy = new Energy();
        Main.gemPouches = new GemPouches();
        Main.hearthstone = new Hearthstone();
        Main.horses = new Horses();
        Main.itemVendors = new ItemVendors();
        Main.listeners = new Listeners();
        Main.logout = new Logout();
        Main.lootChests = new LootChests();
        Main.merchantMechanics = new MerchantMechanics();
        Main.mining = new Mining();
        Main.mobdrops = new Mobdrops();
        Main.mobs = new Mobs();
        Main.moderationMechanics = new ModerationMechanics();
        Main.orbs = new Orbs();
        Main.parties = new Parties();
        Main.professionMechanics = new ProfessionMechanics();
        Main.repairing = new Repairing();
        Main.respawn = new Respawn();
        Main.spawners = new Spawners();
        Main.speedfish = new Speedfish();
        Main.staffs = new Staffs();
        Main.teleportBooks = new TeleportBooks();
        Main.toggles = new Toggles();
        Main.untradeable = new Untradeable();
        Main.alignments.onEnable();
        Main.antibuild.onEnable();
        Main.banks.onEnable();
        Main.buddies.onEnable();
        Main.chatMechanics.onEnable();
        Main.damage.onEnable();
        Main.durability.onEnable();
        Main.enchants.onEnable();
        Main.energy.onEnable();
        Main.gemPouches.onEnable();
        Main.hearthstone.onEnable();
        Main.horses.onEnable();
        Main.itemVendors.onEnable();
        Main.listeners.onEnable();
        Main.logout.onEnable();
        Main.lootChests.onEnable();
        Main.merchantMechanics.onEnable();
        Main.mining.onEnable();
        Main.mobdrops.onEnable();
        Main.mobs.onEnable();
        Main.moderationMechanics.onEnable();
        Main.orbs.onEnable();
        Main.parties.onEnable();
        Main.professionMechanics.onEnable();
        Main.repairing.onEnable();
        Main.respawn.onEnable();
        Main.spawners.onEnable();
        Main.speedfish.onEnable();
        Main.staffs.onEnable();
        Main.teleportBooks.onEnable();
        Main.toggles.onEnable();
        Main.untradeable.onEnable();
        if (!this.setupEconomy()) {
            this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
        }
    }
    
    public void onDisable() {
        Main.alignments.onDisable();
        Main.antibuild.onDisable();
        Main.banks.onDisable();
        Main.buddies.onDisable();
        Main.chatMechanics.onDisable();
        Main.damage.onDisable();
        Main.durability.onDisable();
        Main.enchants.onDisable();
        Main.energy.onDisable();
        Main.gemPouches.onDisable();
        Main.hearthstone.onDisable();
        Main.horses.onDisable();
        Main.itemVendors.onDisable();
        Main.listeners.onDisable();
        Main.logout.onDisable();
        Main.lootChests.onDisable();
        Main.merchantMechanics.onDisable();
        Main.mining.onDisable();
        Main.mobdrops.onDisable();
        Main.mobs.onDisable();
        Main.moderationMechanics.onDisable();
        Main.orbs.onDisable();
        Main.parties.onDisable();
        Main.professionMechanics.onDisable();
        Main.repairing.onDisable();
        Main.respawn.onDisable();
        Main.spawners.onDisable();
        Main.speedfish.onDisable();
        Main.staffs.onDisable();
        Main.teleportBooks.onDisable();
        Main.toggles.onDisable();
        Main.untradeable.onDisable();
        Main.plugin = null;
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (Alignments.tagged.containsKey(p.getName())) {
                Alignments.tagged.remove(p.getName());
            }
            p.saveData();
            p.kickPlayer(String.valueOf(ChatColor.GREEN.toString()) + "You have been safely logged out by the server." + "\n\n" + ChatColor.GRAY.toString() + "Your player data has been synced.");
        }
    }
}
