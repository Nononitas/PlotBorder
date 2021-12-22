package de.nononitas.plotborder;

import de.nononitas.plotborder.util.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Objects;

public class Gui {
    public static void openGui(Type guiType, Player p, int page) {
        String type = guiType.getType();
        FileConfiguration config = PlotBorder.getPlugin().getConfig();

        String title = PlotBorder.color(config.getString("gui-" + type + "-title"));
        int invSize = config.getInt("gui-" + type + "-rows") * 9;
        Inventory inv = Bukkit.createInventory(null, (config.getInt("gui-" + type + "-rows") + 1) * 9, title);

        int index = 0;
        int startSlot = page * invSize;
        int slot = 0;
        for (String section : config.getConfigurationSection(type + "-items").getKeys(false)) {
            if(slot >= startSlot) {
                if(index < invSize) {


                    section = type + "-items." + section;

                    String material = config.getString(section + ".display-material");
                    String displayname = config.getString(section + ".displayname");
                    List<String> lore;
                    if(p.hasPermission(Objects.requireNonNull(config.getString(section + ".permission")))) {
                        lore = config.getStringList(section + ".lore-with-perm");
                    } else {
                        lore = config.getStringList(section + ".lore-without-perm");
                    }


                    ItemStack item = getItem(material, displayname, lore);
                    inv.setItem(index, item);

                    index++;
                } else break;
            }
            slot++;

        }

        ItemStack lastRowPlaceHolderItem = new ItemStack(Material.valueOf(config.getString("last-row-material")));
        for (int i = invSize; i < invSize + 9; i++){
            inv.setItem(i, lastRowPlaceHolderItem);
        }

        ItemStack arrowRight = Heads.WHITE_ARROW_RIGHT.getItemStack();
        ItemStack arrowLeft = Heads.WHITE_ARROW_LEFT.getItemStack();
        SkullMeta meta;
        if(PlotBorder.getPlugin().getConfig().getConfigurationSection(type + "-items").getKeys(false).size() - 1 > slot) {
            meta = (SkullMeta) arrowRight.getItemMeta();
            meta.setDisplayName(PlotBorder.getColoredConfigString("page") + " " + (page + 2));
            arrowRight.setItemMeta(meta);
            inv.setItem(invSize - 1 + 7, arrowRight);
        }
        if(page != 0) {
            meta = (SkullMeta) arrowLeft.getItemMeta();
            meta.setDisplayName(PlotBorder.getColoredConfigString("page") + " " + page);
            arrowLeft.setItemMeta(meta);
            inv.setItem(invSize - 1 + 3, arrowLeft);
        }
        p.openInventory(inv);
    }

    private static ItemStack getItem(String materialString, String displayname, List<String> lore) {
        materialString = materialString.toUpperCase();


        if(Material.getMaterial(materialString) == null) {
            Bukkit.getConsoleSender().sendMessage(PlotBorder.PREFIX + "§4" + materialString + "§c is not a valid material");
            Bukkit.getConsoleSender().sendMessage(PlotBorder.PREFIX + "§cPlease check the config.yml");
            materialString = "AIR";
        }
        Material material = Material.getMaterial(materialString);

        ItemStack item = new ItemStack(material);


        ItemMeta meta = item.getItemMeta();
        if(item.getType() != Material.AIR) {
            meta.setDisplayName(PlotBorder.color(displayname));

            for (int index = 0; index < lore.size(); index++) {
                lore.set(index, PlotBorder.color(lore.get(index)));
            }

            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        return item;
    }

    public enum Type {
        WALL("wall"),
        BORDER("border");
        private final String type;

        Type(String type) {
            this.type = type;

        }

        public String getType() {
            return type;
        }
    }
}
