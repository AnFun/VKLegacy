package me.AnFun.VKLegacy;

import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Effect;
import org.bukkit.Sound;
import java.util.Random;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.GameMode;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import me.confuser.barapi.BarAPI;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.entity.Player;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class Damage implements Listener
{
    HashMap<Player, Long> playerslow;
    public static HashMap<Player, Long> lasthit;
    public static HashMap<Player, Player> lastphit;
    HashMap<UUID, Long> kb;
    ArrayList<String> p_arm;
    
    static {
        Damage.lasthit = new HashMap<Player, Long>();
        Damage.lastphit = new HashMap<Player, Player>();
    }
    
    public Damage() {
        this.playerslow = new HashMap<Player, Long>();
        this.kb = new HashMap<UUID, Long>();
        this.p_arm = new ArrayList<String>();
    }
    
    public void onEnable() {
        Main.log.info("[Damage] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    final float pcnt = (float)(p.getHealth() / p.getMaxHealth() * 100.0);
                    BarAPI.setMessage(p, new StringBuilder().append(ChatColor.LIGHT_PURPLE).append(ChatColor.BOLD).append("HP ").append(ChatColor.LIGHT_PURPLE).append((int)p.getHealth()).append(ChatColor.LIGHT_PURPLE).append(ChatColor.BOLD).append(" / ").append(ChatColor.LIGHT_PURPLE).append((int)p.getMaxHealth()).toString(), pcnt);
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 1L, 1L);
        new BukkitRunnable() {
            public void run() {
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (Damage.this.playerslow.containsKey(p)) {
                        if (System.currentTimeMillis() - Damage.this.playerslow.get(p) <= 3000L) {
                            continue;
                        }
                        p.setWalkSpeed(0.2f);
                    }
                    else {
                        if (p.getWalkSpeed() == 0.2f) {
                            continue;
                        }
                        p.setWalkSpeed(0.2f);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 20L, 20L);
    }
    
    public void onDisable() {
        Main.log.info("[Damage] has been disabled.");
    }
    
    public static int getHp(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            if (lore.size() > 1 && lore.get(1).contains("HP")) {
                try {
                    return Integer.parseInt(lore.get(1).split(": +")[1]);
                }
                catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    public static int getArmor(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            if (lore.size() > 0 && lore.get(0).contains("ARMOR")) {
                try {
                    return Integer.parseInt(lore.get(0).split(" - ")[1].split("%")[0]);
                }
                catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    public static int getDps(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            if (lore.size() > 0 && lore.get(0).contains("DPS")) {
                try {
                    return Integer.parseInt(lore.get(0).split(" - ")[1].split("%")[0]);
                }
                catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    public static int getEnergy(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            if (lore.size() > 2 && lore.get(2).contains("ENERGY REGEN")) {
                try {
                    return Integer.parseInt(lore.get(2).split(": +")[1].split("%")[0]);
                }
                catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    public static int getHps(final ItemStack is) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            if (lore.size() > 2 && lore.get(2).contains("HP REGEN")) {
                try {
                    return Integer.parseInt(lore.get(2).split(": +")[1].split(" HP/s")[0]);
                }
                catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    public static int getPercent(final ItemStack is, final String type) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            for (final String s : lore) {
                if (s.contains(type)) {
                    try {
                        return Integer.parseInt(s.split(": ")[1].split("%")[0]);
                    }
                    catch (Exception e) {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }
    
    public static int getElem(final ItemStack is, final String type) {
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            for (final String s : lore) {
                if (s.contains(type)) {
                    try {
                        return Integer.parseInt(s.split(": +")[1]);
                    }
                    catch (Exception e) {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }
    
    public static List<Integer> getDamageRange(final ItemStack is) {
        final List<Integer> dmg = new ArrayList<Integer>();
        dmg.add(1);
        dmg.add(1);
        if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)is.getItemMeta().getLore();
            if (lore.size() > 0 && lore.get(0).contains("DMG")) {
                try {
                    int min = 1;
                    int max = 1;
                    min = Integer.parseInt(lore.get(0).split("DMG: ")[1].split(" - ")[0]);
                    max = Integer.parseInt(lore.get(0).split(" - ")[1]);
                    dmg.set(0, min);
                    dmg.set(1, max);
                }
                catch (Exception e) {
                    dmg.set(0, 1);
                    dmg.set(1, 1);
                }
            }
        }
        return dmg;
    }
    
    public static int getCrit(final Player p) {
        int crit = 0;
        ItemStack wep = p.getItemInHand();
        if (Staffs.staff.containsKey(p)) {
            wep = Staffs.staff.get(p);
        }
        if (wep != null && wep.getType() != Material.AIR && wep.getItemMeta().hasLore()) {
            final List<String> lore = (List<String>)wep.getItemMeta().getLore();
            for (final String line : lore) {
                if (line.contains("CRITICAL HIT")) {
                    crit = getPercent(wep, "CRITICAL HIT");
                }
            }
            if (wep.getType().name().contains("_AXE")) {
                crit += 3;
            }
            int intel = 0;
            ItemStack[] armorContents;
            for (int length = (armorContents = p.getInventory().getArmorContents()).length, i = 0; i < length; ++i) {
                final ItemStack is = armorContents[i];
                if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                    final int addint = getElem(is, "INT");
                    intel += addint;
                }
            }
            if (intel > 0) {
                crit += (int)Math.round(intel * 0.014);
            }
        }
        return crit;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNpcDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            if (p.hasMetadata("NPC") || p.getPlayerListName().equals("")) {
                e.setCancelled(true);
                e.setDamage(0.0);
            }
            if (p.isOp() || p.getGameMode() == GameMode.CREATIVE || p.isFlying()) {
                e.setCancelled(true);
                e.setDamage(0.0);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNoOpDamage(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            final Player p = (Player)e.getDamager();
            if (p.isOp() || p.getGameMode() == GameMode.CREATIVE || p.isFlying()) {
                e.setCancelled(true);
                e.setDamage(0.0);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onBlodge(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getDamage() <= 0.0) {
                return;
            }
            final Player p = (Player)e.getEntity();
            final PlayerInventory i = p.getInventory();
            int block = 0;
            int dodge = 0;
            if (p.getHealth() > 0.0) {
                ItemStack[] armorContents;
                for (int length = (armorContents = i.getArmorContents()).length, k = 0; k < length; ++k) {
                    final ItemStack is = armorContents[k];
                    if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                        final int addedblock = getPercent(is, "BLOCK");
                        block += addedblock;
                        final int addeddodge = getPercent(is, "DODGE");
                        dodge += addeddodge;
                    }
                }
                int str = 0;
                ItemStack[] armorContents2;
                for (int length2 = (armorContents2 = p.getInventory().getArmorContents()).length, l = 0; l < length2; ++l) {
                    final ItemStack is2 = armorContents2[l];
                    if (is2 != null && is2.getType() != Material.AIR && is2.hasItemMeta() && is2.getItemMeta().hasLore()) {
                        final int addstr = getElem(is2, "STR");
                        str += addstr;
                    }
                }
                if (str > 0) {
                    block += (int)Math.round(str * 0.015);
                }
                final Random random = new Random();
                final int dodger = random.nextInt(100) + 1;
                final int blockr = random.nextInt(100) + 1;
                if (e.getDamager() instanceof Player) {
                    final Player d = (Player)e.getDamager();
                    ItemStack wep = d.getItemInHand();
                    if (Staffs.staff.containsKey(d)) {
                        wep = Staffs.staff.get(d);
                    }
                    int accuracy = getPercent(wep, "ACCURACY");
                    if (accuracy > 0) {
                        final int b4block = block;
                        final int b4dodge = dodge;
                        if (accuracy > 0) {
                            block -= accuracy;
                            accuracy -= b4block;
                        }
                        if (accuracy > 0) {
                            dodge -= accuracy;
                            accuracy -= b4dodge;
                        }
                    }
                    if (blockr <= block) {
                        e.setDamage(0.0);
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1.0f, 1.0f);
                        d.sendMessage("          " + ChatColor.RED + ChatColor.BOLD + "*OPPONENT BLOCKED* (" + p.getName() + ")");
                        p.sendMessage("          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + d.getName() + ")");
                    }
                    else if (dodger <= dodge) {
                        e.setDamage(0.0);
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 1.0f, 1.0f);
                        d.sendMessage("          " + ChatColor.RED + ChatColor.BOLD + "*OPPONENT DODGED* (" + p.getName() + ")");
                        p.sendMessage("          " + ChatColor.GREEN + ChatColor.BOLD + "*DODGE* (" + d.getName() + ")");
                    }
                    else if (blockr <= 80 && p.isBlocking()) {
                        e.setDamage((double)((int)e.getDamage() / 2));
                        p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1.0f, 1.0f);
                        d.sendMessage("          " + ChatColor.RED + ChatColor.BOLD + "*OPPONENT BLOCKED* (" + p.getName() + ")");
                        p.sendMessage("          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + d.getName() + ")");
                    }
                }
                else if (e.getDamager() instanceof LivingEntity) {
                    final LivingEntity li = (LivingEntity)e.getDamager();
                    String mname = "";
                    if (li.hasMetadata("name")) {
                        mname = li.getMetadata("name").get(0).asString();
                    }
                    if (blockr <= block) {
                        e.setDamage(0.0);
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1.0f, 1.0f);
                        p.sendMessage("          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + mname + ChatColor.DARK_GREEN + ")");
                    }
                    else if (dodger <= dodge) {
                        e.setDamage(0.0);
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 1.0f, 1.0f);
                        p.sendMessage("          " + ChatColor.GREEN + ChatColor.BOLD + "*DODGE* (" + mname + ChatColor.GREEN + ")");
                    }
                    else if (blockr <= 80 && p.isBlocking()) {
                        e.setDamage((double)((int)e.getDamage() / 2));
                        p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1.0f, 1.0f);
                        p.sendMessage("          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + mname + ChatColor.DARK_GREEN + ")");
                    }
                }
            }
        }
        if (e.getDamage() <= 0.0) {
            return;
        }
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            final Player p = (Player)e.getDamager();
            final LivingEntity li2 = (LivingEntity)e.getEntity();
            ItemStack wep2 = p.getItemInHand();
            if (Staffs.staff.containsKey(p)) {
                wep2 = Staffs.staff.get(p);
            }
            if (wep2 != null && wep2.getType() != Material.AIR && wep2.getItemMeta().hasLore()) {
                final int min = getDamageRange(wep2).get(0);
                final int max = getDamageRange(wep2).get(1);
                final Random random = new Random();
                int dmg = random.nextInt(max - min + 1) + min;
                final int tier = Merchant.getTier(wep2);
                final List<String> lore = (List<String>)wep2.getItemMeta().getLore();
                for (final String line : lore) {
                    if (line.contains("ICE DMG")) {
                        li2.getWorld().playEffect(li2.getLocation().add(0.0, 1.3, 0.0), Effect.POTION_BREAK, 8194);
                        final int eldmg = getElem(wep2, "ICE DMG");
                        dmg += eldmg;
                        if (tier == 1) {
                            li2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
                        }
                        if (tier == 2) {
                            li2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 0));
                        }
                        if (tier == 3) {
                            li2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
                        }
                        if (tier == 4) {
                            li2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
                        }
                        if (tier == 5) {
                            li2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                        }
                    }
                    if (line.contains("POISON DMG")) {
                        li2.getWorld().playEffect(li2.getLocation().add(0.0, 1.3, 0.0), Effect.POTION_BREAK, 8196);
                        final int eldmg = getElem(wep2, "POISON DMG");
                        dmg += eldmg;
                        if (tier == 1) {
                            li2.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 15, 0));
                        }
                        if (tier == 2) {
                            li2.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 25, 0));
                        }
                        if (tier == 3) {
                            li2.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30, 1));
                        }
                        if (tier == 4) {
                            li2.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 35, 1));
                        }
                        if (tier == 5) {
                            li2.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
                        }
                    }
                    if (line.contains("FIRE DMG")) {
                        final int eldmg = getElem(wep2, "FIRE DMG");
                        dmg += eldmg;
                        if (tier == 1) {
                            li2.setFireTicks(15);
                        }
                        if (tier == 2) {
                            li2.setFireTicks(25);
                        }
                        if (tier == 3) {
                            li2.setFireTicks(30);
                        }
                        if (tier == 4) {
                            li2.setFireTicks(35);
                        }
                        if (tier == 5) {
                            li2.setFireTicks(40);
                        }
                    }
                    if (line.contains("PURE DMG")) {
                        final int eldmg = getElem(wep2, "PURE DMG");
                        dmg += eldmg;
                    }
                }
                final int crit = getCrit(p);
                final int drop = random.nextInt(100) + 1;
                if (drop <= crit) {
                    dmg *= 2;
                    p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.5f, 0.5f);
                    ParticleEffect.CRIT_MAGIC.display(0.0f, 0.0f, 0.0f, 1.0f, 50, li2.getLocation(), 20.0);
                }
                final PlayerInventory j = p.getInventory();
                double dps = 0.0;
                double vit = 0.0;
                double str2 = 0.0;
                ItemStack[] armorContents3;
                for (int length3 = (armorContents3 = j.getArmorContents()).length, n = 0; n < length3; ++n) {
                    final ItemStack is3 = armorContents3[n];
                    if (is3 != null && is3.getType() != Material.AIR && is3.hasItemMeta() && is3.getItemMeta().hasLore()) {
                        final int adddps = getDps(is3);
                        dps += adddps;
                        final int addvit = getElem(is3, "VIT");
                        vit += addvit;
                        final int addstr2 = getElem(is3, "STR");
                        str2 += addstr2;
                    }
                }
                if (vit > 0.0 && wep2.getType().name().contains("_SWORD")) {
                    final double divide = vit / 5000.0;
                    final double pre = dmg * divide;
                    final int cleaned = dmg += (int)pre;
                }
                if (str2 > 0.0 && wep2.getType().name().contains("_AXE")) {
                    final double divide = str2 / 3800.0;
                    final double pre = dmg * divide;
                    final int cleaned = dmg += (int)pre;
                }
                if (dps > 0.0) {
                    final double divide = dps / 100.0;
                    final double pre = dmg * divide;
                    final int cleaned = dmg += (int)pre;
                }
                for (final String line2 : lore) {
                    if (line2.contains("LIFE STEAL")) {
                        li2.getWorld().playEffect(li2.getEyeLocation(), Effect.STEP_SOUND, (Object)Material.REDSTONE_BLOCK);
                        final double base = getPercent(wep2, "LIFE STEAL");
                        final double pcnt = base / 100.0;
                        int life = 1;
                        if ((int)(pcnt * dmg) > 0) {
                            life = (int)(pcnt * dmg);
                        }
                        if (p.getHealth() < p.getMaxHealth() - life) {
                            p.setHealth(p.getHealth() + life);
                            final ArrayList<String> toggles = Toggles.getToggles(p.getName());
                            if (!toggles.contains("debug")) {
                                continue;
                            }
                            p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("            +").append(ChatColor.GREEN).append(life).append(ChatColor.GREEN).append(ChatColor.BOLD).append(" HP ").append(ChatColor.GRAY).append("[").append((int)p.getHealth()).append("/").append((int)p.getMaxHealth()).append("HP]").toString());
                        }
                        else {
                            if (p.getHealth() < p.getMaxHealth() - life) {
                                continue;
                            }
                            p.setHealth(p.getMaxHealth());
                            final ArrayList<String> toggles = Toggles.getToggles(p.getName());
                            if (!toggles.contains("debug")) {
                                continue;
                            }
                            p.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("            +").append(ChatColor.GREEN).append(life).append(ChatColor.GREEN).append(ChatColor.BOLD).append(" HP ").append(ChatColor.GRAY).append("[").append((int)p.getMaxHealth()).append("/").append((int)p.getMaxHealth()).append("HP]").toString());
                        }
                    }
                }
                e.setDamage((double)dmg);
                return;
            }
            e.setDamage(1.0);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onArmor(final EntityDamageEvent e) {
        if (e.getDamage() <= 0.0) {
            return;
        }
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            final PlayerInventory i = p.getInventory();
            double dmg = e.getDamage();
            double arm = 0.0;
            ItemStack[] armorContents;
            for (int length = (armorContents = i.getArmorContents()).length, j = 0; j < length; ++j) {
                final ItemStack is = armorContents[j];
                if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                    final int addarm = getArmor(is);
                    arm += addarm;
                }
            }
            if (arm > 0.0) {
                final double divide = arm / 100.0;
                final double pre = dmg * divide;
                int cleaned = (int)(dmg - pre);
                if (cleaned <= 1) {
                    cleaned = 1;
                }
                dmg = cleaned;
                int health = 0;
                if (p.getHealth() - cleaned > 0.0) {
                    health = (int)(p.getHealth() - cleaned);
                }
                final ArrayList<String> toggles = Toggles.getToggles(p.getName());
                if (toggles.contains("debug")) {
                    if (health < 0) {
                        health = 0;
                    }
                    p.sendMessage(ChatColor.RED + "            -" + cleaned + ChatColor.RED + ChatColor.BOLD + "HP " + ChatColor.GRAY + "[-" + (int)arm + "%A -> -" + (int)pre + ChatColor.BOLD + "DMG" + ChatColor.GRAY + "] " + ChatColor.GREEN + "[" + health + ChatColor.BOLD + "HP" + ChatColor.GREEN + "]");
                }
                e.setDamage((double)cleaned);
            }
            else {
                final ArrayList<String> toggles2 = Toggles.getToggles(p.getName());
                if (toggles2.contains("debug")) {
                    int health2 = (int)(p.getHealth() - dmg);
                    if (health2 < 0) {
                        health2 = 0;
                    }
                    p.sendMessage(ChatColor.RED + "            -" + (int)dmg + ChatColor.RED + ChatColor.BOLD + "HP " + ChatColor.GRAY + "[-0%A -> -0" + ChatColor.BOLD + "DMG" + ChatColor.GRAY + "] " + ChatColor.GREEN + "[" + health2 + ChatColor.BOLD + "HP" + ChatColor.GREEN + "]");
                }
                e.setDamage(dmg);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onDebug(final EntityDamageByEntityEvent e) {
        if (e.getDamage() <= 0.0) {
            return;
        }
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            final Player p = (Player)e.getEntity();
            final Player d = (Player)e.getDamager();
            final int dmg = (int)e.getDamage();
            final ArrayList<String> toggles = Toggles.getToggles(d.getName());
            if (toggles.contains("debug")) {
                d.sendMessage(ChatColor.RED + "            " + dmg + ChatColor.RED + ChatColor.BOLD + " DMG " + ChatColor.RED + "-> " + p.getName());
            }
            Damage.lastphit.put(p, d);
            Damage.lasthit.put(p, System.currentTimeMillis());
        }
        else if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {
            final LivingEntity p2 = (LivingEntity)e.getEntity();
            final Player d = (Player)e.getDamager();
            final int dmg = (int)e.getDamage();
            int health = 0;
            if (p2.getHealth() - dmg > 0.0) {
                health = (int)(p2.getHealth() - dmg);
            }
            String name = "";
            if (p2.hasMetadata("name")) {
                name = p2.getMetadata("name").get(0).asString();
            }
            final ArrayList<String> toggles2 = Toggles.getToggles(d.getName());
            if (toggles2.contains("debug")) {
                d.sendMessage(ChatColor.RED + "            " + dmg + ChatColor.RED + ChatColor.BOLD + " DMG " + ChatColor.RED + "-> " + ChatColor.RESET + name + " [" + health + "HP]");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onKnockback(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof LivingEntity) {
            final LivingEntity p = (LivingEntity)e.getEntity();
            final LivingEntity d = (LivingEntity)e.getDamager();
            if (e.getDamage() <= 0.0) {
                return;
            }
            if (p instanceof Player) {
                final Vector v = p.getLocation().toVector().subtract(d.getLocation().toVector());
                if (v.getX() != 0.0 || v.getY() != 0.0 || v.getZ() != 0.0) {
                    v.normalize();
                }
                p.setVelocity(v.multiply(0.15f));
            }
            else if (!this.kb.containsKey(p.getUniqueId()) || (this.kb.containsKey(p.getUniqueId()) && System.currentTimeMillis() - this.kb.get(p.getUniqueId()) > 500L)) {
                this.kb.put(p.getUniqueId(), System.currentTimeMillis());
                final Vector v = p.getLocation().toVector().subtract(d.getLocation().toVector());
                if (v.getX() != 0.0 || v.getY() != 0.0 || v.getZ() != 0.0) {
                    v.normalize();
                }
                if (d instanceof Player) {
                    final Player dam = (Player)d;
                    if (dam.getItemInHand() != null && dam.getItemInHand().getType().name().contains("_SPADE")) {
                        p.setVelocity(v.multiply(1.25f).setY(0.4));
                    }
                    else {
                        p.setVelocity(v.multiply(0.5f).setY(0.35));
                    }
                }
                else {
                    p.setVelocity(v.multiply(0.5f).setY(0.35));
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            final LivingEntity s = (LivingEntity)e.getEntity();
            if (e.getDamage() >= s.getHealth() && this.kb.containsKey(s.getUniqueId())) {
                this.kb.remove(s.getUniqueId());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPolearmAOE(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {
            final LivingEntity p = (LivingEntity)e.getEntity();
            final Player d = (Player)e.getDamager();
            if (e.getDamage() <= 0.0) {
                return;
            }
            if (d.getItemInHand() != null && d.getItemInHand().getType().name().contains("_SPADE") && !this.p_arm.contains(d.getName())) {
                for (final Entity near : p.getNearbyEntities(2.5, 3.0, 2.5)) {
                    if (near instanceof LivingEntity && near != p && near != d && near != null) {
                        final LivingEntity n = (LivingEntity)near;
                        if (Energy.nodamage.containsKey(d.getName())) {
                            Energy.nodamage.remove(d.getName());
                        }
                        this.p_arm.add(d.getName());
                        n.damage(1.0, (Entity)d);
                        this.p_arm.remove(d.getName());
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onDamageSound(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            if (e.getDamage() <= 0.0) {
                return;
            }
            final Player p = (Player)e.getDamager();
            p.playSound(p.getLocation(), Sound.HURT_FLESH, 1.0f, 1.0f);
            if (e.getEntity() instanceof Player) {
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.WOOD_CLICK, 1.0f, 1.6f);
            }
        }
        if (e.getEntity() instanceof Player && !(e.getDamager() instanceof Player) && e.getDamager() instanceof LivingEntity) {
            final Player p = (Player)e.getEntity();
            p.setWalkSpeed(0.165f);
            this.playerslow.put(p, System.currentTimeMillis());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBypassArmor(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            final LivingEntity li = (LivingEntity)e.getEntity();
            if (e.getDamage() <= 0.0) {
                return;
            }
            final int dmg = (int)e.getDamage();
            e.setDamage(0.0);
            e.setCancelled(true);
            li.playEffect(EntityEffect.HURT);
            li.setLastDamageCause(e);
            if (li.getHealth() - dmg <= 0.0) {
                li.setHealth(0.0);
            }
            else {
                li.setHealth(li.getHealth() - dmg);
            }
        }
    }
}
