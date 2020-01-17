package me.AnFun.VKLegacy;

import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import org.bukkit.ChatColor;
import java.util.Random;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Drops
{
    public static ItemStack createDrop(int tier, final int item) {
        String name = "";
        String rare = "";
        final ItemStack is = new ItemStack(Material.AIR);
        final List<String> lore = new ArrayList<String>();
        final Random random = new Random();
        int rarity = 0;
        final int armdps = random.nextInt(2) + 1;
        final int nrghp = random.nextInt(2) + 1;
        final int elem = random.nextInt(12) + 1;
        final int pure = random.nextInt(4) + 1;
        final int life = random.nextInt(4) + 1;
        final int crit = random.nextInt(4) + 1;
        final int acc = random.nextInt(4) + 1;
        final int dodge = random.nextInt(3) + 1;
        final int block = random.nextInt(3) + 1;
        final int vit = random.nextInt(3) + 1;
        final int str = random.nextInt(3) + 1;
        final int intel = random.nextInt(3) + 1;
        int hp = 0;
        int mindmg = 0;
        int maxdmg = 0;
        int dpsamt = 0;
        int dodgeamt = 0;
        int blockamt = 0;
        int vitamt = 0;
        int intamt = 0;
        int stramt = 0;
        int elemamt = 0;
        int pureamt = 0;
        int lifeamt = 0;
        int critamt = 0;
        int accamt = 0;
        int hps = 0;
        int nrg = 0;
        final int r = random.nextInt(100);
        if (r < 1) {
            rarity = 3;
            rare = new StringBuilder().append(ChatColor.YELLOW).append(ChatColor.ITALIC).append("Unique").toString();
        }
        else if (r < 5) {
            rarity = 2;
            rare = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
        }
        else if (r < 25) {
            rarity = 1;
            rare = new StringBuilder().append(ChatColor.GREEN).append(ChatColor.ITALIC).append("Uncommon").toString();
        }
        else if (r < 100) {
            rarity = 0;
            rare = new StringBuilder().append(ChatColor.GRAY).append(ChatColor.ITALIC).append("Common").toString();
        }
        if (tier == 1) {
            tier = 1;
            if (rarity == 0) {
                dpsamt = 1;
                hp = random.nextInt(21) + 10;
                final int min_min_dmg = 1;
                final int max_min_dmg = 3;
                final int max_max_dmg = 6;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 1) {
                dpsamt = random.nextInt(3) + 1;
                hp = random.nextInt(30) + 31;
                final int min_min_dmg = 3;
                final int max_min_dmg = 6;
                final int max_max_dmg = 9;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 2) {
                dpsamt = random.nextInt(3) + 1;
                hp = random.nextInt(30) + 61;
                final int min_min_dmg = 6;
                final int max_min_dmg = 10;
                final int max_max_dmg = 24;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 3) {
                dpsamt = random.nextInt(3) + 1;
                hp = random.nextInt(30) + 91;
                final int min_min_dmg = 9;
                final int max_min_dmg = 16;
                final int max_max_dmg = 25;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            hps = random.nextInt(11) + 5;
            nrg = random.nextInt(3) + 1;
            dodgeamt = random.nextInt(5) + 1;
            blockamt = random.nextInt(5) + 1;
            vitamt = random.nextInt(15) + 1;
            stramt = random.nextInt(15) + 1;
            intamt = random.nextInt(15) + 1;
            elemamt = random.nextInt(4) + 1;
            pureamt = random.nextInt(4) + 1;
            lifeamt = random.nextInt(30) + 1;
            critamt = random.nextInt(3) + 1;
            accamt = random.nextInt(10) + 1;
            if (item == 0) {
                name = "Staff";
                is.setType(Material.WOOD_HOE);
            }
            if (item == 1) {
                name = "Spear";
                is.setType(Material.WOOD_SPADE);
            }
            if (item == 2) {
                name = "Shortsword";
                is.setType(Material.WOOD_SWORD);
            }
            if (item == 3) {
                name = "Hatchet";
                is.setType(Material.WOOD_AXE);
            }
            if (item == 4) {
                name = "Leather Coif";
                is.setType(Material.LEATHER_HELMET);
            }
            if (item == 5) {
                name = "Leather Chestplate";
                is.setType(Material.LEATHER_CHESTPLATE);
            }
            if (item == 6) {
                name = "Leather Leggings";
                is.setType(Material.LEATHER_LEGGINGS);
            }
            if (item == 7) {
                name = "Leather Boots";
                is.setType(Material.LEATHER_BOOTS);
            }
        }
        if (tier == 2) {
            tier = 2;
            if (rarity == 0) {
                dpsamt = random.nextInt(3) + 1;
                hp = random.nextInt(41) + 70;
                final int min_min_dmg = 10;
                final int max_min_dmg = 13;
                final int max_max_dmg = 18;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 1) {
                dpsamt = random.nextInt(3) + 3;
                hp = random.nextInt(80) + 111;
                final int min_min_dmg = 16;
                final int max_min_dmg = 19;
                final int max_max_dmg = 25;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 2) {
                dpsamt = random.nextInt(3) + 5;
                hp = random.nextInt(50) + 191;
                final int min_min_dmg = 20;
                final int max_min_dmg = 31;
                final int max_max_dmg = 66;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 3) {
                dpsamt = random.nextInt(3) + 5;
                hp = random.nextInt(70) + 241;
                final int min_min_dmg = 22;
                final int max_min_dmg = 36;
                final int max_max_dmg = 71;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            hps = random.nextInt(16) + 10;
            nrg = random.nextInt(3) + 1;
            dodgeamt = random.nextInt(8) + 1;
            blockamt = random.nextInt(8) + 1;
            vitamt = random.nextInt(35) + 1;
            stramt = random.nextInt(35) + 1;
            intamt = random.nextInt(35) + 1;
            elemamt = random.nextInt(9) + 1;
            pureamt = random.nextInt(9) + 1;
            lifeamt = random.nextInt(15) + 1;
            critamt = random.nextInt(6) + 1;
            accamt = random.nextInt(12) + 1;
            if (item == 0) {
                name = "Battletaff";
                is.setType(Material.STONE_HOE);
            }
            if (item == 1) {
                name = "Halberd";
                is.setType(Material.STONE_SPADE);
            }
            if (item == 2) {
                name = "Broadsword";
                is.setType(Material.STONE_SWORD);
            }
            if (item == 3) {
                name = "Great Axe";
                is.setType(Material.STONE_AXE);
            }
            if (item == 4) {
                name = "Medium Helmet";
                is.setType(Material.CHAINMAIL_HELMET);
            }
            if (item == 5) {
                name = "Chainmail";
                is.setType(Material.CHAINMAIL_CHESTPLATE);
            }
            if (item == 6) {
                name = "Chainmail Leggings";
                is.setType(Material.CHAINMAIL_LEGGINGS);
            }
            if (item == 7) {
                name = "Chainmail Boots";
                is.setType(Material.CHAINMAIL_BOOTS);
            }
        }
        if (tier == 3) {
            tier = 3;
            if (rarity == 0) {
                dpsamt = random.nextInt(3) + 5;
                hp = random.nextInt(251) + 200;
                final int min_min_dmg = 25;
                final int max_min_dmg = 31;
                final int max_max_dmg = 46;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 1) {
                dpsamt = random.nextInt(5) + 6;
                hp = random.nextInt(200) + 451;
                final int min_min_dmg = 30;
                final int max_min_dmg = 36;
                final int max_max_dmg = 71;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 2) {
                dpsamt = random.nextInt(3) + 8;
                hp = random.nextInt(99) + 651;
                final int min_min_dmg = 50;
                final int max_min_dmg = 91;
                final int max_max_dmg = 151;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 3) {
                dpsamt = random.nextInt(4) + 8;
                hp = random.nextInt(101) + 750;
                final int min_min_dmg = 60;
                final int max_min_dmg = 101;
                final int max_max_dmg = 161;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            hps = random.nextInt(21) + 35;
            nrg = random.nextInt(3) + 2;
            dodgeamt = random.nextInt(10) + 1;
            blockamt = random.nextInt(10) + 1;
            vitamt = random.nextInt(75) + 1;
            stramt = random.nextInt(75) + 1;
            intamt = random.nextInt(75) + 1;
            elemamt = random.nextInt(15) + 1;
            pureamt = random.nextInt(15) + 1;
            lifeamt = random.nextInt(12) + 1;
            critamt = random.nextInt(8) + 1;
            accamt = random.nextInt(25) + 1;
            if (item == 0) {
                name = "Wizard Staff";
                is.setType(Material.IRON_HOE);
            }
            if (item == 1) {
                name = "Magic Polearm";
                is.setType(Material.IRON_SPADE);
            }
            if (item == 2) {
                name = "Magic Sword";
                is.setType(Material.IRON_SWORD);
            }
            if (item == 3) {
                name = "War Axe";
                is.setType(Material.IRON_AXE);
            }
            if (item == 4) {
                name = "Full Helmet";
                is.setType(Material.IRON_HELMET);
            }
            if (item == 5) {
                name = "Platemail";
                is.setType(Material.IRON_CHESTPLATE);
            }
            if (item == 6) {
                name = "Platemail Leggings";
                is.setType(Material.IRON_LEGGINGS);
            }
            if (item == 7) {
                name = "Platemail Boots";
                is.setType(Material.IRON_BOOTS);
            }
        }
        if (tier == 4) {
            tier = 4;
            if (rarity == 0) {
                dpsamt = random.nextInt(3) + 8;
                hp = random.nextInt(311) + 650;
                final int min_min_dmg = 65;
                final int max_min_dmg = 81;
                final int max_max_dmg = 126;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 1) {
                dpsamt = random.nextInt(3) + 10;
                hp = random.nextInt(490) + 961;
                final int min_min_dmg = 70;
                final int max_min_dmg = 86;
                final int max_max_dmg = 156;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 2) {
                dpsamt = random.nextInt(3) + 11;
                hp = random.nextInt(850) + 1451;
                final int min_min_dmg = 90;
                final int max_min_dmg = 111;
                final int max_max_dmg = 221;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 3) {
                dpsamt = random.nextInt(3) + 12;
                hp = random.nextInt(500) + 2301;
                final int min_min_dmg = 110;
                final int max_min_dmg = 151;
                final int max_max_dmg = 241;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            hps = random.nextInt(16) + 60;
            nrg = random.nextInt(3) + 3;
            dodgeamt = random.nextInt(12) + 1;
            blockamt = random.nextInt(12) + 1;
            vitamt = random.nextInt(115) + 1;
            stramt = random.nextInt(115) + 1;
            intamt = random.nextInt(115) + 1;
            elemamt = random.nextInt(25) + 1;
            pureamt = random.nextInt(25) + 1;
            lifeamt = random.nextInt(10) + 1;
            critamt = random.nextInt(10) + 1;
            accamt = random.nextInt(28) + 1;
            if (item == 0) {
                name = "Ancient Staff";
                is.setType(Material.DIAMOND_HOE);
            }
            if (item == 1) {
                name = "Ancient Polearm";
                is.setType(Material.DIAMOND_SPADE);
            }
            if (item == 2) {
                name = "Ancient Sword";
                is.setType(Material.DIAMOND_SWORD);
            }
            if (item == 3) {
                name = "Ancient Axe";
                is.setType(Material.DIAMOND_AXE);
            }
            if (item == 4) {
                name = "Ancient Full Helmet";
                is.setType(Material.DIAMOND_HELMET);
            }
            if (item == 5) {
                name = "Magic Platemail";
                is.setType(Material.DIAMOND_CHESTPLATE);
            }
            if (item == 6) {
                name = "Magic Platemail Leggings";
                is.setType(Material.DIAMOND_LEGGINGS);
            }
            if (item == 7) {
                name = "Magic Platemail Boots";
                is.setType(Material.DIAMOND_BOOTS);
            }
        }
        if (tier == 5) {
            tier = 5;
            if (rarity == 0) {
                dpsamt = random.nextInt(3) + 11;
                hp = random.nextInt(1051) + 1450;
                final int min_min_dmg = 130;
                final int max_min_dmg = 141;
                final int max_max_dmg = 211;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 1) {
                dpsamt = random.nextInt(3) + 13;
                hp = random.nextInt(1300) + 2501;
                final int min_min_dmg = 150;
                final int max_min_dmg = 161;
                final int max_max_dmg = 261;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 2) {
                dpsamt = random.nextInt(3) + 16;
                hp = random.nextInt(1700) + 3801;
                final int min_min_dmg = 160;
                final int max_min_dmg = 231;
                final int max_max_dmg = 408;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            if (rarity == 3) {
                dpsamt = random.nextInt(3) + 17;
                hp = random.nextInt(500) + 5501;
                final int min_min_dmg = 190;
                final int max_min_dmg = 251;
                final int max_max_dmg = 451;
                mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
                maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
            }
            hps = random.nextInt(41) + 80;
            nrg = random.nextInt(3) + 3;
            dodgeamt = random.nextInt(12) + 1;
            blockamt = random.nextInt(12) + 1;
            vitamt = random.nextInt(315) + 1;
            stramt = random.nextInt(315) + 1;
            intamt = random.nextInt(315) + 1;
            elemamt = random.nextInt(55) + 1;
            pureamt = random.nextInt(55) + 1;
            lifeamt = random.nextInt(8) + 1;
            critamt = random.nextInt(11) + 1;
            accamt = random.nextInt(35) + 1;
            if (item == 0) {
                name = "Legendary Staff";
                is.setType(Material.GOLD_HOE);
            }
            if (item == 1) {
                name = "Legendary Polearm";
                is.setType(Material.GOLD_SPADE);
            }
            if (item == 2) {
                name = "Legendary Sword";
                is.setType(Material.GOLD_SWORD);
            }
            if (item == 3) {
                name = "Legendary Axe";
                is.setType(Material.GOLD_AXE);
            }
            if (item == 4) {
                name = "Legendary Full Helmet";
                is.setType(Material.GOLD_HELMET);
            }
            if (item == 5) {
                name = "Legendary Platemail";
                is.setType(Material.GOLD_CHESTPLATE);
            }
            if (item == 6) {
                name = "Legendary Platemail Leggings";
                is.setType(Material.GOLD_LEGGINGS);
            }
            if (item == 7) {
                name = "Legendary Platemail Boots";
                is.setType(Material.GOLD_BOOTS);
            }
        }
        if (item == 0 || item == 1) {
            mindmg *= (int)0.5;
            if (mindmg < 1) {
                mindmg = 1;
            }
            maxdmg *= (int)0.5;
            if (maxdmg < 1) {
                maxdmg = 1;
            }
            lore.add(ChatColor.RED + "DMG: " + mindmg + " - " + maxdmg);
            if (life == 1) {
                lore.add(ChatColor.RED + "LIFE STEAL: " + lifeamt + "%");
                name = "Vampyric " + name;
            }
            if (crit == 1) {
                lore.add(ChatColor.RED + "CRITICAL HIT: " + critamt + "%");
                name = "Deadly " + name;
            }
            if (elem == 3) {
                lore.add(ChatColor.RED + "ICE DMG: +" + elemamt);
                name = String.valueOf(name) + " of Ice";
            }
            if (elem == 2) {
                lore.add(ChatColor.RED + "POISON DMG: +" + elemamt);
                name = String.valueOf(name) + " of Poison";
            }
            if (elem == 1) {
                lore.add(ChatColor.RED + "FIRE DMG: +" + elemamt);
                name = String.valueOf(name) + " of Fire";
            }
        }
        if (item == 2) {
            lore.add(ChatColor.RED + "DMG: " + mindmg + " - " + maxdmg);
            if (acc == 1) {
                lore.add(ChatColor.RED + "ACCURACY: " + accamt + "%");
                name = "Accurate " + name;
            }
            if (life == 1) {
                lore.add(ChatColor.RED + "LIFE STEAL: " + lifeamt + "%");
                name = "Vampyric " + name;
            }
            if (crit == 1) {
                lore.add(ChatColor.RED + "CRITICAL HIT: " + critamt + "%");
                name = "Deadly " + name;
            }
            if (elem == 3) {
                lore.add(ChatColor.RED + "ICE DMG: +" + elemamt);
                name = String.valueOf(name) + " of Ice";
            }
            if (elem == 2) {
                lore.add(ChatColor.RED + "POISON DMG: +" + elemamt);
                name = String.valueOf(name) + " of Poison";
            }
            if (elem == 1) {
                lore.add(ChatColor.RED + "FIRE DMG: +" + elemamt);
                name = String.valueOf(name) + " of Fire";
            }
        }
        if (item == 3) {
            lore.add(ChatColor.RED + "DMG: " + (int)(mindmg * 1.2) + " - " + (int)(maxdmg * 1.2));
            if (pure == 1) {
                lore.add(ChatColor.RED + "PURE DMG: +" + pureamt);
                name = "Pure " + name;
            }
            if (life == 1) {
                lore.add(ChatColor.RED + "LIFE STEAL: " + lifeamt + "%");
                name = "Vampyric " + name;
            }
            if (crit == 1) {
                lore.add(ChatColor.RED + "CRITICAL HIT: " + critamt + "%");
                name = "Deadly " + name;
            }
            if (elem == 3) {
                lore.add(ChatColor.RED + "ICE DMG: +" + elemamt);
                name = String.valueOf(name) + " of Ice";
            }
            if (elem == 2) {
                lore.add(ChatColor.RED + "POISON DMG: +" + elemamt);
                name = String.valueOf(name) + " of Poison";
            }
            if (elem == 1) {
                lore.add(ChatColor.RED + "FIRE DMG: +" + elemamt);
                name = String.valueOf(name) + " of Fire";
            }
        }
        if (item == 4 || item == 7) {
            dpsamt *= (int)0.5;
            if (dpsamt < 1) {
                dpsamt = 1;
            }
            hp *= (int)0.5;
            if (hp < 1) {
                hp = 1;
            }
            if (armdps == 1) {
                lore.add(ChatColor.RED + "ARMOR: " + dpsamt + " - " + dpsamt + "%");
            }
            if (armdps == 2) {
                lore.add(ChatColor.RED + "DPS: " + dpsamt + " - " + dpsamt + "%");
            }
            lore.add(ChatColor.RED + "HP: +" + hp);
            if (nrghp == 2) {
                lore.add(ChatColor.RED + "ENERGY REGEN: +" + nrg + "%");
            }
            if (nrghp == 1) {
                lore.add(ChatColor.RED + "HP REGEN: +" + hps + " HP/s");
            }
            if (intel == 1) {
                lore.add(ChatColor.RED + "INT: +" + intamt);
            }
            if (str == 1) {
                lore.add(ChatColor.RED + "STR: +" + stramt);
            }
            if (vit == 1) {
                lore.add(ChatColor.RED + "VIT: +" + vitamt);
            }
            if (dodge == 1) {
                lore.add(ChatColor.RED + "DODGE: " + dodgeamt + "%");
                name = "Agile " + name;
            }
            if (nrghp == 1) {
                name = "Mending " + name;
            }
            if (block == 1) {
                lore.add(ChatColor.RED + "BLOCK: " + blockamt + "%");
                name = "Protective " + name;
            }
            if (nrghp == 2) {
                name = String.valueOf(name) + " of Fortitude";
            }
        }
        if (item == 5 || item == 6) {
            if (armdps == 1) {
                lore.add(ChatColor.RED + "ARMOR: " + dpsamt + " - " + dpsamt + "%");
            }
            if (armdps == 2) {
                lore.add(ChatColor.RED + "DPS: " + dpsamt + " - " + dpsamt + "%");
            }
            lore.add(ChatColor.RED + "HP: +" + hp);
            if (nrghp == 2) {
                lore.add(ChatColor.RED + "ENERGY REGEN: +" + nrg + "%");
            }
            if (nrghp == 1) {
                lore.add(ChatColor.RED + "HP REGEN: +" + hps + " HP/s");
            }
            if (intel == 1) {
                lore.add(ChatColor.RED + "INT: +" + intamt);
            }
            if (str == 1) {
                lore.add(ChatColor.RED + "STR: +" + stramt);
            }
            if (vit == 1) {
                lore.add(ChatColor.RED + "VIT: +" + vitamt);
            }
            if (dodge == 1) {
                lore.add(ChatColor.RED + "DODGE: " + dodgeamt + "%");
                name = "Agile " + name;
            }
            if (nrghp == 1) {
                name = "Mending " + name;
            }
            if (block == 1) {
                lore.add(ChatColor.RED + "BLOCK: " + blockamt + "%");
                name = "Protective " + name;
            }
            if (nrghp == 2) {
                name = String.valueOf(name) + " of Fortitude";
            }
        }
        lore.add(rare);
        if (tier == 1) {
            name = ChatColor.WHITE + name;
        }
        if (tier == 2) {
            name = ChatColor.GREEN + name;
        }
        if (tier == 3) {
            name = ChatColor.AQUA + name;
        }
        if (tier == 4) {
            name = ChatColor.LIGHT_PURPLE + name;
        }
        if (tier == 5) {
            name = ChatColor.YELLOW + name;
        }
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        im.setLore((List)lore);
        is.setItemMeta(im);
        return is;
    }
}
