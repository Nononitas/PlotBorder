package de.nononitas.plotborder;

import de.nononitas.plotborder.util.BorderChanger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();


        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if(event.getClickedInventory() == null || event.getCurrentItem() == null) {
            return;
        }
        if(!event.getView().getTitle().equals(PlotBorder.getColoredConfigString("gui-border-title"))
                && !event.getView().getTitle().equals(PlotBorder.getColoredConfigString("gui-wall-title"))) {
            return;
        }
        event.setCancelled(true);
        if((event.getClickedInventory() != event.getView().getTopInventory())) {
            return;
        }
        Gui.Type componentType = (event.getView().getTitle().equals(PlotBorder.getColoredConfigString("gui-border-title"))) ? Gui.Type.BORDER : Gui.Type.WALL;
        String type = componentType.getType();

        if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
            if(event.getSlot() > (PlotBorder.getPlugin().getConfig().getInt("gui-" + type + "-rows") * 9 - 1)) {
                int newPage = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName()
                        .replace(PlotBorder.getColoredConfigString("page") + " ", "")) - 1;
                Gui.openGui(componentType, ((Player) event.getWhoClicked()).getPlayer(), newPage);
                PlotBorder.guiPage.put(p.getUniqueId(), newPage);
                return;
            }
        }

        int page = PlotBorder.guiPage.get(p.getUniqueId());
        int invSize = event.getClickedInventory().getSize() - 9;
        int startSlot = page * invSize;
        if(!PlotBorder.hasPlayerCooldown(p)) {
            int i = 0, slot = 0;
            for (String section : PlotBorder.getPlugin().getConfig().getConfigurationSection(type + "-items").getKeys(false)) {
                if(slot >= startSlot) {
                    if(i == event.getSlot()) {
                        section = type + "-items." + section;
                        String materialToSet = PlotBorder.getPlugin().getConfig().getString(section + ".material");

                        String noPerms = PlotBorder.getColoredConfigString("no-permission");
                        if(!p.hasPermission(PlotBorder.getPlugin().getConfig().getString(section + ".permission")) && !p.hasPermission("plotborder.admin")
                                && !p.hasPermission("plotborder." + type + ".")) {
                            p.sendMessage(noPerms);
                            Bukkit.getScheduler().runTask(PlotBorder.getPlugin(), p::closeInventory);

                        } else {
                            BorderChanger.change(componentType, p, materialToSet, event.getCurrentItem().getItemMeta().getDisplayName());
                        }

                        break;
                    }
                    i++;
                }
                slot++;

            }


        } else {
            String cooldown = PlotBorder.getColoredConfigString("cooldown-m-border");
            cooldown = cooldown.replaceAll("%time%", String.valueOf(PlotBorder.getCooldown(p)));
            p.sendMessage(cooldown);
            Bukkit.getScheduler().runTask(PlotBorder.getPlugin(), p::closeInventory);
        }
    }
}
