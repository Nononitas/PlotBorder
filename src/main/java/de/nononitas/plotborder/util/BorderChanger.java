package de.nononitas.plotborder.util;

import com.plotsquared.core.configuration.ConfigurationUtil;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.function.pattern.Pattern;
import de.nononitas.plotborder.Gui;
import de.nononitas.plotborder.PlotBorder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BorderChanger {

    public static void change(Gui.Type componentType, Player p, String materialToSet, String componentName) {
        String notYourPlot = PlotBorder.getColoredConfigString("not-your-plot");
        String notOnPlot = PlotBorder.getColoredConfigString("not-on-plot");

        PlotPlayer<Player> plotPlayer = PlotPlayer.from(p);
        Plot plot = plotPlayer.getCurrentPlot();
        String type = componentType.getType();
        Pattern pattern = ConfigurationUtil.BLOCK_BUCKET.parseString(materialToSet).toPattern();
        if(plot != null) {
            boolean isComponentChanged = false;
            if(plot.getConnectedPlots().size() > 1) {

                for (Plot plots : plot.getConnectedPlots()) {
                    if(plots.getOwners().contains(p.getUniqueId()) || p.hasPermission("plotborder.admin")) {
                        plots.getPlotModificationManager().setComponent(type, pattern, null, null);
                        isComponentChanged = true;
                    } else {
                        p.sendMessage(notYourPlot);
                        break;
                    }
                }


            } else if(plot.getOwners().contains(p.getUniqueId()) || p.hasPermission("plotborder.admin")) {
                plot.getPlotModificationManager().setComponent(type, pattern, null, null);
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
