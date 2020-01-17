package me.AnFun.VKLegacy;

import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Journal
{
    public static ItemStack journal() {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta bm = (BookMeta)book.getItemMeta();
        bm.setDisplayName(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("Character Journal").toString());
        bm.setLore((List)Arrays.asList(ChatColor.GRAY + "A book that displays", ChatColor.GRAY + "your character's stats"));
        book.setItemMeta((ItemMeta)bm);
        return book;
    }
}
