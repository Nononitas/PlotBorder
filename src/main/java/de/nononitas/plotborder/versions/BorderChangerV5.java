package de.nononitas.plotborder.versions;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import de.nononitas.plotborder.Gui;
import de.nononitas.plotborder.PlotBorder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BorderChangerV5 implements BorderChanger {
    @Override
    public void change(Gui.Type componentType, Player p, String materialToSet, String componentName) {
        PlotPlayer plotPlayer = PlotPlayer.wrap(p);
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

                String changedMessage = PlotBorder.getColoredConfigString(type+"-changed").replaceAll("%name%", componentName);
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
