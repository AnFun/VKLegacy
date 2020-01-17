package me.AnFun.VKLegacy;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.meta.PotionMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import java.util.Random;
import org.bukkit.inventory.ItemStack;

public class LootDrops
{
    public static ItemStack createLootDrop(final int tier) {
        final Random random = new Random();
        final int dodrop = random.nextInt(100);
        if (dodrop < 20) {
            final int whatenchant = random.nextInt(2);
            if (tier < 3 || (tier >= 3 && whatenchant == 0)) {
                return Items.enchant(tier, random.nextInt(2), false);
            }
            return Items.orb(false);
        }
        else {
            if (dodrop >= 40) {
                if (dodrop < 50) {
                    if (tier == 1) {
                        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)1);
                        final ItemMeta potmeta = pot.getItemMeta();
                        potmeta.setDisplayName(ChatColor.WHITE + "Minor Health Potion");
                        potmeta.setLore((List)Arrays.asList(ChatColor.GRAY + "A potion that restores " + ChatColor.WHITE + "15HP"));
                        final PotionMeta pm = (PotionMeta)potmeta;
                        pm.clearCustomEffects();
                        pm.addCustomEffect(PotionEffectType.HEAL.createEffect(0, 0), true);
                        pot.setItemMeta((ItemMeta)pm);
                        return pot;
                    }
                    if (tier == 2) {
                        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)5);
                        final ItemMeta potmeta = pot.getItemMeta();
                        potmeta.setDisplayName(ChatColor.GREEN + "Health Potion");
                        potmeta.setLore((List)Arrays.asList(ChatColor.GRAY + "A potion that restores " + ChatColor.GREEN + "75HP"));
                        final PotionMeta pm = (PotionMeta)potmeta;
                        pm.clearCustomEffects();
                        pm.addCustomEffect(PotionEffectType.HEAL.createEffect(0, 0), true);
                        pot.setItemMeta((ItemMeta)pm);
                        return pot;
                    }
                    if (tier == 3) {
                        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)9);
                        final ItemMeta potmeta = pot.getItemMeta();
                        potmeta.setDisplayName(ChatColor.AQUA + "Major Health Potion");
                        potmeta.setLore((List)Arrays.asList(ChatColor.GRAY + "A potion that restores " + ChatColor.AQUA + "300HP"));
                        final PotionMeta pm = (PotionMeta)potmeta;
                        pm.clearCustomEffects();
                        pm.addCustomEffect(PotionEffectType.HEAL.createEffect(0, 0), true);
                        pot.setItemMeta((ItemMeta)pm);
                        return pot;
                    }
                    if (tier == 4) {
                        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)12);
                        final ItemMeta potmeta = pot.getItemMeta();
                        potmeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Superior Health Potion");
                        potmeta.setLore((List)Arrays.asList(ChatColor.GRAY + "A potion that restores " + ChatColor.LIGHT_PURPLE + "750HP"));
                        final PotionMeta pm = (PotionMeta)potmeta;
                        pm.clearCustomEffects();
                        pm.addCustomEffect(PotionEffectType.HEAL.createEffect(0, 0), true);
                        pot.setItemMeta((ItemMeta)pm);
                        return pot;
                    }
                    if (tier == 5) {
                        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)3);
                        final ItemMeta potmeta = pot.getItemMeta();
                        potmeta.setDisplayName(ChatColor.YELLOW + "Legendary Health Potion");
                        potmeta.setLore((List)Arrays.asList(ChatColor.GRAY + "A potion that restores " + ChatColor.YELLOW + "1800HP"));
                        final PotionMeta pm = (PotionMeta)potmeta;
                        pm.clearCustomEffects();
                        pm.addCustomEffect(PotionEffectType.HEAL.createEffect(0, 0), true);
                        pot.setItemMeta((ItemMeta)pm);
                        return pot;
                    }
                }
                if (dodrop < 75) {
                    if (tier == 1) {
                        final int scrolltype = random.nextInt(2);
                        if (scrolltype == 0) {
                            return TeleportBooks.cyrennica_book();
                        }
                        if (scrolltype == 1) {
                            return TeleportBooks.harrison_book();
                        }
                    }
                    if (tier == 2) {
                        final int scrolltype = random.nextInt(5);
                        if (scrolltype == 0) {
                            return TeleportBooks.cyrennica_book();
                        }
                        if (scrolltype == 1) {
                            return TeleportBooks.harrison_book();
                        }
                        if (scrolltype == 2) {
                            return TeleportBooks.dark_oak_book();
                        }
                        if (scrolltype == 3) {
                            return TeleportBooks.trollsbane_book();
                        }
                        if (scrolltype == 4) {
                            return TeleportBooks.tripoli_book();
                        }
                    }
                    if (tier == 3) {
                        final int scrolltype = random.nextInt(5);
                        if (scrolltype == 0) {
                            return TeleportBooks.cyrennica_book();
                        }
                        if (scrolltype == 1) {
                            return TeleportBooks.dark_oak_book();
                        }
                        if (scrolltype == 2) {
                            return TeleportBooks.trollsbane_book();
                        }
                        if (scrolltype == 3) {
                            return TeleportBooks.gloomy_book();
                        }
                        if (scrolltype == 4) {
                            return TeleportBooks.crestguard_book();
                        }
                    }
                    if (tier == 4) {
                        final int scrolltype = random.nextInt(4);
                        if (scrolltype == 0) {
                            return TeleportBooks.deadpeaks_book();
                        }
                        if (scrolltype == 1) {
                            return TeleportBooks.gloomy_book();
                        }
                        if (scrolltype == 2) {
                            return TeleportBooks.crestwatch_book();
                        }
                        if (scrolltype == 3) {
                            return TeleportBooks.crestguard_book();
                        }
                    }
                    if (tier == 5) {
                        final int scrolltype = random.nextInt(4);
                        if (scrolltype == 0) {
                            return TeleportBooks.deadpeaks_book();
                        }
                        if (scrolltype == 1) {
                            return TeleportBooks.gloomy_book();
                        }
                        if (scrolltype == 2) {
                            return TeleportBooks.crestwatch_book();
                        }
                        if (scrolltype == 3) {
                            return TeleportBooks.crestguard_book();
                        }
                    }
                }
                if (dodrop < 100) {
                    final int whatfoodtodrop = random.nextInt(5);
                    if (whatfoodtodrop == 0) {
                        return new ItemStack(Material.COOKED_BEEF);
                    }
                    if (whatfoodtodrop == 1) {
                        return new ItemStack(Material.BREAD);
                    }
                    if (whatfoodtodrop == 2) {
                        return new ItemStack(Material.PUMPKIN_PIE);
                    }
                    if (whatfoodtodrop == 3) {
                        return new ItemStack(Material.APPLE);
                    }
                    if (whatfoodtodrop == 4) {
                        return new ItemStack(Material.MELON);
                    }
                }
                return new ItemStack(Material.AIR);
            }
            int gemamt = 0;
            if (tier == 1) {
                gemamt = random.nextInt(9) + 8;
            }
            if (tier == 2) {
                gemamt = random.nextInt(17) + 16;
            }
            if (tier == 3) {
                gemamt = random.nextInt(33) + 32;
            }
            if (tier == 4) {
                gemamt = random.nextInt(257) + 256;
            }
            if (tier == 5) {
                gemamt = random.nextInt(513) + 512;
            }
            if (gemamt > 64) {
                return Money.createBankNote(gemamt);
            }
            return Money.makeGems(gemamt);
        }
    }
}
