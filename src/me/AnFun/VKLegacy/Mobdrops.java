package me.AnFun.VKLegacy;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Mobdrops implements Listener
{
    public void onEnable() {
        Main.log.info("[MobDrops] has been enabled.");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, Main.plugin);
    }
    
    public void onDisable() {
        Main.log.info("[MobDrops] has been disabled.");
    }
    
    @EventHandler
    public void onMobDeath(final EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            e.getDrops().clear();
        }
        e.setDroppedExp(0);
    }
    
    @EventHandler
    public void onMobDeath(final EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player)) {
            final LivingEntity s = (LivingEntity)e.getEntity();
            if (e.getDamage() >= s.getHealth() && s.getEquipment().getItemInHand() != null && s.getEquipment().getItemInHand().getType() != Material.AIR) {
                final Random random = new Random();
                final int gems = random.nextInt(2) + 1;
                int gemamt = 0;
                final int scrolldrop = random.nextInt(100);
                final int sackdrop = random.nextInt(100);
                boolean dodrop = false;
                boolean elite = false;
                final int rd = random.nextInt(100);
                if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
                    elite = true;
                }
                if (s.getEquipment().getItemInHand().getType().name().contains("WOOD_")) {
                    gemamt = random.nextInt(16) + 1;
                    if (elite && !this.isCustomNamedElite(s)) {
                        if (rd < 60) {
                            dodrop = true;
                        }
                    }
                    else if (elite && this.isCustomNamedElite(s)) {
                        if (rd < 15) {
                            dodrop = true;
                        }
                    }
                    else if (rd < 30) {
                        dodrop = true;
                    }
                    if (scrolldrop <= 5) {
                        final int scrolltype = random.nextInt(2);
                        if (scrolltype == 0) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.cyrennica_book());
                        }
                        if (scrolltype == 1) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.harrison_book());
                        }
                    }
                    if (sackdrop <= 5) {
                        s.getWorld().dropItemNaturally(s.getLocation(), GemPouches.gemPouch(1));
                    }
                }
                if (s.getEquipment().getItemInHand().getType().name().contains("STONE_")) {
                    gemamt = random.nextInt(17) + 16;
                    if (elite && !this.isCustomNamedElite(s)) {
                        if (rd < 48) {
                            dodrop = true;
                        }
                    }
                    else if (elite && this.isCustomNamedElite(s)) {
                        if (rd < 12) {
                            dodrop = true;
                        }
                    }
                    else if (rd < 24) {
                        dodrop = true;
                    }
                    if (scrolldrop <= 5) {
                        final int scrolltype = random.nextInt(5);
                        if (scrolltype == 0) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.cyrennica_book());
                        }
                        if (scrolltype == 1) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.harrison_book());
                        }
                        if (scrolltype == 2) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.dark_oak_book());
                        }
                        if (scrolltype == 3) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.trollsbane_book());
                        }
                        if (scrolltype == 4) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.tripoli_book());
                        }
                    }
                    if (sackdrop <= 5) {
                        s.getWorld().dropItemNaturally(s.getLocation(), GemPouches.gemPouch(2));
                    }
                }
                if (s.getEquipment().getItemInHand().getType().name().contains("IRON_")) {
                    gemamt = random.nextInt(33) + 32;
                    if (elite && !this.isCustomNamedElite(s)) {
                        if (rd < 36) {
                            dodrop = true;
                        }
                    }
                    else if (elite && this.isCustomNamedElite(s)) {
                        if (rd < 9) {
                            dodrop = true;
                        }
                    }
                    else if (rd < 18) {
                        dodrop = true;
                    }
                    if (scrolldrop <= 5) {
                        final int scrolltype = random.nextInt(5);
                        if (scrolltype == 0) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.cyrennica_book());
                        }
                        if (scrolltype == 1) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.dark_oak_book());
                        }
                        if (scrolltype == 2) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.trollsbane_book());
                        }
                        if (scrolltype == 3) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.gloomy_book());
                        }
                        if (scrolltype == 4) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.crestguard_book());
                        }
                    }
                    if (sackdrop <= 5) {
                        s.getWorld().dropItemNaturally(s.getLocation(), GemPouches.gemPouch(3));
                    }
                }
                if (s.getEquipment().getItemInHand().getType().name().contains("DIAMOND_")) {
                    gemamt = random.nextInt(129) + 128;
                    if (elite && !this.isCustomNamedElite(s)) {
                        if (rd < 24) {
                            dodrop = true;
                        }
                    }
                    else if (elite && this.isCustomNamedElite(s)) {
                        if (rd < 6) {
                            dodrop = true;
                        }
                    }
                    else if (rd < 12) {
                        dodrop = true;
                    }
                    if (scrolldrop <= 5) {
                        final int scrolltype = random.nextInt(4);
                        if (scrolltype == 0) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.deadpeaks_book());
                        }
                        if (scrolltype == 1) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.gloomy_book());
                        }
                        if (scrolltype == 2) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.crestwatch_book());
                        }
                        if (scrolltype == 3) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.crestguard_book());
                        }
                    }
                    if (sackdrop <= 5) {
                        s.getWorld().dropItemNaturally(s.getLocation(), GemPouches.gemPouch(4));
                    }
                }
                if (s.getEquipment().getItemInHand().getType().name().contains("GOLD_")) {
                    gemamt = random.nextInt(257) + 256;
                    if (elite && !this.isCustomNamedElite(s)) {
                        if (rd < 12) {
                            dodrop = true;
                        }
                    }
                    else if (elite && this.isCustomNamedElite(s)) {
                        if (rd < 3) {
                            dodrop = true;
                        }
                    }
                    else if (rd < 6) {
                        dodrop = true;
                    }
                    if (scrolldrop <= 5) {
                        final int scrolltype = random.nextInt(4);
                        if (scrolltype == 0) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.deadpeaks_book());
                        }
                        if (scrolltype == 1) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.gloomy_book());
                        }
                        if (scrolltype == 2) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.crestwatch_book());
                        }
                        if (scrolltype == 3) {
                            s.getWorld().dropItemNaturally(s.getLocation(), TeleportBooks.crestguard_book());
                        }
                    }
                    if (sackdrop <= 5) {
                        s.getWorld().dropItemNaturally(s.getLocation(), GemPouches.gemPouch(4));
                    }
                }
                if (elite) {
                    gemamt *= 3;
                }
                if (gems == 1) {
                    while (gemamt > 0) {
                        s.getWorld().dropItemNaturally(s.getLocation(), Money.makeGems(64));
                        gemamt -= 64;
                    }
                    s.getWorld().dropItemNaturally(s.getLocation(), Money.makeGems(gemamt));
                }
                if (dodrop) {
                    if (!this.isCustomNamedElite(s)) {
                        final ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
                        ItemStack[] armorContents;
                        for (int length = (armorContents = s.getEquipment().getArmorContents()).length, i = 0; i < length; ++i) {
                            final ItemStack is = armorContents[i];
                            if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
                                drops.add(is);
                                drops.add(s.getEquipment().getItemInHand());
                            }
                        }
                        /*final int piece = random.nextInt(drops.size());
                        final ItemStack is2 = drops.get(piece);
                        if (is2.getItemMeta().hasEnchants() && is2.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_MOBS)) {
                            is2.removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
                        }
                        short dura = (short)random.nextInt(is2.getType().getMaxDurability());
                        if (dura == 0) {
                            dura = 1;
                        }
                        if (dura == is2.getType().getMaxDurability()) {
                            dura = (short)(is2.getType().getMaxDurability() - 1);
                        }
                        is2.setDurability(dura);
                        s.getWorld().dropItemNaturally(s.getLocation(), is2);*/

                        for (final ItemStack is2 : drops) {
                            if (is2.getItemMeta().hasEnchants() && is2.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_MOBS)) {
                                is2.removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
                            }
                            short dura = (short) random.nextInt(is2.getType().getMaxDurability());
                            if (dura == 0) {
                                dura = 1;
                            }
                            if (dura == is2.getType().getMaxDurability()) {
                                dura = (short) (is2.getType().getMaxDurability() - 1);
                            }
                            is2.setDurability(dura);
                            s.getWorld().dropItemNaturally(s.getLocation(), is2);
                        }

                    }
                    else if (s.hasMetadata("type")) {
                        final String type = s.getMetadata("type").get(0).asString();
                        if (type.equalsIgnoreCase("mitsuki") || type.equalsIgnoreCase("copjak") || type.equalsIgnoreCase("kingofgreed") || type.equalsIgnoreCase("skeletonking") || type.equalsIgnoreCase("impa") || type.equalsIgnoreCase("bloodbutcher") || type.equalsIgnoreCase("blayshan") || type.equalsIgnoreCase("kilatan")) {
                            final ItemStack is = EliteDrops.createCustomEliteDrop(type);
                            s.getWorld().dropItemNaturally(s.getLocation(), is);
                        }
                    }
                }
            }
        }
    }
    
    boolean isCustomNamedElite(final LivingEntity l) {
        if (l.hasMetadata("type")) {
            final String type = l.getMetadata("type").get(0).asString();
            if (type.equalsIgnoreCase("mitsuki") || type.equalsIgnoreCase("copjak") || type.equalsIgnoreCase("impa") || type.equalsIgnoreCase("skeletonking") || type.equalsIgnoreCase("blayshan") || type.equalsIgnoreCase("kilatan")) {
                return true;
            }
        }
        return false;
    }
}
