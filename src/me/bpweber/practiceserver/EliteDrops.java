package me.bpweber.practiceserver;

import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class EliteDrops
{
    public static ItemStack createCustomEliteDrop(final String mobname) {
        String name = "";
        String llore = ChatColor.GRAY.toString();
        final ItemStack is = new ItemStack(Material.AIR);
        final List<String> lore = new ArrayList<String>();
        final Random random = new Random();
        final int item = random.nextInt(8) + 1;
        int tier = 0;
        String rarity = new StringBuilder().append(ChatColor.YELLOW).append(ChatColor.ITALIC).append("Unique").toString();
        int armdps = 0;
        int nrghp = 0;
        int elem = 0;
        int pure = 0;
        int life = 0;
        int crit = 0;
        int acc = 0;
        int dodge = 0;
        int block = 0;
        int vit = 0;
        int str = 0;
        int intel = 0;
        int hp = 0;
        int mindmg = 0;
        int maxdmg = 0;
        int dpsamt = 0;
        int dodgeamt = 0;
        int blockamt = 0;
        int vitamt = 0;
        int stramt = 0;
        int intamt = 0;
        int elemamt = 0;
        int pureamt = 0;
        int lifeamt = 0;
        int critamt = 0;
        int accamt = 0;
        int hps = 0;
        int nrg = 0;
        if (mobname.equalsIgnoreCase("mitsuki")) {
            nrghp = 2;
            armdps = 1;
            block = 1;
            str = 1;
            elem = 1;
            block = 1;
            life = 1;
            elemamt = 5;
            lifeamt = random.nextInt(16) + 30;
            mindmg = random.nextInt(7) + 6;
            maxdmg = random.nextInt(9) + 17;
            if (item <= 4) {
                name = "Mitsukis Sword of Bloodthirst";
                is.setType(Material.WOOD_SWORD);
                llore = String.valueOf(llore) + "The Master of Ruins blood-stained ridged Sword.";
            }
            if (item == 5) {
                nrg = random.nextInt(2) + 2;
                blockamt = random.nextInt(2) + 2;
                dpsamt = 1;
                stramt = 10;
                hp = random.nextInt(21) + 40;
                name = "Mitsukis Leather Coif";
                is.setType(Material.LEATHER_HELMET);
                llore = String.valueOf(llore) + "A ripped remains of a Leather Coif far from industry standards.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 6) {
                nrg = random.nextInt(2) + 4;
                blockamt = random.nextInt(2) + 5;
                stramt = 25;
                dpsamt = random.nextInt(2) + 2;
                hp = random.nextInt(11) + 100;
                name = "Mitsukis Dirty Leather Rags";
                is.setType(Material.LEATHER_CHESTPLATE);
                llore = String.valueOf(llore) + "Blood stained rags that reek of Zombie flesh";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 7) {
                nrg = random.nextInt(2) + 4;
                blockamt = random.nextInt(2) + 5;
                stramt = 25;
                dpsamt = random.nextInt(2) + 2;
                hp = random.nextInt(11) + 100;
                name = "Mitsukis Ripped Leather Pants";
                is.setType(Material.LEATHER_LEGGINGS);
                llore = String.valueOf(llore) + "Can be referred to as 'shorts' due to intensive ripping.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 8) {
                nrg = random.nextInt(2) + 2;
                blockamt = random.nextInt(2) + 2;
                dpsamt = 1;
                stramt = 10;
                hp = random.nextInt(21) + 40;
                name = "Mitsukis Leather Sandals";
                is.setType(Material.LEATHER_BOOTS);
                llore = String.valueOf(llore) + "Blood stained sandals. Not very comfortable.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            tier = 1;
        }
        if (mobname.equalsIgnoreCase("copjak")) {
            nrghp = 2;
            armdps = 1;
            str = 1;
            elem = 2;
            elemamt = 12;
            crit = 1;
            critamt = random.nextInt(3) + 8;
            mindmg = random.nextInt(3) + 10;
            maxdmg = random.nextInt(6) + 13;
            if (item <= 4) {
                name = "Cop'Jaks Deadly Poleaxe";
                is.setType(Material.STONE_SPADE);
                llore = String.valueOf(llore) + "A long wicked Poleaxe of Trollish design.";
            }
            if (item == 5) {
                stramt = 25;
                nrg = random.nextInt(3) + 3;
                hp = random.nextInt(31) + 140;
                dpsamt = random.nextInt(2) + 3;
                name = "Cop'Jaks Shaman Headgear";
                is.setType(Material.CHAINMAIL_HELMET);
                llore = String.valueOf(llore) + "A standard Shamans headgear consisting of a bears head.";
            }
            if (item == 6) {
                stramt = 45;
                nrg = random.nextInt(2) + 7;
                hp = random.nextInt(71) + 300;
                dpsamt = random.nextInt(2) + 6;
                name = "Cop'Jaks greased Chainmail Chestpiece";
                is.setType(Material.CHAINMAIL_CHESTPLATE);
                llore = String.valueOf(llore) + "A bad fit made for the broad chests of Trolls.";
            }
            if (item == 7) {
                stramt = 45;
                nrg = random.nextInt(2) + 7;
                hp = random.nextInt(71) + 300;
                dpsamt = random.nextInt(2) + 6;
                name = "Cop'Jaks Chainlinked Pants";
                is.setType(Material.CHAINMAIL_LEGGINGS);
                llore = String.valueOf(llore) + "Large greased and ready for action.";
            }
            if (item == 8) {
                stramt = 25;
                nrg = random.nextInt(3) + 3;
                hp = random.nextInt(31) + 140;
                dpsamt = random.nextInt(2) + 3;
                name = "Cop'Jaks Chainmail Boots";
                is.setType(Material.CHAINMAIL_BOOTS);
                llore = String.valueOf(llore) + "Spiked Chainmail boots.";
            }
            tier = 2;
        }
        if (mobname.equalsIgnoreCase("impa")) {
            nrghp = 2;
            armdps = 1;
            str = 1;
            block = 1;
            elem = 2;
            crit = 1;
            elemamt = 15;
            critamt = random.nextInt(2) + 8;
            mindmg = random.nextInt(11) + 40;
            maxdmg = random.nextInt(11) + 70;
            if (item <= 4) {
                name = "Impas Dreaded Polearm";
                is.setType(Material.IRON_SPADE);
                llore = String.valueOf(llore) + "The spearhead of the initial attack on Avalon.";
            }
            if (item == 5) {
                stramt = 75;
                blockamt = 8;
                nrg = random.nextInt(3) + 3;
                hp = random.nextInt(86) + 375;
                dpsamt = random.nextInt(2) + 5;
                name = "Crooked Battle Mask";
                is.setType(Material.IRON_HELMET);
                llore = String.valueOf(llore) + "A skeleton generals black mask";
            }
            if (item == 6) {
                stramt = 75;
                blockamt = 8;
                nrg = random.nextInt(2) + 5;
                hp = random.nextInt(99) + 701;
                dpsamt = random.nextInt(2) + 5;
                name = "Haunting Platemail of Avalons Fright";
                is.setType(Material.IRON_CHESTPLATE);
                llore = String.valueOf(llore) + "A breastplate with the symbol of Impas army carved into it.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 7) {
                stramt = 75;
                blockamt = 8;
                nrg = random.nextInt(2) + 5;
                hp = random.nextInt(99) + 701;
                dpsamt = random.nextInt(2) + 5;
                name = "Warding Skeletal Leggings";
                is.setType(Material.IRON_LEGGINGS);
                llore = String.valueOf(llore) + "Spiked bone leggings of greater skeleton invaders.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 8) {
                stramt = 75;
                blockamt = 8;
                nrg = random.nextInt(3) + 3;
                hp = random.nextInt(86) + 375;
                dpsamt = random.nextInt(2) + 5;
                name = "Skeletal Death Walkers";
                is.setType(Material.IRON_BOOTS);
                llore = String.valueOf(llore) + "The boots with which Impa treaded into this land.";
            }
            tier = 3;
        }
        if (mobname.equalsIgnoreCase("skeletonking")) {
            nrghp = 1;
            armdps = 1;
            vit = 1;
            pure = 1;
            acc = 1;
            pureamt = 25;
            accamt = random.nextInt(8) + 7;
            mindmg = random.nextInt(6) + 45;
            maxdmg = random.nextInt(24) + 67;
            if (item <= 4) {
                name = "The Skeleton Kings Sword of Banishment";
                is.setType(Material.IRON_SWORD);
                llore = String.valueOf(llore) + "A powerful sword enhanced with the soul of the Skeleton King.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 5) {
                vitamt = 49;
                hps = random.nextInt(11) + 30;
                hp = random.nextInt(60) + 400;
                dpsamt = random.nextInt(2) + 4;
                name = "The Skeleton Kings Soul Helmet";
                is.setType(Material.IRON_HELMET);
                llore = String.valueOf(llore) + "A shadowy transparent helmet.";
            }
            if (item == 6) {
                vitamt = 99;
                hps = random.nextInt(11) + 60;
                hp = random.nextInt(200) + 800;
                dpsamt = 8;
                name = "The Skeleton Kings Soul Armour";
                is.setType(Material.IRON_CHESTPLATE);
                llore = String.valueOf(llore) + "Armor imbued with the power of the Skeleton king.";
            }
            if (item == 7) {
                vitamt = 99;
                hps = random.nextInt(11) + 60;
                hp = random.nextInt(200) + 800;
                dpsamt = 8;
                name = "The Skeleton Kings Soul Leggings";
                is.setType(Material.IRON_LEGGINGS);
                llore = String.valueOf(llore) + "Resistant to the most powerful of Physical Damage.";
            }
            if (item == 8) {
                vitamt = 49;
                hps = random.nextInt(11) + 30;
                hp = random.nextInt(60) + 400;
                dpsamt = random.nextInt(2) + 4;
                name = "The Skeleton Kings Soul Boots";
                is.setType(Material.IRON_BOOTS);
                llore = String.valueOf(llore) + "The shining boots of a king.";
            }
            tier = 3;
        }
        if (mobname.equalsIgnoreCase("blayshan")) {
            nrghp = 2;
            armdps = 2;
            elem = 3;
            str = 1;
            elemamt = 70;
            crit = 1;
            critamt = random.nextInt(3) + 8;
            mindmg = random.nextInt(11) + 180;
            maxdmg = random.nextInt(31) + 210;
            if (item <= 4) {
                name = "Blayshans Wicked Axe";
                is.setType(Material.DIAMOND_AXE);
                llore = String.valueOf(llore) + "An Axe with the face of the cursed Blayshan carved into it.";
            }
            if (item == 5) {
                dpsamt = random.nextInt(3) + 5;
                stramt = 145;
                hp = random.nextInt(401) + 800;
                nrg = random.nextInt(2) + 4;
                name = "Blayshans Accursed Helmet";
                is.setType(Material.DIAMOND_HELMET);
                llore = String.valueOf(llore) + "A weirdly shaped aqua blue helmet.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 6) {
                dpsamt = random.nextInt(2) + 6;
                stramt = 245;
                hp = random.nextInt(101) + 1800;
                nrg = random.nextInt(3) + 8;
                name = "Blayshans Wicked Horned Platemail";
                is.setType(Material.DIAMOND_CHESTPLATE);
                llore = String.valueOf(llore) + "Not well made but light with studded mail fists.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 7) {
                dpsamt = random.nextInt(2) + 6;
                stramt = 245;
                hp = random.nextInt(101) + 1800;
                nrg = random.nextInt(3) + 8;
                name = "Blayshans Wicked Horned Leggings";
                is.setType(Material.DIAMOND_LEGGINGS);
                llore = String.valueOf(llore) + "Glistening with the blood of fallen enemies.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 8) {
                dpsamt = random.nextInt(3) + 5;
                stramt = 145;
                hp = random.nextInt(401) + 800;
                nrg = random.nextInt(2) + 4;
                name = "Blayshans Platemail Boots";
                is.setType(Material.DIAMOND_BOOTS);
                llore = String.valueOf(llore) + "A pair of boots shaped to fit a Naga.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            tier = 4;
        }
        if (mobname.equalsIgnoreCase("kilatan")) {
            nrghp = 2;
            armdps = 2;
            elem = 1;
            intel = 1;
            dodge = 1;
            elemamt = 30;
            mindmg = random.nextInt(11) + 140;
            maxdmg = random.nextInt(11) + 170;
            if (item <= 4) {
                name = "Kilatans Staff of Destruction";
                is.setType(Material.GOLD_HOE);
                llore = String.valueOf(llore) + "A powerful staff imbued with the magics of Kilatan";
            }
            if (item == 5) {
                dpsamt = random.nextInt(2) + 6;
                intamt = 145;
                hp = random.nextInt(601) + 1400;
                nrg = random.nextInt(6) + 3;
                dodgeamt = random.nextInt(3) + 5;
                name = "Kilatans Crown of Death";
                is.setType(Material.GOLD_HELMET);
                llore = String.valueOf(llore) + "A golden crown of tyranny and power.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 6) {
                dpsamt = random.nextInt(4) + 12;
                intamt = 345;
                hp = random.nextInt(1101) + 2800;
                nrg = random.nextInt(5) + 6;
                dodgeamt = random.nextInt(11) + 10;
                name = "Kilatans Legendary Platemail";
                is.setType(Material.GOLD_CHESTPLATE);
                llore = String.valueOf(llore) + "The Legendary platemail piece of the Demon Lord Kilatan.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 7) {
                dpsamt = random.nextInt(4) + 12;
                intamt = 345;
                hp = random.nextInt(1101) + 2800;
                nrg = random.nextInt(5) + 6;
                dodgeamt = random.nextInt(11) + 10;
                name = "Kilatans Legendary Leggings";
                is.setType(Material.GOLD_LEGGINGS);
                llore = String.valueOf(llore) + "You can feel the power emanating from this armor piece.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            if (item == 8) {
                dpsamt = random.nextInt(2) + 6;
                intamt = 145;
                hp = random.nextInt(601) + 1400;
                nrg = random.nextInt(6) + 3;
                dodgeamt = random.nextInt(3) + 5;
                name = "Kilatans Legendary Boots";
                is.setType(Material.GOLD_BOOTS);
                llore = String.valueOf(llore) + "Boots that carried the weight of the underworld.";
                rarity = new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("Rare").toString();
            }
            tier = 5;
        }
        if (item <= 4) {
            lore.add(ChatColor.RED + "DMG: " + mindmg + " - " + maxdmg);
            if (pure == 1) {
                lore.add(ChatColor.RED + "PURE DMG: +" + pureamt);
            }
            if (acc == 1) {
                lore.add(ChatColor.RED + "ACCURACY: " + accamt + "%");
            }
            if (life == 1) {
                lore.add(ChatColor.RED + "LIFE STEAL: " + lifeamt + "%");
            }
            if (crit == 1) {
                lore.add(ChatColor.RED + "CRITICAL HIT: " + critamt + "%");
            }
            if (elem == 3) {
                lore.add(ChatColor.RED + "ICE DMG: +" + elemamt);
            }
            if (elem == 2) {
                lore.add(ChatColor.RED + "POISON DMG: +" + elemamt);
            }
            if (elem == 1) {
                lore.add(ChatColor.RED + "FIRE DMG: +" + elemamt);
            }
        }
        if (item == 5 || item == 6 || item == 7 || item == 8) {
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
            }
            if (block == 1) {
                lore.add(ChatColor.RED + "BLOCK: " + blockamt + "%");
            }
        }
        lore.add(llore);
        lore.add(rarity);
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
