package de.nononitas.plotborder.versions;

import com.github.intellectualsites.plotsquared.api.PlotAPI;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import de.nononitas.plotborder.Gui;
import de.nononitas.plotborder.PlotBorder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class BorderChangerV4 implements BorderChanger {
    @Override
    public void change(Gui.Type componentType, Player p, String materialToSet, String componentName) {
        PlotAPI plotAPI = new PlotAPI();
        PlotPlayer plotPlayer = plotAPI.wrapPlayer(p.getUniqueId());
        Plot plot = plotPlayer.getCurrentPlot();
        String type = componentType.getType();
        if(plot != null) {
            boolean isComponentChanged = false;
            if(plot.getConnectedPlots().size() > 1) {

                for (Plot plots : plot.getConnectedPlots()) {
                    if(plots.getOwners().contains(p.getUniqueId()) || p.hasPermission("plotborder.admin")) {
                        plots.setComponent(type, materialToSet);
                        isComponentChanged = true;
                    } else {
                        p.sendMessage(notYourPlot);
                        break;
                    }
                }


            } else if(plot.getOwners().contains(p.getUniqueId()) || p.hasPermission("plotborder.admin")) {
                plot.setComponent(type, materialToSet);
                isComponentChanged = true;
            } else {
                p.sendMessage(notYourPlot);
            }
            if(isComponentChanged) {

                String changedMessage = PlotBorder.getColoredConfigString(type + "-changed").replaceAll("%name%", componentName);
                p.sendMessage(changedMessage);
                if(!p.hasPermission("plotborder.admin") && !p.hasPermission("plotborder.nocooldown." + type)) {
                    PlotBorder.addCooldown(p);
                }
            }

        } else {
            p.sendMessage(notOnPlot);
        }


        Bukkit.getScheduler().runTask(PlotBorder.getPlugin(), p::closeInventory);
    }


}
